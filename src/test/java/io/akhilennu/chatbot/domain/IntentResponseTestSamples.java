package io.akhilennu.chatbot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IntentResponseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static IntentResponse getIntentResponseSample1() {
        return new IntentResponse().id(1L).message("message1");
    }

    public static IntentResponse getIntentResponseSample2() {
        return new IntentResponse().id(2L).message("message2");
    }

    public static IntentResponse getIntentResponseRandomSampleGenerator() {
        return new IntentResponse().id(longCount.incrementAndGet()).message(UUID.randomUUID().toString());
    }
}
