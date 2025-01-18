package xyz.article.variaBukkit;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {
    public static String reColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message).replace("§§", "§");
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(reColor(VariaBukkit.prefix + message));
    }
}
