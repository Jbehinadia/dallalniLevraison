package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ResponsableRestaurantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ResponsableRestaurant.class);
        ResponsableRestaurant responsableRestaurant1 = new ResponsableRestaurant();
        responsableRestaurant1.setId(1L);
        ResponsableRestaurant responsableRestaurant2 = new ResponsableRestaurant();
        responsableRestaurant2.setId(responsableRestaurant1.getId());
        assertThat(responsableRestaurant1).isEqualTo(responsableRestaurant2);
        responsableRestaurant2.setId(2L);
        assertThat(responsableRestaurant1).isNotEqualTo(responsableRestaurant2);
        responsableRestaurant1.setId(null);
        assertThat(responsableRestaurant1).isNotEqualTo(responsableRestaurant2);
    }
}
