package com.billing.service.controller;

import com.billing.service.dto.response.ApiResponse;
import com.billing.service.dto.response.ItemResponseDTO;
import com.billing.service.dto.response.StockResponseDTO;
import com.billing.service.model.Stock;
import com.billing.service.repository.StockRepository;
import com.billing.service.util.ResponseMessageUtil;
import com.billing.service.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/barcode")
@Log4j2
public class BarCodeController {

    private final ResponseUtil responseUtil;
    private final MessageSource messageSource;
    private final StockRepository stockRepository;

    public BarCodeController(ResponseUtil responseUtil, MessageSource messageSource, StockRepository stockRepository) {
        this.responseUtil = responseUtil;
        this.messageSource = messageSource;
        this.stockRepository = stockRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Handle stock bar code request", notes = "Stock bar code request success or failed")
    public ResponseEntity<ApiResponse<Object>> genBarCodeItem(@RequestParam("item") String item, @RequestParam("location") String location, Locale locale) {

        log.info("Stock bar code request controller: {} {} ", item, location);
        List<Stock> stocks = stockRepository.findAllByItem_DescriptionContainingIgnoreCaseAndLocation_Code(item, location);

        List<StockResponseDTO> list = stocks.stream().map(stock -> {
            StockResponseDTO stockResponseDTO = new StockResponseDTO();
            ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
            itemResponseDTO.setCode(stock.getItem().getCode());
            itemResponseDTO.setDescription(stock.getItem().getDescription());
            stockResponseDTO.setItem(itemResponseDTO);
            stockResponseDTO.setLablePrice(stock.getLablePrice());
            return stockResponseDTO;
        }).toList();

        return ResponseEntity.ok().body(responseUtil.success((Object) Map.of("item", list), messageSource.getMessage(ResponseMessageUtil.BILLING_STOCK_FILTER_LIST_SUCCESS, null, locale)));

    }

}
