package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResponsableRestaurantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResponsableRestaurantDTO.class);
        ResponsableRestaurantDTO responsableRestaurantDTO1 = new ResponsableRestaurantDTO();
        responsableRestaurantDTO1.setId(1L);
        ResponsableRestaurantDTO responsableRestaurantDTO2 = new ResponsableRestaurantDTO();
        assertThat(responsableRestaurantDTO1).isNotEqualTo(responsableRestaurantDTO2);
        responsableRestaurantDTO2.setId(responsableRestaurantDTO1.getId());
        assertThat(responsableRestaurantDTO1).isEqualTo(responsableRestaurantDTO2);
        responsableRestaurantDTO2.setId(2L);
        assertThat(responsableRestaurantDTO1).isNotEqualTo(responsableRestaurantDTO2);
        responsableRestaurantDTO1.setId(null);
        assertThat(responsableRestaurantDTO1).isNotEqualTo(responsableRestaurantDTO2);
    }
}
