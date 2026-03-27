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
import dev.serverpanel.plugin.bank.AtmSession.FineEntry;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class AtmListener
implements Listener {
    private final ServerPanelPlugin plugin;
    private static final Map<Inventory, AtmSession> sessions = new HashMap<Inventory, AtmSession>();

    public static Map<Inventory, AtmSession> getSessions() { return sessions; }

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
        String title = event.getView().getTitle();
        Inventory topInv = event.getView().getTopInventory();

        // Меню штрафов
        if (title.equals("\u00a7c\u00a7l\u0428\u0442\u0440\u0430\u0444\u044b")) {
            event.setCancelled(true);
            if (event.getClickedInventory() == null || !event.getClickedInventory().equals(topInv)) return;
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == org.bukkit.Material.GRAY_STAINED_GLASS_PANE) return;

            AtmSession session = null;
            for (AtmSession s : sessions.values()) {
                if (s.getPlayer().equals(player)) { session = s; break; }
            }
            if (session == null) return;

            if (clicked.getType() == org.bukkit.Material.ARROW) {
                player.closeInventory();
                this.openAtmMenu(player);
                return;
            }

            int fineSlot = event.getSlot();
            if (fineSlot < session.getActiveFines().size()) {
                FineEntry fine = session.getActiveFines().get(fineSlot);
                if (!fine.status().equals("pending")) return;
                session.doPayFine(fine);
            }
            return;
        }

        // Главное меню банкомата
        if (!title.contains("\u0411\u0430\u043d\u043a\u043e\u043c\u0430\u0442")) {
            return;
        }
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
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        if (plugin.getAtmManager().isAtm(block.getLocation())) {
            Player player = event.getPlayer();
            if (!player.hasPermission("serverpanel.admin")) {
                event.setCancelled(true);
                player.sendMessage("§cБанкомат нельзя сломать. Используйте §e/unsetatm §cдля удаления.");
            }
        }
    }

    @EventHandler
    public void onBlockExplode(org.bukkit.event.block.BlockExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.getAtmManager().isAtm(block.getLocation()));
    }

    @EventHandler
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        event.blockList().removeIf(block -> plugin.getAtmManager().isAtm(block.getLocation()));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        AtmSession session = this.sessions.get(event.getInventory());
        if (session != null && session.isSwitchingToFines()) {
            session.resetSwitchingToFines();
            return; // не удаляем сессию — игрок переходит в меню штрафов
        }
        this.sessions.remove(event.getInventory());
    }
}

