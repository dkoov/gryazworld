package ru.gryazworld.portal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerEventListener implements Listener {

    private final PortalPlugin plugin;

    public PlayerEventListener(PortalPlugin plugin) {
        this.plugin = plugin;
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
