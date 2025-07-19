package com.billing.service.controller;

import com.billing.service.dto.request.BillingRequestDTO;
import com.billing.service.dto.request.CashInOutRequestDTO;
import com.billing.service.dto.request.ChannelRequestDTO;
import com.billing.service.dto.request.validator.BillingRequestValidatorDTO;
import com.billing.service.dto.request.validator.CashInOutRequestValidatorDTO;
import com.billing.service.dto.request.validator.ChannelRequestValidatorDTO;
import com.billing.service.dto.response.ApiResponse;
import com.billing.service.service.BillingService;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;

@RestController
@RequestMapping(path = "api/v1/billing")
@Log4j2
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;
    private final Gson gson;

    @PostMapping(path = "/today-cash-in-out",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle cash In/Out today view request request ",notes = "Cash In/Out today view request success or failed")
    public ResponseEntity<ApiResponse<Object>> todayView(@RequestBody @Valid ChannelRequestValidatorDTO channelRequestValidatorDTO, Locale locale) {
        log.info("Cash In/Out today view request controller {} ", channelRequestValidatorDTO);
        return billingService.todayView(gson.fromJson(gson.toJson(channelRequestValidatorDTO), ChannelRequestDTO.class), locale);
    }

    @PostMapping(path = "/cash-in-out",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle cash In/Out request request ",notes = "Cash In/Out request success or failed")
    public ResponseEntity<ApiResponse<Object>> cashInOut(@RequestBody @Valid CashInOutRequestValidatorDTO cashInOutRequestValidatorDTO, Locale locale) {
        log.info("Cash In/Out request controller {} ", cashInOutRequestValidatorDTO);
        return billingService.cashInOut(gson.fromJson(gson.toJson(cashInOutRequestValidatorDTO), CashInOutRequestDTO.class), locale);
    }

    @PostMapping(path = "/checkout",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle checkout request request ",notes = "Checkout request success or failed")
    public ResponseEntity<ApiResponse<Object>> checkout(@RequestBody @Valid BillingRequestValidatorDTO billingRequestValidatorDTO, Locale locale) {
        log.info("Checkout request controller {} ", billingRequestValidatorDTO);
        return billingService.checkout(gson.fromJson(gson.toJson(billingRequestValidatorDTO), BillingRequestDTO.class), locale);
    }

}
