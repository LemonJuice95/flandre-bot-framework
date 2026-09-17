package io.lemonjuice.flandre_bot_framework.event;

import java.util.concurrent.atomic.AtomicBoolean;

public class Event {
    final AtomicBoolean cancelled = new AtomicBoolean(false);
}