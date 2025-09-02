package io.akhilennu.chatbot.domain;

import static io.akhilennu.chatbot.domain.BotTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.akhilennu.chatbot.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bot.class);
        Bot bot1 = getBotSample1();
        Bot bot2 = new Bot();
        assertThat(bot1).isNotEqualTo(bot2);

        bot2.setId(bot1.getId());
        assertThat(bot1).isEqualTo(bot2);

        bot2 = getBotSample2();
        assertThat(bot1).isNotEqualTo(bot2);
    }
}
