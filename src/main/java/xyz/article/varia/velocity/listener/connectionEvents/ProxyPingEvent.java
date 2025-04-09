package xyz.article.varia.velocity.listener.connectionEvents;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import xyz.article.varia.velocity.VelocityUtils;

import java.util.*;

import static xyz.article.varia.velocity.RunningDataVelocity.config;

public class ProxyPingEvent {
    private final ProxyServer server;

    public ProxyPingEvent(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyPingEvent(com.velocitypowered.api.event.proxy.ProxyPingEvent event) {
        if (!((boolean) config.get("MotdSystem"))) return;

        ServerPing.Version version;
        ServerPing.Players players;
        Component description;

        if (config.get("MotdVersion") != null) {
            version = new ServerPing.Version(
                    event.getPing().getVersion().getProtocol(),
                    (String) config.get("MotdVersion")
            );
        } else version = event.getPing().getVersion();

        if ((boolean) config.get("MotdSample")) {
            List<ServerPing.SamplePlayer> samplePlayerList = new ArrayList<>();
            if ((boolean) config.get("RealSample")) {
                int maxLength = (int) config.get("MaxSampleLength");
                for (int i = 0; i < server.getAllPlayers().size(); i++) {
                    if (i >= maxLength) break;
                    Player player = server.getAllPlayers().stream().toList().get(i);
                    if ((!((boolean) config.get("ForceShowPlayers"))) && (player.getPlayerSettings().isClientListingAllowed())) {
                        samplePlayerList.add(new ServerPing.SamplePlayer(
                                player.getUsername(),
                                player.getUniqueId()
                        ));
                    }
                }
            } else {
                Object customSampleObj = config.get("CustomSample");
                List<String> finalSampleList = getList(customSampleObj);

                for (String sample : finalSampleList) {
                    samplePlayerList.add(new ServerPing.SamplePlayer(
                            VelocityUtils.reColorPlain(sample),
                            UUID.randomUUID()
                    ));
                }
            }

            players = new ServerPing.Players(
                    (event.getPing().getPlayers().isPresent() ? event.getPing().getPlayers().get().getOnline() : -1),
                    (event.getPing().getPlayers().isPresent() ? event.getPing().getPlayers().get().getMax() : -1),
                    samplePlayerList
            );
        } else {
            players = event.getPing().getPlayers().orElse(null);
        }

        if (config.get("MotdDescription") != null) {
            description = VelocityUtils.reColorMiniMessage((String) config.get("MotdDescription"));
        } else {
            description = event.getPing().getDescriptionComponent();
        }

        ServerPing newServerPing = new ServerPing(
                version,
                players,
                description,
                event.getPing().getFavicon().orElse(null)
        );

        event.setPing(newServerPing);
    }

    @SuppressWarnings("unchecked")
    private static @NotNull List<String> getList(Object customSampleObj) {
        List<String> finalSampleList = new ArrayList<>();

        if (customSampleObj instanceof Map) {
            Map<String, List<String>> sampleMap = (Map<String, List<String>>) customSampleObj;
            Random random = new Random();
            String randomKey = sampleMap.keySet().stream().skip(random.nextInt(sampleMap.size())).findFirst().orElse(null);
            if (randomKey != null && sampleMap.get(randomKey) != null) {
                finalSampleList = sampleMap.get(randomKey);
            }
        } else if (customSampleObj instanceof List) {
            finalSampleList = (List<String>) customSampleObj;
        }
        return finalSampleList;
    }
}
