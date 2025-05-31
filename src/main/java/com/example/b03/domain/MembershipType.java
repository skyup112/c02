package com.example.b03.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "membership_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipType {
    @Id
    @Column(name = "type_id")
    private Byte typeId;

    @Column(name = "type_name", nullable = false, unique = true, length = 50)
    private String typeName;
}