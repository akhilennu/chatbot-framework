package io.akhilennu.chatbot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntentResponseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntentResponseDTO.class);
        IntentResponseDTO intentResponseDTO1 = new IntentResponseDTO();
        intentResponseDTO1.setId(1L);
        IntentResponseDTO intentResponseDTO2 = new IntentResponseDTO();
        assertThat(intentResponseDTO1).isNotEqualTo(intentResponseDTO2);
        intentResponseDTO2.setId(intentResponseDTO1.getId());
        assertThat(intentResponseDTO1).isEqualTo(intentResponseDTO2);
        intentResponseDTO2.setId(2L);
        assertThat(intentResponseDTO1).isNotEqualTo(intentResponseDTO2);
        intentResponseDTO1.setId(null);
        assertThat(intentResponseDTO1).isNotEqualTo(intentResponseDTO2);
    }
}
