package com.eliseubrito.partstock.mapper;

import com.eliseubrito.partstock.dto.PartDTO;
import com.eliseubrito.partstock.entity.Part;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    Part toModel(PartDTO partDTO);

    PartDTO toDTO(Part part);
}
