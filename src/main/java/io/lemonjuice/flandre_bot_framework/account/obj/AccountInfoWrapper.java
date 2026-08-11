package io.lemonjuice.flandre_bot_framework.account.obj;

import io.lemonjuice.flandre_bot_framework.account.AccountInfo;

public class AccountInfoWrapper {
    public long getBotId() {
        return AccountInfo.getBotId();
    }

    public String getBotName() {
        return AccountInfo.getBotName();
    }
}
