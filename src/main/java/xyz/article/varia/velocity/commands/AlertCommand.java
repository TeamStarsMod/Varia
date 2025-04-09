package xyz.article.varia.velocity.commands;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.article.varia.velocity.VelocityUtils;

public class AlertCommand implements RawCommand {
    private final ProxyServer server;

    public AlertCommand(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.arguments().isEmpty()) {
            VelocityUtils.sendMessageWithPrefix(invocation.source(), "&c请输入要发送的消息！");
            return;
        }
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(VelocityUtils.reColorMiniMessage("<white>[</white><red>Alert</red><white>]</white> <dark_red>" + invocation.arguments() + "</dark_red>"));
        }
        server.getConsoleCommandSource().sendMessage(VelocityUtils.reColorMiniMessage("<white>[</white><red>Alert</red><white>]</white> <dark_red>" + invocation.arguments() + "</dark_red>"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("varia.alert");
    }
}
