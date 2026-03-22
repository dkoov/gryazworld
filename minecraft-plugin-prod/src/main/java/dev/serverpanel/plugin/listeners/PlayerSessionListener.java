/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 */
package dev.serverpanel.plugin.listeners;

import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerSessionListener
implements Listener {
    private final ServerPanelPlugin plugin;
    private final Map<UUID, Long> joinTimes = new HashMap<UUID, Long>();

    public PlayerSessionListener(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String nickname = event.getPlayer().getName();
        this.joinTimes.put(uuid, System.currentTimeMillis());
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse response = this.plugin.getApiClient().playerJoin(uuid.toString(), nickname);
            if (!response.isSuccess()) {
                this.plugin.getLogger().warning("Failed to register join for " + nickname + ": " + response.getMessage());
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        long joinedAt = this.joinTimes.remove(uuid) != null ? this.joinTimes.getOrDefault(uuid, System.currentTimeMillis()) : System.currentTimeMillis();
        long sessionMs = System.currentTimeMillis() - joinedAt;
        long sessionSeconds = sessionMs / 1000L;
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse response = this.plugin.getApiClient().playerQuit(uuid.toString(), sessionSeconds);
            if (!response.isSuccess()) {
                this.plugin.getLogger().warning("Failed to register quit for " + event.getPlayer().getName());
            }
        });
    }

    public long getJoinTime(UUID uuid) {
        return this.joinTimes.getOrDefault(uuid, System.currentTimeMillis());
    }
}

