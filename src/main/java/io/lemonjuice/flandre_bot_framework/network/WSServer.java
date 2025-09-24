package io.lemonjuice.flandre_bot_framework.network;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.NetworkConnectedEvent;
import io.lemonjuice.flandre_bot_framework.event.msg.NetworkMessageEvent;
import io.lemonjuice.flandre_bot_framework.handler.NoticeHandler;
import io.lemonjuice.flandre_bot_framework.handler.ReceivingMessageHandler;
import io.lemonjuice.flandre_bot_framework.handler.RequestHandler;
import io.lemonjuice.flandre_bot_framework.handler.WSMetaEventHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import javax.annotation.Nullable;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@Log4j2
@ServerEndpoint(value = "/", configurator = WSServer.Configurator.class)
public class WSServer {
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final AtomicBoolean rejected = new AtomicBoolean(false);
    private final BlockingQueue<String> messageQueue;
    private Thread senderThread;
    private final ConcurrentHashMap<UUID, WSResponse> waitingResponses;
    private Session session;

    @Setter
    private AuthStatus authStatus = AuthStatus.TOKEN_NOT_FOUND;

    public WSServer() {
        this.messageQueue = new LinkedBlockingQueue<>();
        this.senderThread = new Thread(this::sendingLoop, "WS-Sender");
        this.waitingResponses = new ConcurrentHashMap<>();
    }

    public void sendMsg(String action, @Nullable JSONObject msg) {
        JSONObject json = new JSONObject();
        json.put("action", action);
        if(msg != null) {
            json.put("params", msg);
        }
        this.sendText(json.toString());
    }

    public JSONObject request(String action, @Nullable JSONObject msg) {
        JSONObject json = new JSONObject();
        json.put("action", action);
        if(msg != null) {
            json.put("params", msg);
        }
        return this.request(json);
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

    private void sendText(String msg) {
        try {
            this.messageQueue.put(msg);
        } catch (InterruptedException e) {
            log.error("ws消息入队失败！", e);
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


    @OnOpen
    public void onOpen(Session session) {
        try {
            if (this.authStatus != AuthStatus.OK) {
                this.rejected.set(true);
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, authStatus.getMsg()));
                log.warn("已拒绝一个ws客户端的连接请求，原因: {}", authStatus.getMsg());
                return;
            }

            if (!WSServerContainer.getInstance().getRunningStatus().compareAndSet(false, true)) {
                this.rejected.set(true);
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "仅支持单个客户端连接"));
                log.warn("仅支持单个客户端连接，需要管理多个账号请多开应用实例");
                return;
            }

            WSServerContainer.getInstance().setConnection(this);
            this.session = session;
            this.running.set(true);
            this.senderThread = new Thread(this::sendingLoop, "WS-Sender");
            this.senderThread.start();
            log.info("bot已连接！");
            BotEventBus.post(new NetworkConnectedEvent());

        } catch (IOException e) {
            log.warn("未成功关闭一个客户端的连接", e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        if(this.rejected.get()) {
            log.warn("一个连接已被拒绝");
        } else {
            log.info("一个连接已被断开");
        }
        this.running.set(false);
        this.senderThread.interrupt();
        WSServerContainer.getInstance().disconnect(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws服务器出现异常！", throwable);
    }

    public static class Configurator extends ServerEndpointConfig.Configurator {
        private final String TOKEN_PREFIX = "Bearer ";
        private final ThreadLocal<AuthStatus> statusCache = new ThreadLocal<>();

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            this.statusCache.set(this.validateToken(request));
        }

        @Override
        public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
            try {
                T endpoint = endpointClass.getConstructor().newInstance();
                if(endpoint instanceof WSServer wsServer) {
                    wsServer.setAuthStatus(this.statusCache.get());
                }
                return endpoint;
            } catch (Exception e) {
                throw new InstantiationException("创建端点实例失败！");
            } finally {
                this.statusCache.remove();
            }
        }

        private AuthStatus validateToken(HandshakeRequest request) {
            if(WSServerContainer.getInstance().getToken() == null || WSServerContainer.getInstance().getToken().isEmpty()) {
                return AuthStatus.OK;
            }

            Map<String, List<String>> headers = request.getHeaders();
            List<String> tokens = headers.get("Authorization");

            if(tokens == null || tokens.isEmpty()) {
                return AuthStatus.TOKEN_NOT_FOUND;
            }

            String token = tokens.getFirst();

            if(!token.startsWith(TOKEN_PREFIX)) {
                return AuthStatus.INVALID_TOKEN_FORMAT;
            }

            token = token.replaceFirst(Pattern.quote(TOKEN_PREFIX), "");

            return token.equals(WSServerContainer.getInstance().getToken()) ? AuthStatus.OK : AuthStatus.WRONG_TOKEN;
        }
    }

    private enum AuthStatus {
        OK(""),
        TOKEN_NOT_FOUND("请提供token"),
        INVALID_TOKEN_FORMAT("token格式无效"),
        WRONG_TOKEN("token不正确");

        @Getter
        private final String msg;

        private AuthStatus(String msg) {
            this.msg = msg;
        }
    }
}
