package com.returns.service.controller;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.returns.service.dto.request.*;
import com.returns.service.dto.request.validator.CustomerReturnRequestValidatorDTO;
import com.returns.service.dto.request.validator.TodayReturnsRequestValidatorDTO;
import com.returns.service.dto.response.ApiResponse;
import com.returns.service.dto.search.KeywordSearch;
import com.returns.service.service.CustomerReturnService;
import com.returns.service.validator.OnReturn;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.Locale;

@RestController
@RequestMapping(path = "api/v1/customer-returns")
@Log4j2
@RequiredArgsConstructor
public class CustomerReturnController {

    private final CustomerReturnService customerReturnService;
    private final Gson gson;

    @PostMapping(path = "/filter-list", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle customer return filter list request request ", notes = "Customer return filter list request success or failed")
    public ResponseEntity<ApiResponse<Object>> filterList(@RequestBody @Valid PaginationRequest<CustomerReturnSearchDTO> paginationRequest, Locale locale) {
        log.info("Customer return filter list request controller {} ", paginationRequest);
        Type paginationRequestType = new TypeToken<PaginationRequest<CustomerReturnSearchDTO>>() {
        }.getType();
        return customerReturnService.filterList(gson.fromJson(gson.toJson(paginationRequest), paginationRequestType), locale);
    }

    @PostMapping(path = "/view", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle customer return find by ID request request ", notes = "Customer return find by ID request success or failed")
    public ResponseEntity<ApiResponse<Object>> view(@RequestBody @Valid CustomerReturnRequestValidatorDTO customerReturnRequestValidatorDTO, Locale locale) {
        log.info("Customer return find by ID request controller {} ", customerReturnRequestValidatorDTO);
        return customerReturnService.view(gson.fromJson(gson.toJson(customerReturnRequestValidatorDTO), CustomerReturnRequestDTO.class), locale);
    }

    @PostMapping(path = "/returns",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle customer return request request ",notes = "Customer return request success or failed")
    public ResponseEntity<ApiResponse<Object>> returns(@RequestBody @Validated(OnReturn.class) @Valid CustomerReturnRequestValidatorDTO customerReturnRequestValidatorDTO, Locale locale) {
        log.info("Customer return request controller {} ", customerReturnRequestValidatorDTO);
        return customerReturnService.returns(gson.fromJson(gson.toJson(customerReturnRequestValidatorDTO), CustomerReturnRequestDTO.class), locale);
    }

    @PostMapping(path = "/today-returns-list",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle today returns filter list request request ",notes = "Today returns filter list request success or failed")
    public ResponseEntity<ApiResponse<Object>> toDayReturnsFilterList(@RequestBody @Valid PaginationRequest<KeywordSearch> paginationRequest, Locale locale) {
        log.info("Today returns filter list request controller {} ", paginationRequest);
        Type paginationRequestType = new TypeToken<PaginationRequest<KeywordSearch>>(){}.getType();
        return customerReturnService.toDayReturnsFilterList(gson.fromJson(gson.toJson(paginationRequest), paginationRequestType), locale);
    }

    @PostMapping(path = "/today-returns-item", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle today returns item request request ", notes = "Today returns find item  request success or failed")
    public ResponseEntity<ApiResponse<Object>> toDayReturnsByItem(@RequestBody @Valid TodayReturnsRequestValidatorDTO todayReturnsRequestValidatorDTO, Locale locale) {
        log.info("Today returns find item request controller {} ", todayReturnsRequestValidatorDTO);
        return customerReturnService.toDayReturnsByItem(gson.fromJson(gson.toJson(todayReturnsRequestValidatorDTO), TodayReturnsRequestDTO.class), locale);
    }

}
