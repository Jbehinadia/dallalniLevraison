package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.CommandeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Commande} and its DTO {@link CommandeDTO}.
 */
@Mapper(componentModel = "spring", uses = { LivreurMapper.class, ClientMapper.class })
public interface CommandeMapper extends EntityMapper<CommandeDTO, Commande> {
    @Mapping(target = "livreur", source = "livreur", qualifiedByName = "nomLivreur")
    @Mapping(target = "client", source = "client", qualifiedByName = "id")
    CommandeDTO toDto(Commande s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommandeDTO toDtoId(Commande commande);
}
