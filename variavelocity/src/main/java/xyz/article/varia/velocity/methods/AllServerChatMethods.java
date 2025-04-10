package xyz.article.varia.velocity.methods;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;
import xyz.article.varia.velocity.VelocityUtils;

import java.util.List;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

public class AllServerChatMethods {
    public static void sendMessage(String message1, ProxyServer proxyServer, Logger logger, List<String> blackList, Player player) {
        String message = (String) config.get("AllServerChatFormat");
        if (message == null) {
            message = "<yellow>[</yellow><gold>全服</gold><yellow>] %player%</yellow> <gold>»</gold><white> %message%</white>";
            logger.warn("全服聊天格式未配置，已使用默认格式");
        }

        String playerMessage;
        if ((boolean) config.get("AllServerChatDisableMiniMessage")) {
            if (player.hasPermission("varia.useMiniMessage"))
                playerMessage = message1;
            else
                playerMessage = MiniMessage.miniMessage().escapeTags(message1);
        } else {
            playerMessage = message1;
        }

        message = message
                .replace("%player%", player.getUsername())
                .replace("%message%", playerMessage)
                .replace("%server%", (player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : "Unknown"));

        for (RegisteredServer server : proxyServer.getAllServers()) {
            if (blackList.contains(server.getServerInfo().getName())) {
                continue;
            }

            for (Player player1 : server.getPlayersConnected()) {
                player1.sendMessage(VelocityUtils.reColorMiniMessage(message));
            }
        }

        proxyServer.getConsoleCommandSource().sendMessage(VelocityUtils.reColorMiniMessage(message));
    }
}
