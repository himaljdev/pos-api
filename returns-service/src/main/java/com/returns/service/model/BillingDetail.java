package com.returns.service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing_detail")
@Data
public class BillingDetail extends AdminAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,updatable = false,unique = true)
    private Long id;

    @Column(name = "qty",nullable = false)
    private BigDecimal qty;

    @Column(name = "sales_price")
    private BigDecimal salesPrice;

    @Column(name = "sales_disscount")
    private Double salesDiscount;

    @Column(name = "item_cost",nullable = false)
    private BigDecimal itemCost;

    @Column(name = "lable_price",nullable = false)
    private BigDecimal lablePrice;

    @Column(name = "retail_price",nullable = false)
    private BigDecimal retailPrice;

    @Column(name = "wholesale_price",nullable = false)
    private BigDecimal wholesalePrice;

    @Column(name = "retail_discount")
    private Integer retailDiscount;

    @Column(name = "wholesale_discount")
    private Integer wholesaleDiscount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id",referencedColumnName = "id")
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", referencedColumnName = "id")
    private Billing billing;
}
