package xyz.article.variaBukkit;

import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.article.variaBukkit.commands.VariaBukkitCommand;
import xyz.article.variaBukkit.listener.ChatBlock;
import xyz.article.variaBukkit.commands.report.Report;
import xyz.article.variaBukkit.methods.ReportMethods;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public final class VariaBukkit extends JavaPlugin {
    public static PluginManager pluginManager;
    public static Server server;
    public static Plugin plugin;
    public static Logger logger;
    public static File dataFolder;

    public static String prefix = "&bVaria&fBukkit &7>> ";

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        pluginManager = getServer().getPluginManager();
        server = getServer();
        plugin = getPlugin(VariaBukkit.class);
        logger = getLogger();
        dataFolder = getDataFolder();
        saveDefaultConfig();

        logger.info("正在检查/更新您的配置文件，请稍后...");
        if (updateConfig())
            logger.info("配置文件检查/更新完成");
        else
            logger.warning("在检查您的配置文件时出现了些许问题");

        RunningData.init();

        pluginManager.registerEvents(new ChatBlock(), this);

        Objects.requireNonNull(getCommand("Report")).setExecutor(new Report());
        Objects.requireNonNull(getCommand("VariaBukkit")).setExecutor(new VariaBukkitCommand());

        Objects.requireNonNull(getCommand("Report")).setTabCompleter(new Report());

        logger.info(Utils.reColor(prefix + "欢迎来到VariaBukkit！"));
        logger.info(Utils.reColor(prefix + "加载耗时：" + (System.currentTimeMillis() - startTime) + "ms"));
    }

    @Override
    public void onDisable() {
        logger.info(prefix + "VariaBukkit正在关闭！");

        ReportMethods.onDisable();
    }

    public boolean updateConfig() {
        try {
            // 加载旧的配置文件
            YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(new File(dataFolder, "config.yml"));

            // 获取新的配置文件流
            InputStream inputStream = getClassLoader().getResourceAsStream("config.yml");
            if (inputStream == null) {
                return false;
            }

            // 创建临时文件并复制新配置文件内容
            File tempFile = File.createTempFile("config", ".yml");
            tempFile.deleteOnExit(); // 当JVM退出时删除临时文件

            // 检查临时文件是否已经存在，如果存在则删除
            if (tempFile.exists()) {
                tempFile.delete();
            }

            Files.copy(inputStream, tempFile.toPath());

            // 加载新的配置文件
            YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(tempFile);

            // 获取新配置文件中的所有键
            Set<String> newKeys = newConfig.getKeys(true);

            // 检查新键并复制到旧配置文件
            for (String key : newKeys) {
                if (!oldConfig.contains(key)) {
                    // 如果旧配置文件中没有这个键复制过去
                    oldConfig.set(key, newConfig.get(key));
                }
            }

            // 保存配置文件
            oldConfig.save(new File(dataFolder, "config.yml"));
            return true;
        } catch (IOException e) {
            logger.severe("发生错误！" + e);
            return false;
        }
    }
}
