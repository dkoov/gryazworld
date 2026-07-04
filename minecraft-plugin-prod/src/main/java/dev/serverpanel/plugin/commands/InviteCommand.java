package dev.serverpanel.plugin.commands;

import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class InviteCommand implements CommandExecutor {
    private final ServerPanelPlugin plugin;

    public InviteCommand(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u0422\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f \u0438\u0433\u0440\u043e\u043a\u043e\u0432.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(this.plugin.prefix() + "\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: \u00a7f/invite <\u043d\u0438\u043a\u043d\u0435\u0439\u043c>");
            return true;
        }
        String targetNick = args[0];
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin) this.plugin, () -> {
            // Step 1: get invoker's discord_id by their Minecraft nickname
            ApiClient.ApiResponse discordResp = this.plugin.getApiClient().getDiscordId(player.getName());
            if (!discordResp.isSuccess()) {
                this.plugin.getServer().getScheduler().runTask((Plugin) this.plugin, () ->
                    player.sendMessage(this.plugin.prefix() + "\u00a7c\u0412\u0430\u0448 Discord \u043d\u0435 \u043f\u0440\u0438\u0432\u044f\u0437\u0430\u043d. \u041f\u0440\u0438\u0432\u044f\u0436\u0438\u0442\u0435 \u0430\u043a\u043a\u0430\u0443\u043d\u0442 \u043d\u0430 \u0441\u0430\u0439\u0442\u0435.")
                );
                return;
            }
            String discordId = discordResp.getString("discord_id");

            // Step 2: find a community this player can invite into (owner, or deputy with invite rights)
            ApiClient.ApiResponse commResp = this.plugin.getApiClient().getInvitableCommunity(discordId);
            if (!commResp.isSuccess()) {
                this.plugin.getServer().getScheduler().runTask((Plugin) this.plugin, () ->
                    player.sendMessage(this.plugin.prefix() + "\u00a7c" + commResp.getMessage())
                );
                return;
            }
            int communityId = commResp.getInt("id");
            String communityName = commResp.getString("name");

            // Step 3: send invite
            ApiClient.ApiResponse inviteResp = this.plugin.getApiClient().inviteToComm(communityId, discordId, targetNick);
            Player target = this.plugin.getServer().getPlayer(targetNick); // может быть null если оффлайн
            this.plugin.getServer().getScheduler().runTask((Plugin) this.plugin, () -> {
                if (inviteResp.isSuccess()) {
                    player.sendMessage(this.plugin.prefix() + "\u00a7a\u041f\u0440\u0438\u0433\u043b\u0430\u0448\u0435\u043d\u0438\u0435 \u0438\u0433\u0440\u043e\u043a\u0443 \u00a7f" + targetNick + " \u00a7a\u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u043e.");
                    if (target != null) {
                        showInviteMessage(target, communityName, communityId);
                    }
                } else {
                    player.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + inviteResp.getMessage());
                }
            });
        });
        return true;
    }

    public static void showInviteMessage(Player player, String communityName, int communityId) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        TextComponent header = new TextComponent(
            "\n§6[GryazWorld] §fВас приглашают в общину §e" + communityName + "\n"
        );

        TextComponent accept = new TextComponent("§a[✔ Принять]");
        accept.setClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/acceptinvite " + communityId
        ));
        accept.setHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new BaseComponent[]{new TextComponent("§aПринять приглашение")}
        ));

        TextComponent decline = new TextComponent(" §c[✘ Отклонить]");
        decline.setClickEvent(new ClickEvent(
            ClickEvent.Action.RUN_COMMAND,
            "/declineinvite " + communityId
        ));
        decline.setHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new BaseComponent[]{new TextComponent("§cОтклонить приглашение")}
        ));

        player.spigot().sendMessage(header);
        player.spigot().sendMessage(accept, decline);
        player.spigot().sendMessage(new TextComponent("\n"));
    }
}
