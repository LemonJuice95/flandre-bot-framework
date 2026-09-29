package io.lemonjuice.flandre_bot_framework.message.pattern;

import io.lemonjuice.flandre_bot_framework.message.MessageSegmentList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MultiMessageMatcher {
    private final List<MessageMatcher> matchers = new ArrayList<>();
    private Boolean matches = null;
    @Getter
    private MessageMatcher succeededMatcher = null;
    private int succeededMatcherIdx = -1;
    private int startAt = -1;

    public MultiMessageMatcher(List<MessagePattern> patterns, MessageSegmentList segments) {
        patterns.forEach(p -> {
            this.matchers.add(p.matcher(segments));
        });
    }

    public void reset(MessageSegmentList segments) {
        this.matchers.forEach(m -> m.reset(segments));
        this.succeededMatcher = null;
        this.matches = null;
        this.startAt = -1;
        this.succeededMatcherIdx = -1;
    }

    public void reset() {
        this.matchers.forEach(MessageMatcher::reset);
        this.succeededMatcher = null;
        this.matches = null;
        this.startAt = -1;
        this.succeededMatcherIdx = -1;
    }

    public boolean matches() {
        if(this.matches != null) {
            return this.matches;
        }

        boolean matches = false;
        for(MessageMatcher matcher : this.matchers) {
            matches = matcher.matches();
            if(matches) {
                this.succeededMatcher = matcher;
                this.succeededMatcherIdx = this.matchers.indexOf(matcher);
                break;
            }
        }

        this.matches = matches;
        return matches;
    }

    public boolean simplyMatches() {
        if(this.matches != null) {
            return this.matches;
        }

        boolean matches = false;
        for(MessageMatcher matcher : this.matchers) {
            matches = matcher.simplyMatches();
            if(matches) {
                this.succeededMatcher = matcher;
                this.succeededMatcherIdx = this.matchers.indexOf(matcher);
                break;
            }
        }

        this.matches = matches;
        return matches;
    }

    public boolean find() {
        List<MessageMatcher> foundMatchers = this.matchers.stream()
                .filter(MessageMatcher::find)
                .toList();
        if(!foundMatchers.isEmpty()) {
            this.succeededMatcher = foundMatchers.stream()
                    .max((m1, m2) -> {
                        int len1 = m1.group(0).size();
                        int len2 = m2.group(0).size();
                        if (len1 == len2) {
                            return Integer.compare(this.matchers.indexOf(m2), this.matchers.indexOf(m1));
                        }
                        return Integer.compare(len1, len2);
                    })
                    .orElse(null);
            this.succeededMatcherIdx = this.matchers.indexOf(this.succeededMatcher);
            this.startAt = this.succeededMatcher.getStartAt();
            this.matchers.forEach(m -> m.seekTo(this.startAt));
        }
        return !foundMatchers.isEmpty();
    }

    /**
     * 返回成功匹配的匹配器的对应捕获组
     * @param groupId 捕获组id
     * @return 捕获的消息段列表
     */
    public MessageSegmentList group(int groupId) {
        return this.succeededMatcher.group(groupId);
    }

    /**
     * 返回成功匹配的匹配器的对应捕获组
     * @param pairs 多对整数，每对整数的第一项为匹配器的索引（取构建MultiMessagePattern时传入参数的索引），第二项为当匹配成功的matcher索引为该对的前者时，所取用捕获组的id
     * @return 捕获的消息段列表
     */
    public MessageSegmentList group(int... pairs) {
        if(pairs.length % 2 != 0) {
            throw new IllegalArgumentException("传入参数个数必须为偶数");
        }
        for(int i = 0; i < pairs.length; i += 2) {
            int matcherIdx = pairs[i];
            if(this.succeededMatcherIdx == matcherIdx) {
                return this.succeededMatcher.group(pairs[i + 1]);
            }
        }

        return null;
    }
}
