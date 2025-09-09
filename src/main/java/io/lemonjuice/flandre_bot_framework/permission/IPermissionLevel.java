package io.lemonjuice.flandre_bot_framework.permission;

import io.lemonjuice.flandre_bot_framework.model.Message;

public interface IPermissionLevel {
    public boolean validatePermission(Message message);
}
