package io.lemonjuice.flandre_bot_framework.plugins;

/**
 * 可用于包装导致插件加载失败的异常，传入失败原因
 * 或者在没有异常抛出，但实际上插件处于不可用状态时主动抛出
 */
public class PluginLoadingException extends RuntimeException {
    public PluginLoadingException() {
        super();
    }

    public PluginLoadingException(String message) {
        super(message);
    }
}
