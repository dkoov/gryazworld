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
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerSessionListener implements Listener {
    private final ServerPanelPlugin plugin;
    private final Map<UUID, Long> joinTimes = new HashMap<>();
    // uuid -> token для поллинга статуса
    private final Map<String, String> pendingTokens = new ConcurrentHashMap<>();

    public PlayerSessionListener(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        String nickname = event.getPlayer().getName();
        String uuid = event.getPlayer().getUniqueId().toString();
        String ip = event.getAddress().getHostAddress();

        // Проверка вайтлиста
        ApiClient.ApiResponse discordResp = plugin.getApiClient().get("/mc/player/discord-id?nickname=" + nickname);
        if (!discordResp.isSuccess() || discordResp.data == null
                || !discordResp.data.has("discord_id")
                || discordResp.data.get("discord_id").isJsonNull()) {
            event.disallow(
                PlayerLoginEvent.Result.KICK_WHITELIST,
                "§cВы не зарегистрированы на сайте!\n§fЗарегистрируйтесь на §bgryazworld.ru"
            );
            return;
        }

        // Проверка IP
        String body = "{\"uuid\":\"" + uuid + "\",\"ip\":\"" + ip + "\",\"nickname\":\"" + nickname + "\"}";
        ApiClient.ApiResponse ipResp = plugin.getApiClient().post("/mc/player/check-ip", body);
        if (ipResp.isSuccess() && ipResp.data != null) {
            boolean allowed = ipResp.data.has("allowed") && ipResp.data.get("allowed").getAsBoolean();
            if (!allowed) {
                String reason = ipResp.data.has("reason") ? ipResp.data.get("reason").getAsString() : "";
                if ("new_ip".equals(reason)) {
                    String token = ipResp.data.has("token") ? ipResp.data.get("token").getAsString() : "";
                    plugin.getPendingFreezeUUIDs().add(uuid);
                    if (!token.isEmpty()) pendingTokens.put(uuid, token);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String uuidStr = uuid.toString();
        String nickname = player.getName();
        this.joinTimes.put(uuid, System.currentTimeMillis());

        // Заморозка при новом IP
        if (plugin.getPendingFreezeUUIDs().remove(uuidStr)) {
            plugin.getFrozenPlayers().add(uuidStr);
            player.sendMessage(plugin.prefix() + "§eПодтвердите вход в Discord! Вы заморожены до подтверждения.");
            player.sendMessage(plugin.prefix() + "§7Проверьте личные сообщения от бота GryazAlert.");
            // Запускаем поллинг статуса каждые 3 секунды
            String token = pendingTokens.get(uuidStr);
            if (token != null) {
                startAuthPolling(player, uuidStr, token);
            }
        }

        String serverName = plugin.getConfig().getString("server-name", "unknown");
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin) this.plugin, () -> {
            ApiClient.ApiResponse response = this.plugin.getApiClient().playerJoin(uuidStr, nickname, serverName);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        if (plugin.getFrozenPlayers().contains(event.getPlayer().getUniqueId().toString())) {
            // Разрешаем поворот головы, блокируем перемещение
            if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                    || event.getFrom().getBlockY() != event.getTo().getBlockY()
                    || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        if (plugin.getFrozenPlayers().contains(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.prefix() + "§cПодтвердите вход в Discord чтобы использовать команды!");
        }
    }

    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (plugin.getFrozenPlayers().contains(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        if (plugin.getFrozenPlayers().contains(event.getPlayer().getUniqueId().toString())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.prefix() + "§cПодтвердите вход в Discord!");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String uuidStr = uuid.toString();
        plugin.getFrozenPlayers().remove(uuidStr);
        pendingTokens.remove(uuidStr);
        Long joinedAt = this.joinTimes.remove(uuid);
        long sessionSeconds = joinedAt != null ? (System.currentTimeMillis() - joinedAt) / 1000 : 0;
        String serverName = plugin.getConfig().getString("server-name", "unknown");
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin) this.plugin, () -> {
            ApiClient.ApiResponse response = this.plugin.getApiClient().playerQuit(uuidStr, sessionSeconds, serverName);
            if (!response.isSuccess()) {
                this.plugin.getLogger().warning("Failed to register quit for " + event.getPlayer().getName());
            }
        });
    }

    private void startAuthPolling(Player player, String uuidStr, String token) {
        // Поллинг каждые 60 тиков (3 секунды), максимум 40 итераций (2 минуты)
        int[] attempts = {0};
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task -> {
            attempts[0]++;
            if (attempts[0] > 40 || !player.isOnline()) {
                task.cancel();
                pendingTokens.remove(uuidStr);
                if (player.isOnline()) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.getFrozenPlayers().remove(uuidStr);
                        if (plugin.getFrozenPlayers().remove(uuidStr) || true) {
                            player.kickPlayer("§cВремя подтверждения истекло. Попробуйте снова.");
                        }
                    });
                }
                return;
            }

            ApiClient.ApiResponse resp = plugin.getApiClient().authStatus(token);
            if (!resp.isSuccess()) return;

            String status = resp.data.has("status") ? resp.data.get("status").getAsString() : "";
            if ("confirmed".equals(status)) {
                task.cancel();
                pendingTokens.remove(uuidStr);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getFrozenPlayers().remove(uuidStr);
                    player.sendMessage(plugin.prefix() + "§aВход подтверждён! Добро пожаловать.");
                });
            } else if ("denied".equals(status)) {
                task.cancel();
                pendingTokens.remove(uuidStr);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getFrozenPlayers().remove(uuidStr);
                    player.kickPlayer("§cВход отклонён. Если это были вы — войдите снова.");
                });
            }
        }, 60L, 60L);
    }

    public long getJoinTime(UUID uuid) {
        return this.joinTimes.getOrDefault(uuid, System.currentTimeMillis());
    }
}
