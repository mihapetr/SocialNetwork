package com.mihapetr.socialnetwork.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Message getMessageSample1() {
        return new Message().id(1L).senderName("senderName1").content("content1");
    }

    public static Message getMessageSample2() {
        return new Message().id(2L).senderName("senderName2").content("content2");
    }

    public static Message getMessageRandomSampleGenerator() {
        return new Message().id(longCount.incrementAndGet()).senderName(UUID.randomUUID().toString()).content(UUID.randomUUID().toString());
    }
}
