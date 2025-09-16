package io.lemonjuice.flandre_bot_framework.event.request;

import io.lemonjuice.flandre_bot_framework.event.Event;
import io.lemonjuice.flandre_bot_framework.model.request.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestEvent extends Event {
    private final FriendRequest request;
}
