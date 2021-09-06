package com.carlopezg.creditservice.domain;

import com.carlopezg.creditservice.enums.CreditLineRequestStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table
public class CreditLineRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String applicant;

    @Column
    private Double requestedCreditLine;

    @Column
    private Double recommendedCreditLine;

    @Column
    private Date requestedDate;

    @Column
    @Enumerated(EnumType.STRING)
    private CreditLineRequestStatus status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
}
