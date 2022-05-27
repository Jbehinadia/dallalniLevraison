package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommandeDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommandeDetails.class);
        CommandeDetails commandeDetails1 = new CommandeDetails();
        commandeDetails1.setId(1L);
        CommandeDetails commandeDetails2 = new CommandeDetails();
        commandeDetails2.setId(commandeDetails1.getId());
        assertThat(commandeDetails1).isEqualTo(commandeDetails2);
        commandeDetails2.setId(2L);
        assertThat(commandeDetails1).isNotEqualTo(commandeDetails2);
        commandeDetails1.setId(null);
        assertThat(commandeDetails1).isNotEqualTo(commandeDetails2);
    }
}
