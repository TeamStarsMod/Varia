package xyz.article.varia.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class VariaVelocityCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 0) {
            invocation.source().sendMessage(
                    Component.text("Varia", NamedTextColor.AQUA)
                    .append(Component.text("Velocity", NamedTextColor.WHITE))
                            .append(Component.text(" by TeamArticle", NamedTextColor.DARK_GRAY)));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("varia.variavelocity");
    }
}
