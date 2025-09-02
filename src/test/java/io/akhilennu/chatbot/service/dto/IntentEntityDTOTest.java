package io.akhilennu.chatbot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntentEntityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntentEntityDTO.class);
        IntentEntityDTO intentEntityDTO1 = new IntentEntityDTO();
        intentEntityDTO1.setId(1L);
        IntentEntityDTO intentEntityDTO2 = new IntentEntityDTO();
        assertThat(intentEntityDTO1).isNotEqualTo(intentEntityDTO2);
        intentEntityDTO2.setId(intentEntityDTO1.getId());
        assertThat(intentEntityDTO1).isEqualTo(intentEntityDTO2);
        intentEntityDTO2.setId(2L);
        assertThat(intentEntityDTO1).isNotEqualTo(intentEntityDTO2);
        intentEntityDTO1.setId(null);
        assertThat(intentEntityDTO1).isNotEqualTo(intentEntityDTO2);
    }
}
