package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.ResponsableRestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ResponsableRestaurant} and its DTO {@link ResponsableRestaurantDTO}.
 */
@Mapper(componentModel = "spring", uses = { RestaurantMapper.class })
public interface ResponsableRestaurantMapper extends EntityMapper<ResponsableRestaurantDTO, ResponsableRestaurant> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "id")
    ResponsableRestaurantDTO toDto(ResponsableRestaurant s);
}
