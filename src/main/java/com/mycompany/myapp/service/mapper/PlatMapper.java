package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.PlatDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Plat} and its DTO {@link PlatDTO}.
 */
@Mapper(componentModel = "spring", uses = { MenuMapper.class, TypePlatMapper.class })
public interface PlatMapper extends EntityMapper<PlatDTO, Plat> {
    @Mapping(target = "menu", source = "menu", qualifiedByName = "nomMenu")
    @Mapping(target = "typePlat", source = "typePlat", qualifiedByName = "type")
    PlatDTO toDto(Plat s);

    @Named("nomPlat")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomPlat", source = "nomPlat")
    PlatDTO toDtoNomPlat(Plat plat);
}
