package xyz.article.varia.bukkit;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class BukkitUtils {
    public static String reColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message).replace("§§", "§");
    }

    public static void sendMessageWithPrefix(CommandSender sender, String message) {
        sender.sendMessage(reColor(VariaBukkit.prefix + message));
    }
}
