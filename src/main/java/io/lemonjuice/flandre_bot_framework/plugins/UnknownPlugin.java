package io.lemonjuice.flandre_bot_framework.plugins;

class UnknownPlugin implements BotPlugin {
    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public void load() {
    }
}
