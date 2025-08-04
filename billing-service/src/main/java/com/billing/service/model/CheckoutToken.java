package com.billing.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "checkout_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutToken extends AdminAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,updatable = false,unique = true)
    private Long id;

    @Column(name = "token",nullable = false,updatable = false,unique = true)
    private String token;

    public CheckoutToken(String token) {
        this.token = token;
    }
}
