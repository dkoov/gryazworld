/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package dev.serverpanel.plugin.commands;

import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AdminBankCommand
implements CommandExecutor {
    private final ServerPanelPlugin plugin;

    public AdminBankCommand(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args2) {
        String sub;
        if (!sender.hasPermission("serverpanel.admin")) {
            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u0442 \u043f\u0440\u0430\u0432.");
            return true;
        }
        if (args2.length < 1) {
            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: \u00a7f/adminbank <\u0438\u0433\u0440\u043e\u043a> [balance|add <\u043a\u043e\u043b-\u0432\u043e>|remove <\u043a\u043e\u043b-\u0432\u043e>]");
            return true;
        }
        Player target = this.plugin.getServer().getPlayer(args2[0]);
        if (target == null) {
            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u0418\u0433\u0440\u043e\u043a \u00a7f" + args2[0] + " \u00a7c\u043d\u0435 \u0432 \u0441\u0435\u0442\u0438.");
            return true;
        }
        switch (sub = args2.length > 1 ? args2[1].toLowerCase() : "balance") {
            case "balance": {
                this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
                    ApiClient.ApiResponse resp = this.plugin.getApiClient().getBalance(target.getUniqueId().toString());
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                        if (resp.isSuccess()) {
                            sender.sendMessage(this.plugin.prefix() + "\u00a77\u0411\u0430\u043b\u0430\u043d\u0441 \u00a7f" + target.getName() + "\u00a77: \u00a7b" + resp.getInt("balance") + " \u0430\u043b\u043c\u0430\u0437\u043e\u0432");
                        } else {
                            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + resp.getMessage());
                        }
                    });
                });
                break;
            }
            case "add": 
            case "remove": {
                int amount;
                if (args2.length < 3) {
                    sender.sendMessage(this.plugin.prefix() + "\u00a7c\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e.");
                    return true;
                }
                try {
                    amount = Integer.parseInt(args2[2]);
                    if (amount <= 0) {
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException e) {
                    sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e.");
                    return true;
                }
                int finalAmount = sub.equals("remove") ? -amount : amount;
                this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
                    ApiClient.ApiResponse resp = this.plugin.getApiClient().deposit(target.getUniqueId().toString(), finalAmount);
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                        if (resp.isSuccess()) {
                            sender.sendMessage(this.plugin.prefix() + "\u00a7a\u0411\u0430\u043b\u0430\u043d\u0441 \u00a7f" + target.getName() + "\u00a7a \u043e\u0431\u043d\u043e\u0432\u043b\u0451\u043d. \u041d\u043e\u0432\u044b\u0439 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7b" + resp.getInt("new_balance") + " \u0430\u043b\u043c.");
                        } else {
                            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + resp.getMessage());
                        }
                    });
                });
                break;
            }
            default: {
                sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u044f. \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439: balance, add, remove");
            }
        }
        return true;
    }
}

