package io.lemonjuice.flandre_bot.permission;

import io.lemonjuice.flandre_bot.model.Message;
import io.lemonjuice.flandre_bot.utils.DebugModeUtils;

public enum PermissionLevel implements IPermissionLevel {
    NORMAL {
        @Override
        public boolean validatePermission(Message message) {
            return true;
        }
    },
    ADMIN {
        @Override
        public boolean validatePermission(Message message) {
            return message.sender.role.equals("admin") ||
                    OWNER.validatePermission(message);
        }
    },
    OWNER {
        @Override
        public boolean validatePermission(Message message) {
            return message.sender.role.equals("owner") ||
                    DEBUG.validatePermission(message);
        }
    },
    DEBUG {
        @Override
        public boolean validatePermission(Message message) {
            return DebugModeUtils.hasDebugPermission(message.userId);
        }
    };
}
