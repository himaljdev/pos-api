package com.billing.service.service.util;

import com.billing.service.dto.request.PaginationRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("stockFilterKeyGenerator")
@Log4j2
public class StockFilterKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        log.info("Call stockFilterKeyGenerator");
        StringBuilder key = new StringBuilder();
        if (params.length > 0 && params[0] instanceof PaginationRequest<?> paginationRequest) {
            key.append(paginationRequest.getUsername()).append(":");
            key.append(paginationRequest.getPage()).append(":");
            key.append(paginationRequest.getSize()).append(":");
            key.append(paginationRequest.getSearch() != null ? paginationRequest.getSearch().toString() : "null");
        }
        return key.toString();
    }
}




