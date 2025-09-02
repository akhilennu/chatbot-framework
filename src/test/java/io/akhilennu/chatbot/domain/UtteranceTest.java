package io.akhilennu.chatbot.domain;

import static io.akhilennu.chatbot.domain.IntentTestSamples.*;
import static io.akhilennu.chatbot.domain.UtteranceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtteranceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Utterance.class);
        Utterance utterance1 = getUtteranceSample1();
        Utterance utterance2 = new Utterance();
        assertThat(utterance1).isNotEqualTo(utterance2);

        utterance2.setId(utterance1.getId());
        assertThat(utterance1).isEqualTo(utterance2);

        utterance2 = getUtteranceSample2();
        assertThat(utterance1).isNotEqualTo(utterance2);
    }

    @Test
    void intentTest() {
        Utterance utterance = getUtteranceRandomSampleGenerator();
        Intent intentBack = getIntentRandomSampleGenerator();

        utterance.setIntent(intentBack);
        assertThat(utterance.getIntent()).isEqualTo(intentBack);

        utterance.intent(null);
        assertThat(utterance.getIntent()).isNull();
    }
}
