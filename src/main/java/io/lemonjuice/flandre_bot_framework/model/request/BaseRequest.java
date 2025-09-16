package io.lemonjuice.flandre_bot_framework.model.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseRequest {
    public final long time;
    public final long selfId;
    public final long userId;
    public final String comment;
    protected final String flag;

    public abstract void accept();
    public abstract void deny();

    public static abstract class Builder<T extends BaseRequest> {
        public long time;
        public long selfId;
        public long userId;
        public String comment;
        public String flag;

        public Builder<T> time(long time) {
            this.time = time;
            return this;
        }

        public Builder<T> selfId(long selfId) {
            this.selfId = selfId;
            return this;
        }

        public Builder<T> userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder<T> comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder<T> flag(String flag) {
            this.flag = flag;
            return this;
        }

        public abstract T build();
    }
}
