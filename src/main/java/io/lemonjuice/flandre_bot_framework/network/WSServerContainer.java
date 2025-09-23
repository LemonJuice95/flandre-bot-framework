package io.lemonjuice.flandre_bot_framework.network;

import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.glassfish.tyrus.server.Server;
import org.json.JSONObject;

import javax.annotation.Nullable;
import javax.websocket.DeploymentException;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class WSServerContainer implements INetworkImpl {
    @Getter
    private static final WSServerContainer instance = new WSServerContainer();
    @Getter
    private volatile String token;
    @Getter
    private final AtomicBoolean runningStatus = new AtomicBoolean(false);

    private final Server server = new Server(BotBasicConfig.SERVER_LISTENING_IPS.get(), BotBasicConfig.SERVER_PORT.get(), "/", null, WSServer.class);

    @Setter
    private volatile WSServer connection;

    private WSServerContainer() {
    }

    public void disconnect(WSServer connection) {
        if(this.connection == connection) {
            this.connection = null;
            this.runningStatus.set(false);
        }
    }

    @Override
    public boolean init(String token) {
        try {
            this.token = token;
            this.server.start();
            log.info("Websocket服务器已启动");
            return true;
        } catch (DeploymentException e) {
            log.error("Websocket服务器启动失败！", e);
            return false;
        }
    }

    @Override
    public void close() {
        server.stop();
        this.connection = null;
        log.info("Websocket服务器已关闭");
    }

    @Override
    public void sendMsg(String action, @Nullable JSONObject msg) {
        try {
            this.connection.sendMsg(action, msg);
        } catch (NullPointerException e) {
            log.error("Bot未连接");
        }
    }

    @Override
    public JSONObject request(String action, @Nullable JSONObject msg) {
        try {
            return this.connection.request(action, msg);
        } catch (NullPointerException e) {
            log.error("Bot未连接");
            JSONObject failedResult = new JSONObject();
            failedResult.put("retcode", -1);
            return failedResult;
        }
    }
}
