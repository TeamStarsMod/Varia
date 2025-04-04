package xyz.article.varia.velocity.listener.connectionEvents;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.yaml.snakeyaml.Yaml;
import xyz.article.varia.velocity.VariaVelocity;
import xyz.article.varia.velocity.VelocityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerJoinProxyEvent {
    private final ProxyServer server;
    private final File config;
    private final Logger logger;

    public PlayerJoinProxyEvent(ProxyServer server, File config, Logger logger) {
        this.server = server;
        this.config = config;
        this.logger = logger;
    }

    @Subscribe
    public void onPlayerJoinProxyEvent(LoginEvent event) {
        Player player = event.getPlayer();

        if (!config.exists()) {
            logger.warning("未找到配置文件，这可能是个错误！");
            return;
        }

        // Velocity没有提供配置文件读取类...
        Yaml yaml = new Yaml();
        Map<String, Object> configMap;
        try {
            configMap = yaml.load(new FileInputStream(config));
        } catch (IOException e) {
            logger.warning("读取配置文件时出现了错误！" + e);
            return;
        }

        if ((boolean) configMap.get("PlayerJoinProxyPrompt")) {
            String message = (String) configMap.get("PlayerJoinProxyPromptContent");
            if (message != null) {
                for (Player player1 : server.getAllPlayers()) {
                    player1.sendPlainMessage(VelocityUtils.reColor(VariaVelocity.prefix + message.replace("%player%", player.getUsername())));
                }
            }
        }
    }
}
