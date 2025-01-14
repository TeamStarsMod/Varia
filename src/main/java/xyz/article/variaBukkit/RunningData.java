package xyz.article.variaBukkit;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class RunningData {
    public static YamlConfiguration config;

    public static void init() {
        config = YamlConfiguration.loadConfiguration(new File(VariaBukkit.dataFolder + "config.yml"));
    }
}
