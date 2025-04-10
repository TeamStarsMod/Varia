package xyz.article.varia.bukkit.commands.time;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.article.varia.bukkit.BukkitUtils;
import xyz.article.varia.bukkit.VariaBukkit;

import java.util.List;

import static xyz.article.varia.bukkit.RunningDataBukkit.config;

public class Time implements CommandExecutor, TabCompleter {
    private static BukkitRunnable currentTask = null;

    // 时间常量
    private static final long DAY = 1000L;
    private static final long NOON = 6000L;
    private static final long SUNSET = 12000L;
    private static final long NIGHT = 13000L;
    private static final long MIDNIGHT = 18000L;
    private static final long SUNRISE = 23000L;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        World world;
        if (commandSender instanceof Player) {
            world = ((Player) commandSender).getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }

        if (args.length < 2) {
            sendUsage(commandSender);
            return false;
        }

        long targetTime = parseTimeInput(args[1]);
        if (targetTime < 0) {
            BukkitUtils.sendMessageWithPrefix(commandSender, "&c无效的时间值！");
            sendUsage(commandSender);
            return false;
        }

        if (args[0].equalsIgnoreCase("set")) {
            setWorldTime(world, targetTime, commandSender);
            return true;
        } else {
            BukkitUtils.sendMessageWithPrefix(commandSender, "&c未知的设置类型！");
            sendUsage(commandSender);
            return false;
        }
    }

    private void sendUsage(CommandSender sender) {
        BukkitUtils.sendMessageWithPrefix(sender, "用法：/time set <时间|day|noon|sunset|night|midnight|sunrise>");
    }

    private long parseTimeInput(String input) {
        // 处理预定义时间词
        switch (input.toLowerCase()) {
            case "day": return DAY;
            case "noon": return NOON;
            case "sunset": return SUNSET;
            case "night": return NIGHT;
            case "midnight": return MIDNIGHT;
            case "sunrise": return SUNRISE;
            default:
                try {
                    long time = Long.parseLong(input);
                    // 确保时间在0-23999之间
                    return Math.max(0, Math.min(time, 23999));
                } catch (NumberFormatException e) {
                    return -1; // 表示无效输入
                }
        }
    }

    private void setWorldTime(World world, long targetTime, CommandSender sender) {
        // 取消当前正在运行的任务
        if (currentTask != null) {
            currentTask.cancel();
        }

        final long step = config.getInt("TimeStep"); // 每次变化的时间步长
        final long startTime = world.getTime();

        // 计算最短路径 (考虑时间的循环性质)
        long directDistance = Math.abs(targetTime - startTime);
        long wrapAroundDistance = 24000 - directDistance;
        final boolean increasing = (directDistance <= wrapAroundDistance) ?
                (targetTime > startTime) :
                (targetTime < startTime);

        currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = world.getTime();

                // 检查是否到达目标时间 (考虑循环)
                if (Math.abs(currentTime - targetTime) <= step ||
                        Math.abs(currentTime - targetTime + 24000) <= step ||
                        Math.abs(currentTime - targetTime - 24000) <= step) {
                    world.setTime(targetTime);
                    currentTask = null; // 清除任务引用
                    this.cancel();
                    return;
                }

                // 计算下一时间点
                long nextTime = currentTime + (increasing ? step : -step);
                // 处理时间循环
                if (nextTime >= 24000) nextTime -= 24000;
                if (nextTime < 0) nextTime += 24000;

                world.setTime(nextTime);
            }
        };
        currentTask.runTaskTimer(VariaBukkit.plugin, 0L, 1L); // 每tick运行一次

        BukkitUtils.sendMessageWithPrefix(sender, "已将世界 " + world.getName() + " 的时间设置为 " + targetTime);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return switch (strings.length) {
            case 1 -> List.of("set");
            case 2 -> List.of("day", "noon", "sunset", "night", "midnight", "sunrise");
            default -> List.of();
        };
    }
}