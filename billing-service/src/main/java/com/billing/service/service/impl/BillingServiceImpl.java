package com.billing.service.service.impl;

import com.billing.service.dto.request.BillingRequestDTO;
import com.billing.service.dto.request.CashInOutRequestDTO;
import com.billing.service.dto.request.ChannelRequestDTO;
import com.billing.service.dto.response.ApiResponse;
import com.billing.service.dto.response.CashInOutResponseDTO;
import com.billing.service.dto.response.InvoiceResponseDTO;
import com.billing.service.enums.SalesType;
import com.billing.service.model.*;
import com.billing.service.repository.*;
import com.billing.service.service.BillingService;
import com.billing.service.util.DateTimeUtil;
import com.billing.service.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import java.math.BigDecimal;

import com.billing.service.dto.request.BillingItemRequestDTO;
import com.billing.service.util.ResponseMessageUtil;
import org.springframework.cache.annotation.Cacheable;

import com.billing.service.mapper.BillingMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.billing.service.dto.response.StockUpdateDTO;


@Service
@Log4j2
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BillingServiceImpl implements BillingService {

    private final CashierUserRepository cashierUserRepository;
    private final CustomerRepository customerRepository;
    private final ResponseUtil responseUtil;
    private final MessageSource messageSource;
    private final StockRepository stockRepository;
    private final BillingRepository billingRepository;
    private final BillingDetailRepository billingDetailRepository;
    private final InvoiceSequenceRepository invoiceSequenceRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CashInOutRepository cashInOutRepository;

    @Cacheable(value = "cashierUser", key = "#username")
    public Optional<CashierUser> findByUsername(String username) {
        return cashierUserRepository.findByUsername(username);
    }

    @Cacheable(value = "customer", key = "#customerId")
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Cacheable(value = "stock", key = "#id + ':' + #locationCode")
    public Optional<Stock> getStock(Long id, String locationCode) {
        return stockRepository.findByIdAndLocation_Code(id, locationCode);
    }

    @Cacheable(value = "cashInOut", key = "#cashier + ':' + #startdate + ':' + #endDate")
    public List<CashInOut> getCashInOut(String cashier, Date startDate, Date endDate) {
        return cashInOutRepository.findAllByCashierUser_UsernameAndCreatedDateBetween(cashier,startDate,endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> todayView(ChannelRequestDTO channelRequestDTO, Locale locale) {
      try {
          log.info("Today view {}", channelRequestDTO);
          Date startOfToday = DateTimeUtil.getStartOfToday();
          Date endOfToday = DateTimeUtil.getEndOfToday();
          log.info("Today view end {}", endOfToday);
          log.info("Today view start {}", startOfToday);
          List<CashInOut> cashInOut = getCashInOut(channelRequestDTO.getUsername(), startOfToday,endOfToday);
          List<CashInOutResponseDTO> inOutResponseDTOS = cashInOut.stream().map(BillingMapper::toCashInOut).toList();
          return ResponseEntity.ok().body(responseUtil.success(inOutResponseDTOS, messageSource.getMessage(ResponseMessageUtil.CASH_IN_OUT_RETRIEVE_SUCCESSFULLY,null, locale)));
      }catch (Exception e) {
          log.error(e);
          throw e;
      }
    }

    @Override
    @Transactional(readOnly = false)
    public ResponseEntity<ApiResponse<Object>> cashInOut(CashInOutRequestDTO cashInOutRequestDTO, Locale locale) {
        try {
            log.info("cashInOut received request {}", cashInOutRequestDTO);
            return findByUsername(cashInOutRequestDTO.getUsername()).map(user -> {
                CashInOut cashInOut = BillingMapper.toCashInOut(cashInOutRequestDTO, user);
                cashInOutRepository.saveAndFlush(cashInOut);
                return ResponseEntity.ok().body(responseUtil.success(null, messageSource.getMessage(ResponseMessageUtil.CASH_IN_OUT_ADDED_SUCCESSFULLY,
                        new Object[]{com.billing.service.enums.CashInOut.valueOf(cashInOutRequestDTO.getCashInOut()).equals(com.billing.service.enums.CashInOut.IN) ? "In" : "Out"}
                        , locale)));
            }).orElseGet(() -> {
                log.info("cashier user not found {}", cashInOutRequestDTO.getUsername());
                return ResponseEntity.ok().body(responseUtil.error(null, 1013, messageSource.getMessage(ResponseMessageUtil.CASHIER_USER_NOT_FOUND, new Object[]{cashInOutRequestDTO.getUsername()}, locale)));
            });

        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ResponseEntity<ApiResponse<Object>> checkout(BillingRequestDTO billingRequestDTO, Locale locale) {
        try {
            log.info("Processing billing request {}", billingRequestDTO.toString());
            return cashierUserRepository.findByUsername(billingRequestDTO.getCashierUser())
                    .map(cashier -> getCustomerById(billingRequestDTO.getCustomer())
                            .map(customer -> {
                                // Merge duplicate stock entries by summing qty
                                List<BillingItemRequestDTO> items = billingRequestDTO.getBillingItem();
                                Map<Long, BillingItemRequestDTO> mergedItems = new HashMap<>();
                                for (BillingItemRequestDTO item : items) {
                                    Long stockKey = item.getStock();
                                    if (mergedItems.containsKey(stockKey)) {
                                        BillingItemRequestDTO existing = mergedItems.get(stockKey);
                                        existing.setQty(existing.getQty() + item.getQty());
                                    } else {
                                        BillingItemRequestDTO copy = new BillingItemRequestDTO();
                                        copy.setStock(item.getStock());
                                        copy.setQty(item.getQty());
                                        copy.setSalesPrice(item.getSalesPrice());
                                        copy.setSalesDiscount(item.getSalesDiscount());
                                        mergedItems.put(stockKey, copy);
                                    }
                                }
                                List<BillingItemRequestDTO> mergedList = new ArrayList<>(mergedItems.values());
                                BigDecimal totalAmount = BigDecimal.ZERO;
                                for (BillingItemRequestDTO itemReq : mergedList) {
                                    Long stockId = itemReq.getStock();
                                    int reqQty = itemReq.getQty();

                                    Stock stock = getStock(stockId, cashier.getLocation().getCode())
                                            .orElse(null);
                                    if (stock == null) {
                                        log.info("Stock not found for item {} at location {}", stockId, cashier.getLocation().getCode());
                                        return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.STOCK_NOT_FOUND, new Object[]{stockId}, locale)));
                                    }
                                    if (stock.getQty() < reqQty) {
                                        log.info("Insufficient stock for item {}: requested {}, available {}", stockId, reqQty, stock.getQty());
                                        return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.INSUFFICIENT_STOCK, new Object[]{stockId, reqQty, stock.getQty()}, locale)));
                                    }
                                    BigDecimal itemCost = stock.getItemCost();

                                    BigDecimal reqTotal;
                                    if (billingRequestDTO.getSalesType().equals(SalesType.NORMAL.name())) {
                                        reqTotal = itemReq.getSalesPrice();
                                        log.info("Sales price {}", reqTotal);
                                    } else { // WHOLESALE
                                        reqTotal = BigDecimal.valueOf(itemReq.getSalesDiscount() / 100);
                                        log.info("Sales discount {}", reqTotal);
                                    }

                                    if (itemCost.compareTo(reqTotal) > 0) {
                                        log.info("Item cost {} less than requested total {} for item {}", itemCost, reqTotal, stockId);
                                        return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.ITEM_COST_INVALID, new Object[]{stockId}, locale)));
                                    }
                                    BigDecimal totAmount = reqTotal.multiply(BigDecimal.valueOf(itemReq.getQty()));
                                    totalAmount = totalAmount.add(totAmount);
                                    log.info("Total amount {}", totalAmount);
                                }
                                // Check if totalAmount >= payAmount
                                if (totalAmount.compareTo(billingRequestDTO.getTotalAmount()) > 0) {
                                    log.info("Total billing amount {} less than total amount amount {}", totalAmount, billingRequestDTO.getTotalAmount());
                                    return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.TOTAL_AMOUNT_INVALID, new Object[]{totalAmount, billingRequestDTO.getTotalAmount()}, locale)));
                                }
                                // All checks passed, proceed with billing logic
                                log.info("All billing checks passed. Total amount: {}", totalAmount);
                                // 1. Create Billing entity using mapper
                                log.info("Generating next invoice number");
                                String invoiceNumber = getNextInvoiceNumber();
                                log.info("Generated invoice number: {}", invoiceNumber);
                                Billing billing = BillingMapper.toBilling(billingRequestDTO, cashier, customer, cashier.getLocation(), totalAmount, invoiceNumber);
                                log.info("Saving billing entity: {}", billing);
                                billingRepository.saveAndFlush(billing);
                                log.info("Saved billing entity with ID: {}", billing.getId());
                                // 2. Create BillingDetail for each item and update stock
                                for (BillingItemRequestDTO itemReq : mergedList) {
                                    Stock stock = getStock(itemReq.getStock(), cashier.getLocation().getCode()).orElse(null);
                                    if (stock == null) continue; // Should not happen due to earlier checks
                                    log.info("Mapping billing detail for stock: {}", stock.getId());
                                    BillingDetail detail = BillingMapper.toBillingDetail(billing, itemReq, stock);
                                    log.info("Saving billing detail: {}", detail);
                                    billingDetailRepository.saveAndFlush(detail);
                                    log.info("Saved billing detail with ID: {}", detail.getId());
                                    // Update stock qty
                                    log.info("Updating stock qty for stock ID {}: {} - {}", stock.getId(), stock.getQty(), itemReq.getQty());
                                    stock.setQty(stock.getQty() - itemReq.getQty());
                                    stockRepository.saveAndFlush(stock);
                                    log.info("Updated stock qty for stock ID {}: {}", stock.getId(), stock.getQty());
                                    // Send real-time update
                                    StockUpdateDTO update = new StockUpdateDTO();
                                    update.setStockId(stock.getId());
                                    update.setNewQty(stock.getQty());
                                    messagingTemplate.convertAndSend("/topic/stock-updates", update);
                                }
                                InvoiceResponseDTO billingInvoice = BillingMapper.toBillingInvoice(billing);
                                log.info("Billing response invoice mapper {}", billingInvoice);
                                return ResponseEntity.ok().body(responseUtil.success((Object) billingInvoice, messageSource.getMessage(ResponseMessageUtil.BILLING_PROCESSED_SUCCESS, null, locale)));
                            }).orElseGet(() -> {
                                log.info("Customer not found {}", billingRequestDTO.getCustomer());
                                return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.CUSTOMER_NOT_FOUND, new Object[]{billingRequestDTO.getCustomer()}, locale)));
                            })).orElseGet(() -> {
                        log.info("Cashier not found {}", billingRequestDTO.getCashierUser());
                        return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.CASHIER_NOT_FOUND, new Object[]{billingRequestDTO.getCashierUser()}, locale)));
                    });
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Transactional
    public String getNextInvoiceNumber() {
        log.info("Fetching next invoice number from sequence");
        InvoiceSequence seq = invoiceSequenceRepository.findById(1).orElseThrow();
        long next = seq.getNextVal();
        log.info("Current sequence value: {}", next);
        seq.setNextVal(next + 1);
        invoiceSequenceRepository.saveAndFlush(seq);
        String padded = String.format("%010d", next);
        log.info("Returning padded invoice number: {}", padded);
        return padded;
    }
}




