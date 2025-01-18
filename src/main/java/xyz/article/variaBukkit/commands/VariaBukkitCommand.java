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
    List<String> helpList = getHelpList();
    int pageSize = 6;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(Utils.reColor("&bVaria&fBukkit &7 by TeamArticle (使用/vb authors来查看所有作者)"));
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

            case "help" -> {
                if (strings.length == 1) {
                    sendHelpPage(commandSender, 1);
                    return true;
                }
                int page;
                try {
                    page = Integer.parseInt(strings[1]);
                }catch (NumberFormatException e) {
                    Utils.sendMessage(commandSender, "&c页数只能是整数！");
                    return false;
                }
                if (!(page > 0)) {
                    Utils.sendMessage(commandSender, "&c页数只能是正数！");
                    return false;
                }
                sendHelpPage(commandSender, page);
                return true;
            }

            default -> {
                Utils.sendMessage(commandSender,"&c未知的子命令！");
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of("authors", "reload", "help");
    }

    private void sendHelpPage(CommandSender sender, int page) {
        int totalPages = (int) Math.ceil((double) helpList.size() / pageSize);
        if (page > totalPages) {
            Utils.sendMessage(sender, "&c没有这一页！");
            return;
        }

        int start = (page - 1) * pageSize;
        int end = Math.min(page * pageSize, helpList.size());

        for (int i = start; i < end; i++) {
            sender.sendMessage(helpList.get(i));
        }
        sender.sendMessage("---- 帮助信息 第 " + page + " 页 / 共 " + totalPages + " 页 ----");
    }

    public List<String> getHelpList() {
        List<String> list = new ArrayList<>();
        list.add("/vb authors : 查看VariaBukkit的所有作者");
        list.add("/vb reload : 重载配置文件");
        list.add("/vb help : 查看帮助(你正在看的)");
        list.add("/report <被举报者名称> <理由> : 举报一位玩家");
        return list;
    }
}
