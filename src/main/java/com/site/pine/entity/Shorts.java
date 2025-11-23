package com.site.pine.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Timestamp;

@Entity
public class Shorts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String content;
    @CreationTimestamp
    private Timestamp writeDate;
    @CreationTimestamp
    private Timestamp updateDate;

    // member manytwoone 필요
}
