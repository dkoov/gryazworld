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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BankCommand
implements CommandExecutor {
    private final ServerPanelPlugin plugin;

    public BankCommand(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args2) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u0422\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f \u0438\u0433\u0440\u043e\u043a\u043e\u0432.");
            return true;
        }
        Player player = (Player)sender;
        if (args2.length == 0 || args2[0].equalsIgnoreCase("balance")) {
            this.showBalance(player);
            return true;
        }
        if (args2[0].equalsIgnoreCase("transfer") && args2.length >= 3) {
            this.doTransfer(player, args2[1], args2[2]);
            return true;
        }
        if (args2[0].equalsIgnoreCase("pay") && args2.length >= 3) {
            this.doTransfer(player, args2[1], args2[2]);
            return true;
        }
        if (args2[0].equalsIgnoreCase("fines")) {
            this.showFines(player);
            return true;
        }
        player.sendMessage(this.plugin.prefix() + "\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        player.sendMessage("\u00a7f/bank balance \u00a77- \u043f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0431\u0430\u043b\u0430\u043d\u0441");
        player.sendMessage("\u00a7f/bank transfer <\u0438\u0433\u0440\u043e\u043a> <\u0441\u0443\u043c\u043c\u0430> \u00a77- \u043f\u0435\u0440\u0435\u0432\u0435\u0441\u0442\u0438 \u0430\u043b\u043c\u0430\u0437\u044b");
        player.sendMessage("\u00a7f/bank pay <\u0438\u0433\u0440\u043e\u043a> <\u0441\u0443\u043c\u043c\u0430> \u00a77- \u043f\u0435\u0440\u0435\u0432\u0435\u0441\u0442\u0438 \u0430\u043b\u043c\u0430\u0437\u044b");
        player.sendMessage("\u00a7f/bank fines \u00a77- \u043f\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0448\u0442\u0440\u0430\u0444\u044b");
        return true;
    }

    private void showBalance(Player player) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse resp = this.plugin.getApiClient().getBalance(player.getUniqueId().toString());
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                if (resp.isSuccess()) {
                    player.sendMessage(this.plugin.prefix() + "\u00a77\u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7b" + resp.getInt("balance") + " \u00a77\u0430\u043b\u043c\u0430\u0437\u043e\u0432");
                } else {
                    player.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + resp.getMessage());
                }
            });
        });
    }

    private void doTransfer(Player player, String targetName, String amountStr) {
        int amount;
        Player target = this.plugin.getServer().getPlayer(targetName);
        if (target == null) {
            player.sendMessage(this.plugin.prefix() + "\u00a7c\u0418\u0433\u0440\u043e\u043a \u00a7f" + targetName + " \u00a7c\u043d\u0435 \u0432 \u0441\u0435\u0442\u0438.");
            return;
        }
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            player.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u0432\u0435\u0440\u043d\u0430\u044f \u0441\u0443\u043c\u043c\u0430.");
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u043b\u044c\u0437\u044f \u043f\u0435\u0440\u0435\u0432\u043e\u0434\u0438\u0442\u044c \u0441\u0430\u043c\u043e\u043c\u0443 \u0441\u0435\u0431\u0435.");
            return;
        }
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse resp = this.plugin.getApiClient().transfer(player.getUniqueId().toString(), target.getUniqueId().toString(), amount);
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                if (resp.isSuccess()) {
                    player.sendMessage(this.plugin.prefix() + "\u00a7a\u041f\u0435\u0440\u0435\u0432\u0435\u0434\u0435\u043d\u043e \u00a7f" + amount + " \u00a7a\u0430\u043b\u043c\u0430\u0437\u043e\u0432 \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7f" + target.getName() + "\u00a7a. \u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7f" + resp.getInt("from_balance"));
                    target.sendMessage(this.plugin.prefix() + "\u00a7a\u0418\u0433\u0440\u043e\u043a \u00a7f" + player.getName() + " \u00a7a\u043f\u0435\u0440\u0435\u0432\u0451\u043b \u0432\u0430\u043c \u00a7f" + amount + " \u00a7a\u0430\u043b\u043c\u0430\u0437\u043e\u0432. \u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441: \u00a7f" + resp.getInt("to_balance"));
                } else {
                    player.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + resp.getMessage());
                }
            });
        });
    }

    private void showFines(Player player) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse resp = this.plugin.getApiClient().getPlayerFines(player.getUniqueId().toString());
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                if (!resp.isSuccess()) {
                    player.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430 \u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0438 \u0448\u0442\u0440\u0430\u0444\u043e\u0432.");
                    return;
                }
                JsonArray arr = resp.data.getAsJsonArray("fines");
                if (arr == null || arr.isEmpty()) {
                    player.sendMessage(this.plugin.prefix() + "\u00a7a\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0448\u0442\u0440\u0430\u0444\u043e\u0432!");
                    return;
                }
                ArrayList<JsonObject> pending = new ArrayList<JsonObject>();
                arr.forEach(el -> {
                    JsonObject obj = el.getAsJsonObject();
                    if (obj.has("status") && obj.get("status").getAsString().equals("pending")) {
                        pending.add(obj);
                    }
                });
                if (pending.isEmpty()) {
                    player.sendMessage(this.plugin.prefix() + "\u00a7a\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0445 \u0448\u0442\u0440\u0430\u0444\u043e\u0432!");
                    return;
                }
                player.sendMessage(this.plugin.prefix() + "\u00a7c\u00a7l\u0412\u0430\u0448\u0438 \u0448\u0442\u0440\u0430\u0444\u044b:");
                pending.forEach(obj -> {
                    String deadline = obj.has("deadline") && !obj.get("deadline").isJsonNull() ? obj.get("deadline").getAsString() : null;
                    String timeLeft = "\u0411\u0435\u0437 \u0441\u0440\u043e\u043a\u0430";
                    if (deadline != null) {
                        try {
                            String dlStr = deadline.endsWith("Z") ? deadline : deadline + "Z";
                            java.time.Instant dl = java.time.Instant.parse(dlStr);
                            long diff = dl.getEpochSecond() - java.time.Instant.now().getEpochSecond();
                            if (diff <= 0) timeLeft = "\u0421\u0440\u043e\u043a \u0438\u0441\u0442\u0451\u043a";
                            else {
                                long hours = diff / 3600;
                                long minutes = (diff % 3600) / 60;
                                timeLeft = hours > 0 ? hours + " \u0447. " + minutes + " \u043c\u0438\u043d." : minutes + " \u043c\u0438\u043d.";
                            }
                        } catch (Exception e) { timeLeft = deadline; }
                    }
                    player.sendMessage("\u00a7c#" + obj.get("id").getAsInt() + " \u00a7f" + obj.get("amount").getAsInt() + " \u0430\u043b\u043c. \u00a77- " + obj.get("reason").getAsString() + " \u00a7e(\u043e\u0441\u0442\u0430\u043b\u043e\u0441\u044c: " + timeLeft + ")");
                });
                player.sendMessage("\u00a77\u041e\u043f\u043b\u0430\u0442\u0438\u0442\u044c \u043c\u043e\u0436\u043d\u043e \u0447\u0435\u0440\u0435\u0437 \u0431\u0430\u043d\u043a\u043e\u043c\u0430\u0442 \u0438\u043b\u0438 \u00a7f\u043d\u0430 \u0441\u0430\u0439\u0442\u0435 gryazworld.ru");
            });
        });
    }
}

