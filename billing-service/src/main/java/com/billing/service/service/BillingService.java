package com.billing.service.service;


import com.billing.service.dto.request.*;
import com.billing.service.dto.response.ApiResponse;
import com.billing.service.dto.search.KeywordSearch;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface BillingService {
    ResponseEntity<ApiResponse<Object>> referenceDate(ChannelRequestDTO channelRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> allStock(ChannelRequestDTO channelRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> toDayCashInOut(ChannelRequestDTO channelRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> cashInOut(CashInOutRequestDTO cashInOutRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> checkout(BillingRequestDTO billingRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> lastInvoice(ChannelRequestDTO channelRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> toDaySalesFilterList(PaginationRequest<KeywordSearch> paginationRequest, Locale locale);
    ResponseEntity<ApiResponse<Object>> toDaySalesByItem(TodayBillingRequestDTO todayBillingRequestDTO, Locale locale);
}
