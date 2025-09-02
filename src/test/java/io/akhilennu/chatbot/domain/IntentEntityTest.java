package io.akhilennu.chatbot.domain;

import static io.akhilennu.chatbot.domain.IntentEntityTestSamples.*;
import static io.akhilennu.chatbot.domain.IntentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class IntentEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntentEntity.class);
        IntentEntity intentEntity1 = getIntentEntitySample1();
        IntentEntity intentEntity2 = new IntentEntity();
        assertThat(intentEntity1).isNotEqualTo(intentEntity2);

        intentEntity2.setId(intentEntity1.getId());
        assertThat(intentEntity1).isEqualTo(intentEntity2);

        intentEntity2 = getIntentEntitySample2();
        assertThat(intentEntity1).isNotEqualTo(intentEntity2);
    }

    @Test
    void intentsTest() {
        IntentEntity intentEntity = getIntentEntityRandomSampleGenerator();
        Intent intentBack = getIntentRandomSampleGenerator();

        intentEntity.addIntents(intentBack);
        assertThat(intentEntity.getIntents()).containsOnly(intentBack);
        assertThat(intentBack.getEntities()).containsOnly(intentEntity);

        intentEntity.removeIntents(intentBack);
        assertThat(intentEntity.getIntents()).doesNotContain(intentBack);
        assertThat(intentBack.getEntities()).doesNotContain(intentEntity);

        intentEntity.intents(new HashSet<>(Set.of(intentBack)));
        assertThat(intentEntity.getIntents()).containsOnly(intentBack);
        assertThat(intentBack.getEntities()).containsOnly(intentEntity);

        intentEntity.setIntents(new HashSet<>());
        assertThat(intentEntity.getIntents()).doesNotContain(intentBack);
        assertThat(intentBack.getEntities()).doesNotContain(intentEntity);
    }
}
