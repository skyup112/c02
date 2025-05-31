package com.example.b03.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTypeDTO {
    private Byte typeId;
    private String typeName;
}
