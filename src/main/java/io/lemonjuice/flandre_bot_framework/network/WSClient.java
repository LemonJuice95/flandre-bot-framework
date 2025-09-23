package io.lemonjuice.flandre_bot_framework.network;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.NetworkConnectedEvent;
import io.lemonjuice.flandre_bot_framework.event.meta.NetworkDisconnectedEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.NetworkMessageEvent;
import io.lemonjuice.flandre_bot_framework.handler.NoticeHandler;
import io.lemonjuice.flandre_bot_framework.handler.ReceivingMessageHandler;
import io.lemonjuice.flandre_bot_framework.handler.RequestHandler;
import io.lemonjuice.flandre_bot_framework.handler.WSMetaEventHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import javax.annotation.Nullable;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@ClientEndpoint(configurator = WSClient.Configurator.class)
public class WSClient implements INetworkImpl {
    @Getter
    private static final WSClient instance = new WSClient();

    private final BlockingQueue<String> messageQueue;
    private final AtomicBoolean running;
    private final Thread senderThread;

    private final ConcurrentHashMap<UUID, WSResponse> waitingResponses = new ConcurrentHashMap<>();

    private Session session;
    private String token = "";

    private WSClient() {
        this.messageQueue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(false);
        this.senderThread = new Thread(this::sendingLoop, "WS-Sender");
    }

    @Override
    public synchronized boolean init(String token) {
        try {
            this.token = token;
            this.session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(BotBasicConfig.CLIENT_URL.get()));
            return true;
        } catch (Exception e) {
            log.error("Bot连接失败！", e);
            return false;
        }
    }

    @Override
    public void sendMsg(String action, @Nullable JSONObject msg) {
        JSONObject message = new JSONObject();
        message.put("action", action);
        if(msg != null) {
            message.put("params", msg);
        }
        this.sendJson(message);
    }

    @Override
    public JSONObject request(String action, @Nullable JSONObject msg) {
        JSONObject request = new JSONObject();
        request.put("action", action);
        if(msg != null) {
            request.put("params", msg);
        }
        return this.request(request);
    }

    private JSONObject request(JSONObject request) {
        JSONObject failedResult = new JSONObject();
        failedResult.put("retcode", -1);
        if(!running.get()) {
            return failedResult;
        }

        UUID uuid = UUID.randomUUID();
        request.put("echo", uuid.toString());
        WSResponse response = new WSResponse();

        this.waitingResponses.putIfAbsent(uuid, response);
        this.sendText(request.toString());

        try {
            if (response.await()) {
                return response.getResponse();
            } else {
                return failedResult;
            }
        } finally {
            this.waitingResponses.remove(uuid);
        }
    }

    private void sendJson(JSONObject json) {
        this.sendText(json.toString());
    }

    private void sendText(String message) {
        try {
            this.messageQueue.put(message);
        } catch (InterruptedException e) {
            log.error("WebSocket消息入队失败", e);
            Thread.currentThread().interrupt();
        }
    }

    private void sendingLoop() {
        while(this.running.get()) {
            try {
                String msg = this.messageQueue.take();
                this.session.getAsyncRemote().sendText(msg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public synchronized void close() {
        try {
            this.session.close();
        } catch (NullPointerException ignored) {
            //说明连接不存在，无需处理
        } catch (IOException e) {
            log.error("关闭ws客户端失败！");
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Bot连接成功！");
        this.running.set(true);
        this.senderThread.start();

        BotEventBus.post(new NetworkConnectedEvent());
    }

    @OnMessage
    public void onMessage(String json) {
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("echo")) {
            try {
                UUID uuid = UUID.fromString(jsonObject.getString("echo"));
                WSResponse response = this.waitingResponses.get(uuid);
                if (response != null) {
                    response.present(jsonObject);
                }
            } catch (IllegalArgumentException ignored) {
                //echo不符合uuid格式时，不执行逻辑
            }
        }

        String postType = jsonObject.optString("post_type", "");

        switch (postType) {
            case "message" -> ReceivingMessageHandler.handle(jsonObject);
            case "meta_event" -> WSMetaEventHandler.handle(jsonObject);
            case "request" -> RequestHandler.handle(jsonObject);
            case "notice" -> NoticeHandler.handle(jsonObject);
        }

        BotEventBus.post(new NetworkMessageEvent(jsonObject));
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Bot连接已断开!");
        this.running.set(false);
        this.senderThread.interrupt();
        this.waitingResponses.values().forEach(res -> {
            JSONObject failedResult = new JSONObject();
            failedResult.put("retcode", -1);
            res.present(failedResult);
        });
        Thread.startVirtualThread(new WSReconnect());
        BotEventBus.post(new NetworkDisconnectedEvent());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws客户端出现异常！", throwable);
    }

    public static class Configurator extends ClientEndpointConfig.Configurator {
        @Override
        public void beforeRequest(Map<String, List<String>> headers) {
            if(instance.token != null && !instance.token.isEmpty()) {
                headers.put("Authorization", Collections.singletonList("Bearer " + instance.token));
            }
        }
    }
}
