package xyz.article.varia.velocity.commands;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import xyz.article.varia.velocity.RunningDataVelocity;
import xyz.article.varia.velocity.VelocityUtils;
import xyz.article.varia.velocity.methods.AllServerChatMethods;

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
            VelocityUtils.sendMessageWithPrefix(invocation.source(), "<red>玩家才能使用此命令！</red>");
            return;
        }

        if (invocation.arguments().isEmpty()) {
            boolean isInList = RunningDataVelocity.allChatModePlayers.contains(player);
            if (isInList)
                RunningDataVelocity.allChatModePlayers.remove(player);
            else
                RunningDataVelocity.allChatModePlayers.add(player);
            VelocityUtils.sendMessageWithPrefix(invocation.source(), "<green>已" + (isInList ? "退出" : "进入") + "全服聊天模式</green>");
            return;
        }

        List<String> blackList = (List<String>) config.get("AllServerChatBlackList");
        if (player.getCurrentServer().isPresent()) {
            if (blackList.contains(player.getCurrentServer().get().getServerInfo().getName())) {
                VelocityUtils.sendMessageWithPrefix(invocation.source(), "<red>抱歉，您不能在此服务器使用全服聊天！</red>");
                return;
            }
        } else {
            VelocityUtils.sendMessageWithPrefix(invocation.source(), "<red>出错了！</red>");
            return;
        }

        AllServerChatMethods.sendMessage(invocation.arguments(), proxyServer, logger, blackList, player);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("varia.allserverchat");
    }
}
