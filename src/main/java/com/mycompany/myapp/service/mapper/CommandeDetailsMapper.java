package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.service.dto.CommandeDetailsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CommandeDetails} and its DTO {@link CommandeDetailsDTO}.
 */
@Mapper(componentModel = "spring", uses = { CommandeMapper.class, PlatMapper.class })
public interface CommandeDetailsMapper extends EntityMapper<CommandeDetailsDTO, CommandeDetails> {
    @Mapping(target = "commande", source = "commande", qualifiedByName = "id")
    @Mapping(target = "plat", source = "plat", qualifiedByName = "id")
    CommandeDetailsDTO toDto(CommandeDetails s);
}
