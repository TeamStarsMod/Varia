package xyz.article.variaBukkit;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RunningData {
    public static YamlConfiguration config;

    //Report
    public static final List<String> pendingReports = new ArrayList<>();

    public static void init() {
        config = YamlConfiguration.loadConfiguration(new File(VariaBukkit.dataFolder + "config.yml"));
    }
}
