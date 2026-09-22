package io.lemonjuice.flandre_bot_framework.event.meta;

import io.lemonjuice.flandre_bot_framework.config.BotConfig;
import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.ICancelableEvent;
import lombok.Getter;

/**
 * 在配置被重新加载时推送，
 * <b>注意：订阅者方法中不应该存在对config.relaod()的调用，否则将导致死锁或无限递归</b>
 */
@Getter
public class ConfigReloadEvent extends Event {
    private final BotConfig config;

    public ConfigReloadEvent(BotConfig config) {
        this.config = config;
    }

    public static class Pre extends ConfigReloadEvent implements ICancelableEvent {
        public Pre(BotConfig config) {
            super(config);
        }
    }

    public static class Post extends ConfigReloadEvent {
        public Post(BotConfig config) {
            super(config);
        }
    }
}
