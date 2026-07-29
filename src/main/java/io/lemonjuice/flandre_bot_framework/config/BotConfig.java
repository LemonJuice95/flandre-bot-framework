package io.lemonjuice.flandre_bot_framework.config;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Properties;

@Log4j2
public class BotConfig {
    private final Properties properties = new Properties();
    private final File cfgFile;
    private final File defaultFile;

    /**
     * 标记是否在释放默认配置文件时视为加载失败
     * 并使load()方法返回false
     */
    @Setter
    private boolean failWhenExport = false;

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

    //TODO 完善List相关方法

    public boolean load() {
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
        try (InputStream input = new FileInputStream(this.cfgFile)) {
            this.properties.load(input);
            return true;
        } catch (IOException e) {
            log.warn("读取配置文件{}失败！", this.cfgFile.getName(), e);
            return false;
        }
    }
}
