package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommandeDetailsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommandeDetailsDTO.class);
        CommandeDetailsDTO commandeDetailsDTO1 = new CommandeDetailsDTO();
        commandeDetailsDTO1.setId(1L);
        CommandeDetailsDTO commandeDetailsDTO2 = new CommandeDetailsDTO();
        assertThat(commandeDetailsDTO1).isNotEqualTo(commandeDetailsDTO2);
        commandeDetailsDTO2.setId(commandeDetailsDTO1.getId());
        assertThat(commandeDetailsDTO1).isEqualTo(commandeDetailsDTO2);
        commandeDetailsDTO2.setId(2L);
        assertThat(commandeDetailsDTO1).isNotEqualTo(commandeDetailsDTO2);
        commandeDetailsDTO1.setId(null);
        assertThat(commandeDetailsDTO1).isNotEqualTo(commandeDetailsDTO2);
    }
}
