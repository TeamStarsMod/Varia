package xyz.article.varia.bukkit;

import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.article.varia.bukkit.commands.BukkitCommandRegister;
import xyz.article.varia.bukkit.listener.BukkitListenerRegister;
import xyz.article.varia.bukkit.methods.ReportMethods;

import java.io.*;
import java.nio.file.Files;
import java.util.Set;
import java.util.logging.Logger;

public final class VariaBukkit extends JavaPlugin {
    public static PluginManager pluginManager;
    public static Server server;
    public static JavaPlugin plugin;
    public static Logger logger;
    public static File dataFolder;

    public static String prefix = "&bVaria&fBukkit &7>> ";

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        pluginManager = getServer().getPluginManager();
        server = getServer();
        plugin = this;
        logger = getLogger();
        dataFolder = getDataFolder();

        if (dataFolder.mkdirs()) logger.info("已创建数据文件夹");

        try (InputStream inputStream = getClassLoader().getResourceAsStream("bukkit/config.yml")) {
            if (inputStream != null) {
                File configFile = new File(dataFolder, "config.yml");
                if (!configFile.exists()) {
                    Files.copy(inputStream, configFile.toPath());
                }
                File defaultConfigFile = new File(dataFolder, "default_config.yml");
                defaultConfigFile.delete();
                Files.copy(inputStream, defaultConfigFile.toPath());
            } else logger.severe("内部配置文件不存在！这可能会导致很严重的问题！");
        } catch (IOException e) {
            logger.severe("在插件复制配置文件时出现了错误！" + e);
        }

        logger.info("正在检查/更新您的配置文件，请稍后...");
        if (updateConfig())
            logger.info("配置文件检查/更新完成");
        else
            logger.warning("在检查您的配置文件时出现了些许问题");

        RunningDataBukkit.config = YamlConfiguration.loadConfiguration(new File(dataFolder, "config.yml"));

        if (RunningDataBukkit.config.getString("Prefix") != null) prefix = RunningDataBukkit.config.getString("Prefix");

        BukkitListenerRegister.registerListeners(pluginManager, plugin);

        BukkitCommandRegister.registerCommands(plugin);

        logger.info(BukkitUtils.reColor(prefix + "欢迎来到VariaBukkit！"));
        logger.info(BukkitUtils.reColor(prefix + "加载耗时：" + (System.currentTimeMillis() - startTime) + "ms"));
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
            InputStream inputStream = getClassLoader().getResourceAsStream("bukkit/config.yml");
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
