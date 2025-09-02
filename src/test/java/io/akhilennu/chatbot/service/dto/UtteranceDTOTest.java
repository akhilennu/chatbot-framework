package io.akhilennu.chatbot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtteranceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtteranceDTO.class);
        UtteranceDTO utteranceDTO1 = new UtteranceDTO();
        utteranceDTO1.setId(1L);
        UtteranceDTO utteranceDTO2 = new UtteranceDTO();
        assertThat(utteranceDTO1).isNotEqualTo(utteranceDTO2);
        utteranceDTO2.setId(utteranceDTO1.getId());
        assertThat(utteranceDTO1).isEqualTo(utteranceDTO2);
        utteranceDTO2.setId(2L);
        assertThat(utteranceDTO1).isNotEqualTo(utteranceDTO2);
        utteranceDTO1.setId(null);
        assertThat(utteranceDTO1).isNotEqualTo(utteranceDTO2);
    }
}
