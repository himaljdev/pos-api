package com.billing.service.service.impl;

import com.billing.service.dto.CustomerLimit;
import com.billing.service.dto.PagingResult;
import com.billing.service.dto.SimpleBaseDTO;
import com.billing.service.dto.request.ChannelRequestDTO;
import com.billing.service.dto.request.PaginationRequest;
import com.billing.service.dto.response.ApiResponse;
import com.billing.service.dto.response.BillingResponseDTO;
import com.billing.service.dto.search.KeywordSearch;
import com.billing.service.enums.Status;
import com.billing.service.mapper.BillingMapper;
import com.billing.service.model.Billing;
import com.billing.service.model.Customer;
import com.billing.service.repository.CustomerRepository;
import com.billing.service.service.CustomerService;
import com.billing.service.specification.BillingSpecification;
import com.billing.service.specification.CustomerSpecification;
import com.billing.service.util.PaginationUtil;
import com.billing.service.util.ResponseMessageUtil;
import com.billing.service.util.ResponseUtil;
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
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ResponseUtil responseUtil;
    private final MessageSource messageSource;

    @Cacheable(value = "customer")
    public List<Customer> customer() {
        return customerRepository.findAllByStatus(Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> filterList(PaginationRequest<KeywordSearch> paginationRequest , Locale locale) {
        try {
            log.info("Customer filter {} ",paginationRequest);
            Pageable pageable = PaginationUtil.getPageable(paginationRequest);

            Page<Customer> customers = Objects.nonNull(paginationRequest.getSearch()) ?
                    customerRepository.findAll(CustomerSpecification.getSpecification(paginationRequest.getSearch()), pageable) :
                    customerRepository.findAll(CustomerSpecification.getSpecification(), pageable);
            log.info("Customer filter records");
            long totalElements = Objects.nonNull(paginationRequest.getSearch()) ?
                    customerRepository.count(CustomerSpecification.getSpecification(paginationRequest.getSearch())) :
                    customerRepository.count(CustomerSpecification.getSpecification());

            List<BillingResponseDTO> billingResponseDTOStream = null ;//customers.stream().map(BillingMapper::toBillingResponse).toList();

            return ResponseEntity.ok().body(responseUtil.success((Object) new PagingResult<BillingResponseDTO>(billingResponseDTOStream, billingResponseDTOStream.size(), totalElements),
                    messageSource.getMessage(ResponseMessageUtil.TODAY_SALES_FILTER_LIST_SUCCESS,
                            null, locale)));

        }catch (Exception e) {
            log.info(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> getAllRefDataCustomers(ChannelRequestDTO channelRequestDTO, Locale locale) {

        try {
            log.info("Get all customers {}", channelRequestDTO);

            HashMap<String,Object> responseMap = new HashMap<>();
            List<SimpleBaseDTO> customerBase =  new ArrayList<>();
            HashMap<String,Object> map = new HashMap<>();

            List<Customer> customerList = customer().stream().filter(cu -> !cu.getId().equals(1L)).toList();

//            customerList.forEach(customer ->{
//                log.info("Customer : {}", customer.getId());
//                customerBase.add(new SimpleBaseDTO(String.valueOf(customer.getId()),customer.getFirstName()+" "+customer.getLastName()));
//                log.info("Customer added : {}", customer.getId());
//                map.put(String.valueOf(customer.getId()),new CustomerLimit(customer.getFundLimit(),customer.getCustomerBalance().getPendingBalance()));
//                log.info("Customer completed: {}", customer.getId());
//            });

            responseMap.put("customerBase",customerBase);
            responseMap.put("customerDetails",map);

            return ResponseEntity.ok().body(responseUtil.success((Object) responseMap, messageSource.getMessage(ResponseMessageUtil.CUSTOMER_REFERENCE_DATE_SUCCESS, null, locale)));

        }catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

}
