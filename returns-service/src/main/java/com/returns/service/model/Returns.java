package com.returns.service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "returns")
@Data
public class Returns extends AdminAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,updatable = false,unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_id",referencedColumnName = "id",nullable = false)
    private Billing billing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_code",referencedColumnName = "code")
    private Location location;

    @Column(name = "returns_invoice",nullable = false,unique = true,updatable = false)
    private String returnsInvoice;

    @Column(name = "remark")
    private String remark;

    @Column(name = "debit_Amount",nullable = false)
    private BigDecimal debitAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_user_id",referencedColumnName = "id")
    private CashierUser cashierUser;

    @OneToMany(mappedBy = "returns")
    private List<ReturnDetails> returnDetails;
}
