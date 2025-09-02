package io.akhilennu.chatbot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BotTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Bot getBotSample1() {
        return new Bot().id(1L).name("name1").description("description1");
    }

    public static Bot getBotSample2() {
        return new Bot().id(2L).name("name2").description("description2");
    }

    public static Bot getBotRandomSampleGenerator() {
        return new Bot().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
