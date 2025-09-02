package io.akhilennu.chatbot.domain;

import static io.akhilennu.chatbot.domain.IntentResponseTestSamples.*;
import static io.akhilennu.chatbot.domain.IntentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntentResponseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntentResponse.class);
        IntentResponse intentResponse1 = getIntentResponseSample1();
        IntentResponse intentResponse2 = new IntentResponse();
        assertThat(intentResponse1).isNotEqualTo(intentResponse2);

        intentResponse2.setId(intentResponse1.getId());
        assertThat(intentResponse1).isEqualTo(intentResponse2);

        intentResponse2 = getIntentResponseSample2();
        assertThat(intentResponse1).isNotEqualTo(intentResponse2);
    }

    @Test
    void intentTest() {
        IntentResponse intentResponse = getIntentResponseRandomSampleGenerator();
        Intent intentBack = getIntentRandomSampleGenerator();

        intentResponse.setIntent(intentBack);
        assertThat(intentResponse.getIntent()).isEqualTo(intentBack);
        assertThat(intentBack.getResponse()).isEqualTo(intentResponse);

        intentResponse.intent(null);
        assertThat(intentResponse.getIntent()).isNull();
        assertThat(intentBack.getResponse()).isNull();
    }
}
