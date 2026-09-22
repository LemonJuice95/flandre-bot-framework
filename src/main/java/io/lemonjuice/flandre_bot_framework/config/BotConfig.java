package io.lemonjuice.flandre_bot_framework.config;

import io.lemonjuice.flandre_bot_framework.event.BotEventBus;
import io.lemonjuice.flandre_bot_framework.event.meta.ConfigReloadEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class BotConfig {
    private static final Pattern listPattern = Pattern.compile("\\{(.*)}");

    private volatile Properties properties = new Properties();
    private final File cfgFile;
    private final File defaultFile;
    private final List<ConfigItem<?>> items = new ArrayList<>();

    /**
     * 标记是否在释放默认配置文件时视为加载失败
     * 并使load()方法返回false
     */
    @Setter
    private boolean failWhenExport = false;
    @Getter
    private boolean exportedOnLastLoad = false;

    public BotConfig(File cfgFile, File defaultFile) {
        this.cfgFile = cfgFile;
        this.defaultFile = defaultFile;
    }

    public BotConfig(File cfgFile) {
        this(cfgFile, null);
    }

    public BotConfig(String cfgFile, String defaultFile) {
        this(new File(cfgFile), new File(defaultFile));
    }

    public BotConfig(String cfgFile) {
        this(new File(cfgFile));
    }

    public <T> ConfigItem<T> register(ConfigItem<T> item) {
        this.items.add(item);
        return item;
    }

    public <T> ConfigItem<T> register(Supplier<T> provider) {
        return this.register(new ConfigItem<>(provider));
    }

    public <T> ConfigItem<T> register(Function<String, T> provider, String key) {
        return this.register(new ConfigItem<>(provider, key));
    }

    public <T> ConfigItem<T> register(BiFunction<String, T, T> provider, String key, T defaultValue) {
        return this.register(new ConfigItem<>(provider, key, defaultValue));
    }

    public BotConfig failWhenExport() {
        this.failWhenExport = true;
        return this;
    }

    public String getString(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    public String getString(String key) {
        return this.properties.getProperty(key);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(this.properties.getProperty(key));
        } catch (NumberFormatException | NullPointerException e) {
            log.warn("配置文件{}中配置项{}的值无效(应为int)，将使用默认值{}", this.cfgFile.getName(), key, defaultValue);
            return defaultValue;
        }
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(this.properties.getProperty(key));
        } catch (NumberFormatException | NullPointerException e) {
            log.warn("配置文件{}中配置项{}的值无效(应为long)，将使用默认值{}", this.cfgFile.getName(), key, defaultValue);
            return defaultValue;
        }
    }

    public long getLong(String key) {
        return this.getLong(key, 0);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String valueStr = this.getString(key);
        if("true".equalsIgnoreCase(valueStr)) return true;
        else if("false".equalsIgnoreCase(valueStr)) return false;
        else {
            log.warn("配置文件{}中配置项{}的值无效(应为boolean)，将使用默认值{}", this.cfgFile.getName(), key, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> clazz) {
        String valueStr = this.getString(key);
        if (valueStr == null) {
            log.warn("配置文件{}中找不到配置项{}，将使用默认值null", this.cfgFile.getName(), key);
            return null;
        }
        for (T ele : clazz.getEnumConstants()) {
            if(ele.name().equalsIgnoreCase(valueStr.trim())) {
                return ele;
            }
        }
        log.warn("配置文件{}中配置项{}的值无效(可用值列表请参考{}类或查阅文档)，将使用默认值null", this.cfgFile.getName(), key, clazz.getName());
        return null;
    }

    public <T extends Enum<T>> T getEnum(String key, T defaultValue) {
        String valueStr = this.getString(key);
        if(defaultValue == null) {
            throw new IllegalArgumentException("传入的默认值不允许为null");
        }
        if (valueStr == null) {
            log.warn("配置文件{}中找不到配置项{}，将使用默认值{}", this.cfgFile.getName(), key, defaultValue);
            return defaultValue;
        }
        for (T ele : defaultValue.getDeclaringClass().getEnumConstants()) {
            if(ele.name().equalsIgnoreCase(valueStr.trim())) {
                return ele;
            }
        }
        log.warn("配置文件{}中配置项{}的值无效(可用值列表请参考{}类或查阅文档)，将使用默认值{}", this.cfgFile.getName(), key, defaultValue.getDeclaringClass().getName(), defaultValue);
        return defaultValue;
    }

    public List<String> getStringList(String key) {
        String rawStr = this.getString(key);
        if(rawStr == null) {
            log.warn("找不到配置文件{}中的{}项", this.cfgFile.getName(), key);
            return new ArrayList<>();
        }

        List<String> result = new ArrayList<>();
        String valueStr = rawStr;
        Matcher matcher = listPattern.matcher(rawStr);
        if(matcher.find()) {
            valueStr = matcher.group(1);
        }

        boolean escapeNext = false;
        int leftSpaces = 0, rightSpaces = 0;
        boolean inQuote = false;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < valueStr.length(); i++) {
            char c = valueStr.charAt(i);
            if(escapeNext) {
                builder.append(c);
                escapeNext = false;
                continue;
            }
            switch (c) {
                case '\\' -> {
                    escapeNext = true;
                }
                case '\"' -> {
                    inQuote = !inQuote;
                }
                case ',' -> {
                    if(inQuote) {
                        builder.append(c);
                    } else {
                        String str = " ".repeat(leftSpaces) + builder.toString().trim() + " ".repeat(rightSpaces);
                        result.add(str);
                        builder = new StringBuilder();
                        leftSpaces = 0;
                        rightSpaces = 0;
                    }
                }
                case ' ' -> {
                    if(inQuote) {
                        if (builder.toString().isBlank()) {
                            leftSpaces++;
                        } else {
                            rightSpaces++;
                        }
                    }
                }
                default -> {
                    builder.append(c);
                    rightSpaces = 0;
                }
            }
        }
        if(!result.isEmpty() || !builder.isEmpty()) {
            result.add(" ".repeat(leftSpaces) + builder.toString().trim() + " ".repeat(rightSpaces));
        }
        return result;
    }

    public List<Integer> getIntList(String key) {
        List<Integer> result = new ArrayList<>();
        List<String> rawList = this.getStringList(key);
        rawList.forEach(s -> {
            try {
                result.add(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                log.warn("配置文件{}的配置项{}中出现了非法值{}(应为Integer), 将丢弃该值", cfgFile.getName(), key, s);
            }
        });
        return result;
    }

    public List<Long> getLongList(String key) {
        List<Long> result = new ArrayList<>();
        List<String> rawList = this.getStringList(key);
        rawList.forEach(s -> {
            try {
                result.add(Long.valueOf(s));
            } catch (NumberFormatException e) {
                log.warn("配置文件{}的配置项{}中出现了非法值{}(应为Long), 将丢弃该值", cfgFile.getName(), key, s);
            }
        });
        return result;
    }

    public List<Boolean> getBooleanList(String key) {
        List<Boolean> result = new ArrayList<>();
        List<String> rawList = this.getStringList(key);
        rawList.forEach(s -> {
            if(s.trim().equalsIgnoreCase("true")) {
                result.add(Boolean.TRUE);
            } else if(s.trim().equalsIgnoreCase("false")) {
                result.add(Boolean.FALSE);
            } else {
                log.warn("配置文件{}的配置项{}中出现了非法值{}(应为Boolean), 将丢弃该值", cfgFile.getName(), key, s);
            }
        });
        return result;
    }

    public synchronized boolean load() {
        if(!this.cfgFile.getParentFile().exists()) {
            this.cfgFile.getParentFile().mkdirs();
        }
        if(!this.cfgFile.exists()) {
            try {
                if (this.defaultFile != null) {
                    try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(this.defaultFile.getPath());
                         OutputStream output = new FileOutputStream(this.cfgFile)) {
                        output.write(input.readAllBytes());
                    }
                } else {
                    this.cfgFile.createNewFile();
                }
                this.exportedOnLastLoad = true;
            } catch (IOException | NullPointerException e) {
                    log.warn("释放配置文件{}失败！", this.cfgFile.getName(), e);
                    return false;
            }


            return this.loadProperties() && !this.failWhenExport;
        } else {
            return this.loadProperties();
        }
    }

    private boolean loadProperties() {
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(this.cfgFile), StandardCharsets.UTF_8)) {
            properties.load(reader);
            this.properties = properties;
            return true;
        } catch (IOException e) {
            log.warn("读取配置文件{}失败！", this.cfgFile.getName(), e);
            return false;
        }
    }

    public synchronized ReloadResult reload() {
        if(!BotEventBus.postCancelable(new ConfigReloadEvent.Pre(this))) {
            if (this.loadProperties()) {
                this.items.forEach(ConfigItem::reset);
                Thread.startVirtualThread(() -> BotEventBus.post(new ConfigReloadEvent.Post(this)));
                return ReloadResult.SUCCEED;
            }
            return ReloadResult.FAILED;
        }
        return ReloadResult.CANCELLED;
    }
}
