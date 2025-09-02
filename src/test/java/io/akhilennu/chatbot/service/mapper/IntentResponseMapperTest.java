package io.akhilennu.chatbot.service.mapper;

import static io.akhilennu.chatbot.domain.IntentResponseAsserts.*;
import static io.akhilennu.chatbot.domain.IntentResponseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntentResponseMapperTest {

    private IntentResponseMapper intentResponseMapper;

    @BeforeEach
    void setUp() {
        intentResponseMapper = new IntentResponseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntentResponseSample1();
        var actual = intentResponseMapper.toEntity(intentResponseMapper.toDto(expected));
        assertIntentResponseAllPropertiesEquals(expected, actual);
    }
}
