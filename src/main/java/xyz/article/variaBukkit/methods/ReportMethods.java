package xyz.article.variaBukkit.methods;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.article.variaBukkit.RunningData;
import xyz.article.variaBukkit.Utils;
import xyz.article.variaBukkit.VariaBukkit;

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
        boolean opFound = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("VariaBukkit.reportNotice")) {
                if (RunningData.config.getBoolean("ReportTitle")) {
                    player.sendTitle(Utils.reColor("&e新的举报！"), Utils.reColor("&a举报者：" + reportingPlayerName + " &a被举报者：&c" + reportedPlayerName), 10, 80, 10);
                }
                player.sendMessage(VariaBukkit.prefix + "收到了一个新举报！");
                player.sendMessage(Utils.reColor("&a举报玩家：&e" + reportingPlayerName));
                player.sendMessage(Utils.reColor("&c被举报玩家：&e" + reportedPlayerName));
                player.sendMessage("&c举报原因：&a" + reportReason);
                opFound = true;
                break;
            }
        }
        if (!opFound) {
            // 如果没有在线的 OP，存储到待发送列表
            RunningData.pendingReports.add(reportingPlayerName + "|" + reportedPlayerName + "|" + reportReason);
        }
    }

    public static void onDisable() {
        //尝试发送未发送的Report给OP
        for (String report : RunningData.pendingReports) {
            String[] parts = report.split("\\|");
            ReportMethods.sendReportToOnlineOp(parts[0], parts[1], parts[2]);
        }
    }
}
