package xyz.article.varia.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import xyz.article.varia.velocity.commands.AlertCommand;
import xyz.article.varia.velocity.commands.AllServerChatCommand;
import xyz.article.varia.velocity.commands.HubCommand;
import xyz.article.varia.velocity.commands.VariaVelocityCommand;
import xyz.article.varia.velocity.listener.connectionEvents.PlayerJoinProxyEvent;
import xyz.article.varia.velocity.listener.connectionEvents.PlayerLeaveProxyEvent;
import xyz.article.varia.velocity.listener.connectionEvents.PlayerSwitchServerEvent;
import xyz.article.varia.velocity.listener.connectionEvents.ProxyPingEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

@Plugin(id = "varia", name = "VariaVelocity", version = "1.0-SNAPSHOT", description = "Add more features to the server", authors = {"NekoEpisode"})
public class VariaVelocity {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private File configFile;

    public static String PREFIX = "&bVaria&fVelocity &7>> ";

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
                File defaultConfigFile = new File(dataDirectory.toFile(), "default_config.yml");
                defaultConfigFile.delete();
                Files.copy(inputStream, defaultConfigFile.toPath());
            } else logger.error("内部配置文件不存在！这可能会导致很严重的问题！");
        } catch (IOException e) {
            logger.error("在插件复制配置文件时出现了错误！", e);
        }

        logger.info("正在检查/更新您的配置文件，请稍后...");
        if (updateConfig(dataDirectory, logger))
            logger.info("配置文件检查/更新完成");
        else
            logger.warn("在检查您的配置文件时出现了些许问题");

        Yaml yaml = new Yaml();
        try {
            config = yaml.load(new FileInputStream(configFile));
        } catch (IOException e) {
            logger.error("读取配置文件时出现了错误！", e);
        }

        // 注册监听器
        server.getEventManager().register(this, new PlayerJoinProxyEvent(server));
        server.getEventManager().register(this, new PlayerLeaveProxyEvent(server));
        server.getEventManager().register(this, new PlayerSwitchServerEvent(server));
        server.getEventManager().register(this, new ProxyPingEvent(server));

        // 注册主命令
        CommandMeta commandMeta = server.getCommandManager().metaBuilder("variavelocity")
                .aliases("vv")
                .plugin(this)
                .build();
        server.getCommandManager().register(commandMeta, new VariaVelocityCommand(dataDirectory, logger));

        // 注册Alert命令
        if ((boolean) config.get("AlertCommand")) {
            CommandMeta alertCommandMeta = server.getCommandManager().metaBuilder("alert")
                    .plugin(this)
                    .build();
            server.getCommandManager().register(alertCommandMeta, new AlertCommand(server, logger));
        }

        // 注册Hub命令
        if ((boolean) config.get("HubSystem")) {
            CommandMeta hubCommandMeta = server.getCommandManager().metaBuilder((String) config.get("HubCommand"))
                    .plugin(this)
                    .build();
            server.getCommandManager().register(hubCommandMeta, new HubCommand(logger, server));
        }

        // 注册AllServerChat命令
        if ((boolean) config.get("AllServerChat")) {
            CommandMeta allServerChatCommandMeta = server.getCommandManager().metaBuilder((String) config.get("AllServerChatCommand"))
                    .plugin(this)
                    .build();
            server.getCommandManager().register(allServerChatCommandMeta, new AllServerChatCommand(logger, server));
        }
    }

    public static boolean updateConfig(Path dataDirectory, Logger logger) {
        try {
            // 加载旧的配置文件
            Yaml yaml = new Yaml();
            File oldConfigFile = new File(dataDirectory.toFile(), "config.yml");
            Map<String, Object> oldConfig = new HashMap<>();

            if (oldConfigFile.exists()) {
                try (FileInputStream fis = new FileInputStream(oldConfigFile)) {
                    oldConfig = yaml.load(fis);
                    if (oldConfig == null) {
                        oldConfig = new HashMap<>();
                    }
                }
            }

            // 获取新的配置文件流
            InputStream inputStream = VariaVelocity.class.getClassLoader().getResourceAsStream("velocity/config.yml");
            if (inputStream == null) {
                return false;
            }

            // 加载新的配置文件
            Map<String, Object> newConfig = yaml.load(inputStream);
            if (newConfig == null) {
                return false;
            }

            // 递归合并配置
            mergeConfigs(oldConfig, newConfig);

            // 保存配置文件
            try (FileWriter writer = new FileWriter(oldConfigFile)) {
                yaml.dump(oldConfig, writer);
            }
            return true;
        } catch (IOException e) {
            logger.error("发生错误！", e);
            return false;
        }
    }

    // 递归合并两个配置Map
    private static void mergeConfigs(Map<String, Object> oldConfig, Map<String, Object> newConfig) {
        for (Map.Entry<String, Object> entry : newConfig.entrySet()) {
            String key = entry.getKey();
            Object newValue = entry.getValue();

            if (!oldConfig.containsKey(key)) {
                // 如果旧配置中没有这个键，直接添加
                oldConfig.put(key, newValue);
            } else {
                // 如果旧配置中有这个键，且两个值都是Map，则递归合并
                Object oldValue = oldConfig.get(key);
                if (oldValue instanceof Map && newValue instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> oldSubMap = (Map<String, Object>) oldValue;
                    @SuppressWarnings("unchecked")
                    Map<String, Object> newSubMap = (Map<String, Object>) newValue;
                    mergeConfigs(oldSubMap, newSubMap);
                }
            }
        }
    }
}
