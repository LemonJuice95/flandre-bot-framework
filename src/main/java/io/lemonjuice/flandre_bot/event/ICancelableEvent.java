package io.lemonjuice.flandre_bot.event;

public interface ICancelableEvent {
    default void setCancelled(boolean cancelled) {
        ((Event) this).cancelled = cancelled;
    }

    default boolean isCancelled() {
        return ((Event) this).cancelled;
    }
}
