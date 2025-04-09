package xyz.article.varia.velocity.listener.connectionEvents;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.article.varia.velocity.VariaVelocity;
import xyz.article.varia.velocity.VelocityUtils;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

public class PlayerLeaveProxyEvent {
    private final ProxyServer server;

    public PlayerLeaveProxyEvent(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onPlayerLeaveProxyEvent(DisconnectEvent event) {
        Player player = event.getPlayer();

        if ((boolean) config.get("PlayerLeaveProxyPrompt")) {
            String message = (String) config.get("PlayerLeaveProxyPromptContent");
            if (message != null) {
                for (Player player1 : server.getAllPlayers()) {
                    player1.sendMessage(VelocityUtils.reColorMiniMessage(VariaVelocity.PREFIX + message.replace("%player%", player.getUsername())));
                }
            }
        }
    }
}
