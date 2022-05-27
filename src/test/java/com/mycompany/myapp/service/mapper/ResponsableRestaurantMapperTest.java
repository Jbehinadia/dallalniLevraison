package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponsableRestaurantMapperTest {

    private ResponsableRestaurantMapper responsableRestaurantMapper;

    @BeforeEach
    public void setUp() {
        responsableRestaurantMapper = new ResponsableRestaurantMapperImpl();
    }
}
