package com.returns.service.service.impl;

import com.returns.service.dto.PagingResult;
import com.returns.service.dto.request.CustomerReturnRequestDTO;
import com.returns.service.dto.request.CustomerReturnRequestItemDTO;
import com.returns.service.dto.request.CustomerReturnSearchDTO;
import com.returns.service.dto.request.PaginationRequest;
import com.returns.service.dto.response.ApiResponse;
import com.returns.service.dto.response.BillingDetailResponseDTO;
import com.returns.service.dto.response.BillingResponseDTO;
import com.returns.service.mapper.ReturnMapper;
import com.returns.service.model.Billing;
import com.returns.service.repository.BillingRepository;
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

import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomerReturnServiceImpl implements CustomerReturnService {

    private final ResponseUtil responseUtil;
    private final MessageSource messageSource;
    private final BillingRepository billingRepository;

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
                List<BillingDetailResponseDTO> billingDetailResponseDTOS = billing.getBillingDetails().stream().map(ReturnMapper::toCustomerReturnDetails).toList();
                return ResponseEntity.ok().body(responseUtil.success((Object) Map.of("billingDetail",billingDetailResponseDTOS), messageSource.getMessage(ResponseMessageUtil.BILLING_DETAIL_RETRIEVED_SUCCESS,
                      null, locale)));
            }).orElseGet(() -> {
                log.info("Billing user not found {}", customerReturnRequestDTO.getUsername());
                return ResponseEntity.ok().body(responseUtil.error(null, 1015, messageSource.getMessage(ResponseMessageUtil.BILLING_NOT_FOUND, new Object[]{customerReturnRequestDTO.getInvoiceNo()}, locale)));
            });

        }catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ResponseEntity<ApiResponse<Object>> returns(CustomerReturnRequestDTO customerReturnRequestDTO, Locale locale) {
        try {
            log.info("Customer return items {}", customerReturnRequestDTO);
            return (ResponseEntity<ApiResponse<Object>>) findByInvoiceNo(customerReturnRequestDTO.getInvoiceNo()).map(billing -> {

                for (CustomerReturnRequestItemDTO cus : customerReturnRequestDTO.getCustomerReturnRequestItemDTOList()){

                }

                return null;

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
