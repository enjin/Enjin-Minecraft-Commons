package com.enjin.minecraft_commons.spigot.reflection;

import com.enjin.minecraft_commons.spigot.reflection.resolver.FieldResolver;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Minecraft {

    static final Pattern NUMERIC_VERSION_PATTERN = Pattern.compile("v([0-9])_([0-9]*)_R([0-9])");

    public static final Version VERSION;

    static {
        VERSION = Version.getVersion();
    }

    public static String getVersion() {
        return VERSION.name() + ".";
    }

    public enum Version {
        UNKNOWN(-1) {
            @Override
            public boolean matchesPackageName(String packageName) {
                return false;
            }
        },

        // Minecraft 1.7 Revisions
        v1_7_R1(10701),
        v1_7_R2(10702),
        v1_7_R3(10703),
        v1_7_R4(10704),

        // Minecraft 1.8 Revisions
        v1_8_R1(10801),
        v1_8_R2(10802),
        v1_8_R3(10803),
        v1_8_R4(10804),

        // Minecraft 1.9 Revisions
        v1_9_R1(10901),
        v1_9_R2(10902),

        // Minecraft 1.10 Revisions
        v1_10_R1(11001),

        // Minecraft 1.11 Revisions
        v1_11_R1(11101),

        // Minecraft 1.12 Revisions
        v1_12_R1(11201),

        // Minecraft 1.13 Revisions
        v1_13_R1(11301),
        v1_13_R2(11302);

        private int version;

        Version(int version) {
            this.version = version;
        }

        public int version() {
            return version;
        }

        public boolean olderThan(Version version) {
            return version() < version.version();
        }

        public boolean newerThan(Version version) {
            return version() > version.version();
        }

        public boolean sameAs(Version version) {
            return version() == version.version();
        }

        public boolean sameOrOlderThan(Version version) {
            return sameAs(version) || olderThan(version);
        }

        public boolean sameOrNewerThan(Version version) {
            return sameAs(version) || newerThan(version);
        }

        public boolean inRange(Version oldVersion, Version newVersion) {
            return (sameAs(oldVersion) || newerThan(oldVersion)) && (sameAs(newVersion) || olderThan(newVersion));
        }

        public boolean matchesPackageName(String packageName) {
            return packageName.toLowerCase().contains(name().toLowerCase());
        }

        public static Version getVersion() {
            String name           = Bukkit.getServer().getClass().getPackage().getName();
            String versionPackage = name.substring(name.lastIndexOf('.') + 1) + ".";

            for (Version version : values()) {
                if (version.matchesPackageName(versionPackage)) {
                    return version;
                }
            }

            Matcher matcher = NUMERIC_VERSION_PATTERN.matcher(versionPackage);
            while (matcher.find()) {
                if (matcher.groupCount() < 3) {
                    continue;
                }

                String majorString = matcher.group(1);
                String minorString = matcher.group(2);
                String patchString = matcher.group(2);

                if (minorString.length() == 1) {
                    minorString = "0" + minorString;
                }

                if (patchString.length() == 1) {
                    patchString = "0" + patchString;
                }

                String numVersionString = majorString + minorString + patchString;
                int    numVersion       = Integer.parseInt(numVersionString);
                String pack             = versionPackage.substring(0, versionPackage.length() - 1);

                try {
                    Field     valuesField = new FieldResolver(Version.class).resolve("$VALUES");
                    Version[] oldValues   = (Version[]) valuesField.get(null);
                    valuesField.set(null, oldValues);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }

            return UNKNOWN;
        }

        @Override
        public String toString() {
            return name() + " (" + version() + ")";
        }
    }

}
