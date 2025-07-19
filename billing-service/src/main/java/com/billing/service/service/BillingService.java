package com.billing.service.service;


import com.billing.service.dto.request.BillingRequestDTO;
import com.billing.service.dto.request.CashInOutRequestDTO;
import com.billing.service.dto.request.ChannelRequestDTO;
import com.billing.service.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface BillingService {
    ResponseEntity<ApiResponse<Object>> todayView(ChannelRequestDTO channelRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> cashInOut(CashInOutRequestDTO cashInOutRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> checkout(BillingRequestDTO billingRequestDTO, Locale locale);
}
