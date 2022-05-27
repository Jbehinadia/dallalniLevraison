package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.ResponsableRestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ResponsableRestaurant} and its DTO {@link ResponsableRestaurantDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ResponsableRestaurantMapper extends EntityMapper<ResponsableRestaurantDTO, ResponsableRestaurant> {
    @Named("nomResponsable")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomResponsable", source = "nomResponsable")
    ResponsableRestaurantDTO toDtoNomResponsable(ResponsableRestaurant responsableRestaurant);
}
