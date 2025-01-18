package xyz.article.variaBukkit.methods;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.article.variaBukkit.RunningData;
import xyz.article.variaBukkit.VariaBukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportMethods {
    public static void storeReport(String reportingPlayerName, String reportedPlayerName, String reportReason) {
        File dataFolder = VariaBukkit.dataFolder;
        File reportFile = new File(dataFolder, "report.yml");
        try (FileWriter writer = new FileWriter(reportFile, true)) {
            writer.write("ReportingPlayer: " + reportingPlayerName + "\n");
            writer.write("ReportedPlayer: " + reportedPlayerName + "\n");
            writer.write("Reason: " + reportReason + "\n");
            writer.write("---\n");
        } catch (IOException e) {
            VariaBukkit.logger.severe("无法写入报告文件：" + e.getMessage());
        }
    }

    public static void sendReportToOnlineOp(String reportingPlayerName, String reportedPlayerName, String reportReason) {
        boolean opFound = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("VariaBukkit.reportNotice")) {
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
