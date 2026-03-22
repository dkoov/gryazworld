/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package dev.serverpanel.plugin.managers;

import dev.serverpanel.lib.gson.JsonArray;
import dev.serverpanel.lib.gson.JsonObject;
import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FineCheckTask
extends BukkitRunnable {
    private final ServerPanelPlugin plugin;

    public FineCheckTask(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        ApiClient.ApiResponse resp = this.plugin.getApiClient().getOverdueFines();
        if (!resp.isSuccess()) {
            return;
        }
        if (!resp.data.has("overdue")) {
            return;
        }
        JsonArray arr = resp.data.getAsJsonArray("overdue");
        if (arr.isEmpty()) {
            return;
        }
        this.plugin.getLogger().info("Processing " + arr.size() + " overdue fines...");
        arr.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            String uuid = obj.get("player_uuid").getAsString();
            int fineId = obj.get("fine_id").getAsInt();
            String reason = "\u041d\u0435\u043e\u043f\u043b\u0430\u0447\u0435\u043d\u043d\u044b\u0439 \u0448\u0442\u0440\u0430\u0444 #" + fineId;
            ApiClient.ApiResponse warnResp = this.plugin.getApiClient().issueWarn("system", uuid, reason);
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                Player player = this.plugin.getServer().getPlayer(UUID.fromString(uuid));
                if (player != null) {
                    if (warnResp.isSuccess()) {
                        player.sendMessage(this.plugin.prefix() + "\u00a7c\u00a7l\u0412\u0410\u0420\u041d! \u00a7c\u0428\u0442\u0440\u0430\u0444 \u00a7f#" + fineId + " \u00a7c\u043d\u0435 \u0431\u044b\u043b \u043e\u043f\u043b\u0430\u0447\u0435\u043d \u0432\u043e\u0432\u0440\u0435\u043c\u044f. \u0412\u0430\u043c \u0432\u044b\u0434\u0430\u043d \u0432\u0430\u0440\u043d \u2116" + warnResp.getInt("total_warns") + "!");
                    } else {
                        player.sendMessage(this.plugin.prefix() + "\u00a7c\u0412\u0430\u0448 \u0448\u0442\u0440\u0430\u0444 \u00a7f#" + fineId + " \u00a7c\u043f\u0440\u043e\u0441\u0440\u043e\u0447\u0435\u043d!");
                    }
                }
            });
        });
    }
}

