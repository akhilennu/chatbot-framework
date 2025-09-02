package io.akhilennu.chatbot.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FollowupTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Followup getFollowupSample1() {
        return new Followup().id(1L).question("question1").targetEntity("targetEntity1").order(1);
    }

    public static Followup getFollowupSample2() {
        return new Followup().id(2L).question("question2").targetEntity("targetEntity2").order(2);
    }

    public static Followup getFollowupRandomSampleGenerator() {
        return new Followup()
            .id(longCount.incrementAndGet())
            .question(UUID.randomUUID().toString())
            .targetEntity(UUID.randomUUID().toString())
            .order(intCount.incrementAndGet());
    }
}
