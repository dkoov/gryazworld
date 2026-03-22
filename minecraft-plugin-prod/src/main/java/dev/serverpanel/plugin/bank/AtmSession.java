/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package dev.serverpanel.plugin.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.serverpanel.plugin.ServerPanelPlugin;
import dev.serverpanel.plugin.api.ApiClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class AtmSession {
    private final ServerPanelPlugin plugin;
    private final Player player;
    private final int balance;
    private final List<FineEntry> fines = new ArrayList<FineEntry>();
    private Inventory inv;
    private static final int SLOT_BALANCE = 4;
    private static final int SLOT_DEPOSIT_1 = 19;
    private static final int SLOT_DEPOSIT_8 = 20;
    private static final int SLOT_DEPOSIT_32 = 21;
    private static final int SLOT_DEPOSIT_64 = 22;
    private static final int SLOT_FINES_START = 27;

    public AtmSession(ServerPanelPlugin plugin, Player player, ApiClient.ApiResponse balResp, ApiClient.ApiResponse finesResp) {
        this.plugin = plugin;
        this.player = player;
        int n = this.balance = balResp.isSuccess() ? balResp.getInt("balance") : 0;
        if (finesResp.isSuccess() && finesResp.data.has("fines")) {
            JsonArray arr = finesResp.data.getAsJsonArray("fines");
            arr.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                if (!obj.has("status") || !obj.get("status").getAsString().equals("pending")) {
                    return;
                }
                this.fines.add(new FineEntry(obj.get("id").getAsInt(), obj.get("amount").getAsInt(), obj.get("reason").getAsString(), obj.get("deadline").getAsString()));
            });
        }
    }

    public Inventory buildInventory() {
        int i;
        int rows = this.fines.isEmpty() ? 4 : Math.min(6, 4 + (int)Math.ceil((double)this.fines.size() / 9.0));
        this.inv = Bukkit.createInventory(null, (int)(rows * 9), (String)"\u00a78\u00a7l\ud83c\udfe6 \u0411\u0430\u043d\u043a\u043e\u043c\u0430\u0442");
        ItemStack pane = this.makeItem(Material.GRAY_STAINED_GLASS_PANE, "\u00a7r", List.of());
        for (i = 0; i < this.inv.getSize(); ++i) {
            this.inv.setItem(i, pane);
        }
        this.inv.setItem(4, this.makeItem(Material.DIAMOND, "\u00a7b\u00a7l\u0412\u0430\u0448 \u0431\u0430\u043b\u0430\u043d\u0441", List.of("\u00a77" + this.balance + " \u0430\u043b\u043c\u0430\u0437\u043e\u0432")));
        this.inv.setItem(19, this.depositBtn(1));
        this.inv.setItem(20, this.depositBtn(8));
        this.inv.setItem(21, this.depositBtn(32));
        this.inv.setItem(22, this.depositBtn(64));
        this.inv.setItem(13, this.makeItem(Material.BOOK, "\u00a7e\u00a7l\u041a\u0430\u043a \u043f\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u044c", List.of("\u00a77\u041f\u043e\u043b\u043e\u0436\u0438\u0442\u0435 \u0430\u043b\u043c\u0430\u0437\u044b \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c", "\u00a77\u0438 \u043d\u0430\u0436\u043c\u0438\u0442\u0435 \u043a\u043d\u043e\u043f\u043a\u0443 \u0434\u0435\u043f\u043e\u0437\u0438\u0442\u0430.")));
        if (!this.fines.isEmpty()) {
            this.inv.setItem(27, this.makeItem(Material.RED_BANNER, "\u00a7c\u00a7l\u0412\u0430\u0448\u0438 \u0448\u0442\u0440\u0430\u0444\u044b", List.of("\u00a77\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043d\u0430 \u0448\u0442\u0440\u0430\u0444 \u0447\u0442\u043e\u0431\u044b \u043e\u043f\u043b\u0430\u0442\u0438\u0442\u044c")));
            for (i = 0; i < this.fines.size() && i < 8; ++i) {
                FineEntry fine = this.fines.get(i);
                this.inv.setItem(28 + i, this.makeItem(Material.PAPER, "\u00a7c\u00a7l\u0428\u0442\u0440\u0430\u0444 #" + fine.id, List.of("\u00a77\u0421\u0443\u043c\u043c\u0430: \u00a7c" + fine.amount + " \u0430\u043b\u043c\u0430\u0437\u043e\u0432", "\u00a77\u041f\u0440\u0438\u0447\u0438\u043d\u0430: \u00a7f" + fine.reason, "\u00a77\u0414\u0435\u0434\u043b\u0430\u0439\u043d: \u00a7e" + fine.deadline, "", "\u00a7a\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0447\u0442\u043e\u0431\u044b \u043e\u043f\u043b\u0430\u0442\u0438\u0442\u044c")));
            }
        }
        return this.inv;
    }

    public void handleClick(int slot, ItemStack item) {
        int idx;
        if (slot == 19) {
            this.doDeposit(1);
        } else if (slot == 20) {
            this.doDeposit(8);
        } else if (slot == 21) {
            this.doDeposit(32);
        } else if (slot == 22) {
            this.doDeposit(64);
        } else if (slot >= 28 && !this.fines.isEmpty() && (idx = slot - 27 - 1) >= 0 && idx < this.fines.size()) {
            this.doPayFine(this.fines.get(idx));
        }
    }

    private void doDeposit(int amount) {
        int count = Arrays.stream(this.player.getInventory().getContents()).filter(s -> s != null && s.getType() == Material.DIAMOND).mapToInt(ItemStack::getAmount).sum();
        if (count < amount) {
            this.player.sendMessage(this.plugin.prefix() + "\u00a7c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0430\u043b\u043c\u0430\u0437\u043e\u0432. \u0415\u0441\u0442\u044c: \u00a7f" + count);
            return;
        }
        this.player.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.DIAMOND, amount)});
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse resp = this.plugin.getApiClient().deposit(this.player.getUniqueId().toString(), amount);
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                if (resp.isSuccess()) {
                    this.player.sendMessage(this.plugin.prefix() + "\u00a7a\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0432\u043d\u0435\u0441\u0435\u043d\u043e \u00a7f" + amount + " \u00a7a\u0430\u043b\u043c\u0430\u0437\u043e\u0432 \u043d\u0430 \u0441\u0447\u0451\u0442. \u0411\u0430\u043b\u0430\u043d\u0441: \u00a7f" + resp.getInt("balance"));
                    this.player.closeInventory();
                } else {
                    this.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.DIAMOND, amount)});
                    this.player.sendMessage(this.plugin.prefix() + "\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: " + resp.getMessage());
                }
            });
        });
    }

    private void doPayFine(FineEntry fine) {
        this.player.closeInventory();
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            ApiClient.ApiResponse resp = this.plugin.getApiClient().payFine(this.player.getUniqueId().toString(), fine.id);
            this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> {
                if (resp.isSuccess()) {
                    this.player.sendMessage(this.plugin.prefix() + "\u00a7a\u0428\u0442\u0440\u0430\u0444 \u00a7f#" + fine.id + " \u00a7a\u043e\u043f\u043b\u0430\u0447\u0435\u043d! \u041e\u0441\u0442\u0430\u0442\u043e\u043a: \u00a7f" + resp.getInt("balance") + " \u00a7a\u0430\u043b\u043c\u0430\u0437\u043e\u0432.");
                } else {
                    this.player.sendMessage(this.plugin.prefix() + "\u00a7c\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u043f\u043b\u0430\u0442\u0438\u0442\u044c \u0448\u0442\u0440\u0430\u0444: " + resp.getMessage());
                }
            });
        });
    }

    public boolean isOurInventory(Inventory other) {
        return this.inv != null && this.inv.equals((Object)other);
    }

    private ItemStack depositBtn(int amount) {
        ItemStack item = new ItemStack(Material.DIAMOND, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00a7b\u00a7l\u0412\u043d\u0435\u0441\u0442\u0438 " + amount + " \u0430\u043b\u043c\u0430\u0437\u043e\u0432");
        meta.setLore(List.of("\u00a77\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0447\u0442\u043e\u0431\u044b \u043f\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u044c \u0431\u0430\u043b\u0430\u043d\u0441"));
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP});
        item.setItemMeta(meta);
        return item;
    }

    public record FineEntry(int id, int amount, String reason, String deadline) {
    }
}

