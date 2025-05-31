package com.example.b03.service;

import com.example.b03.dto.MembershipTypeDTO;
import java.util.List;

public interface MembershipTypeService {
    List<MembershipTypeDTO> getAll();
    MembershipTypeDTO getById(Byte id);
}
