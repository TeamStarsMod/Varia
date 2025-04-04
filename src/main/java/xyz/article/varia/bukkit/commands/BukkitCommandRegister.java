package xyz.article.varia.bukkit.commands;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.article.varia.bukkit.commands.report.Report;
import xyz.article.varia.bukkit.commands.time.Time;

import java.util.Objects;

public class BukkitCommandRegister {
    public static void registerCommands(JavaPlugin plugin) {
        Objects.requireNonNull(plugin.getCommand("Report")).setExecutor(new Report());
        Objects.requireNonNull(plugin.getCommand("VariaBukkit")).setExecutor(new VariaBukkitCommand());
        Objects.requireNonNull(plugin.getCommand("Time")).setExecutor(new Time());
    }

    public static void registerTabCompleters(JavaPlugin plugin) {
        Objects.requireNonNull(plugin.getCommand("Report")).setTabCompleter(new Report());
    }
}
