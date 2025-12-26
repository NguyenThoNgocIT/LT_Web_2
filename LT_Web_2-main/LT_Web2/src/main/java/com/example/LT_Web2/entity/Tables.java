package com.example.LT_Web2.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Entity
@Table(name="tables")
@Data
public class Tables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;

    private String location;

    @Enumerated(EnumType.STRING)
    private TableStatus status =TableStatus.AVAILABLE;

}
