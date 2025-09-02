package io.akhilennu.chatbot.service.mapper;

import static io.akhilennu.chatbot.domain.UtteranceAsserts.*;
import static io.akhilennu.chatbot.domain.UtteranceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UtteranceMapperTest {

    private UtteranceMapper utteranceMapper;

    @BeforeEach
    void setUp() {
        utteranceMapper = new UtteranceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUtteranceSample1();
        var actual = utteranceMapper.toEntity(utteranceMapper.toDto(expected));
        assertUtteranceAllPropertiesEquals(expected, actual);
    }
}
