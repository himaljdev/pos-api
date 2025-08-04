package com.returns.service.mapper;

import com.returns.service.dto.SimpleBaseDTO;
import com.returns.service.dto.request.CustomerReturnRequestDTO;
import com.returns.service.dto.request.CustomerReturnRequestItemDTO;
import com.returns.service.dto.response.*;
import com.returns.service.enums.*;
import com.returns.service.model.*;

import java.math.BigDecimal;
import java.util.List;


public class ReturnMapper {

    public static BillingResponseDTO toCustomerReturn(Billing billing) {
        BillingResponseDTO responseDTO = new BillingResponseDTO();

        CashierUserResponseDTO cashierUserResponseDTO = new CashierUserResponseDTO();
        cashierUserResponseDTO.setUsername(billing.getCashierUser().getUsername());

        responseDTO.setId(billing.getId());
        responseDTO.setInvoiceNumber(billing.getInvoiceNumber());
        responseDTO.setTotalAmount(billing.getTotalAmount());
        responseDTO.setCashierUser(cashierUserResponseDTO);
        responseDTO.setPaymentType(billing.getPaymentType().name());
        responseDTO.setPaymentTypeDescription(PaymentType.valueOf(billing.getPaymentType().name()).getDescription());

        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setId(billing.getCustomer().getId());
        customerResponseDTO.setName(billing.getCustomer().getFirstName() + " " + billing.getCustomer().getLastName());

        responseDTO.setCustomer(customerResponseDTO);

        SimpleBaseDTO simpleBaseDTO = new SimpleBaseDTO();
        simpleBaseDTO.setCode(billing.getLocation().getCode());
        simpleBaseDTO.setDescription(billing.getLocation().getDescription());

        responseDTO.setLocation(simpleBaseDTO);
        responseDTO.setSalesType(billing.getSalesType().name());
        responseDTO.setSalesTypeDescription(SalesType.valueOf(billing.getSalesType().name()).getDescription());

        responseDTO.setTotalAmount(billing.getTotalAmount());
        responseDTO.setPayAmount(billing.getPayAmount());
        responseDTO.setRemark(billing.getRemark());
        responseDTO.setCreatedDate(billing.getCreatedDate());
        return responseDTO;
    }

    public static BillingDetailResponseDTO toCustomerReturnDetails(BillingDetail billingDetail) {

        BillingDetailResponseDTO billingDetailResponseDTO = new BillingDetailResponseDTO();
        billingDetailResponseDTO.setId(billingDetail.getId());
        billingDetailResponseDTO.setQty(billingDetail.getQty());
        billingDetailResponseDTO.setReturnsQty(billingDetail.getReturnsQty());
        billingDetailResponseDTO.setSalesPrice(billingDetail.getSalesPrice());
        billingDetailResponseDTO.setSalesDiscount(billingDetail.getSalesDiscount());
        billingDetailResponseDTO.setItemCost(billingDetail.getItemCost());
        billingDetailResponseDTO.setLablePrice(billingDetail.getLablePrice());
        billingDetailResponseDTO.setRetailPrice(billingDetail.getRetailPrice());
        billingDetailResponseDTO.setWholesalePrice(billingDetail.getWholesalePrice());
        billingDetailResponseDTO.setRetailDiscount(billingDetail.getRetailDiscount());
        billingDetailResponseDTO.setWholesaleDiscount(billingDetail.getWholesaleDiscount());

        StockResponseDTO stockResponseDTO = new StockResponseDTO();
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setCode(billingDetail.getStock().getItem().getCode());
        itemResponseDTO.setDescription(billingDetail.getStock().getItem().getDescription());

        SimpleBaseDTO cate = new SimpleBaseDTO();
        cate.setCode(billingDetail.getStock().getItem().getCategory().getCode());
        cate.setDescription(billingDetail.getStock().getItem().getCategory().getDescription());

        SimpleBaseDTO brand = new SimpleBaseDTO();
        brand.setCode(billingDetail.getStock().getItem().getBrand().getCode());
        brand.setDescription(billingDetail.getStock().getItem().getBrand().getDescription());

        itemResponseDTO.setCategory(cate);
        itemResponseDTO.setBrand(brand);
        stockResponseDTO.setItem(itemResponseDTO);
        billingDetailResponseDTO.setStock(stockResponseDTO);

        return billingDetailResponseDTO;
    }

    public static BillingDetailResponseDTO toCustomerReturnDetails(BillingDetail billingDetail, List<ReturnDetails> returnDetailsList) {
        BillingDetailResponseDTO billingDetailResponseDTO = toCustomerReturnDetails(billingDetail);
        // Sum qty for this BillingDetail (by matching stock or other unique key)
//        java.math.BigDecimal totalReturnQty = java.math.BigDecimal.ZERO;
//        if (returnDetailsList != null) {
//            for (ReturnDetails rd : returnDetailsList) {
//                // Assuming ReturnDetails has a Stock or BillingDetail reference, match by stock
//                if (rd.getReturns().getBilling().getId().equals(billingDetail.getBilling().getId()) &&
//                    rd.getReturns().getBilling().getInvoiceNumber().equals(billingDetail.getBilling().getInvoiceNumber()) &&
//                    rd.getReturns().getBilling().getId() != null) {
//                    totalReturnQty = totalReturnQty.add(rd.getQty());
//                }
//            }
//        }
//        billingDetailResponseDTO.setQty(totalReturnQty);
        return billingDetailResponseDTO;
    }


    public static Returns returns(CustomerReturnRequestDTO customerReturnRequestDTO,
                                             Billing billing,Location location,String invoiceNumber,CashierUser cashierUser) {
        Returns returns = new Returns();
        returns.setBilling(billing);
        returns.setLocation(location);
        returns.setRemark(customerReturnRequestDTO.getRemark());
        returns.setDebitAmount(customerReturnRequestDTO.getDebitAmount());
        returns.setReturnsInvoice(invoiceNumber);
        returns.setCashierUser(cashierUser);
        return returns;
    }


    public static ReturnDetails returnsDetails(Returns returns,CustomerReturnRequestItemDTO requestItemDTO) {
        ReturnDetails returnsDetails = new ReturnDetails();
        returnsDetails.setReturns(returns);
        returnsDetails.setQty(requestItemDTO.getQty());
        return returnsDetails;
    }

    public static InvoiceResponseDTO toReturnInvoice(Returns returns,Billing billing) {
        InvoiceResponseDTO invoiceResponseDTO = new InvoiceResponseDTO();
        invoiceResponseDTO.setInvoiceNumber(returns.getReturnsInvoice());
        invoiceResponseDTO.setCounter(returns.getCashierUser().getFirstName());
        invoiceResponseDTO.setCustomerName(billing.getCustomer().getFirstName()+" "+billing.getCustomer().getLastName());
        invoiceResponseDTO.setOutletName(returns.getLocation().getCity());
        invoiceResponseDTO.setInvoiceDate(returns.getCreatedDate());
        return invoiceResponseDTO;
    }

    public static ReturnsResponseDTO toReturnsResponse(Returns returns) {
        ReturnsResponseDTO returnsResponseDTO = new ReturnsResponseDTO();
        returnsResponseDTO.setId(returns.getId());
        returnsResponseDTO.setRemark(returns.getRemark());
        returnsResponseDTO.setInvoiceNumber(returns.getReturnsInvoice());
        returnsResponseDTO.setTotalAmount(returns.getDebitAmount());
        returnsResponseDTO.setLocation(new SimpleBaseDTO(null,null));
        returnsResponseDTO.setCustomerName(returns.getBilling().getCustomer().getFirstName()+" "+returns.getBilling().getCustomer().getLastName());
        returnsResponseDTO.setCustomerMobile(returns.getBilling().getCustomer().getTelNo());
        returnsResponseDTO.setCreateDate(returns.getCreatedDate());
        return returnsResponseDTO;
    }

    public static ReturnsItemResponseDTO toReturnsItemResponse(ReturnDetails returnDetails) {
        ReturnsItemResponseDTO returnsItemResponseDTO = new ReturnsItemResponseDTO();
        returnsItemResponseDTO.setId(returnDetails.getId());
        returnsItemResponseDTO.setQty(returnDetails.getQty());

        return returnsItemResponseDTO;
    }

} 