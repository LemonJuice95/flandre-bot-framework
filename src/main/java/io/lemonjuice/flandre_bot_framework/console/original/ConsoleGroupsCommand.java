package io.lemonjuice.flandre_bot_framework.console.original;

import io.lemonjuice.flandre_bot_framework.account.ContextManager;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;
import io.lemonjuice.flandre_bot_framework.message.GroupContext;
import io.lemonjuice.flandre_bot_framework.utils.data.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConsoleGroupsCommand extends ConsoleCommandRunner {
    private static final int GROUPS_PER_PAGE = 20;
    private static final List<Pair<String, String>> HELP = new ArrayList<>();

    static {
        HELP.add(Pair.of("groups h(elp)", "显示此列表"));
        HELP.add(Pair.of("groups l(ist) [页数]", "显示群聊列表"));
        HELP.add(Pair.of("groups r(efresh)", "刷新群聊列表"));
    }

    public ConsoleGroupsCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        try {
            String action = this.args[0];
            switch (action) {
                case "h", "help" -> this.showHelp();
                case "l", "list" -> this.listGroups();
                case "r", "refresh" -> ContextManager.initGroupContexts();
                default -> throw new IllegalArgumentException("未知操作");
            }
        } catch (Exception e) {
            BotConsole.println("格式错误，使用'groups help'查看命令用法");
        }
    }

    private void showHelp() {
        BotConsole.println("groups命令帮助:");
        for(Pair<String, String> helpLine : HELP) {
            BotConsole.println(String.format("%-45s -%s", helpLine.getFirst(), helpLine.getSecond()));
        }
    }

    private void listGroups() {
        List<GroupContext> groups = ContextManager.getGroups();
        if(groups.isEmpty()) {
            BotConsole.println("当前群聊列表为空");
            return;
        }

        int page = this.args.length >= 2 ? Integer.parseInt(this.args[1]) : 1;
        int maxPage = Math.ceilDiv(groups.size(), GROUPS_PER_PAGE);
        page = Math.clamp(page, 1, maxPage);
        int startIndex = GROUPS_PER_PAGE * (page - 1);
        int endIndex = Math.min(startIndex + GROUPS_PER_PAGE, groups.size());
        BotConsole.println(String.format("群聊列表(第%d页/共%d页)", page, maxPage));
        BotConsole.println(String.format("%-18s%s", "群号", "群名"));
        for(int i = startIndex; i < endIndex; i++) {
            GroupContext context = groups.get(i);
            BotConsole.println(String.format("%-20d%s", context.getGroupId(), context.getGroupName()));
        }
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("groups");
    }

    @Override
    public String getUsingFormat() {
        return "'groups help'";
    }

    @Override
    public String getDescription() {
        return "群聊列表命令相关帮助";
    }
}
