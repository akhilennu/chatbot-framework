package io.akhilennu.chatbot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IntentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Intent getIntentSample1() {
        return new Intent().id(1L).name("name1").description("description1");
    }

    public static Intent getIntentSample2() {
        return new Intent().id(2L).name("name2").description("description2");
    }

    public static Intent getIntentRandomSampleGenerator() {
        return new Intent().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
