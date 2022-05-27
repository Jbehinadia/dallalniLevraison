package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypePlatMapperTest {

    private TypePlatMapper typePlatMapper;

    @BeforeEach
    public void setUp() {
        typePlatMapper = new TypePlatMapperImpl();
    }
}
