package com.billing.service.mapper;

import com.billing.service.dto.request.CashInOutRequestDTO;
import com.billing.service.dto.response.CashInOutResponseDTO;
import com.billing.service.dto.response.InvoiceResponseDTO;
import com.billing.service.model.*;
import com.billing.service.dto.request.BillingRequestDTO;
import com.billing.service.dto.request.BillingItemRequestDTO;
import com.billing.service.enums.PaymentType;
import com.billing.service.enums.SalesType;

import java.math.BigDecimal;


public class BillingMapper {
    public static Billing toBilling(BillingRequestDTO dto, CashierUser cashier, Customer customer, Location location, BigDecimal totalAmount, String invoiceNumber) {
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

    public static InvoiceResponseDTO toBillingInvoice(Billing billing) {
        InvoiceResponseDTO invoiceResponseDTO = new InvoiceResponseDTO();
        invoiceResponseDTO.setInvoiceNumber(billing.getInvoiceNumber());
        invoiceResponseDTO.setCounter(billing.getCashierUser().getUsername());
        invoiceResponseDTO.setCustomerName(billing.getCustomer().getFirstName()+" "+billing.getCustomer().getLastName());
        invoiceResponseDTO.setPaymentType(billing.getPaymentType().name());
        invoiceResponseDTO.setPaymentTypeDescription(PaymentType.valueOf(billing.getPaymentType().name()).getDescription());
        invoiceResponseDTO.setSalesType(billing.getSalesType().name());
        invoiceResponseDTO.setSalesTypeDescription(SalesType.valueOf(billing.getSalesType().name()).getDescription());
        invoiceResponseDTO.setOutletName(billing.getLocation().getDescription());
        invoiceResponseDTO.setInvoiceDate(billing.getCreatedDate());
        return invoiceResponseDTO;
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

} 