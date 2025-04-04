package xyz.article.varia.bukkit.listener.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import xyz.article.varia.bukkit.RunningDataBukkit;
import xyz.article.varia.bukkit.VariaBukkit;

import java.util.List;

public class ChatBlock implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (RunningDataBukkit.config.getBoolean("BlockWord")) {
            String message = e.getMessage();
            List<String> blockWords = RunningDataBukkit.config.getStringList("BlockWords");
            String mode = RunningDataBukkit.config.getString("BlockWordMode");
            if (mode == null) mode = "Replace";
            for (String blockWord : blockWords) {
                if (message.contains(blockWord)) {
                    if (mode.equalsIgnoreCase("Replace")) {
                        message = message.replace(blockWord, "*".repeat(blockWord.length()));
                    } else if (mode.equalsIgnoreCase("Cancel")){
                        e.setCancelled(true);
                        return;
                    }else {
                        VariaBukkit.logger.warning("未知的屏蔽词类型！请检查配置文件！");
                        return;
                    }
                }
            }
            e.setMessage(message);
        }
    }
}
