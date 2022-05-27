package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.TypePlatDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TypePlat} and its DTO {@link TypePlatDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TypePlatMapper extends EntityMapper<TypePlatDTO, TypePlat> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TypePlatDTO toDtoId(TypePlat typePlat);
}
