package xyz.article.varia.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

import static xyz.article.varia.bukkit.VariaBukkit.logger;

@SuppressWarnings("unchecked")
public class CommandRegisterManager {
    private static CommandMap commandMap;
    private volatile static Map<String, Command> knownCommands;

    static {
        initializeCommandMap();
    }

    private static void initializeCommandMap() {
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            }

            if (commandMap instanceof SimpleCommandMap) {
                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            }
        } catch (Exception e) {
            logger.severe("初始化CommandRegister时发生错误: " + e.getMessage());
        }
    }

    /**
     * 注册带自动补全的命令
     * @param plugin 插件实例
     * @param command 命令名称
     * @param executor 命令执行器
     * @param tabCompleter 命令补全器
     * @param aliases 命令别名
     */
    public static void registerCommand(Plugin plugin, String command, CommandExecutor executor,
                                       TabCompleter tabCompleter, String... aliases) {
        try {
            Command wrappedCmd = createCommandWrapper(command, executor, tabCompleter, aliases);
            registerCommandInternal(plugin, command, wrappedCmd, aliases);
        } catch (Exception e) {
            logger.severe("注册命令 " + command + " 时发生错误: " + e.getMessage());
        }
    }

    /**
     * 注册基本命令
     * @param plugin 插件实例
     * @param command 命令名称
     * @param executor 命令执行器
     * @param aliases 命令别名
     */
    public static void registerCommand(Plugin plugin, String command,
                                       CommandExecutor executor, String... aliases) {
        registerCommand(plugin, command, executor, null, aliases);
    }

    private static Command createCommandWrapper(String command, CommandExecutor executor,
                                                TabCompleter tabCompleter, String[] aliases) {
        return new Command(command) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
                return executor.onCommand(sender, this, label, args);
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                                     @NotNull String[] args) throws IllegalArgumentException {
                if (tabCompleter != null) {
                    List<String> completions = tabCompleter.onTabComplete(sender, this, alias, args);
                    return completions != null ? completions : super.tabComplete(sender, alias, args);
                }
                return super.tabComplete(sender, alias, args);
            }
        };
    }

    private static void registerCommandInternal(Plugin plugin, String command,
                                                Command cmd, String[] aliases) {
        // 设置别名
        if (aliases != null && aliases.length > 0) {
            cmd.setAliases(Arrays.asList(aliases));
        }

        // 注册到CommandMap
        commandMap.register(plugin.getName(), cmd);

        // 添加到knownCommands
        knownCommands.put(plugin.getName().toLowerCase() + ":" + command.toLowerCase(), cmd);
        knownCommands.put(command.toLowerCase(), cmd);

        if (aliases != null) {
            for (String alias : aliases) {
                knownCommands.put(plugin.getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
                knownCommands.put(alias.toLowerCase(), cmd);
            }
        }
    }

    /**
     * 取消注册命令
     * @param command 要移除的命令名称
     */
    public static void unregisterCommand(String command) {
        try {
            // 创建命令名称的变体
            String[] keys = {
                    command.toLowerCase(),
                    "minecraft:" + command.toLowerCase(),
                    "bukkit:" + command.toLowerCase()
            };

            // 从knownCommands中移除
            for (String key : keys) {
                if (knownCommands.containsKey(key)) {
                    Command cmd = knownCommands.remove(key);

                    // 同时移除所有别名
                    if (cmd != null && !cmd.getAliases().isEmpty()) {
                        for (String alias : cmd.getAliases()) {
                            knownCommands.remove(alias.toLowerCase());
                            knownCommands.remove("minecraft:" + alias.toLowerCase());
                            knownCommands.remove("bukkit:" + alias.toLowerCase());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("取消注册命令 " + command + " 时发生错误: " + e.getMessage());
        }
    }
}