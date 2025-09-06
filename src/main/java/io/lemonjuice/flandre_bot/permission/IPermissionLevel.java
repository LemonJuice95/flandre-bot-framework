package io.lemonjuice.flandre_bot.permission;

import io.lemonjuice.flandre_bot.model.Message;

public interface IPermissionLevel {
    public boolean validatePermission(Message message);
}
