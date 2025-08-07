package com.mihapetr.socialnetwork.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ChatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Chat getChatSample1() {
        return new Chat().id(1L).initiatorName("initiatorName1");
    }

    public static Chat getChatSample2() {
        return new Chat().id(2L).initiatorName("initiatorName2");
    }

    public static Chat getChatRandomSampleGenerator() {
        return new Chat().id(longCount.incrementAndGet()).initiatorName(UUID.randomUUID().toString());
    }
}
