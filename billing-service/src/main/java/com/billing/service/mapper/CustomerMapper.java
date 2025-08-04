package com.billing.service.mapper;

import com.billing.service.dto.response.CustomerResponseDTO;
import com.billing.service.enums.Status;
import com.billing.service.enums.Title;
import com.billing.service.model.Customer;

public class CustomerMapper {

    public static CustomerResponseDTO toCustomerResponse(Customer customer) {
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setId(customer.getId());
        customerResponseDTO.setTitle(customer.getTitle().name());
        customerResponseDTO.setTitleDescription(Title.valueOf(customer.getTitle().name()).getDescription());
        customerResponseDTO.setName(customer.getFirstName() + " " + customer.getLastName());
        customerResponseDTO.setStatus(customer.getStatus().name());
        customerResponseDTO.setStatusDescription(Status.valueOf(customer.getStatus().name()).getDescription());
        customerResponseDTO.setCity(customer.getCity());
        customerResponseDTO.setTelNo(customer.getTelNo());
        customerResponseDTO.setEmail(customer.getEmail());
    //    customerResponseDTO.setFundLimit(customer.getFundLimit());
      //  customerResponseDTO.setPendingBalance(customer.getCustomerBalance().getPendingBalance());
        //customerResponseDTO.setT(customer.getCustomerBalance().getPendingBalance());
        return customerResponseDTO;
    }
}
