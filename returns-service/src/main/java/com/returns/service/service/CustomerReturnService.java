package com.returns.service.service;

import com.returns.service.dto.request.CustomerReturnRequestDTO;
import com.returns.service.dto.request.CustomerReturnSearchDTO;
import com.returns.service.dto.request.PaginationRequest;
import com.returns.service.dto.request.TodayReturnsRequestDTO;
import com.returns.service.dto.response.ApiResponse;
import com.returns.service.dto.search.KeywordSearch;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public interface CustomerReturnService {
    ResponseEntity<ApiResponse<Object>> filterList(PaginationRequest<CustomerReturnSearchDTO> paginationRequest, Locale locale);
    ResponseEntity<ApiResponse<Object>> view(CustomerReturnRequestDTO customerReturnRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> returns(CustomerReturnRequestDTO customerReturnRequestDTO, Locale locale);
    ResponseEntity<ApiResponse<Object>> toDayReturnsFilterList(PaginationRequest<KeywordSearch> paginationRequest, Locale locale);
    ResponseEntity<ApiResponse<Object>> toDayReturnsByItem(TodayReturnsRequestDTO todayReturnsRequestDTO, Locale locale);
}
