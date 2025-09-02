package io.akhilennu.chatbot.service.mapper;

import static io.akhilennu.chatbot.domain.IntentAsserts.*;
import static io.akhilennu.chatbot.domain.IntentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntentMapperTest {

    private IntentMapper intentMapper;

    @BeforeEach
    void setUp() {
        intentMapper = new IntentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntentSample1();
        var actual = intentMapper.toEntity(intentMapper.toDto(expected));
        assertIntentAllPropertiesEquals(expected, actual);
    }
}
