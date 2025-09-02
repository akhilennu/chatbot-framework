package io.akhilennu.chatbot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntentDTO.class);
        IntentDTO intentDTO1 = new IntentDTO();
        intentDTO1.setId(1L);
        IntentDTO intentDTO2 = new IntentDTO();
        assertThat(intentDTO1).isNotEqualTo(intentDTO2);
        intentDTO2.setId(intentDTO1.getId());
        assertThat(intentDTO1).isEqualTo(intentDTO2);
        intentDTO2.setId(2L);
        assertThat(intentDTO1).isNotEqualTo(intentDTO2);
        intentDTO1.setId(null);
        assertThat(intentDTO1).isNotEqualTo(intentDTO2);
    }
}
