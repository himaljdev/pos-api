package com.billing.service.service;

import com.billing.service.dto.request.ChannelRequestDTO;
import com.billing.service.dto.request.PaginationRequest;
import com.billing.service.dto.response.ApiResponse;
import com.billing.service.dto.search.KeywordSearch;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface CustomerService {
    ResponseEntity<ApiResponse<Object>> getAllRefDataCustomers(ChannelRequestDTO channelRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> filterList(PaginationRequest<KeywordSearch> paginationRequest , Locale locale);
}
