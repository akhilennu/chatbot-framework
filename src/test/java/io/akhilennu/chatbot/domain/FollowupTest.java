package io.akhilennu.chatbot.domain;

import static io.akhilennu.chatbot.domain.FollowupTestSamples.*;
import static io.akhilennu.chatbot.domain.IntentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Followup.class);
        Followup followup1 = getFollowupSample1();
        Followup followup2 = new Followup();
        assertThat(followup1).isNotEqualTo(followup2);

        followup2.setId(followup1.getId());
        assertThat(followup1).isEqualTo(followup2);

        followup2 = getFollowupSample2();
        assertThat(followup1).isNotEqualTo(followup2);
    }

    @Test
    void intentTest() {
        Followup followup = getFollowupRandomSampleGenerator();
        Intent intentBack = getIntentRandomSampleGenerator();

        followup.setIntent(intentBack);
        assertThat(followup.getIntent()).isEqualTo(intentBack);

        followup.intent(null);
        assertThat(followup.getIntent()).isNull();
    }
}
