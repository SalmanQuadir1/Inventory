package com.medicalstore.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., A-01-01

    @Column(nullable = false, unique = true)
    private String barcode;

    private Double weightCapacity;
    private Double dimensions; // Optional

    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;
}
