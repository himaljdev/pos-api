package com.billing.service.service.impl;

import com.billing.service.dto.PagingResult;
import com.billing.service.dto.SimpleBaseDTO;
import com.billing.service.dto.request.*;
import com.billing.service.dto.response.*;
import com.billing.service.dto.search.KeywordSearch;
import com.billing.service.enums.SalesType;
import com.billing.service.enums.Status;
import com.billing.service.model.*;
import com.billing.service.repository.*;
import com.billing.service.service.BillingService;
import com.billing.service.specification.BillingSpecification;
import com.billing.service.specification.StockSpecification;
import com.billing.service.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.cache.annotation.Cacheable;
import com.billing.service.mapper.BillingMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;


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
    private final CheckoutTokenRepository checkoutTokenRepository;

    @Value("${other.item}")
    private String otherItem;

    @Cacheable(value = "lastInvoice", key = "#cashier")
    public Billing lastInvoice(String cashier) {
        return billingRepository.findTopByCashierUser_UsernameOrderByCreatedDateDesc(cashier);
    }

    @Cacheable(value = "billingItem", key = "#billingId")
    public Optional<Billing> billingItem(Long billingId) {
        return billingRepository.findById(billingId);
    }

    @Cacheable(value = "cashierUser", key = "#username")
    public Optional<CashierUser> findByUsername(String username) {
        return cashierUserRepository.findByUsername(username);
    }

    @Cacheable(value = "customer", key = "#customerId")
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Cacheable(value = "stock", key = "#id + ':' + #locationCode + ':' +#status")
    public Optional<Stock> getStock(Long id, String locationCode, Status status) {
        return stockRepository.findByIdAndLocation_CodeAndStatus(id, locationCode, status);
    }

    @Cacheable(value = "cashInOut", key = "#cashier + ':' + #startdate + ':' + #endDate")
    public List<CashInOut> getCashInOut(String cashier, Date startDate, Date endDate) {
        return cashInOutRepository.findAllByCashierUser_UsernameAndCreatedDateBetween(cashier, startDate, endDate);
    }

    @Cacheable("activeCustomers")
    public List<CustomerResponseDTO> getActiveCustomers() {
        return customerRepository.findAllByStatusNot(Status.DELETE).stream()
                .filter(cu -> !cu.getId().equals(1L))
                .map(cu -> {
                    CustomerResponseDTO dto = new CustomerResponseDTO();
                    dto.setId(cu.getId());
                    dto.setName(cu.getTitle().getDescription() + "." + cu.getFirstName() + " " + cu.getLastName());
                    dto.setStatus(cu.getStatus().name());
                    dto.setStatusDescription(cu.getStatus().getDescription());
                    return dto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> referenceDate(ChannelRequestDTO channelRequestDTO, Locale locale) {
        try {
            log.info("Get reference date {}", channelRequestDTO);
            Map<String, Object> responseMap = new HashMap<>();

            List<CustomerResponseDTO> customerResponseDTOS = getActiveCustomers();

            List<SimpleBaseDTO> salesType = Arrays.stream(SalesType.values())
                    .map(st -> new SimpleBaseDTO(st.name(), st.getDescription())).toList();

            List<SimpleBaseDTO> cashInOutType = Arrays.stream(com.billing.service.enums.CashInOut.values())
                    .map(ci -> new SimpleBaseDTO(ci.name(), ci.getDescription())).toList();

            responseMap.put("customer", customerResponseDTOS);
            responseMap.put("salesType", salesType);
            responseMap.put("cashInOut", cashInOutType);

            return ResponseEntity.ok().body(responseUtil.success((Object) responseMap, messageSource.getMessage(ResponseMessageUtil.BILLING_REFERENCE_DATE_SUCCESS, null, locale)));
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(
//            value = "stockFilterList",
//            keyGenerator = "stockFilterKeyGenerator"
//    )
    public ResponseEntity<ApiResponse<Object>> allStock(ChannelRequestDTO channelRequestDTO, Locale locale) {
        try {
            log.info("Stock filter list request {}", channelRequestDTO);

//            PaginationRequest pa = new PaginationRequest();
//            Pageable e = PaginationUtil.getPageable(pa);

            return findByUsername(channelRequestDTO.getUsername()).map(user -> {

//                Specification<Stock> spec = StockSpecification.getSpecification(user.getLocation().getCode());
//
//                Page<Stock> stocks = stockRepository.findAll(spec, e);

                List<Stock> stocks = stockRepository.findAll(StockSpecification.getSpecification(user.getLocation().getCode()));
                log.info("Stock filter records {}", stocks);
                log.info("Stock filter records map start");
                List<StockResponseDTO> responseDTOList = stocks.stream()
                        .map(BillingMapper::toStock).toList();
                log.info("Stock filter records map finish");
                return ResponseEntity.ok().body(responseUtil.success((Object) Map.of("stock", responseDTOList), messageSource.getMessage(ResponseMessageUtil.BILLING_STOCK_FILTER_LIST_SUCCESS, null, locale)));

            }).orElseGet(() -> {
                log.info("cashier user not found {}", channelRequestDTO.getUsername());
                return ResponseEntity.ok().body(responseUtil.error(null, 1013, messageSource.getMessage(ResponseMessageUtil.CASHIER_USER_NOT_FOUND, new Object[]{channelRequestDTO.getUsername()}, locale)));
            });

        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> toDayCashInOut(ChannelRequestDTO channelRequestDTO, Locale locale) {
        try {
            log.info("Today view {}", channelRequestDTO);
            Date startOfToday = DateTimeUtil.getStartOfToday();
            Date endOfToday = DateTimeUtil.getEndOfToday();
            log.info(startOfToday);
            log.info(endOfToday);
            List<CashInOut> cashInOut = getCashInOut(channelRequestDTO.getUsername(), startOfToday, endOfToday);
            List<CashInOutResponseDTO> inOutResponseDTOS = cashInOut.stream().map(BillingMapper::toCashInOut).toList();
            return ResponseEntity.ok().body(responseUtil.success(Map.of("cash", inOutResponseDTOS), messageSource.getMessage(ResponseMessageUtil.CASH_IN_OUT_RETRIEVE_SUCCESSFULLY, null, locale)));
        } catch (Exception e) {
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
                        new Object[]{com.billing.service.enums.CashInOut.valueOf(cashInOutRequestDTO.getCashInOut()).getDescription()}
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

            String checkoutToken = billingRequestDTO.getCheckoutToken();
            Optional<CheckoutToken> tokenOpt;

            if (checkoutToken == null || checkoutToken.isEmpty()) {
                log.info("Checkout token is empty");
                return ResponseEntity.ok().body(
                        responseUtil.error(null, 1017,
                                messageSource.getMessage(ResponseMessageUtil.CHECKOUT_TOKEN_NOT_FOUND, null, locale)));
            }

            tokenOpt = checkoutTokenRepository.findByToken(checkoutToken);

            if (tokenOpt.isEmpty()) {
                log.info("Token not found in DB: {}", checkoutToken);
                return ResponseEntity.ok().body(
                        responseUtil.error(null, 1018,
                                messageSource.getMessage(ResponseMessageUtil.CHECKOUT_TOKEN_NOT_FOUND, null, locale)));
            }

            String tokenValue = tokenOpt.get().getToken();

            if (billingRepository.existsByToken(tokenOpt.get())) {
                log.info("Billing token {} already exists", tokenValue);
                return ResponseEntity.ok().body(
                        responseUtil.error(null, 1019,
                                messageSource.getMessage(ResponseMessageUtil.DUPLICATE_BILLING_FOUND, null, locale)));
            }

            return cashierUserRepository.findByUsername(billingRequestDTO.getCashierUser())
                    .map(cashier -> getCustomerById(billingRequestDTO.getCustomer())
                            .map(customer -> {

                                List<BillingItemRequestDTO> items = billingRequestDTO.getBillingItem();
                                Map<Long, List<BillingItemRequestDTO>> groupedItems = new HashMap<>();
                                for (BillingItemRequestDTO item : items) {
                                    Long stockKey = item.getStock();
                                    groupedItems.computeIfAbsent(stockKey, k -> new ArrayList<>()).add(item);
                                }
                                List<BillingItemRequestDTO> otherItem = new ArrayList<>();
                                BigDecimal totalAmount = BigDecimal.ZERO;

                                for (Map.Entry<Long, List<BillingItemRequestDTO>> entry : groupedItems.entrySet()) {
                                    Long stockId = entry.getKey();
                                    List<BillingItemRequestDTO> itemList = entry.getValue();

                                    for (BillingItemRequestDTO itemReq : itemList) {
                                        log.info("Processing item {}", stockId);
                                        Boolean isOther = itemReq.getOther();
                                        log.info("Processing item isOther {}", isOther);
                                        BigDecimal reqQty = itemReq.getQty();

                                        if (isOther) {
                                            log.info("Other stock {} ", stockId);
                                            otherItem.add(itemReq);
                                            continue;
                                        }

                                        Stock stock = getStock(stockId, cashier.getLocation().getCode(), Status.ACTIVE)
                                                .orElse(null);

                                        if (stock == null) {
                                            log.info("Stock not found for item {} at location {}", stockId, cashier.getLocation().getCode());
                                            return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.STOCK_NOT_FOUND, new Object[]{stockId}, locale)));
                                        }

                                        BigDecimal totalQty = itemList.stream()
                                                .filter(item -> !item.getOther())
                                                .map(BillingItemRequestDTO::getQty)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        if (stock.getQty().compareTo(totalQty) < 0) {
                                            log.info("Insufficient stock for item {}: requested {}, available {}", stockId, totalQty, stock.getQty());
                                            return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.INSUFFICIENT_STOCK, new Object[]{stockId, totalQty, stock.getQty()}, locale)));
                                        }
                                        BigDecimal itemCost = stock.getItemCost();

                                        BigDecimal reqTotal;
                                        if (billingRequestDTO.getSalesType().equals(SalesType.NORMAL.name())) {
                                            reqTotal = itemReq.getSalesPrice() != null ? itemReq.getSalesPrice() : stock.getRetailPrice();
                                            log.info("Sales price {}", reqTotal);
                                        } else {
                                            reqTotal = stock.getWholesalePrice();
                                        }

                                        if (itemCost.compareTo(reqTotal) > 0) {
                                            log.info("Item cost {} less than requested total {} for item {}", itemCost, reqTotal, stockId);
                                            return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.ITEM_COST_INVALID, new Object[]{stockId}, locale)));
                                        }
                                        BigDecimal totAmount = reqTotal.multiply(reqQty);
                                        totalAmount = totalAmount.add(totAmount);
                                        log.info("Total amount {}", totalAmount);
                                    }
                                }

                                if (!otherItem.isEmpty()) {
                                    for (BillingItemRequestDTO billingItemRequestDTO : otherItem) {
                                        Stock stock = getStock(billingItemRequestDTO.getStock(), cashier.getLocation().getCode(), Status.ACTIVE)
                                                .orElse(null);

                                        if (stock == null) {
                                            log.info("Stock not found for item {} at location {}", billingItemRequestDTO.getStock(), cashier.getLocation().getCode());
                                            return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.STOCK_NOT_FOUND, new Object[]{billingItemRequestDTO.getStock()}, locale)));
                                        }

                                        BigDecimal itemCost = stock.getItemCost();
                                        BigDecimal reqTotal = billingItemRequestDTO.getSalesPrice();

                                        if (itemCost.compareTo(reqTotal) > 0) {
                                            log.info("Item cost {} less than requested total {} for item {}", itemCost, reqTotal, billingItemRequestDTO.getStock());
                                            return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.ITEM_COST_INVALID, new Object[]{billingItemRequestDTO.getStock()}, locale)));
                                        }

                                        BigDecimal totAmount = reqTotal.multiply(billingItemRequestDTO.getQty());
                                        totalAmount = totalAmount.add(totAmount);
                                        log.info("Other item Total amount {}", totalAmount);
                                    }
                                }

                                if (totalAmount.compareTo(billingRequestDTO.getTotalAmount()) > 0) {
                                    log.info("Total billing amount {} less than total amount amount {}", totalAmount, billingRequestDTO.getTotalAmount());
                                    return ResponseEntity.ok().body(responseUtil.error(null, 1012, messageSource.getMessage(ResponseMessageUtil.TOTAL_AMOUNT_INVALID, new Object[]{totalAmount, billingRequestDTO.getTotalAmount()}, locale)));
                                }

                                log.info("All billing checks passed. Total amount: {}", totalAmount);
                                log.info("Generating next invoice number");
                                String invoiceNumber = getNextInvoiceNumber();
                                log.info("Generated invoice number: {}", invoiceNumber);
                                Billing billing = BillingMapper.toBilling(billingRequestDTO, cashier, customer, cashier.getLocation(), totalAmount, invoiceNumber, tokenOpt.get());
                                log.info("Saving billing entity: {}", billing);
                                billingRepository.saveAndFlush(billing);
                                log.info("Saved billing entity with ID: {}", billing.getId());

                                for (Map.Entry<Long, List<BillingItemRequestDTO>> entry : groupedItems.entrySet()) {
                                    Long stockId = entry.getKey();
                                    List<BillingItemRequestDTO> itemList = entry.getValue();
                                    Stock stock = getStock(stockId, cashier.getLocation().getCode(), Status.ACTIVE).orElse(null);
                                    if (stock == null) continue;
                                    BigDecimal totalQty = itemList.stream()
                                            .filter(item -> !item.getOther())
                                            .map(BillingItemRequestDTO::getQty)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    for (BillingItemRequestDTO itemReq : itemList) {
                                        log.info("Mapping billing detail for stock: {}", stockId);
                                        BillingDetail detail = BillingMapper.toBillingDetail(billing, itemReq, stock);
                                        log.info("Saving billing detail: {}", detail);
                                        billingDetailRepository.saveAndFlush(detail);
                                        log.info("Saved billing detail with ID: {}", detail.getId());
                                    }

                                    log.info("Updating stock qty for stock ID {}: {} - {}", stockId, stock.getQty(), totalQty);
                                    stock.setQty(stock.getQty().subtract(totalQty));
                                    stockRepository.saveAndFlush(stock);
                                    log.info("Updated stock qty for stock ID {}: {}", stockId, stock.getQty());

                                    StockUpdateDTO update = new StockUpdateDTO();
                                    update.setStockId(stock.getId());
                                    update.setNewQty(stock.getQty());
                                    messagingTemplate.convertAndSend("/topic/stock-updates", update);
                                }
                                InvoiceResponseDTO billingInvoice = BillingMapper.toBillingInvoice(billing,false,null,0,null);
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
            log.error("Error processing checkout", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> lastInvoice(ChannelRequestDTO channelRequestDTO, Locale locale) {
        try {
            log.info("Last invoice request: {}", channelRequestDTO);
            Billing lastBill = lastInvoice(channelRequestDTO.getUsername());
            boolean isNormal = lastBill.getSalesType().equals(SalesType.NORMAL);

            List<InvoiceItemResponseDTO> itemResponseDTOS = lastBill.getBillingDetailList().stream().map(bd -> {
                log.info("Map billing details {} ", bd.getId());
                BigDecimal tot;
                if (isNormal) {
                    tot = bd.getQty().multiply(bd.getRetailPrice());
                } else {
                    tot = bd.getQty().multiply(bd.getWholesalePrice());
                }
                return BillingMapper.toBillingInvoiceItem(bd, tot);
            }).toList();
            log.info("Total cost cal start");
            BigDecimal totalCost = lastBill.getPayAmount().subtract(lastBill.getTotalAmount());
            log.info("Total cost {}", totalCost);
            InvoiceResponseDTO billingInvoice = BillingMapper.toBillingInvoice(lastBill, true, itemResponseDTOS, itemResponseDTOS.size(), totalCost);

            log.info("Billing response invoice mapper {}", billingInvoice);
            return ResponseEntity.ok().body(responseUtil.success((Object) billingInvoice, messageSource.getMessage(ResponseMessageUtil.LATEST_BILLING_RETRIEVE_SUCCESS, null, locale)));

        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> toDaySalesFilterList(PaginationRequest<KeywordSearch> paginationRequest , Locale locale) {
      try {
          log.info("Today sales view {} ",paginationRequest);
          Pageable pageable = PaginationUtil.getPageable(paginationRequest);

          Page<Billing> billings = Objects.nonNull(paginationRequest.getSearch()) ?
                  billingRepository.findAll(BillingSpecification.getSpecification(paginationRequest.getSearch(),paginationRequest.getUsername()), pageable) :
                  billingRepository.findAll(BillingSpecification.getSpecification(paginationRequest.getUsername()), pageable);
          log.info("Billing filter records");
          long totalElements = Objects.nonNull(paginationRequest.getSearch()) ?
                  billingRepository.count(BillingSpecification.getSpecification(paginationRequest.getSearch(),paginationRequest.getUsername())) :
                  billingRepository.count(BillingSpecification.getSpecification(paginationRequest.getUsername()));

          List<BillingResponseDTO> billingResponseDTOStream = billings.stream().map(BillingMapper::toBillingResponse).toList();

          return ResponseEntity.ok().body(responseUtil.success((Object) new PagingResult<BillingResponseDTO>(billingResponseDTOStream, billingResponseDTOStream.size(), totalElements),
                  messageSource.getMessage(ResponseMessageUtil.TODAY_SALES_FILTER_LIST_SUCCESS,
                          null, locale)));

      }catch (Exception e) {
          log.info(e);
          throw e;
      }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Object>> toDaySalesByItem(TodayBillingRequestDTO todayBillingRequestDTO, Locale locale) {
       try {
           log.info("Today sales item {} ",todayBillingRequestDTO.getId());
           return billingItem(todayBillingRequestDTO.getId()).map(bi -> {
               List<BillingItemResponseDTO> billingItemResponseDTOS = bi.getBillingDetailList()
                       .stream().map(BillingMapper::toBillingItemResponse).toList();
               log.info("Billing item invoice mapper ");
               return ResponseEntity.ok().body(responseUtil.success((Object)Map.of( "sales",billingItemResponseDTOS), messageSource.getMessage(ResponseMessageUtil.BILLING_ITEM_RETRIEVE_SUCCESS, null, locale)));
           }).orElseGet(() -> {
              log.info("Billing item not found {}",todayBillingRequestDTO.getId());
               return ResponseEntity.ok().body(responseUtil.error(null, 1018, messageSource.getMessage(ResponseMessageUtil.BILLING_NOT_FOUND, new Object[]{todayBillingRequestDTO.getId()}, locale)));
           });
       }catch (Exception e) {
           log.error(e);
           throw e;
       }
    }

    @Override
    @Transactional(readOnly = false)
    public ResponseEntity<ApiResponse<Object>> checkoutToken(ChannelRequestDTO channelRequestDTO, Locale locale) {

        try {
            log.info("checkout token {} ",channelRequestDTO);
            String s = RandomGeneratorUtil.generateRandomToken();
            log.info("Token generated {}", s);
            checkoutTokenRepository.saveAndFlush(new CheckoutToken(s));
            return ResponseEntity.ok().body(responseUtil.success((Object)Map.of("checkoutToken",s), messageSource.getMessage(ResponseMessageUtil.CHECKOUT_TOKEN_GENERATE_SUCCESS, null, locale)));
        }catch (Exception e) {
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




