package com.billing.service.mapper;

import com.billing.service.dto.SimpleBaseDTO;
import com.billing.service.dto.request.CashInOutRequestDTO;
import com.billing.service.dto.response.*;
import com.billing.service.enums.Status;
import com.billing.service.enums.Unit;
import com.billing.service.model.*;
import com.billing.service.dto.request.BillingRequestDTO;
import com.billing.service.dto.request.BillingItemRequestDTO;
import com.billing.service.enums.PaymentType;
import com.billing.service.enums.SalesType;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
public class BillingMapper {
    public static Billing toBilling(BillingRequestDTO dto, CashierUser cashier, Customer customer, Location location, BigDecimal totalAmount, String invoiceNumber,CheckoutToken checkoutToken) {
        Billing billing = new Billing();
        billing.setInvoiceNumber(invoiceNumber);
        billing.setCashierUser(cashier);
        billing.setPaymentType(PaymentType.valueOf(dto.getPaymentType()));
        billing.setCustomer(customer);
        billing.setLocation(location);
        billing.setSalesType(SalesType.valueOf(dto.getSalesType()));
        billing.setTotalAmount(totalAmount);
        billing.setPayAmount(dto.getPayAmount());
        billing.setRemark(dto.getRemark());
        billing.setToken(checkoutToken);
        return billing;
    }

    public static BillingDetail toBillingDetail(Billing billing, BillingItemRequestDTO itemReq, Stock stock) {
        BillingDetail detail = new BillingDetail();
        detail.setBilling(billing);
        detail.setQty(itemReq.getQty());
        detail.setSalesPrice(itemReq.getSalesPrice());
        detail.setSalesDiscount(itemReq.getSalesDiscount());
        detail.setItemCost(stock.getItemCost());
        detail.setLablePrice(stock.getLablePrice());
        detail.setRetailPrice(stock.getRetailPrice());
        detail.setWholesalePrice(stock.getWholesalePrice());
        detail.setRetailDiscount(stock.getRetailDiscount());
        detail.setWholesaleDiscount(stock.getWholesaleDiscount());
        detail.setStock(stock);
        return detail;
    }

    public static InvoiceResponseDTO toBillingInvoice(Billing billing, boolean isState,
                                                      List<InvoiceItemResponseDTO> invoiceItemResponseDTOList,
                                                      int totalItem,BigDecimal balance) {
        InvoiceResponseDTO invoiceResponseDTO = new InvoiceResponseDTO();
        invoiceResponseDTO.setInvoiceNumber(billing.getInvoiceNumber());
        invoiceResponseDTO.setCounter(billing.getCashierUser().getFirstName());
        invoiceResponseDTO.setCustomerName(billing.getCustomer().getFirstName()+" "+billing.getCustomer().getLastName());
        invoiceResponseDTO.setPaymentType(billing.getPaymentType().name());
        invoiceResponseDTO.setPaymentTypeDescription(PaymentType.valueOf(billing.getPaymentType().name()).getDescription());
        invoiceResponseDTO.setSalesType(billing.getSalesType().name());
        invoiceResponseDTO.setSalesTypeDescription(SalesType.valueOf(billing.getSalesType().name()).getDescription());
        invoiceResponseDTO.setOutletName(billing.getLocation().getCity());
        invoiceResponseDTO.setInvoiceDate(billing.getCreatedDate());

        if(isState){
            invoiceResponseDTO.setInvoiceItems(invoiceItemResponseDTOList);
            invoiceResponseDTO.setTotalItems(totalItem);
            invoiceResponseDTO.setTotalAmount(billing.getTotalAmount());
            invoiceResponseDTO.setCashAmount(billing.getPayAmount());
            invoiceResponseDTO.setBalanceAmount(balance);
        }
        return invoiceResponseDTO;
    }

    public static InvoiceItemResponseDTO toBillingInvoiceItem(BillingDetail billingDetail,BigDecimal total) {

        InvoiceItemResponseDTO invoiceItemResponseDTO = new InvoiceItemResponseDTO();
        invoiceItemResponseDTO.setItem(billingDetail.getStock().getItem().getDescription());
        invoiceItemResponseDTO.setQty(billingDetail.getQty());
        invoiceItemResponseDTO.setSalesDiscount(billingDetail.getSalesDiscount());
        invoiceItemResponseDTO.setSalesPrice(billingDetail.getSalesPrice());
        invoiceItemResponseDTO.setTotal(total);

        return invoiceItemResponseDTO;
    }

    public static CashInOut toCashInOut(CashInOutRequestDTO cashInOutRequestDTO,CashierUser cashierUser) {
        CashInOut cashInOut = new CashInOut();
        cashInOut.setCashierUser(cashierUser);
        cashInOut.setCashInOut(com.billing.service.enums.CashInOut.valueOf(cashInOutRequestDTO.getCashInOut()));
        cashInOut.setRemark(cashInOutRequestDTO.getRemark());
        cashInOut.setAmount(cashInOutRequestDTO.getAmount());
        return cashInOut;
    }

    public static CashInOutResponseDTO toCashInOut(CashInOut cashInOut) {
        CashInOutResponseDTO cashInOutResponseDTO = new CashInOutResponseDTO();
        cashInOutResponseDTO.setCashInOut(cashInOut.getCashInOut().name());
        cashInOutResponseDTO.setCashInOutDescription(com.billing.service.enums.CashInOut.valueOf(cashInOut.getCashInOut().name()).getDescription());
        cashInOutResponseDTO.setRemark(cashInOut.getRemark());
        cashInOutResponseDTO.setAmount(cashInOut.getAmount());
        cashInOutResponseDTO.setDate(cashInOut.getCreatedDate());
        cashInOutResponseDTO.setId(cashInOut.getId());
        return cashInOutResponseDTO;
    }

    public static BillingResponseDTO toBillingResponse(Billing billing) {
        BillingResponseDTO billingResponseDTO = new BillingResponseDTO();
        billingResponseDTO.setId(billing.getId());
        billingResponseDTO.setInvoiceNumber(billing.getInvoiceNumber());
        billingResponseDTO.setLocation(new SimpleBaseDTO(billing.getLocation().getCode(),billing.getLocation().getDescription()));
        billingResponseDTO.setCustomerName(billing.getCustomer().getFirstName() + " " +billing.getCustomer().getLastName());
        billingResponseDTO.setCustomerMobile(billing.getCustomer().getTelNo());
        billingResponseDTO.setPaymentType(billing.getPaymentType().name());
        billingResponseDTO.setPaymentTypeDescription(PaymentType.valueOf(billing.getPaymentType().name()).getDescription());
        billingResponseDTO.setSalesType(billing.getSalesType().name());
        billingResponseDTO.setSalesTypeDescription(SalesType.valueOf(billing.getSalesType().name()).getDescription());
        billingResponseDTO.setTotalAmount(billing.getTotalAmount());
        billingResponseDTO.setPayAmount(billing.getPayAmount());
        billingResponseDTO.setRemark(billing.getRemark());
        billingResponseDTO.setCreateDate(billing.getCreatedDate());
        return billingResponseDTO;
    }

    public static BillingItemResponseDTO toBillingItemResponse(BillingDetail billingDetail) {
        BillingItemResponseDTO billingItemResponseDTO = new BillingItemResponseDTO();

        billingItemResponseDTO.setId(billingDetail.getId());
        billingItemResponseDTO.setQty(billingDetail.getQty());
        billingItemResponseDTO.setSalesPrice(billingDetail.getSalesPrice());
        billingItemResponseDTO.setSalesDiscount(billingDetail.getSalesDiscount());
        billingItemResponseDTO.setItemCost(billingDetail.getItemCost());
        billingItemResponseDTO.setLablePrice(billingDetail.getLablePrice());
        billingItemResponseDTO.setRetailPrice(billingDetail.getRetailPrice());
        billingItemResponseDTO.setWholesalePrice(billingDetail.getWholesalePrice());
        billingItemResponseDTO.setRetailDiscount(billingDetail.getRetailDiscount());
        billingItemResponseDTO.setWholesaleDiscount(billingDetail.getWholesaleDiscount());
        billingItemResponseDTO.setItemName(billingDetail.getStock().getItem().getDescription());

        return billingItemResponseDTO;
    }


    public static StockResponseDTO toStock(Stock stock) {

        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setId(stock.getItem().getId());
        itemResponseDTO.setCode(stock.getItem().getCode());
        itemResponseDTO.setDescription(stock.getItem().getDescription());
        itemResponseDTO.setStatus(stock.getItem().getStatus().name());
        itemResponseDTO.setUnit(stock.getItem().getUnit().name());
        itemResponseDTO.setUnitDescription(Unit.valueOf(stock.getItem().getUnit().name()).getDescription());

        SimpleBaseDTO category = new SimpleBaseDTO();
        category.setCode(stock.getItem().getCategory().getCode());
        category.setDescription(stock.getItem().getCategory().getDescription());

        SimpleBaseDTO brand = new SimpleBaseDTO();
        brand.setCode(stock.getItem().getBrand().getCode());
        brand.setDescription(stock.getItem().getBrand().getDescription());

        itemResponseDTO.setCategory(category);
        itemResponseDTO.setBrand(brand);

        StockResponseDTO stockResponseDTO = new StockResponseDTO();
        stockResponseDTO.setId(stock.getId());
        stockResponseDTO.setLablePrice(stock.getLablePrice());
        stockResponseDTO.setItemCost(stock.getItemCost());
        stockResponseDTO.setRetailPrice(stock.getRetailPrice());
        stockResponseDTO.setWholesalePrice(stock.getWholesalePrice());
        stockResponseDTO.setRetailDiscount(stock.getRetailDiscount());
        stockResponseDTO.setWholesaleDiscount(stock.getWholesaleDiscount());
        stockResponseDTO.setQty(stock.getQty());
        stockResponseDTO.setItem(itemResponseDTO);
        stockResponseDTO.setStatus(stock.getStatus().name());
        stockResponseDTO.setStatusDescription(Status.valueOf(stock.getStatus().name()).getDescription());

        return stockResponseDTO;
    }

} 