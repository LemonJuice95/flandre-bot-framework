package io.lemonjuice.flandre_bot_framework.event.msg;

import io.lemonjuice.flandre_bot_framework.command.CommandRunner;
import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.event.ICancelableEvent;
import io.lemonjuice.flandre_bot_framework.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 在命令执行被权限拒绝时推送
 */
@Getter
@AllArgsConstructor
public class PermissionDeniedEvent extends Event implements ICancelableEvent {
    private final Message message;
    private final CommandRunner commandRunner;
}
