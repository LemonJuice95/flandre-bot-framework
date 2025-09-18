package io.lemonjuice.flandre_bot_framework.account;

import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.network.NetworkContainer;
import io.lemonjuice.flandre_bot_framework.network.WSClient;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class ContextManager {
    private static final ConcurrentHashMap<Long, GroupContext> GROUP_CONTEXTS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, FriendContext> FRIEND_CONTEXTS = new ConcurrentHashMap<>();

    private static final Object groupContextInitLock = new Object();
    private static final Object friendContextInitLock = new Object();

    public static List<GroupContext> getGroups() {
        return List.copyOf(GROUP_CONTEXTS.values());
    }

    public static List<FriendContext> getFriends() {
        return List.copyOf(FRIEND_CONTEXTS.values());
    }

    public static GroupContext getGroup(long id) {
        Optional<GroupContext> optContext = Optional.ofNullable(GROUP_CONTEXTS.get(id));
        return optContext.orElse(new GroupContext(id, ""));
    }

    public static FriendContext getFriend(long id) {
        Optional<FriendContext> optContext = Optional.ofNullable(FRIEND_CONTEXTS.get(id));
        return optContext.orElse(new FriendContext(id, ""));
    }

    public synchronized static void init() {
        initGroupContexts();
        initFriendContexts();
    }

    public static void addGroup(long groupId) {
        JSONObject response = NetworkContainer.getImpl().request("get_group_detail_info", null);
        if(response.optInt("retcode", -1) == 0){
            try {
                JSONObject group = response.getJSONObject("data");
                long id = groupId;
                String name = group.getString("group_name");
                GROUP_CONTEXTS.putIfAbsent(id, new GroupContext(id, name));
            } catch (JSONException e) {
                log.error("添加群聊[{}]信息失败！", groupId, e);
            }
        } else {
            log.error("获取群聊[{}]信息失败！", groupId);
        }
    }

    public static void removeGroup(long groupId) {
        GROUP_CONTEXTS.remove(groupId);
    }

    public static void addFriend(long userId) {
        JSONObject response = NetworkContainer.getImpl().request("get_stranger_info", null);
        if(response.optInt("retcode", -1) == 0) {
            try {
                JSONObject friend = response.getJSONObject("data");
                long id = userId;
                String nickname = friend.getString("nickname");
                FRIEND_CONTEXTS.putIfAbsent(id, new FriendContext(id, nickname));
            } catch (JSONException e) {
                log.error("添加好友[{}]信息失败！", userId, e);
            }
        } else {
            log.error("获取好友[{}]信息失败！", userId);
        }
    }

    public static void initGroupContexts() {
        synchronized (groupContextInitLock) {
            log.info("正在初始化群聊列表");
            JSONObject response = NetworkContainer.getImpl().request("get_group_list", null);
            if (response.optInt("retcode", -1) == 0) {
                GROUP_CONTEXTS.clear();
                JSONArray groupList = response.optJSONArray("data", new JSONArray());
                for (int i = 0; i < groupList.length(); i++) {
                    JSONObject group = groupList.getJSONObject(i);
                    long id = group.getLong("group_id");
                    String name = group.getString("group_name");
                    GroupContext context = new GroupContext(id, name);
                    GROUP_CONTEXTS.put(id, context);
                }
            } else {
                log.error("群聊列表拉取失败！");
            }
        }
        log.info("群聊列表初始化完毕！");
    }

    public static void initFriendContexts() {
        synchronized (friendContextInitLock) {
            log.info("正在初始化好友列表");
            JSONObject response = NetworkContainer.getImpl().request("get_friend_list", null);
            if (response.optInt("retcode", -1) == 0) {
                FRIEND_CONTEXTS.clear();
                JSONArray friendList = response.optJSONArray("data", new JSONArray());
                for (int i = 0; i < friendList.length(); i++) {
                    JSONObject friend = friendList.getJSONObject(i);
                    long id = friend.getLong("user_id");
                    String nickname = friend.getString("nickname");
                    FriendContext context = new FriendContext(id, nickname);
                    FRIEND_CONTEXTS.put(id, context);
                }
            } else {
                log.error("好友列表拉取失败！");
            }
        }
        log.info("好友列表初始化完毕！");
    }
}
