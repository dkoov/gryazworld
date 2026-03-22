/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 */
package dev.serverpanel.plugin.listeners;

import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import dev.serverpanel.plugin.bank.AtmSession;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class AtmListener
implements Listener {
    private final ServerPanelPlugin plugin;
    private final Map<Inventory, AtmSession> sessions = new HashMap<Inventory, AtmSession>();

    public AtmListener(ServerPanelPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!this.plugin.getAtmManager().isAtm(event.getClickedBlock().getLocation())) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        this.plugin.getLogger().info("ATM clicked by " + player.getName() + " at " + String.valueOf(event.getClickedBlock().getLocation()));
        this.openAtmMenu(player);
    }

    private void openAtmMenu(Player player) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse balResp = this.plugin.getApiClient().getBalance(player.getUniqueId().toString());
            ApiClient.ApiResponse finesResp = this.plugin.getApiClient().getPlayerFines(player.getUniqueId().toString());
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                AtmSession session = new AtmSession(this.plugin, player, balResp, finesResp);
                Inventory inv = session.buildInventory();
                this.sessions.put(inv, session);
                player.openInventory(inv);
            });
        });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (!event.getView().getTitle().contains("\u0411\u0430\u043d\u043a\u043e\u043c\u0430\u0442")) {
            return;
        }
        Inventory topInv = event.getView().getTopInventory();
        AtmSession session = this.sessions.get(topInv);
        this.plugin.getLogger().info("Inventory click by " + player.getName() + " slot: " + event.getSlot() + " session: " + (session != null));
        if (session == null) {
            return;
        }
        event.setCancelled(true);
        if (event.getClickedInventory() != null && event.getClickedInventory().equals((Object)topInv)) {
            session.handleClick(event.getSlot(), event.getCurrentItem());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        this.sessions.remove(event.getInventory());
    }
}

