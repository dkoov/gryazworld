package dev.serverpanel.plugin.commands;

import dev.serverpanel.plugin.api.ApiClient;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptInviteCommand implements CommandExecutor {
    private final ApiClient apiClient;

    public AcceptInviteCommand(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length < 1) return true;
        Player player = (Player) sender;
        int communityId;
        try {
            communityId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(apiClient.getPlugin(), () -> {
            ApiClient.ApiResponse profileResp = apiClient.getDiscordId(player.getName());
            if (!profileResp.isSuccess()) {
                Bukkit.getScheduler().runTask(apiClient.getPlugin(), () ->
                    player.sendMessage("§cНе удалось получить профиль")
                );
                return;
            }
            String body = "{\"nickname\":\"" + player.getName() + "\"}";
            ApiClient.ApiResponse resp = apiClient.post("/web/communities/" + communityId + "/accept-invite", body);
            Bukkit.getScheduler().runTask(apiClient.getPlugin(), () -> {
                if (resp.isSuccess()) {
                    player.sendMessage("§aВы вступили в общину!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                } else {
                    player.sendMessage("§cОшибка: " + resp.getMessage());
                }
            });
        });
        return true;
    }
}
