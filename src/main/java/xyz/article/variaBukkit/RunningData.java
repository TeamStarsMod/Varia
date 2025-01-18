package xyz.article.variaBukkit;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RunningData {
    //Config
    public static YamlConfiguration config;

    //Report
    public static final List<String> pendingReports = new ArrayList<>();

    public static void init() {
        //运行数据初始化
        config = YamlConfiguration.loadConfiguration(new File(VariaBukkit.dataFolder + "config.yml"));
    }
}
