package io.akhilennu.chatbot.service.mapper;

import static io.akhilennu.chatbot.domain.IntentEntityAsserts.*;
import static io.akhilennu.chatbot.domain.IntentEntityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntentEntityMapperTest {

    private IntentEntityMapper intentEntityMapper;

    @BeforeEach
    void setUp() {
        intentEntityMapper = new IntentEntityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntentEntitySample1();
        var actual = intentEntityMapper.toEntity(intentEntityMapper.toDto(expected));
        assertIntentEntityAllPropertiesEquals(expected, actual);
    }
}
