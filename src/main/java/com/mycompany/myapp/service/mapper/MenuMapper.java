package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.MenuDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Menu} and its DTO {@link MenuDTO}.
 */
@Mapper(componentModel = "spring", uses = { RestaurantMapper.class })
public interface MenuMapper extends EntityMapper<MenuDTO, Menu> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "nomRestaurant")
    MenuDTO toDto(Menu s);

    @Named("nomMenu")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomMenu", source = "nomMenu")
    MenuDTO toDtoNomMenu(Menu menu);
}
