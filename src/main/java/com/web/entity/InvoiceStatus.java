package com.web.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.web.enums.Status;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_status")
@Getter
@Setter
public class InvoiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private LocalDateTime createdDate;

    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonBackReference
    private Invoice invoice;

}
