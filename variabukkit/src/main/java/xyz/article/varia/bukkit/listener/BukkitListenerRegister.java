package xyz.article.varia.bukkit.listener;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.article.varia.bukkit.listener.chat.ChatBlock;

public class BukkitListenerRegister {
    public static void registerListeners(PluginManager pluginManager, JavaPlugin instance) {
        pluginManager.registerEvents(new ChatBlock(), instance);
    }
}
