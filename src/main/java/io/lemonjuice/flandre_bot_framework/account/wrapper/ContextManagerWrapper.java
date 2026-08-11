package io.lemonjuice.flandre_bot_framework.account.wrapper;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;

import java.util.List;

public class ContextManagerWrapper {
    public List<GroupContext> getGroups() {
        return ContextManager.getGroups();
    }

    public List<FriendContext> getFriends() {
        return ContextManager.getFriends();
    }

    public GroupContext getGroup(long id) {
        return ContextManager.getGroup(id);
    }

    public FriendContext getFriend(long id) {
        return ContextManager.getFriend(id);
    }

    public void addGroup(long id) {
        ContextManager.addGroup(id);
    }

    public void removeGroup(long id) {
        ContextManager.removeGroup(id);
    }

    public void addFriend(long id) {
        ContextManager.addFriend(id);
    }

    public void removeFriend(long id) {
        ContextManager.removeFriend(id);
    }

    public void refreshAll() {
        ContextManager.init();
    }

    public void refreshGroups() {
        ContextManager.initGroupContexts();
    }

    public void refreshFriends() {
        ContextManager.initFriendContexts();
    }
}
