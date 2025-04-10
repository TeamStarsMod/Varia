package xyz.article.varia.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import xyz.article.varia.velocity.RunningDataVelocity;
import xyz.article.varia.velocity.VelocityUtils;
import xyz.article.varia.velocity.methods.AllServerChatMethods;

import java.util.List;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

public class PlayerChatEvent {
    private final ProxyServer proxyServer;
    private final Logger logger;

    public PlayerChatEvent(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    @SuppressWarnings("unchecked")
    public void onPlayerChatEvent(com.velocitypowered.api.event.player.PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (RunningDataVelocity.allChatModePlayers.contains(player)) {
            List<String> blackList = (List<String>) config.get("AllServerChatBlackList");
            if (player.getCurrentServer().isPresent()) {
                if (blackList.contains(player.getCurrentServer().get().getServerInfo().getName())) {
                    return;
                }
            } else {
                VelocityUtils.sendMessageWithPrefix(player, "<red>出错了！</red>");
                return;
            }

            AllServerChatMethods.sendMessage(event.getMessage(), proxyServer, logger, blackList, player);
            event.setResult(com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult.denied());
        }
    }
}
