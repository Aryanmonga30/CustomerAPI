package com.api.customer.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name="customerTable")
@Data
@Entity
public class CustomerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name="client")
    private int client;
    @Column(name="name")
    private String name;
    @Column(name="last_modified_date")
    private Timestamp lastModifiedDate;
    @Column(name="create_date")
    private Timestamp createDate;
    @Column(name="phone_number")
    private String phoneNumber;
    @Column(name="customer_code")
    private String customerCode;
    @Column(name="email")
    private String email;
    @Column(name="enable")
    private boolean enable;

}
