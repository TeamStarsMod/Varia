package xyz.article.varia.bukkit.commands.time;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.article.varia.bukkit.BukkitUtils;
import xyz.article.varia.bukkit.VariaBukkit;

public class Time implements CommandExecutor {
    private static BukkitRunnable currentTask = null;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        World world;
        if (commandSender instanceof Player) {
            world = ((Player) commandSender).getWorld();
        } else {
            world = Bukkit.getWorlds().get(0);
        }

        if (args.length < 2) {
            BukkitUtils.sendMessageWithPrefix(commandSender, "用法：/time set <时间>");
            return false;
        }

        long targetTime;
        if (args[1].equalsIgnoreCase("day")) {
            targetTime = 1000L;
        } else {
            try {
                targetTime = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                BukkitUtils.sendMessageWithPrefix(commandSender, "&c时间只能是正整数！");
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("set")) {
            // 取消当前正在运行的任务
            if (currentTask != null) {
                currentTask.cancel();
            }

            final long step = 200; // 每次变化的时间步长
            final long startTime = world.getTime();
            final long endTime = targetTime;
            final boolean increasing = endTime > startTime;

            currentTask = new BukkitRunnable() {
                @Override
                public void run() {
                    long currentTime = world.getTime();
                    if ((increasing && currentTime >= endTime) || (!increasing && currentTime <= endTime)) {
                        world.setTime(endTime);
                        currentTask = null; // 清除任务引用
                        this.cancel();
                        return;
                    }
                    world.setTime(currentTime + (increasing ? step : -step));
                }
            };
            currentTask.runTaskTimer(VariaBukkit.plugin, 0L, 1L); // 每tick运行一次

            BukkitUtils.sendMessageWithPrefix(commandSender, "已将世界 " + world.getName() + " 的时间设置为 " + targetTime);
            return true;
        } else {
            BukkitUtils.sendMessageWithPrefix(commandSender, "&c未知的设置类型！");
            return false;
        }
    }
}