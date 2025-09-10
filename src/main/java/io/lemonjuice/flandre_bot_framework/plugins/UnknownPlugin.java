package io.lemonjuice.flandre_bot_framework.plugins;

import java.util.List;

class UnknownPlugin implements BotPlugin {
    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public List<Class<? extends BotPlugin>> getDependencies() {
        return List.of();
    }

    @Override
    public void load() {
    }
}
