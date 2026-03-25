package com.medicalstore.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String supplierName;

    private String contactPerson;

    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    // --- Marg ERP Advanced Fields ---
    private String gstin;
    
    private String dlNumber; // Drug License Number

    private String bankName;

    private String accountNumber;

    private String ifscCode;

    private String state; // Important for CGST/SGST vs IGST calculation
    // ---------------------------------
}
