package dev.serverpanel.plugin.listeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import dev.serverpanel.plugin.commands.InviteCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerSessionListener implements Listener {
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ApiClient.ApiResponse resp = plugin.getApiClient().get("/web/invites/pending?nickname=" + player.getName());
            if (resp.isSuccess()) {
                JsonArray invites = resp.data.getAsJsonArray("invites");
                if (invites != null && invites.size() > 0) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (JsonElement el : invites) {
                            JsonObject inv = el.getAsJsonObject();
                            int commId = inv.get("community_id").getAsInt();
                            String commName = inv.get("community_name").getAsString();
                            InviteCommand.showInviteMessage(player, commName, commId);
                        }
                    });
                }
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
