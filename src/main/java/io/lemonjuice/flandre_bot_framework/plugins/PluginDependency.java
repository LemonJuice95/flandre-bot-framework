package io.lemonjuice.flandre_bot_framework.plugins;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record PluginDependency(Class<? extends BotPlugin> pluginClass, Version minVersion, Version maxVersion) {

    public PluginDependency(Class<? extends BotPlugin> pluginClass, Version minVersion) {
        this(pluginClass, minVersion, Version.ANY);
    }

    public PluginDependency(Class<? extends BotPlugin> pluginClass, String minVersionStr, String maxVersionStr) {
        this(pluginClass, Version.parseVersion(minVersionStr), Version.parseVersion(maxVersionStr));
    }

    public PluginDependency(Class<? extends BotPlugin> pluginClass, String minVersionStr) {
        this(pluginClass, Version.parseVersion(minVersionStr), Version.ANY);
    }

    public PluginDependency(Class<? extends BotPlugin> pluginClass) {
        this(pluginClass, Version.ANY, Version.ANY);
    }

    public boolean isVersionValid(Version version) {
        boolean minVersionValid = this.minVersion.equals(Version.ANY) || version.compareTo(this.minVersion) >= 0;
        boolean maxVersionValid = this.maxVersion.equals(Version.ANY) || version.compareTo(this.maxVersion) <= 0;
        return  minVersionValid && maxVersionValid;
    }

    public static record Version(int major, int minor, int patch) implements Comparable<Version> {
        public static final Version ANY = new Version(-1, -1, -1);
        public static final Pattern parserPattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)");

        public static Version parseVersion(String versionStr) {
            if(versionStr.isEmpty() || versionStr.equalsIgnoreCase("any") || versionStr.equalsIgnoreCase("x.x.x")) {
                return ANY;
            }
            Matcher matcher = parserPattern.matcher(versionStr);
            if(matcher.find()) {
                try {
                    return new Version(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3))
                    );
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("不正确的版本号格式");
                }
            } else {
                throw new IllegalArgumentException("不正确的版本号格式");
            }
        }

        @Override
        public String toString() {
            if(this.equals(ANY)) {
                return "x.x.x";
            }
            return String.format("%d.%d.%d", this.major, this.minor, this.patch);
        }

        @Override
        public int compareTo(Version o) {
            if(this.major == o.major) {
                if(this.minor == o.minor) {
                    return Integer.compare(this.patch, o.patch);
                }
                return Integer.compare(this.minor, o.minor);
            }
            return Integer.compare(this.major, o.major);
        }
    }
}
