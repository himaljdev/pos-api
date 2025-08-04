package com.returns.service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "returns_invoice_sequence")
@Data
public class ReturnsInvoiceSequence {
    @Id
    private Integer id;
    private Long nextVal;
}