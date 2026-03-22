/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package dev.serverpanel.plugin.commands;

import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class UnwarnCommand
implements CommandExecutor {
    private final ServerPanelPlugin plugin;

    public UnwarnCommand(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args2) {
        String displayName;
        String uuid;
        Player online;
        if (!sender.hasPermission("serverpanel.admin")) {
            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u0442 \u043f\u0440\u0430\u0432.");
            return true;
        }
        if (args2.length < 1) {
            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: \u00a7f/unwarn <\u0438\u0433\u0440\u043e\u043a> [\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e]");
            return true;
        }
        String targetName = args2[0];
        int amount = 1;
        if (args2.length >= 2) {
            try {
                amount = Integer.parseInt(args2[1]);
                if (amount < 1) {
                    sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u0431\u043e\u043b\u044c\u0448\u0435 0.");
                    return true;
                }
            }
            catch (NumberFormatException e) {
                sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e: \u00a7f" + args2[1]);
                return true;
            }
        }
        if ((online = Bukkit.getPlayer((String)targetName)) != null) {
            uuid = online.getUniqueId().toString();
            displayName = online.getName();
        } else {
            OfflinePlayer offline = Bukkit.getOfflinePlayer((String)targetName);
            if (offline.getUniqueId() == null || !offline.hasPlayedBefore()) {
                sender.sendMessage(this.plugin.prefix() + "\u00a7c\u0418\u0433\u0440\u043e\u043a \u00a7f" + targetName + " \u00a7c\u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d.");
                return true;
            }
            uuid = offline.getUniqueId().toString();
            displayName = offline.getName() != null ? offline.getName() : targetName;
        }
        int finalAmount = amount;
        String finalUuid = uuid;
        String finalName = displayName;
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse resp = this.plugin.getApiClient().removeWarn(finalUuid, finalAmount);
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                if (resp.isSuccess()) {
                    int newWarns = resp.getInt("warns");
                    sender.sendMessage(this.plugin.prefix() + "\u00a7a\u0421\u043d\u044f\u0442\u043e \u00a7f" + finalAmount + " \u00a7a\u0432\u0430\u0440\u043d(\u043e\u0432) \u0441 \u00a7f" + finalName + "\u00a7a. \u041e\u0441\u0442\u0430\u043b\u043e\u0441\u044c \u0432\u0430\u0440\u043d\u043e\u0432: \u00a7c" + newWarns);
                    if (online != null) {
                        online.sendMessage(this.plugin.prefix() + "\u00a7a\u0421 \u0432\u0430\u0441 \u0441\u043d\u044f\u0442\u043e \u00a7f" + finalAmount + " \u00a7a\u0432\u0430\u0440\u043d(\u043e\u0432). \u0422\u0435\u043a\u0443\u0449\u0435\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0432\u0430\u0440\u043d\u043e\u0432: \u00a7c" + newWarns);
                    }
                } else {
                    sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + resp.getMessage());
                }
            });
        });
        return true;
    }
}

