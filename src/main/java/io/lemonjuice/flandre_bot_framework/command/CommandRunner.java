package io.lemonjuice.flandre_bot_framework.command;

import io.lemonjuice.flandre_bot_framework.event.msg.PermissionDeniedEvent;
import io.lemonjuice.flandre_bot_framework.model.Message;
import io.lemonjuice.flandre_bot_framework.permission.IPermissionLevel;

/**
 * 本类及所有子类的非静态成员变量/常量
 * 不会导致线程安全问题，
 * 但在处理外部共享状态的读/写时需保证操作原子性
 * （除非在配置中开启了同步执行模式）
 */
public abstract class CommandRunner {
    protected final Message command;

    public CommandRunner(Message command) {
        this.command = command;
    }

    /**
     * 标记命令的类型（群聊/所有私聊/好友）
     * @return 命令类型的枚举值 {@link Type}
     */
    public abstract Type getType();

    /**
     * 标记执行命令时所需的权限（普通成员/管理员/群主 或不对外开放的调试权限）
     * 调试权限以外的其他权限仅在群聊中生效
     * 框架中原生的权限等级参见 {@link io.lemonjuice.flandre_bot_framework.permission.PermissionLevel}
     * 处理权限不足时执行命令的逻辑请订阅事件 {@link PermissionDeniedEvent}
     * @return 权限等级
     */
    public abstract IPermissionLevel getPermissionLevel();

    /**
     * 匹配命令格式
     * @return this.command是否匹配当前命令的格式
     */
    public abstract boolean matches();

    /**
     * 执行命令逻辑
     */
    public abstract void apply();

    /**
     * @return 是否在当前命令被执行后阻断在注册列表后方的命令继续匹配
     */
    public boolean blockAfterCommands() {
        return true;
    }

    public enum Type {
        GROUP,
        PRIVATE,
        FRIEND,
        GENERAL;
    }
}
