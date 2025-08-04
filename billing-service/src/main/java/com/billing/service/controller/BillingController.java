package com.billing.service.controller;

import com.billing.service.dto.request.*;
import com.billing.service.dto.request.validator.BillingRequestValidatorDTO;
import com.billing.service.dto.request.validator.CashInOutRequestValidatorDTO;
import com.billing.service.dto.request.validator.ChannelRequestValidatorDTO;
import com.billing.service.dto.request.validator.TodayBillingRequestValidatorDTO;
import com.billing.service.dto.response.ApiResponse;
import com.billing.service.dto.search.KeywordSearch;
import com.billing.service.service.BillingService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Locale;

@RestController
@RequestMapping(path = "api/v1/billing")
@Log4j2
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;
    private final Gson gson;

    @PostMapping(path = "/reference", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle reference list request request ", notes = "Reference list request success or failed")
    public ResponseEntity<ApiResponse<Object>> referenceDate(@RequestBody @Valid ChannelRequestValidatorDTO channelRequestValidatorDTO, Locale locale) {
        log.info("Reference request controller {} ", channelRequestValidatorDTO);
        return billingService.referenceDate(gson.fromJson(gson.toJson(channelRequestValidatorDTO), ChannelRequestDTO.class), locale);
    }

    @PostMapping(path = "/stock-list", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle stock  list request request ", notes = "Stock list request success or failed")
    public ResponseEntity<ApiResponse<Object>> allStock(@RequestBody @Valid ChannelRequestValidatorDTO channelRequestValidatorDTO, Locale locale) {
        log.info("Stock list request controller {} ", channelRequestValidatorDTO);
        return billingService.allStock(gson.fromJson(gson.toJson(channelRequestValidatorDTO), ChannelRequestDTO.class), locale);
    }

    @PostMapping(path = "/today-cash-in-out", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle cash In/Out today view request request ", notes = "Cash In/Out today view request success or failed")
    public ResponseEntity<ApiResponse<Object>> toDayCashInOut(@RequestBody @Valid ChannelRequestValidatorDTO channelRequestValidatorDTO, Locale locale) {
        log.info("Cash In/Out today view request controller {} ", channelRequestValidatorDTO);
        return billingService.toDayCashInOut(gson.fromJson(gson.toJson(channelRequestValidatorDTO), ChannelRequestDTO.class), locale);
    }

    @PostMapping(path = "/cash-in-out", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle cash In/Out request request ", notes = "Cash In/Out request success or failed")
    public ResponseEntity<ApiResponse<Object>> cashInOut(@RequestBody @Valid CashInOutRequestValidatorDTO cashInOutRequestValidatorDTO, Locale locale) {
        log.info("Cash In/Out request controller {} ", cashInOutRequestValidatorDTO);
        return billingService.cashInOut(gson.fromJson(gson.toJson(cashInOutRequestValidatorDTO), CashInOutRequestDTO.class), locale);
    }

    @PostMapping(path = "/checkout", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle checkout request request ", notes = "Checkout request success or failed")
    public ResponseEntity<ApiResponse<Object>> checkout(@RequestBody @Valid BillingRequestValidatorDTO billingRequestValidatorDTO, Locale locale) {
        log.info("Checkout request controller {} ", billingRequestValidatorDTO);
        return billingService.checkout(gson.fromJson(gson.toJson(billingRequestValidatorDTO), BillingRequestDTO.class), locale);
    }

    @PostMapping(path = "/last-invoice", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle latest invoice request request ", notes = "Latest invoice request success or failed")
    public ResponseEntity<ApiResponse<Object>> lastInvoice(@RequestBody @Valid ChannelRequestValidatorDTO channelRequestValidatorDTO, Locale locale) {
        log.info("Latest invoice request controller {} ", channelRequestValidatorDTO);
        return billingService.lastInvoice(gson.fromJson(gson.toJson(channelRequestValidatorDTO), ChannelRequestDTO.class), locale);
    }

    @PostMapping(path = "/today-sales-list",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle today sales filter list request request ",notes = "Today sales filter list request success or failed")
    public ResponseEntity<ApiResponse<Object>> toDaySalesFilterList(@RequestBody @Valid PaginationRequest<KeywordSearch> paginationRequest, Locale locale) {
        log.info("Today sales filter list request controller {} ", paginationRequest);
        Type paginationRequestType = new TypeToken<PaginationRequest<KeywordSearch>>(){}.getType();
        return billingService.toDaySalesFilterList(gson.fromJson(gson.toJson(paginationRequest), paginationRequestType), locale);
    }

    @PostMapping(path = "/today-sales-item", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle today sales item request request ", notes = "Today sales find item  request success or failed")
    public ResponseEntity<ApiResponse<Object>> toDaySalesByItem(@RequestBody @Valid TodayBillingRequestValidatorDTO todayBillingRequestValidatorDTO, Locale locale) {
        log.info("Today sales find item request controller {} ", todayBillingRequestValidatorDTO);
        return billingService.toDaySalesByItem(gson.fromJson(gson.toJson(todayBillingRequestValidatorDTO), TodayBillingRequestDTO.class), locale);
    }

    @PostMapping(path = "/checkout-token", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle checkout token request request ", notes = "Checkout token request success or failed")
    public ResponseEntity<ApiResponse<Object>> checkoutToken(@RequestBody @Valid ChannelRequestValidatorDTO channelRequestValidatorDTO, Locale locale) {
        log.info("Checkout token request controller {} ", channelRequestValidatorDTO);
        return billingService.checkoutToken(gson.fromJson(gson.toJson(channelRequestValidatorDTO), ChannelRequestDTO.class), locale);
    }

}
