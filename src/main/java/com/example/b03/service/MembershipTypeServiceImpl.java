package com.example.b03.service;

import com.example.b03.dto.MembershipTypeDTO;
import com.example.b03.repository.MembershipTypeRepository;
import com.example.b03.service.MembershipTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipTypeServiceImpl implements MembershipTypeService {

    private final MembershipTypeRepository membershipTypeRepository;

    @Override
    public List<MembershipTypeDTO> getAll() {
        return membershipTypeRepository.findAll().stream()
                .map(entity -> MembershipTypeDTO.builder()
                        .typeId(entity.getTypeId())
                        .typeName(entity.getTypeName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public MembershipTypeDTO getById(Byte id) {
        return membershipTypeRepository.findById(id)
                .map(entity -> MembershipTypeDTO.builder()
                        .typeId(entity.getTypeId())
                        .typeName(entity.getTypeName())
                        .build())
                .orElse(null);
    }

}
