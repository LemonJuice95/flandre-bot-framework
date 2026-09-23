package io.lemonjuice.flandre_bot_framework.console.original;

import io.lemonjuice.flandre_bot_framework.config.BotConfig;
import io.lemonjuice.flandre_bot_framework.config.ConfigLookup;
import io.lemonjuice.flandre_bot_framework.config.ReloadResult;
import io.lemonjuice.flandre_bot_framework.console.BotConsole;
import io.lemonjuice.flandre_bot_framework.console.ConsoleCommandRunner;

import java.util.List;

public class ConsoleConfigCommand extends ConsoleCommandRunner {
    public ConsoleConfigCommand(String[] args) {
        super(args);
    }

    @Override
    public void apply() {
        try {
            String operation = this.args[0];
            switch (operation) {
                case "l", "list" -> {
                    BotConsole.println(String.format("%-15s %s", "名称", "描述"));
                    ConfigLookup.CONFIGS.forEach((name, cfg) -> {
                        BotConsole.println(String.format("%-15s %s", name, cfg.getDescription()));
                    });
                }
                case "r", "reload" -> {
                    String name = this.args[1];
                    if(name.equalsIgnoreCase("all")) {
                        BotConsole.println("重载所有配置...");
                        ConfigLookup.CONFIGS.forEach((na, cfg) -> {
                            ReloadResult result = cfg.reload();
                            switch (result) {
                                case SUCCEED -> BotConsole.println(String.format("%s: 配置已重载", na));
                                case FAILED -> BotConsole.println(String.format("%s: 配置重载失败", na));
                                case CANCELLED -> BotConsole.println(String.format("%s: 配置重载被取消", na));
                            }
                        });
                    } else {
                        BotConfig config = ConfigLookup.CONFIGS.get(name);
                        if (config == null) {
                            BotConsole.println(String.format("不存在名为'%s'的配置", name));
                        } else {
                            ReloadResult result = config.reload();
                            switch (result) {
                                case SUCCEED -> BotConsole.println("配置已重载");
                                case FAILED -> BotConsole.println("配置重载失败");
                                case CANCELLED -> BotConsole.println("配置重载被取消");
                            }
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            BotConsole.println("格式错误，正确格式：'config <l(ist)|r(eload)> [名称|all]'");
        }
    }

    @Override
    public List<String> getCommandBodies() {
        return List.of("config",  "cfg");
    }

    @Override
    public String getUsingFormat() {
        return "'config <l(ist)|r(eload)> [名称|all]'";
    }

    @Override
    public String getDescription() {
        return "列出配置列表|重载对应配置";
    }
}
