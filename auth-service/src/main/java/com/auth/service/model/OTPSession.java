package com.auth.service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "otp_session")
@Data
public class OTPSession extends Audit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp",nullable = false)
    private String otp;

    @Column(name = "success",nullable = false)
    private boolean success;

    @Column(name = "validated",nullable = false)
    private boolean validated;

}
