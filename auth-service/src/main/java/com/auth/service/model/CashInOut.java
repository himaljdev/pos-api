package com.auth.service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cash_in_out")
@Data
public class CashInOut extends AdminAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,updatable = false,unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_user_id",referencedColumnName = "id")
    private CashierUser cashierUser;

    @Column(name = "cash_in_out", nullable = false)
    @Enumerated(EnumType.STRING)
    private com.auth.service.enums.CashInOut cashInOut;

    @Column(name = "remark")
    private String remark;

    @Column(name = "amount",nullable = false)
    private Double amount;

}
