package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Restaurant} and its DTO {@link RestaurantDTO}.
 */
@Mapper(componentModel = "spring", uses = { ResponsableRestaurantMapper.class })
public interface RestaurantMapper extends EntityMapper<RestaurantDTO, Restaurant> {
    @Mapping(target = "responsableRestaurant", source = "responsableRestaurant", qualifiedByName = "nomResponsable")
    RestaurantDTO toDto(Restaurant s);

    @Named("nomRestaurant")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomRestaurant", source = "nomRestaurant")
    RestaurantDTO toDtoNomRestaurant(Restaurant restaurant);
}
