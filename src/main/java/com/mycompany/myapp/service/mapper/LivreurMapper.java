package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.LivreurDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Livreur} and its DTO {@link LivreurDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LivreurMapper extends EntityMapper<LivreurDTO, Livreur> {
    @Named("nomLivreur")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomLivreur", source = "nomLivreur")
    LivreurDTO toDtoNomLivreur(Livreur livreur);
}
