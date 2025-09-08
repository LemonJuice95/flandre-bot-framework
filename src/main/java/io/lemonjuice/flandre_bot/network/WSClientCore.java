package io.lemonjuice.flandre_bot.network;

import io.lemonjuice.flandre_bot.event.BotEventBus;
import io.lemonjuice.flandre_bot.event.meta.HeartBeatEvent;
import io.lemonjuice.flandre_bot.event.msg.WSMessageEvent;
import io.lemonjuice.flandre_bot.handler.ReceivingMessageHandler;
import io.lemonjuice.flandre_bot.model.Message;
import io.lemonjuice.flandre_bot.utils.MessageParser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

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
@ClientEndpoint(configurator = WSClientCore.Configurator.class)
public class WSClientCore {
    @Getter
    private static WSClientCore instance;

    private final BlockingQueue<String> messageQueue;
    private final AtomicBoolean running;
    private final Thread senderThread;

    private final ConcurrentHashMap<UUID, WSResponse> waitingResponses = new ConcurrentHashMap<>();

    private final Session session;
    private static String token;

    private WSClientCore(String url) throws DeploymentException, IOException {
        this.messageQueue = new LinkedBlockingQueue<>();
        this.running = new AtomicBoolean(false);
        this.senderThread = new Thread(this::sendingLoop, "WS-Sender");
        this.session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(url));
    }

    public synchronized static boolean connect(String url, String token) {
        try {
            WSClientCore.token = token;
            instance = new WSClientCore(url);
        } catch (Exception e) {
            log.error("Bot连接失败！", e);
            return false;
        }
        return true;
    }

    public JSONObject request(JSONObject request) {
        UUID uuid = UUID.randomUUID();
        request.put("echo", uuid.toString());
        WSResponse response = new WSResponse();

        this.waitingResponses.putIfAbsent(uuid, response);
        this.sendText(request.toString());

        try {
            if (response.await()) {
                return response.getResponse();
            } else {
                JSONObject failedResult = new JSONObject();
                failedResult.put("retcode", -1);
                return failedResult;
            }
        } finally {
            this.waitingResponses.remove(uuid);
        }
    }

    public void sendJson(JSONObject json) {
        this.sendText(json.toString());
    }

    public void sendText(String message) {
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

    public synchronized static void close() {
        try {
            instance.session.close();
        } catch (IOException e) {
            log.error("关闭ws客户端失败！");
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Bot已启动！");
        this.running.set(true);
        this.senderThread.start();
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

        //TODO 以后类型多起来了用switch
        if (postType.equals("message")) {
            Message message = MessageParser.tryParse(jsonObject);
            ReceivingMessageHandler.handle(message);
        }

        if (postType.equals("meta_event")) {
            if (jsonObject.getString("meta_event_type").equals("heartbeat")) {
                BotEventBus.post(new HeartBeatEvent());
            }
        }

        BotEventBus.post(new WSMessageEvent(jsonObject));
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Bot已断开连接!");
        this.running.set(false);
        this.senderThread.interrupt();
        new Thread(new WSReconnect()).start();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws客户端出现异常！", throwable);
    }

    public static class Configurator extends ClientEndpointConfig.Configurator {
        @Override
        public void beforeRequest(Map<String, List<String>> headers) {
            if(token != null && !token.isEmpty()) {
                headers.put("Authorization", Collections.singletonList("Bearer " + token));
            }
        }
    }
}
