package com.returns.service.service.impl;

import com.returns.service.dto.PagingResult;
import com.returns.service.dto.request.*;
import com.returns.service.dto.response.*;
import com.returns.service.dto.search.KeywordSearch;
import com.returns.service.enums.Status;
import com.returns.service.mapper.ReturnMapper;
import com.returns.service.model.*;
import com.returns.service.repository.*;
import com.returns.service.service.CustomerReturnService;
import com.returns.service.specification.BillingSpecification;
import com.returns.service.specification.ReturnsSpecification;
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
    private final CashierUserRepository cashierUserRepository;
    private final ReturnsRepository returnsRepository;
    private final ReturnsDetailRepository returnsDetailRepository;
    private final StockRepository stockRepository;
    private final BillingDetailRepository billingDetailRepository;
    private final ReturnsInvoiceSequenceRepository returnsInvoiceSequenceRepository;

    @Cacheable(value = "billing", key = "#invoiceNo")
    public Optional<Billing> findByInvoiceNo(String invoiceNo) {
        return billingRepository.findByInvoiceNumber(invoiceNo);
    }

    @Cacheable(value = "returnItem", key = "#returnId")
    public Optional<Returns> returnItem(Long returnId) {
        return returnsRepository.findById(returnId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> filterList(PaginationRequest<CustomerReturnSearchDTO> paginationRequest, Locale locale) {
        try {
            log.info("Customer return filter list {}", paginationRequest);
            Pageable pageable = PaginationUtil.getPageable(paginationRequest);
            Optional<CashierUser> cashierUser = cashierUserRepository.findByUsername(paginationRequest.getUsername());
            Page<Billing> billings = Objects.nonNull(paginationRequest.getSearch()) ?
                    billingRepository.findAll(BillingSpecification.getSpecification(paginationRequest.getSearch(),cashierUser.get().getLocation().getCode()), pageable) :
                    billingRepository.findAll(BillingSpecification.getSpecification(cashierUser.get().getLocation().getCode()), pageable);
            log.info("Customer return filter records {}", billings);
            long totalElements = Objects.nonNull(paginationRequest.getSearch()) ?
                    billingRepository.count(BillingSpecification.getSpecification(paginationRequest.getSearch(),cashierUser.get().getLocation().getCode())) :
                    billingRepository.count(BillingSpecification.getSpecification(cashierUser.get().getLocation().getCode()));
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
                    .map(CustomerReturnRequestItemDTO::getBillingId)
                    .toList();

               List<Long> billingIds = billingDetails.stream()
                       .map(BillingDetail::getId)
                       .toList();

                List<Long> matchedStocks = requestedStockIds.stream()
                    .filter(billingIds::contains).toList();

                if(matchedStocks.size() != requestedStockIds.size()) {
                    log.info("Customer return returns items {}", matchedStocks);
                    return ResponseEntity.ok().body(responseUtil.error(null, 1016, messageSource.getMessage(ResponseMessageUtil.SOME_ITEMS_NOT_REGISTER_INVOICE, new Object[]{customerReturnRequestDTO.getInvoiceNo()}, locale)));
                }

               Map<Long, BillingDetail> stockIdToBillingDetail = billingDetails.stream()
                       .filter(bd -> bd.getId() != null)
                       .collect(Collectors.toMap(BillingDetail::getId, bd -> bd));

               BigDecimal calculatedReturnAmount = BigDecimal.ZERO;
               for (CustomerReturnRequestItemDTO itemDTO : customerReturnRequestDTO.getCustomerReturnRequestItemDTOList()) {
                   BillingDetail bd = stockIdToBillingDetail.get(itemDTO.getBillingId());

                   if(bd.getQty().compareTo(itemDTO.getQty()) < 0) {
                       log.info("Invalid qty of item {}", itemDTO.getBillingId());
                       return ResponseEntity.ok().body(responseUtil.error(null, 1039, messageSource.getMessage(ResponseMessageUtil.INSUFFICIENT_STOCK, new Object[]{itemDTO.getQty()}, locale)));
                   }else if(itemDTO.getQty().compareTo(bd.getReturnsQty()) < 0){
                       log.info("Invalid qty of item {}", itemDTO.getBillingId());
                       return ResponseEntity.ok().body(responseUtil.error(null, 1039, messageSource.getMessage(ResponseMessageUtil.INSUFFICIENT_STOCK, new Object[]{itemDTO.getQty()}, locale)));

                   }

                   if (bd.getSalesPrice() != null && itemDTO.getQty() != null) {
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

               for (CustomerReturnRequestItemDTO itemDTO : customerReturnRequestDTO.getCustomerReturnRequestItemDTOList()) {
                   BillingDetail bd = stockIdToBillingDetail.get(itemDTO.getBillingId());
                   log.info("Billing return update");
                   bd.setReturnsQty(bd.getReturnsQty().add(itemDTO.getQty()));
                   billingDetailRepository.saveAndFlush(bd);
               }
               log.info("Generating next invoice number");
               String invoiceNumber = getNextInvoiceNumber();
               log.info("Generated invoice number: {}", invoiceNumber);
               Returns returns = ReturnMapper.returns(customerReturnRequestDTO, billing, userOpt.get().getLocation(),invoiceNumber,userOpt.get());
               returnsRepository.saveAndFlush(returns);
               log.info("Returns entity saved: {}", returns);

               for (CustomerReturnRequestItemDTO itemDTO : customerReturnRequestDTO.getCustomerReturnRequestItemDTOList()) {
                   ReturnDetails returnDetails = ReturnMapper.returnsDetails(returns, itemDTO);
                   returnsDetailRepository.saveAndFlush(returnDetails);
                   Optional<BillingDetail> billingDetail = billingDetailRepository.findById(itemDTO.getBillingId());
                   if (billingDetail.isPresent()) {
                       Stock stock = billingDetail.get().getStock();
                       stock.setQty(stock.getQty().add(itemDTO.getQty()));
                       stockRepository.saveAndFlush(stock);
                       log.info("Stock updated: {}", stock.getId());
                   }

                   log.info("ReturnDetails entity saved: {}", returnDetails);
               }

               InvoiceResponseDTO billingInvoice = ReturnMapper.toReturnInvoice(returns,billing);

               return ResponseEntity.ok().body(responseUtil.success((Object) billingInvoice, messageSource.getMessage(ResponseMessageUtil.CUSTOMER_ITEM_RETURN_SUCCESS,
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
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> toDayReturnsFilterList(PaginationRequest<KeywordSearch> paginationRequest, Locale locale) {
        try {

            log.info("Today returns view {} ",paginationRequest);
            Pageable pageable = PaginationUtil.getPageable(paginationRequest);

            Page<Returns> returns = Objects.nonNull(paginationRequest.getSearch()) ?
                    returnsRepository.findAll(ReturnsSpecification.getSpecification(paginationRequest.getSearch(),paginationRequest.getUsername()), pageable) :
                    returnsRepository.findAll(ReturnsSpecification.getSpecification(paginationRequest.getUsername()), pageable);
            log.info("Returns filter records");
            long totalElements = Objects.nonNull(paginationRequest.getSearch()) ?
                    returnsRepository.count(ReturnsSpecification.getSpecification(paginationRequest.getSearch(),paginationRequest.getUsername())) :
                    returnsRepository.count(ReturnsSpecification.getSpecification(paginationRequest.getUsername()));

            List<ReturnsResponseDTO> returnsResponseDTOS = returns.stream().map(ReturnMapper::toReturnsResponse).toList();

            return ResponseEntity.ok().body(responseUtil.success((Object) new PagingResult<ReturnsResponseDTO>(returnsResponseDTOS, returnsResponseDTOS.size(), totalElements),
                    messageSource.getMessage(ResponseMessageUtil.TODAY_RETURNS_FILTER_LIST_SUCCESS,
                            null, locale)));

        }catch (Exception e) {
            log.info(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> toDayReturnsByItem(TodayReturnsRequestDTO todayReturnsRequestDTO, Locale locale) {
        try {
            log.info("Today returns item {} ",todayReturnsRequestDTO.getId());
            return returnItem(todayReturnsRequestDTO.getId()).map(bi -> {
                List<ReturnsItemResponseDTO> returnsItemResponseDTOS = bi.getReturnDetails()
                        .stream().map(ReturnMapper::toReturnsItemResponse).toList();
                log.info("Returns item invoice mapper ");
                return ResponseEntity.ok().body(responseUtil.success((Object) Map.of("billingDetail", returnsItemResponseDTOS), messageSource.getMessage(ResponseMessageUtil.CUSTOMER_RETURNS_RETRIEVE_SUCCESS, null, locale)));
            }).orElseGet(() -> {
                log.info("Returns item not found {}",todayReturnsRequestDTO.getId());
                return ResponseEntity.ok().body(responseUtil.error(null, 1019, messageSource.getMessage(ResponseMessageUtil.CUSTOMER_RETURNS_NOT_FOUND, new Object[]{todayReturnsRequestDTO.getId()}, locale)));
            });
        }catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Transactional
    public String getNextInvoiceNumber() {
        log.info("Fetching next invoice number from sequence");
        ReturnsInvoiceSequence seq = returnsInvoiceSequenceRepository.findById(1).orElseThrow();
        long next = seq.getNextVal();
        log.info("Current sequence value: {}", next);
        seq.setNextVal(next + 1);
        returnsInvoiceSequenceRepository.saveAndFlush(seq);
        String padded = String.format("R%09d", next);
        log.info("Returning padded invoice number: {}", padded);
        return padded;
    }
}
