package com.returns.service.service.impl;

import com.returns.service.dto.PagingResult;
import com.returns.service.dto.request.CustomerReturnRequestDTO;
import com.returns.service.dto.request.CustomerReturnRequestItemDTO;
import com.returns.service.dto.request.CustomerReturnSearchDTO;
import com.returns.service.dto.request.PaginationRequest;
import com.returns.service.dto.response.ApiResponse;
import com.returns.service.dto.response.BillingDetailResponseDTO;
import com.returns.service.dto.response.BillingResponseDTO;
import com.returns.service.dto.response.StockResponseDTO;
import com.returns.service.enums.Status;
import com.returns.service.mapper.ReturnMapper;
import com.returns.service.model.*;
import com.returns.service.repository.*;
import com.returns.service.service.CustomerReturnService;
import com.returns.service.specification.BillingSpecification;
import com.returns.service.util.PaginationUtil;
import com.returns.service.util.ResponseMessageUtil;
import com.returns.service.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomerReturnServiceImpl implements CustomerReturnService {

    private final ResponseUtil responseUtil;
    private final MessageSource messageSource;
    private final BillingRepository billingRepository;
    private final StockRepository stockRepository;
    private final CashierUserRepository cashierUserRepository;
    private final ReturnsRepository returnsRepository;
    private final ReturnsDetailRepository returnsDetailRepository;

    @Cacheable(value = "billing", key = "#invoiceNo")
    public Optional<Billing> findByInvoiceNo(String invoiceNo) {
        return billingRepository.findByInvoiceNumber(invoiceNo);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> filterList(PaginationRequest<CustomerReturnSearchDTO> paginationRequest, Locale locale) {
        try {
            log.info("Customer return filter list {}", paginationRequest);
            Pageable pageable = PaginationUtil.getPageable(paginationRequest);

            Page<Billing> billings = Objects.nonNull(paginationRequest.getSearch()) ?
                    billingRepository.findAll(BillingSpecification.getSpecification(paginationRequest.getSearch()), pageable) :
                    billingRepository.findAll(BillingSpecification.getSpecification(), pageable);
            log.info("Customer return filter records {}", billings);
            long totalElements = Objects.nonNull(paginationRequest.getSearch()) ?
                    billingRepository.count(BillingSpecification.getSpecification(paginationRequest.getSearch())) :
                    billingRepository.count(BillingSpecification.getSpecification());
            log.info("Customer return filter records map start");
            List<BillingResponseDTO> responseDTOList = billings.stream()
                    .map(ReturnMapper::toCustomerReturn).toList();
            log.info("Customer return filter records map finish");
            return ResponseEntity.ok().body(responseUtil.success((Object) new PagingResult<BillingResponseDTO>(responseDTOList, responseDTOList.size(), totalElements),
                    messageSource.getMessage(ResponseMessageUtil.BILLING_FILTER_LIST_SUCCESS,
                            null, locale)));
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> view(CustomerReturnRequestDTO customerReturnRequestDTO, Locale locale) {
        try {
            log.info("Customer return view {}", customerReturnRequestDTO);

            return findByInvoiceNo(customerReturnRequestDTO.getInvoiceNo()).map(billing -> {
                // Fetch all return details for this billing
                List<ReturnDetails> returnDetailsList = returnsDetailRepository.findAllByReturns_Billing_Id(billing.getId());
                List<BillingDetailResponseDTO> billingDetailResponseDTOS = billing.getBillingDetails().stream()
                    .map(bd -> ReturnMapper.toCustomerReturnDetails(bd, returnDetailsList)).toList();
                return ResponseEntity.ok().body(responseUtil.success((Object) Map.of("billingDetail", billingDetailResponseDTOS), messageSource.getMessage(ResponseMessageUtil.BILLING_DETAIL_RETRIEVED_SUCCESS,
                        null, locale)));
            }).orElseGet(() -> {
                log.info("Billing user not found {}", customerReturnRequestDTO.getUsername());
                return ResponseEntity.ok().body(responseUtil.error(null, 1015, messageSource.getMessage(ResponseMessageUtil.BILLING_NOT_FOUND, new Object[]{customerReturnRequestDTO.getInvoiceNo()}, locale)));
            });

        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ResponseEntity<ApiResponse<Object>> returns(CustomerReturnRequestDTO customerReturnRequestDTO, Locale locale) {
        try {
            log.info("Customer return items {}", customerReturnRequestDTO);
           return findByInvoiceNo(customerReturnRequestDTO.getInvoiceNo()).map(billing -> {

                List<BillingDetail> billingDetails = billing.getBillingDetails();

                List<Long> requestedStockIds = customerReturnRequestDTO.getCustomerReturnRequestItemDTOList().stream()
                    .map(CustomerReturnRequestItemDTO::getStock)
                    .toList();

                List<StockResponseDTO> matchedStocks = billingDetails.stream()
                    .filter(bd -> bd.getStock() != null && requestedStockIds.contains(bd.getStock().getId()))
                    .map(bd -> ReturnMapper.toCustomerReturnDetails(bd).getStock())
                    .toList();

                if(matchedStocks.size() != requestedStockIds.size()) {
                    log.info("Customer return returns items {}", matchedStocks);
                    return ResponseEntity.ok().body(responseUtil.error(null, 1016, messageSource.getMessage(ResponseMessageUtil.SOME_ITEMS_NOT_REGISTER_INVOICE, new Object[]{customerReturnRequestDTO.getInvoiceNo()}, locale)));
                }

               Map<Long, BillingDetail> stockIdToBillingDetail = billingDetails.stream()
                       .filter(bd -> bd.getStock() != null)
                       .collect(Collectors.toMap(bd -> bd.getStock().getId(), bd -> bd));

               BigDecimal calculatedReturnAmount = BigDecimal.ZERO;
               for (CustomerReturnRequestItemDTO itemDTO : customerReturnRequestDTO.getCustomerReturnRequestItemDTOList()) {
                   BillingDetail bd = stockIdToBillingDetail.get(itemDTO.getStock());
                   if (bd != null && bd.getSalesPrice() != null && itemDTO.getQty() != null) {
                       calculatedReturnAmount = calculatedReturnAmount.add(bd.getSalesPrice().multiply(itemDTO.getQty()));
                   }
               }

               if (calculatedReturnAmount.compareTo(customerReturnRequestDTO.getDebitAmount()) != 0) {
                   log.info("Calculated return amount {} does not match debitAmount {}", calculatedReturnAmount, customerReturnRequestDTO.getDebitAmount());
                   return ResponseEntity.ok().body(
                           responseUtil.error(
                                   null,
                                   1017,
                                   messageSource.getMessage(ResponseMessageUtil.RETURN_AMOUNT_DO_NOT_MATCH, new Object[]{customerReturnRequestDTO.getDebitAmount()}, locale)
                           )
                   );
               }

               Optional<CashierUser> userOpt = cashierUserRepository.findByUsername(customerReturnRequestDTO.getUsername());
               if (userOpt.isEmpty()) {
                   log.info("User not found: {}", customerReturnRequestDTO.getUsername());
                   return ResponseEntity.ok().body(responseUtil.error(null, 1014, messageSource.getMessage(ResponseMessageUtil.CASHIER_USER_NOT_FOUND, new Object[]{customerReturnRequestDTO.getUsername()}, locale)));
               }

               Returns returns = ReturnMapper.returns(customerReturnRequestDTO, billing, userOpt.get().getLocation());
               returnsRepository.saveAndFlush(returns);
               log.info("Returns entity saved: {}", returns);

               for (CustomerReturnRequestItemDTO itemDTO : customerReturnRequestDTO.getCustomerReturnRequestItemDTOList()) {
                   ReturnDetails returnDetails = ReturnMapper.returnsDetails(returns, itemDTO);
                   returnsDetailRepository.saveAndFlush(returnDetails);
                   log.info("ReturnDetails entity saved: {}", returnDetails);
               }

               return ResponseEntity.ok().body(responseUtil.success(null, messageSource.getMessage(ResponseMessageUtil.CUSTOMER_ITEM_RETURN_SUCCESS,
                       null, locale)));

            }).orElseGet(() -> {
                log.info("Billing user not found {}", customerReturnRequestDTO.getUsername());
                return ResponseEntity.ok().body(responseUtil.error(null, 1015, messageSource.getMessage(ResponseMessageUtil.BILLING_NOT_FOUND, new Object[]{customerReturnRequestDTO.getInvoiceNo()}, locale)));
            });
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }
}
