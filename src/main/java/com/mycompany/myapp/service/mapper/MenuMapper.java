package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.MenuDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Menu} and its DTO {@link MenuDTO}.
 */
@Mapper(componentModel = "spring", uses = { RestaurantMapper.class })
public interface MenuMapper extends EntityMapper<MenuDTO, Menu> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "id")
    MenuDTO toDto(Menu s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MenuDTO toDtoId(Menu menu);
}
