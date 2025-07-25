package com.returns.service.model;

import com.returns.service.enums.Status;
import com.returns.service.enums.Title;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customer")
@Data
public class Customer extends AdminAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,updatable = false,unique = true)
    private Long id;

    @Column(name = "title",nullable = false)
    @Enumerated(EnumType.STRING)
    private Title title;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "city",nullable = false)
    private String city;

    @Column(name = "tel_no")
    private String telNo;

    @Column(name = "email")
    private String email;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

}
