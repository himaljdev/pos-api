package com.billing.service.model;

import com.billing.service.enums.PaymentType;
import com.billing.service.enums.SalesType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing")
@Data
public class Billing extends AdminAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,updatable = false,unique = true)
    private Long id;

    @Column(name = "invoice_number",nullable = false,updatable = false,unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_user_id",referencedColumnName = "id")
    private CashierUser cashierUser;

    @Column(name = "payment_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_code",referencedColumnName = "code")
    private Location location;

    @Column(name = "sale_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private SalesType salesType;

    @Column(name = "total_amount",nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "pay_amount",nullable = false)
    private BigDecimal payAmount;

    @Column(name = "remark",nullable = false)
    private String remark;

}
