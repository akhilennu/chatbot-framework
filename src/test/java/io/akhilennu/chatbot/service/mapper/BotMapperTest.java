package io.akhilennu.chatbot.service.mapper;

import static io.akhilennu.chatbot.domain.BotAsserts.*;
import static io.akhilennu.chatbot.domain.BotTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BotMapperTest {

    private BotMapper botMapper;

    @BeforeEach
    void setUp() {
        botMapper = new BotMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBotSample1();
        var actual = botMapper.toEntity(botMapper.toDto(expected));
        assertBotAllPropertiesEquals(expected, actual);
    }
}
