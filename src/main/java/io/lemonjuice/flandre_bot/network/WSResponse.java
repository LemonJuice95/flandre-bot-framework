package io.lemonjuice.flandre_bot.network;

import lombok.Getter;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WSResponse {
    @Getter
    private volatile JSONObject response;
    private final CountDownLatch present = new CountDownLatch(1);

    public boolean await() {
        try {
            return present.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public synchronized void present(JSONObject response) {
        this.response = response;
        this.present.countDown();
    }
}
