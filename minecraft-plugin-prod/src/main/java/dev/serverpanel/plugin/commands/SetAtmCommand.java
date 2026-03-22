/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package dev.serverpanel.plugin.commands;

import dev.serverpanel.plugin.ServerPanelPlugin;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAtmCommand
implements CommandExecutor {
    private final ServerPanelPlugin plugin;

    public SetAtmCommand(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args2) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u0422\u043e\u043b\u044c\u043a\u043e \u0434\u043b\u044f \u0438\u0433\u0440\u043e\u043a\u043e\u0432.");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("serverpanel.admin")) {
            player.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435\u0442 \u043f\u0440\u0430\u0432.");
            return true;
        }
        Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage(this.plugin.prefix() + "\u00a7c\u041f\u043e\u0441\u043c\u043e\u0442\u0440\u0438\u0442\u0435 \u043d\u0430 \u0431\u043b\u043e\u043a (\u043c\u0430\u043a\u0441\u0438\u043c\u0443\u043c 5 \u0431\u043b\u043e\u043a\u043e\u0432).");
            return true;
        }
        if (this.plugin.getAtmManager().isAtm(target.getLocation())) {
            this.plugin.getAtmManager().removeAtm(target.getLocation());
            player.sendMessage(this.plugin.prefix() + "\u00a7e\u0411\u0430\u043d\u043a\u043e\u043c\u0430\u0442 \u00a7cub\u0440\u0430\u043d \u00a7e\u0441 \u0431\u043b\u043e\u043a\u0430 \u00a7f" + target.getType().name() + " \u00a7e(" + target.getX() + "," + target.getY() + "," + target.getZ() + ")");
        } else {
            this.plugin.getAtmManager().addAtm(target.getLocation());
            player.sendMessage(this.plugin.prefix() + "\u00a7a\u0411\u043b\u043e\u043a \u00a7f" + target.getType().name() + " \u00a7a\u0442\u0435\u043f\u0435\u0440\u044c \u0431\u0430\u043d\u043a\u043e\u043c\u0430\u0442! \u00a77(" + target.getX() + "," + target.getY() + "," + target.getZ() + ")");
        }
        return true;
    }
}

