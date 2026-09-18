package io.lemonjuice.flandre_bot_framework.plugins;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginDependency {
    private static final Pattern versionExprPattern = Pattern.compile("([(\\[])\\s*(\\d+\\.\\d+\\.\\d+)?\\s*,\\s*(\\d+\\.\\d+\\.\\d+)?([)\\]])");
    private final Class<? extends BotPlugin> pluginClass;
    private final Version minVersion;
    private final Version maxVersion;
    private final boolean minInclusive;
    private final boolean maxInclusive;

    public PluginDependency(Class<? extends BotPlugin> pluginClass, Version minVersion, Version maxVersion, boolean minInclusive, boolean maxInclusive) {
        this.pluginClass = pluginClass;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public PluginDependency(Class<? extends BotPlugin> pluginClass, String versionStr) {
        Matcher matcher = versionExprPattern.matcher(versionStr.trim());
        Matcher versionMatcher = Version.parserPattern.matcher(versionStr.trim());

        Version minVersion;
        Version maxVersion;
        boolean minInclusive;
        boolean maxInclusive;

        if (matcher.matches()) {
            minVersion = Version.parseVersion(matcher.group(2));
            maxVersion = Version.parseVersion(matcher.group(3));
            minInclusive = matcher.group(1).equals("[");
            maxInclusive = matcher.group(4).equals("]");
        } else if(versionMatcher.matches()) {
            minVersion = Version.parseVersion(versionStr);
            maxVersion = Version.ANY;
            maxInclusive = true;
            minInclusive = true;
        } else {
            throw new IllegalArgumentException("非法的版本表达式");
        }

        this.pluginClass = pluginClass;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public PluginDependency(Class<? extends BotPlugin> pluginClass) {
        this(pluginClass, Version.ANY, Version.ANY, true, true);
    }

    public boolean isVersionValid(Version version) {
        boolean minVersionValid = this.minVersion.equals(Version.ANY) ||
                version.compareTo(this.minVersion) > 0 ||
                (version.compareTo(this.minVersion) == 0 && this.minInclusive);

        boolean maxVersionValid = this.maxVersion.equals(Version.ANY) ||
                version.compareTo(this.maxVersion) < 0 ||
                (version.compareTo(this.maxVersion) == 0 && this.maxInclusive);

        return minVersionValid && maxVersionValid;
    }

    public Class<? extends BotPlugin> pluginClass() {
        return pluginClass;
    }

    public Version minVersion() {
        return minVersion;
    }

    public Version maxVersion() {
        return maxVersion;
    }

    public boolean minInclusive() {
        return minInclusive;
    }

    public boolean maxInclusive() {
        return maxInclusive;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PluginDependency) obj;
        return Objects.equals(this.pluginClass, that.pluginClass) &&
                Objects.equals(this.minVersion, that.minVersion) &&
                Objects.equals(this.maxVersion, that.maxVersion) &&
                this.minInclusive == that.minInclusive &&
                this.maxInclusive == that.maxInclusive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pluginClass, minVersion, maxVersion, minInclusive, maxInclusive);
    }

    @Override
    public String toString() {
        return "PluginDependency[" +
                "pluginClass=" + pluginClass + ", " +
                "minVersion=" + minVersion + ", " +
                "maxVersion=" + maxVersion + ", " +
                "minInclusive=" + minInclusive + ", " +
                "maxInclusive=" + maxInclusive + ']';
    }


    public static record Version(int major, int minor, int patch) implements Comparable<Version> {
        public static final Version ANY = new Version(-1, -1, -1);
        public static final Pattern parserPattern = Pattern.compile("^(\\d+|x)\\.(\\d+|x)\\.(\\d+|x)");

        public static Version parseVersion(String versionStr) {
            if (versionStr == null || versionStr.isBlank() || versionStr.equalsIgnoreCase("any") || versionStr.equalsIgnoreCase("x.x.x")) {
                return ANY;
            }
            Matcher matcher = parserPattern.matcher(versionStr);
            if (matcher.find()) {
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
            if (this.equals(ANY)) {
                return "x.x.x";
            }
            return String.format("%d.%d.%d", this.major, this.minor, this.patch);
        }

        @Override
        public int compareTo(Version o) {
            if (this.major == o.major) {
                if (this.minor == o.minor) {
                    return Integer.compare(this.patch, o.patch);
                }
                return Integer.compare(this.minor, o.minor);
            }
            return Integer.compare(this.major, o.major);
        }
    }
}
