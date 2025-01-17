package xyz.article.variaBukkit.Listener.report;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class report extends JavaPlugin implements CommandExecutor {
    private List<String> pendingReports = new ArrayList<>();

    @Override
    public void onEnable() {
        getCommand("report").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player reportingPlayer = (Player) sender;
            if (args.length >= 2) {
                String reportedPlayerName = args[0];
                StringBuilder reportReason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reportReason.append(args[i]).append(" ");
                }
                // 存储举报信息到文件
                storeReport(reportingPlayer.getName(), reportedPlayerName, reportReason.toString());
                // 尝试发送给在线的 OP
                sendReportToOnlineOp(reportingPlayer.getName(), reportedPlayerName, reportReason.toString());
                reportingPlayer.sendMessage("举报已提交，我们会尽快处理。");
            } else {
                reportingPlayer.sendMessage("使用方法：/report <玩家名称> <举报原因>");
            }
        } else {
            sender.sendMessage("该命令只能由玩家使用。");
        }
        return true;
    }

    private void storeReport(String reportingPlayerName, String reportedPlayerName, String reportReason) {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                getLogger().severe("无法创建插件数据目录。");
                return;
            }
        }
        File reportFile = new File(dataFolder, "report.yml");
        try (FileWriter writer = new FileWriter(reportFile, true)) {
            writer.write("ReportingPlayer: " + reportingPlayerName + "\n");
            writer.write("ReportedPlayer: " + reportedPlayerName + "\n");
            writer.write("Reason: " + reportReason + "\n");
            writer.write("---\n");
        } catch (IOException e) {
            getLogger().severe("无法写入报告文件：" + e.getMessage());
        }
    }

    private void sendReportToOnlineOp(String reportingPlayerName, String reportedPlayerName, String reportReason) {
        boolean opFound = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage("新举报：");
                player.sendMessage("举报玩家：" + reportingPlayerName);
                player.sendMessage("被举报玩家：" + reportedPlayerName);
                player.sendMessage("举报原因：" + reportReason);
                opFound = true;
                break;
            }
        }
        if (!opFound) {
            // 如果没有在线的 OP，存储到待发送列表
            pendingReports.add(reportingPlayerName + "|" + reportedPlayerName + "|" + reportReason);
        }
    }

    @Override
    public void onDisable() {
        // 当插件关闭时，尝试发送未发送的报告给可能在线的 OP
        for (String report : pendingReports) {
            String[] parts = report.split("\\|");
            sendReportToOnlineOp(parts[0], parts[1], parts[2]);
        }
    }
}
