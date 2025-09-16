package io.lemonjuice.flandre_bot_framework.console.original;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.FriendContext;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConsoleFriendsCommand extends ConsoleCommandRunner {
    private static final int FRIENDS_PER_PAGE = 20;
    private static final List<Pair<String, String>> HELP = new ArrayList<>();

    static {
        HELP.add(Pair.of("friends h(elp)", "显示此列表"));
        HELP.add(Pair.of("friends l(ist) [页数]", "显示好友列表"));
        HELP.add(Pair.of("friends r(efresh)", "刷新好友列表"));
    }

    public ConsoleFriendsCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        try {
            String action = this.args[0];
            switch (action) {
                case "h", "help" -> this.showHelp();
                case "l", "list" -> this.listFriends();
                case "r", "refresh" -> ContextManager.initFriendContexts();
                default -> throw new IllegalArgumentException("未知操作");
            }
        } catch (Exception e) {
            BotConsole.println("格式错误，使用'friends help'查看命令用法");
        }
    }

    private void showHelp() {
        BotConsole.println("friends命令帮助:");
        for(Pair<String, String> helpLine : HELP) {
            BotConsole.println(String.format("%-45s -%s", helpLine.getFirst(), helpLine.getSecond()));
        }
    }

    private void listFriends() {
        List<FriendContext> friends = ContextManager.getFriends();
        if(friends.isEmpty()) {
            BotConsole.println("当前好友列表为空");
            return;
        }

        int page = this.args.length >= 2 ? Integer.parseInt(this.args[1]) : 1;
        int maxPage = Math.ceilDiv(friends.size(), FRIENDS_PER_PAGE);
        page = Math.clamp(page, 1, maxPage);
        int startIndex = FRIENDS_PER_PAGE * (page - 1);
        int endIndex = Math.min(startIndex + FRIENDS_PER_PAGE, friends.size());
        BotConsole.println(String.format("好友列表(第%d页/共%d页)", page, maxPage));
        BotConsole.println(String.format("%-19s%s", "qq号", "昵称"));
        for(int i = startIndex; i < endIndex; i++) {
            FriendContext context = friends.get(i);
            BotConsole.println(String.format("%-20d%s", context.getFriendId(), context.getNickname()));
        }
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("friends");
    }

    @Override
    public String getUsingFormat() {
        return "'friends help'";
    }

    @Override
    public String getDescription() {
        return "好友列表命令相关帮助";
    }
}
