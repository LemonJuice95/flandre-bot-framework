package io.lemonjuice.flandre_bot_framework.command.group;

import io.lemonjuice.flandre_bot_framework.message.segment.AtMessageSegment;
import io.lemonjuice.flandre_bot_framework.message.segment.TextMessageSegment;
import io.lemonjuice.flandre_bot_framework.model.Message;

import java.util.Set;

/**
 * 群聊下简单命令的执行器（仅匹配完整命令体）<br>
 * 可以匹配多条简单命令
 */
public abstract class MultiSimpleGroupCommandRunner extends GroupCommandRunner {
    public MultiSimpleGroupCommandRunner(Message command) {
        super(command);
    }

    /**
     * @return 是否需要在命令开头@bot
     */
    protected abstract boolean needAtFirst();

    /**
     * @return 命令体字符串集合
     */
    protected abstract Set<String> getCommandBodies();

    @Override
    public boolean matches() {
        if(this.needAtFirst()) {
            if(this.command.message.getSegments().size() == 2 &&
               this.command.message.getSegments().getFirst() instanceof AtMessageSegment atSeg &&
               this.command.message.getSegments().get(1) instanceof TextMessageSegment textSeg) {
               return atSeg.getQQ() == this.command.selfId && this.getCommandBodies().contains(textSeg.getContent());
            }
            return false;
        }
        return this.command.message.isSimpleText() &&
                this.getCommandBodies().contains(this.command.message.toString());
    }
}