package io.akhilennu.chatbot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UtteranceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Utterance getUtteranceSample1() {
        return new Utterance().id(1L).text("text1").language("language1");
    }

    public static Utterance getUtteranceSample2() {
        return new Utterance().id(2L).text("text2").language("language2");
    }

    public static Utterance getUtteranceRandomSampleGenerator() {
        return new Utterance().id(longCount.incrementAndGet()).text(UUID.randomUUID().toString()).language(UUID.randomUUID().toString());
    }
}
