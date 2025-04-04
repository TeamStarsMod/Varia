package xyz.article.varia.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.yaml.snakeyaml.Yaml;
import xyz.article.varia.velocity.VelocityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class HubCommand implements SimpleCommand {
    private final File configFile;
    private final Logger logger;
    private final ProxyServer server;

    public HubCommand(File configFile, Logger logger, ProxyServer server) {
        this.configFile = configFile;
        this.logger = logger;
        this.server = server;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player player)) {
            VelocityUtils.sendMessageWithPrefix(source, "&c玩家才能使用此命令！");
            return;
        }

        if (!configFile.exists()) {
            logger.warning("配置文件不存在，这可能是个错误！");
            return;
        }

        Yaml yaml = new Yaml();
        Map<String, Object> configMap;
        try {
            configMap = yaml.load(new FileInputStream(configFile));
        } catch (Exception e) {
            logger.warning("读取配置文件时出现了错误！" + e);
            return;
        }

        if (!((boolean) configMap.get("HubSystem"))) {
            VelocityUtils.sendMessageWithPrefix(source, "&c此服务器未启用Hub系统！");
            return;
        }

        String hubServerName = (String) configMap.get("HubServerName");
        Optional<ServerConnection> playerNowServer = player.getCurrentServer();
        if (playerNowServer.isPresent()) {
            if (((ArrayList<String>) configMap.get("HubBlackList")).contains(playerNowServer.get().getServerInfo().getName())) {
                VelocityUtils.sendMessageWithPrefix(source, "&c您当前所在的服务器已在黑名单中，无法使用此命令！");
                return;
            }
        } else {
            logger.warning("玩家 " + player.getUsername() + " 未连接到任何服务器！");
            VelocityUtils.sendMessageWithPrefix(source, "&c抱歉，出现了些问题，您暂时不可以使用此命令");
            return;
        }

        RegisteredServer registeredServer = null;
        for (RegisteredServer registeredServer1 : server.getAllServers()) {
            if (registeredServer1.getServerInfo().getName().equals(hubServerName)) {
                registeredServer = registeredServer1;
                break;
            }
        }
        if (registeredServer == null) {
            VelocityUtils.sendMessageWithPrefix(source, "&c未找到名为 " + hubServerName + " 的服务器！");
            return;
        }
        player.createConnectionRequest(registeredServer).connect();
        VelocityUtils.sendMessageWithPrefix(source, "&a正在连接到 " + hubServerName + " ...");
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("varia.hub");
    }
}
