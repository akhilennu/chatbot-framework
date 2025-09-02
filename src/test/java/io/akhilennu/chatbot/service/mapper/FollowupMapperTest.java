package io.akhilennu.chatbot.service.mapper;

import static io.akhilennu.chatbot.domain.FollowupAsserts.*;
import static io.akhilennu.chatbot.domain.FollowupTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FollowupMapperTest {

    private FollowupMapper followupMapper;

    @BeforeEach
    void setUp() {
        followupMapper = new FollowupMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFollowupSample1();
        var actual = followupMapper.toEntity(followupMapper.toDto(expected));
        assertFollowupAllPropertiesEquals(expected, actual);
    }
}
