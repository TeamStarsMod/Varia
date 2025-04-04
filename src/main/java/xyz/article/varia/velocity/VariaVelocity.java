package xyz.article.varia.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.article.varia.velocity.commands.AlertCommand;
import xyz.article.varia.velocity.commands.HubCommand;
import xyz.article.varia.velocity.commands.VariaVelocityCommand;
import xyz.article.varia.velocity.listener.connectionEvents.PlayerJoinProxyEvent;
import xyz.article.varia.velocity.listener.connectionEvents.PlayerLeaveProxyEvent;
import xyz.article.varia.velocity.listener.connectionEvents.PlayerSwitchServerEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "varia", name = "VariaVelocity", version = "1.0-SNAPSHOT", description = "Add more features to the server", authors = {"NekoEpisode"})
public class VariaVelocity {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private File configFile;

    public static String prefix = "&bVaria&fVelocity &7>> ";

    @Inject
    public VariaVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        // 请勿在此处注册内容
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 在这里注册内容
        File configDir = new File(dataDirectory.toUri());
        if (configDir.mkdirs()) logger.info("已创建数据文件夹");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("velocity/config.yml")) {
            if (inputStream != null) {
                configFile = new File(dataDirectory.toFile(), "config.yml");
                if (!configFile.exists()) {
                    Files.copy(inputStream, configFile.toPath());
                }
            } else logger.severe("内部配置文件不存在！这可能会导致很严重的问题！");
        } catch (IOException e) {
            logger.severe("在插件复制配置文件时出现了错误！" + e);
        }
        logger.info("Velocity不支持自动更新配置文件！");

        // 注册监听器
        server.getEventManager().register(this, new PlayerJoinProxyEvent(server, configFile, logger));
        server.getEventManager().register(this, new PlayerLeaveProxyEvent(server, configFile, logger));
        server.getEventManager().register(this, new PlayerSwitchServerEvent(server, configFile, logger));

        // 注册主命令
        CommandMeta commandMeta = server.getCommandManager().metaBuilder("variavelocity")
                .aliases("vv")
                .plugin(this)
                .build();
        server.getCommandManager().register(commandMeta, new VariaVelocityCommand());

        // 注册Alert命令
        CommandMeta alertCommandMeta = server.getCommandManager().metaBuilder("alert")
                .plugin(this)
                .build();
        server.getCommandManager().register(alertCommandMeta, new AlertCommand(server, logger));

        // 注册Hub命令
        CommandMeta hubCommandMeta = server.getCommandManager().metaBuilder("hub")
               .plugin(this)
               .build();
        server.getCommandManager().register(hubCommandMeta, new HubCommand(configFile, logger, server));
    }
}
