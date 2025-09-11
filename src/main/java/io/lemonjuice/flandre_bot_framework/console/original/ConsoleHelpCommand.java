package io.lemonjuice.flandre_bot_framework.console.original;

import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandLookup;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;

import java.util.List;

public class ConsoleHelpCommand extends ConsoleCommandRunner {
    private static final int COMMANDS_PER_PAGE = 20;

    private final int page;
    private final int maxPage;

    public ConsoleHelpCommand(String[] args) {
        super(args);
        this.maxPage = Math.ceilDiv(ConsoleCommandLookup.COMMAND_HELPS.size(), COMMANDS_PER_PAGE);

        int page;
        try {
            page = Integer.parseInt(args[0]);
            page = Math.clamp(page, 1, this.maxPage);
        } catch (Exception e) {
            page = 1;
        }
        this.page = page;
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("help", "?");
    }

    @Override
    public void apply() {
        System.out.println(String.format("控制台命令帮助 (第%d页/共%d页)", this.page, this.maxPage));
        int startIndex = Math.max(COMMANDS_PER_PAGE * (this.page - 1), 0);
        int endIndex = Math.min(startIndex + COMMANDS_PER_PAGE, ConsoleCommandLookup.COMMAND_HELPS.size());
        for(int i = startIndex; i < endIndex; i++) {
            System.out.println(ConsoleCommandLookup.COMMAND_HELPS.get(i));
        }
    }

    @Override
    public String getUsingFormat() {
        return "'help [页码]' 或 '? [页码]'";
    }

    @Override
    public String getDescription() {
        return "显示此列表";
    }
}
