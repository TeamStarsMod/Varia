package xyz.article.varia.velocity.commands;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;
import xyz.article.varia.velocity.VelocityUtils;

import java.util.List;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

public class AllServerChatCommand implements RawCommand {
    private final Logger logger;
    private final ProxyServer proxyServer;

    public AllServerChatCommand(Logger logger, ProxyServer proxyServer) {
        this.logger = logger;
        this.proxyServer = proxyServer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            VelocityUtils.sendMessageWithPrefix(invocation.source(), "&c玩家才能使用此命令！");
            return;
        }

        List<String> blackList = (List<String>) config.get("AllServerChatBlackList");
        if (player.getCurrentServer().isPresent()) {
            if (blackList.contains(player.getCurrentServer().get().getServerInfo().getName())) {
                VelocityUtils.sendMessageWithPrefix(invocation.source(), "&c抱歉，您不能在此服务器使用全服聊天！");
                return;
            }
        }

        if (invocation.arguments().isEmpty()) {
            VelocityUtils.sendMessageWithPrefix(invocation.source(), "&c请输入要发送的消息！");
            return;
        }

        String message = (String) config.get("AllServerChatFormat");
        if (message == null) {
            message = "&7[&a全服&7]&r %player% &8»&r %message%";
            logger.warn("全服聊天格式未配置，已使用默认格式");
        }

        message = message
                .replace("%player%", player.getUsername())
                .replace("%message%", invocation.arguments())
                .replace("%server%", (player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : "Unknown"));

        for (RegisteredServer server : proxyServer.getAllServers()) {
            if (blackList.contains(server.getServerInfo().getName())) {
                continue;
            }

            for (Player player1 : server.getPlayersConnected()) {
                player1.sendPlainMessage(VelocityUtils.reColor(message));
            }
        }

        logger.info(VelocityUtils.reColor(message));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("varia.allserverchat");
    }
}
