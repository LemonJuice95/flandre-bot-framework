package io.lemonjuice.flandre_bot_framework.console;

import java.util.List;

/**
 * 控制台命令执行的基类
 * 控制台命令在一个线程内串行执行
 */
public abstract class ConsoleCommandRunner {
    protected final String[] args;

    /**
     * 构造函数中建议妥善处理所有可能的异常（包括未受检异常）
     * @param args 命令参数
     */
    public ConsoleCommandRunner(String[] args) {
        this.args = args;
    }

    /**
     * 执行命令逻辑
     */
    public abstract void apply();

    /**
     * 获取对应的命令体
     * @return 命令体列表
     */
    public abstract List<String> getCommandBodies();

    /**
     * 获取help命令结果中命令的格式
     * @return 命令格式
     */
    public abstract String getUsingFormat();

    /**
     * 获取help命令结果中的描述
     * @return 命令描述
     */
    public abstract String getDescription();
}
