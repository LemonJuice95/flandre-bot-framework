package io.lemonjuice.flandre_bot_framework.command;

import io.lemonjuice.flandre_bot_framework.command.annotations.ForceNoPooling;
import io.lemonjuice.flandre_bot_framework.command.annotations.Resettable;
import io.lemonjuice.flandre_bot_framework.command.pool.CommandRunnerPool;
import io.lemonjuice.flandre_bot_framework.config.BotBasicConfig;
import io.lemonjuice.flandre_bot_framework.model.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class BotCommandLookup {
    public static final ClassValue<Boolean> SHOULD_POOLING = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            if(!CommandRunner.class.isAssignableFrom(type)) {
                return false;
            }
            if(type.isAnnotationPresent(ForceNoPooling.class)) {
                return false;
            }
            List<Field> fields = new ArrayList<>();
            Class<?> clazz = type;
            while(clazz != CommandRunner.class.getSuperclass()) {
                Collections.addAll(fields, clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();
            }

            return fields.stream()
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .filter(f -> !f.isAnnotationPresent(Resettable.class))
                    .findAny()
                    .isEmpty();
        }
    };

    public static final List<CommandRunnerSlot> GROUP_COMMANDS = new ArrayList<>();
    public static final List<CommandRunnerSlot> PRIVATE_COMMANDS = new ArrayList<>();
    public static final List<CommandRunnerSlot> FRIEND_COMMANDS = new ArrayList<>();

    public synchronized static void reloadConfig() {
        boolean shouldReload = BotBasicConfig.POOLING_COMMAND_RUNNERS.changed() ||
                BotBasicConfig.RUNNER_POOL_MAXIMUM_CAPACITY.changed();
        if(!shouldReload) return;
        Set<CommandRunnerSlot> slots = new HashSet<>();
        slots.addAll(GROUP_COMMANDS);
        slots.addAll(PRIVATE_COMMANDS);
        slots.addAll(FRIEND_COMMANDS);

        boolean pooling = BotBasicConfig.POOLING_COMMAND_RUNNERS.get();
        slots.forEach(s -> refreshPooling(s, pooling));
    }

    private static void refreshPooling(CommandRunnerSlot slot, boolean pooling) {
        if(pooling) {
            if(!SHOULD_POOLING.get(slot.runnerClass)) {
                return;
            }
            if(slot.getPool() instanceof CommandRunnerPool pool) {
                pool.reloadConfig(BotBasicConfig.RUNNER_POOL_MAXIMUM_CAPACITY.get());
                return;
            }
            slot.usePool(BotBasicConfig.RUNNER_POOL_INITIAL_CAPACITY.get(), BotBasicConfig.RUNNER_POOL_MAXIMUM_CAPACITY.get(), slot.provider);
        } else {
            slot.useDummyPool();
        }
    }
}
