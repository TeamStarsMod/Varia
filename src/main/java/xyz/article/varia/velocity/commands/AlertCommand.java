package xyz.article.varia.velocity.commands;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.article.varia.velocity.VelocityUtils;

import java.util.logging.Logger;

public class AlertCommand implements RawCommand {
    private final ProxyServer server;
    private final Logger logger;

    public AlertCommand(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().isEmpty()) {
            VelocityUtils.sendMessageWithPrefix(invocation.source(), "&c请输入要发送的消息！");
            return;
        }
        for (Player player : server.getAllPlayers()) {
            player.sendPlainMessage(VelocityUtils.reColor("&f[&cAlert&f] &4" + invocation.arguments()));
        }
        logger.info(VelocityUtils.reColor("&f[&cAlert&f] &4" + invocation.arguments()));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("varia.alert");
    }
}
