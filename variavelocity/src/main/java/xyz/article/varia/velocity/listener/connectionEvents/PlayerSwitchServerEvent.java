package xyz.article.varia.velocity.listener.connectionEvents;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import xyz.article.varia.velocity.VariaVelocity;
import xyz.article.varia.velocity.VelocityUtils;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

public class PlayerSwitchServerEvent {
    private final ProxyServer server;

    public PlayerSwitchServerEvent(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onPlayerSwitchServerEvent(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        ServerInfo serverInfo = event.getServer().getServerInfo();

        if ((boolean) config.get("PlayerSwitchServerPrompt")) {
            String message = (String) config.get("PlayerSwitchServerPromptContent");
            if (message != null) {
                for (Player player1 : server.getAllPlayers()) {
                    player1.sendMessage(VelocityUtils.reColorMiniMessage(VariaVelocity.PREFIX + message.replace("%player%", player.getUsername().replace("%server%", serverInfo.getName()))));
                }
            }
        }
    }
}
