package xyz.article.varia.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import xyz.article.varia.velocity.VariaVelocity;
import xyz.article.varia.velocity.VelocityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

public class VariaVelocityCommand implements SimpleCommand {
    private final Path dataDirectory;
    private final Logger logger;

    public VariaVelocityCommand(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 0) {
            invocation.source().sendMessage(
                    Component.text("Varia", NamedTextColor.AQUA)
                    .append(Component.text("Velocity", NamedTextColor.WHITE))
                            .append(Component.text(" by TeamArticle", NamedTextColor.DARK_GRAY)));
        }

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    if (invocation.source().hasPermission("varia.reload")) {
                        logger.info("正在检查/更新您的配置文件，请稍后...");
                        if (VariaVelocity.updateConfig(dataDirectory, logger))
                            logger.info("配置文件检查/更新完成");
                        else
                            logger.warn("在检查您的配置文件时出现了些许问题");

                        Yaml yaml = new Yaml();
                        try {
                            File configFile = new File(dataDirectory.toFile(), "config.yml");
                            config = yaml.load(new FileInputStream(configFile));
                        } catch (IOException e) {
                            VelocityUtils.sendMessageWithPrefix(invocation.source(), "<red>重载失败！查看你的控制台！</red>");
                            logger.error("在插件重载配置文件时出现了错误！", e);
                        }
                        VelocityUtils.sendMessageWithPrefix(invocation.source(), "<green>重载完成！</green>");
                        VelocityUtils.sendMessageWithPrefix(invocation.source(), "<yellow>某些设置可能需要重启代理生效</yellow>");
                    }else {
                        VelocityUtils.sendMessageWithPrefix(invocation.source(), "<red>需要权限：</red><yellow>varia.reload</yellow>");
                    }
                }
            }
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("varia.variavelocity");
    }
}
