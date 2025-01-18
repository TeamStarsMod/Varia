package xyz.article.variaBukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.article.variaBukkit.RunningData;
import xyz.article.variaBukkit.Utils;
import xyz.article.variaBukkit.VariaBukkit;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VariaBukkitCommand implements CommandExecutor, TabCompleter {
    List<String> helpList = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(Utils.reColor("&bVaria&fBukkit &7 by TeamArticle"));
            return true;
        }
        switch (strings[0].toLowerCase()) {
            case "authors" -> {
                //获取plugin.yml里的作者列表
                List<String> authorsList = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(
                                Objects.requireNonNull(
                                VariaBukkitCommand.class.getClassLoader().getResourceAsStream("plugin.yml")
                                )
                        )
                ).getStringList("authors");
                Utils.sendMessage(commandSender, "全部作者：");
                for (String author : authorsList) {
                    if (author.equalsIgnoreCase("NekoEpisode")) {
                        commandSender.sendMessage(Utils.reColor("&6NekoEpisode"));
                    }else {
                        commandSender.sendMessage(author);
                    }
                }
                return true;
            }

            case "reload" -> {
                if (commandSender.hasPermission("VariaBukkit.Reload")) {
                    RunningData.config = YamlConfiguration.loadConfiguration(new File(VariaBukkit.dataFolder + "config.yml"));
                    Utils.sendMessage(commandSender, "&a配置文件重载完成！");
                    return true;
                }else {
                    Utils.sendMessage(commandSender, "&c需要权限：&eVariaBukkit.Reload");
                    return false;
                }
            }

            default -> {
                Utils.sendMessage(commandSender,"&c未知的子命令！");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of("authors");
    }
}
