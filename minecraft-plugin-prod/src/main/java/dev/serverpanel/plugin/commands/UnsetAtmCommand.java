package dev.serverpanel.plugin.commands;

import dev.serverpanel.plugin.managers.AtmManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnsetAtmCommand implements CommandExecutor {
    private final AtmManager atmManager;

    public UnsetAtmCommand(AtmManager atmManager) {
        this.atmManager = atmManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cТолько игроки могут использовать эту команду");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("serverpanel.admin")) {
            player.sendMessage("§cНет прав");
            return true;
        }
        org.bukkit.block.Block target = player.getTargetBlockExact(5);
        if (target == null || !atmManager.isAtm(target.getLocation())) {
            player.sendMessage("§cПосмотрите на банкомат и повторите команду");
            return true;
        }
        atmManager.removeAtm(target.getLocation());
        target.setType(org.bukkit.Material.AIR);
        player.sendMessage("§aБанкомат удалён");
        return true;
    }
}
