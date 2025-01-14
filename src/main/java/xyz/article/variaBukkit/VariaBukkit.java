package xyz.article.variaBukkit;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.article.variaBukkit.Listener.ChatBlock;

import java.io.File;
import java.util.logging.Logger;

public final class VariaBukkit extends JavaPlugin {
    public static PluginManager pluginManager;
    public static Server server;
    public static Plugin plugin;
    public static Logger logger;
    public static File dataFolder;

    @Override
    public void onEnable() {
        pluginManager = getServer().getPluginManager();
        server = getServer();
        plugin = getPlugin(VariaBukkit.class);
        logger = getLogger();
        dataFolder = getDataFolder();
        saveDefaultConfig();

        RunningData.init();

        pluginManager.registerEvents(new ChatBlock(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
