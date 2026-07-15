package ru.gryazworld.portal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEventListener implements Listener {

    private final PortalPlugin plugin;
    private final Map<UUID, Long> joinTimes = new HashMap<>();

    public PlayerEventListener(PortalPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        joinTimes.put(player.getUniqueId(), System.currentTimeMillis());
        String nickname = player.getName();
        String uuid = player.getUniqueId().toString();
        String server = plugin.getConfig().getString("server-name", "unknown");
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getApiClient().sendJoin(uuid, nickname, server));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Long joinedAt = joinTimes.remove(uuid);
        long sessionSeconds = joinedAt != null ? (System.currentTimeMillis() - joinedAt) / 1000L : 0L;
        String uuidStr = uuid.toString();
        String server = plugin.getConfig().getString("server-name", "unknown");
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getApiClient().sendQuit(uuidStr, sessionSeconds, server));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        String nickname = event.getEntity().getName();
        @SuppressWarnings("deprecation")
        String raw = event.getDeathMessage();
        String deathMessage = (raw != null && !raw.isEmpty()) ? raw : nickname + " умер";
        String server = plugin.getConfig().getString("server-name", "unknown");
        String uuidStr = event.getEntity().getUniqueId().toString();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getApiClient().sendDeath(uuidStr, nickname, deathMessage, server));
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        String nickname = event.getPlayer().getName();
        String message = event.getMessage();
        String server = plugin.getConfig().getString("server-name", "unknown");
        String uuidStr = event.getPlayer().getUniqueId().toString();
        plugin.getApiClient().sendChat(uuidStr, nickname, message, server);
    }
}
