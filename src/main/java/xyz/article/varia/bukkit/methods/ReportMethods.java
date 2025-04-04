package xyz.article.varia.bukkit.methods;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.article.varia.bukkit.RunningDataBukkit;
import xyz.article.varia.bukkit.BukkitUtils;
import xyz.article.varia.bukkit.VariaBukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportMethods {
    public static void storeReport(String reportingPlayerName, String reportedPlayerName, String reportReason) {
        File dataFolder = VariaBukkit.dataFolder;
        File reportFile = new File(dataFolder, "report.yml");
        try {
            YamlConfiguration reportYaml = YamlConfiguration.loadConfiguration(reportFile);
            List<String> list;
            if (reportYaml.get(reportedPlayerName) != null)
                list = reportYaml.getStringList(reportedPlayerName);
            else
                list = new ArrayList<>();

            list.add("举报者:" + reportingPlayerName + "|被举报者:" +reportedPlayerName + "|理由:" + reportReason);
            reportYaml.set(reportedPlayerName, list);
            reportYaml.save(reportFile);
        } catch (IOException e) {
            VariaBukkit.logger.severe("无法写入报告文件：" + e.getMessage());
        }
    }

    public static void sendReportToOnlineOp(String reportingPlayerName, String reportedPlayerName, String reportReason) {
        boolean found = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("VariaBukkit.reportNotice")) {
                if (RunningDataBukkit.config.getBoolean("ReportTitle")) {
                    player.sendTitle(BukkitUtils.reColor("&e新的举报！"), BukkitUtils.reColor("&a举报者：" + reportingPlayerName + " &a被举报者：&c" + reportedPlayerName), 10, 80, 10);
                }
                player.sendMessage(BukkitUtils.reColor(VariaBukkit.prefix + "收到了一个新举报！"));
                player.sendMessage(BukkitUtils.reColor("&a举报玩家：&e" + reportingPlayerName));
                player.sendMessage(BukkitUtils.reColor("&c被举报玩家：&e" + reportedPlayerName));
                player.sendMessage(BukkitUtils.reColor("&c举报原因：&a" + reportReason));
                found = true;
                break;
            }
        }
        if (!found) {
            // 如果没有在线的可通知管理员，存储到待发送列表
            RunningDataBukkit.pendingReports.add(reportingPlayerName + "|" + reportedPlayerName + "|" + reportReason);
        }
    }

    public static void onDisable() {
        //尝试发送未发送的Report给可通知管理员
        for (String report : RunningDataBukkit.pendingReports) {
            String[] parts = report.split("\\|");
            ReportMethods.sendReportToOnlineOp(parts[0], parts[1], parts[2]);
        }
    }
}
