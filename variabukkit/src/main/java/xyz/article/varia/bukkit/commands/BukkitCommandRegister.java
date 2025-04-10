package xyz.article.varia.bukkit.commands;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.article.varia.bukkit.RunningDataBukkit;
import xyz.article.varia.bukkit.commands.report.Report;
import xyz.article.varia.bukkit.commands.time.Time;

public class BukkitCommandRegister {
    public static void registerCommands(JavaPlugin plugin) {
        if (RunningDataBukkit.config.getBoolean("VariaTime")) CommandRegisterManager.registerCommand(plugin, "Time", new Time(), new Time());
        CommandRegisterManager.registerCommand(plugin, "Report", new Report(), new Report());
        CommandRegisterManager.registerCommand(plugin, "VariaBukkit", new VariaBukkitCommand(), "vb");
    }
}
