package dev.serverpanel.plugin.commands;

import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import java.util.Arrays;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FineCommand implements CommandExecutor {
    private final ServerPanelPlugin plugin;

    public FineCommand(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args2) {
        if (!sender.hasPermission("serverpanel.admin")) {
            sender.sendMessage(this.plugin.prefix() + this.plugin.getConfig().getString("messages.no-permission", "\u041d\u0435\u0442 \u043f\u0440\u0430\u0432.").replace("&", "\u00a7"));
            return true;
        }

        if (args2.length > 0 && args2[0].equalsIgnoreCase("list")) {
            if (args2.length < 2) {
                sender.sendMessage("§cИспользование: /fine list <ник>");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cТолько для игроков.");
                return true;
            }
            String targetNick = args2[1];
            Player player = (Player) sender;

            Bukkit.getScheduler().runTaskAsynchronously((Plugin) plugin, () -> {
                ApiClient.ApiResponse resp = plugin.getApiClient().get("/mc/fines/by-nick/" + targetNick);
                Bukkit.getScheduler().runTask((Plugin) plugin, () -> {
                    if (!resp.isSuccess()) {
                        player.sendMessage("§cОшибка получения штрафов");
                        return;
                    }
                    JsonArray fines = resp.data.getAsJsonArray("fines");
                    if (fines == null || fines.size() == 0) {
                        player.sendMessage("§aУ игрока §e" + targetNick + " §aнет штрафов");
                        return;
                    }
                    player.sendMessage("§6=== Штрафы игрока " + targetNick + " ===");
                    for (JsonElement el : fines) {
                        JsonObject fine = el.getAsJsonObject();
                        int fineId = fine.get("id").getAsInt();
                        String reason = fine.get("reason").getAsString();
                        int amount = fine.get("amount").getAsInt();
                        String status = fine.get("status").getAsString();

                        TextComponent line = new TextComponent(
                            "§7#" + fineId + " §f" + reason + " §e" + amount + "💎 §7[" + status + "] "
                        );

                        if (status.equals("pending")) {
                            TextComponent cancel = new TextComponent("§c[Отменить]");
                            cancel.setClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/fine cancel " + fineId
                            ));
                            cancel.setHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new BaseComponent[]{new TextComponent("§cОтменить штраф #" + fineId)}
                            ));
                            line.addExtra(cancel);
                        }
                        player.spigot().sendMessage(line);
                    }
                });
            });
            return true;
        }

        if (args2.length > 0 && args2[0].equalsIgnoreCase("cancel")) {
            if (args2.length < 2) {
                sender.sendMessage("§cИспользование: /fine cancel <id>");
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cТолько для игроков.");
                return true;
            }
            int fineId;
            try {
                fineId = Integer.parseInt(args2[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cID штрафа должен быть числом.");
                return true;
            }
            Player player = (Player) sender;

            Bukkit.getScheduler().runTaskAsynchronously((Plugin) plugin, () -> {
                String body = "{\"fine_id\":" + fineId + ",\"admin_nickname\":\"" + player.getName() + "\"}";
                ApiClient.ApiResponse resp = plugin.getApiClient().post("/mc/fines/cancel", body);
                Bukkit.getScheduler().runTask((Plugin) plugin, () -> {
                    if (resp.isSuccess()) {
                        player.sendMessage("§aШтраф §e#" + fineId + " §aотменён");
                    } else {
                        player.sendMessage("§cОшибка: " + resp.getMessage());
                    }
                });
            });
            return true;
        }

        // Existing: /fine <ник> <сумма> <часов> <причина>
        String string;
        int hours;
        int amount;
        String targetName;
        String targetUuid;

        if (args2.length < 4) {
            sender.sendMessage(this.plugin.prefix() + "§cИспользование: §f/fine <игрок> <сумма> <часов> <причина> §7| /fine list <ник> §7| /fine cancel <id>");
            return true;
        }
        Player online = this.plugin.getServer().getPlayer(args2[0]);
        if (online != null) {
            targetUuid = online.getUniqueId().toString();
            targetName = online.getName();
        } else {
            OfflinePlayer offline = this.plugin.getServer().getOfflinePlayer(args2[0]);
            if (!offline.hasPlayedBefore()) {
                sender.sendMessage(this.plugin.prefix() + "\u00a7c\u0418\u0433\u0440\u043e\u043a \u00a7f" + args2[0] + " \u00a7c\u043d\u0438\u043a\u043e\u0433\u0434\u0430 \u043d\u0435 \u0431\u044b\u043b \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435.");
                return true;
            }
            targetUuid = offline.getUniqueId().toString();
            targetName = offline.getName() != null ? offline.getName() : args2[0];
        }
        try {
            amount = Integer.parseInt(args2[1]);
            hours = Integer.parseInt(args2[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.prefix() + "\u00a7c\u0421\u0443\u043c\u043c\u0430 \u0438 \u0447\u0430\u0441\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0431\u044b\u0442\u044c \u0447\u0438\u0441\u043b\u0430\u043c\u0438.");
            return true;
        }
        String reason = String.join((CharSequence)" ", Arrays.copyOfRange(args2, 3, args2.length));
        if (sender instanceof Player) {
            Player p = (Player) sender;
            string = p.getUniqueId().toString();
        } else {
            string = "console";
        }
        String adminUuid = string;
        int finalAmount = amount;
        int finalHours = hours;
        Player finalOnline = online;
        String finalTargetName = targetName;
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin) this.plugin, () -> {
            ApiClient.ApiResponse resp = this.plugin.getApiClient().issueFine(adminUuid, targetUuid, finalAmount, reason, finalHours);
            this.plugin.getServer().getScheduler().runTask((Plugin) this.plugin, () -> {
                if (resp.isSuccess()) {
                    int fineId = resp.getInt("fine_id");
                    sender.sendMessage(this.plugin.prefix() + "\u00a7a\u0428\u0442\u0440\u0430\u0444 \u00a7f#" + fineId + " \u00a7a\u0432\u044b\u0434\u0430\u043d \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7f" + finalTargetName + " \u00a7a\u043d\u0430 \u00a7f" + finalAmount + " \u00a7a\u0430\u043b\u043c\u0430\u0437\u043e\u0432. \u0421\u0440\u043e\u043a: \u00a7f" + finalHours + "\u0447.");
                    if (finalOnline != null) {
                        finalOnline.sendMessage(this.plugin.prefix() + "\u00a7c\u0412\u0430\u043c \u0432\u044b\u0434\u0430\u043d \u0448\u0442\u0440\u0430\u0444 \u00a7f#" + fineId + " \u00a7c\u043d\u0430 \u00a7f" + finalAmount + " \u00a7c\u0430\u043b\u043c\u0430\u0437\u043e\u0432. \u041f\u0440\u0438\u0447\u0438\u043d\u0430: \u00a7f" + reason + "\u00a7c. \u0421\u0440\u043e\u043a: \u00a7f" + finalHours + " \u0447.");
                    }
                } else {
                    sender.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + resp.getMessage());
                }
            });
        });
        return true;
    }
}
