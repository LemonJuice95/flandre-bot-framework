package io.lemonjuice.flandre_bot_framework.event;

public interface ICancelableEvent {
    default void setCancelled(boolean cancelled) {
        ((Event) this).cancelled = cancelled;
    }

    default boolean isCancelled() {
        return ((Event) this).cancelled;
    }
}
