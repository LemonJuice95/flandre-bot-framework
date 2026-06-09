package io.lemonjuice.flandre_bot_framework.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupMember {
    public final long userId;
    public final String nickname;
    public final String card;
    public final String role;
}
