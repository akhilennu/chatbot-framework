package io.akhilennu.chatbot.domain;

import static io.akhilennu.chatbot.domain.BotTestSamples.*;
import static io.akhilennu.chatbot.domain.FollowupTestSamples.*;
import static io.akhilennu.chatbot.domain.IntentEntityTestSamples.*;
import static io.akhilennu.chatbot.domain.IntentResponseTestSamples.*;
import static io.akhilennu.chatbot.domain.IntentTestSamples.*;
import static io.akhilennu.chatbot.domain.UtteranceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class IntentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Intent.class);
        Intent intent1 = getIntentSample1();
        Intent intent2 = new Intent();
        assertThat(intent1).isNotEqualTo(intent2);

        intent2.setId(intent1.getId());
        assertThat(intent1).isEqualTo(intent2);

        intent2 = getIntentSample2();
        assertThat(intent1).isNotEqualTo(intent2);
    }

    @Test
    void responseTest() {
        Intent intent = getIntentRandomSampleGenerator();
        IntentResponse intentResponseBack = getIntentResponseRandomSampleGenerator();

        intent.setResponse(intentResponseBack);
        assertThat(intent.getResponse()).isEqualTo(intentResponseBack);

        intent.response(null);
        assertThat(intent.getResponse()).isNull();
    }

    @Test
    void utterancesTest() {
        Intent intent = getIntentRandomSampleGenerator();
        Utterance utteranceBack = getUtteranceRandomSampleGenerator();

        intent.addUtterances(utteranceBack);
        assertThat(intent.getUtterances()).containsOnly(utteranceBack);
        assertThat(utteranceBack.getIntent()).isEqualTo(intent);

        intent.removeUtterances(utteranceBack);
        assertThat(intent.getUtterances()).doesNotContain(utteranceBack);
        assertThat(utteranceBack.getIntent()).isNull();

        intent.utterances(new HashSet<>(Set.of(utteranceBack)));
        assertThat(intent.getUtterances()).containsOnly(utteranceBack);
        assertThat(utteranceBack.getIntent()).isEqualTo(intent);

        intent.setUtterances(new HashSet<>());
        assertThat(intent.getUtterances()).doesNotContain(utteranceBack);
        assertThat(utteranceBack.getIntent()).isNull();
    }

    @Test
    void followupsTest() {
        Intent intent = getIntentRandomSampleGenerator();
        Followup followupBack = getFollowupRandomSampleGenerator();

        intent.addFollowups(followupBack);
        assertThat(intent.getFollowups()).containsOnly(followupBack);
        assertThat(followupBack.getIntent()).isEqualTo(intent);

        intent.removeFollowups(followupBack);
        assertThat(intent.getFollowups()).doesNotContain(followupBack);
        assertThat(followupBack.getIntent()).isNull();

        intent.followups(new HashSet<>(Set.of(followupBack)));
        assertThat(intent.getFollowups()).containsOnly(followupBack);
        assertThat(followupBack.getIntent()).isEqualTo(intent);

        intent.setFollowups(new HashSet<>());
        assertThat(intent.getFollowups()).doesNotContain(followupBack);
        assertThat(followupBack.getIntent()).isNull();
    }

    @Test
    void botTest() {
        Intent intent = getIntentRandomSampleGenerator();
        Bot botBack = getBotRandomSampleGenerator();

        intent.setBot(botBack);
        assertThat(intent.getBot()).isEqualTo(botBack);

        intent.bot(null);
        assertThat(intent.getBot()).isNull();
    }

    @Test
    void entitiesTest() {
        Intent intent = getIntentRandomSampleGenerator();
        IntentEntity intentEntityBack = getIntentEntityRandomSampleGenerator();

        intent.addEntities(intentEntityBack);
        assertThat(intent.getEntities()).containsOnly(intentEntityBack);

        intent.removeEntities(intentEntityBack);
        assertThat(intent.getEntities()).doesNotContain(intentEntityBack);

        intent.entities(new HashSet<>(Set.of(intentEntityBack)));
        assertThat(intent.getEntities()).containsOnly(intentEntityBack);

        intent.setEntities(new HashSet<>());
        assertThat(intent.getEntities()).doesNotContain(intentEntityBack);
    }
}
