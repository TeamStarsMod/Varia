package xyz.article.variaBukkit.commands.report;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.article.variaBukkit.Utils;
import xyz.article.variaBukkit.methods.ReportMethods;

public class Report implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player reportingPlayer) {
            if (args.length >= 2) {
                String reportedPlayerName = args[0];
                StringBuilder reportReason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reportReason.append(args[i]).append(" ");
                }
                // 存储举报信息到文件
                ReportMethods.storeReport(reportingPlayer.getName(), reportedPlayerName, reportReason.toString());
                // 尝试发送给在线的 OP
                ReportMethods.sendReportToOnlineOp(reportingPlayer.getName(), reportedPlayerName, reportReason.toString());
                Utils.sendMessage(sender,"&a举报已提交！");
            } else {
                Utils.sendMessage(sender, "使用方法：/report <玩家名称> <举报原因>");
            }
        } else {
            Utils.sendMessage(sender, "&c该命令只能由玩家使用！");
        }
        return true;
    }
}
