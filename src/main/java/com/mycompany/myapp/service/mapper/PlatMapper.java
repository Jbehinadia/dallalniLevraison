package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.PlatDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Plat} and its DTO {@link PlatDTO}.
 */
@Mapper(componentModel = "spring", uses = { MenuMapper.class, TypePlatMapper.class })
public interface PlatMapper extends EntityMapper<PlatDTO, Plat> {
    @Mapping(target = "menu", source = "menu", qualifiedByName = "id")
    @Mapping(target = "typePlat", source = "typePlat", qualifiedByName = "id")
    PlatDTO toDto(Plat s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlatDTO toDtoId(Plat plat);
}
