package io.akhilennu.chatbot.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FollowupDTO.class);
        FollowupDTO followupDTO1 = new FollowupDTO();
        followupDTO1.setId(1L);
        FollowupDTO followupDTO2 = new FollowupDTO();
        assertThat(followupDTO1).isNotEqualTo(followupDTO2);
        followupDTO2.setId(followupDTO1.getId());
        assertThat(followupDTO1).isEqualTo(followupDTO2);
        followupDTO2.setId(2L);
        assertThat(followupDTO1).isNotEqualTo(followupDTO2);
        followupDTO1.setId(null);
        assertThat(followupDTO1).isNotEqualTo(followupDTO2);
    }
}
