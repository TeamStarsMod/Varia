package xyz.article.variaBukkit;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.article.variaBukkit.Commands.VariaBukkitCommand;
import xyz.article.variaBukkit.Listener.ChatBlock;
import xyz.article.variaBukkit.Listener.report.report;

import java.io.File;
import java.util.Objects;
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

        RunningData.init();

        pluginManager.registerEvents(new ChatBlock(), this);
        getCommand("report").setExecutor(new report());

        Objects.requireNonNull(getCommand("VariaBukkit")).setExecutor(new VariaBukkitCommand());

        logger.info(Utils.reColor(prefix + "欢迎来到VariaBukkit！"));
        logger.info(Utils.reColor(prefix + "加载耗时：" + (System.currentTimeMillis() - startTime) + "ms"));
    }

    @Override
    public void onDisable() {
        logger.info(prefix + "VariaBukkit正在关闭！");
    }
}
