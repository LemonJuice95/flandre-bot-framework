package io.lemonjuice.flandre_bot_framework.utils.data;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Pair<F, S> {
    private final F first;
    private final S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair<?,?> pair) {
            return Objects.equals(this.first, pair.getFirst()) &&
                    Objects.equals(this.second, pair.getSecond());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }
}
