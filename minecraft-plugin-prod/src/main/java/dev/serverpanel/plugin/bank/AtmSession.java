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
    private final List<FineEntry> activeFines = new ArrayList<FineEntry>();
    private Inventory inv;
    private boolean switchingToFines = false;
    private static final int SLOT_DEPOSIT = 10;
    private static final int SLOT_BALANCE = 13;
    private static final int SLOT_WITHDRAW = 16;
    private static final int SLOT_FINES = 22;
    private static final int SLOT_INFO = 26;

    public AtmSession(ServerPanelPlugin plugin, Player player, ApiClient.ApiResponse balResp, ApiClient.ApiResponse finesResp) {
        this.plugin = plugin;
        this.player = player;
        int n = this.balance = balResp.isSuccess() ? balResp.getInt("balance") : 0;
        if (finesResp.isSuccess() && finesResp.data.has("fines")) {
            JsonArray arr = finesResp.data.getAsJsonArray("fines");
            arr.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String status = obj.has("status") ? obj.get("status").getAsString() : "pending";
                if (!status.equals("pending")) return;
                String deadline = obj.has("deadline") && !obj.get("deadline").isJsonNull()
                    ? obj.get("deadline").getAsString() : null;
                this.activeFines.add(new FineEntry(obj.get("id").getAsInt(), obj.get("amount").getAsInt(), obj.get("reason").getAsString(), deadline, status));
            });
        }
    }

    public Inventory buildInventory() {
        this.inv = plugin.getServer().createInventory(null, 27, "§a§lБанкомат");
        ItemStack pane = this.makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", List.of());
        for (int i = 0; i < this.inv.getSize(); ++i) {
            this.inv.setItem(i, pane);
        }

        // Слот 10 — пополнение
        ItemStack deposit = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta depositMeta = deposit.getItemMeta();
        depositMeta.setDisplayName("§a§lПополнить");
        depositMeta.setLore(Arrays.asList("§7Положите алмазы в инвентарь", "§7и нажмите для выбора суммы"));
        deposit.setItemMeta(depositMeta);
        this.inv.setItem(10, deposit);

        // Слот 4 — баланс
        ItemStack balance = new ItemStack(Material.DIAMOND);
        ItemMeta balanceMeta = balance.getItemMeta();
        balanceMeta.setDisplayName("§b§lБаланс");
        balanceMeta.setLore(Arrays.asList("§7Текущий баланс:", "§e" + this.balance + " алмазов"));
        balance.setItemMeta(balanceMeta);
        this.inv.setItem(13, balance);

        // Слот 8 — снятие
        ItemStack withdraw = new ItemStack(Material.RED_CONCRETE);
        ItemMeta withdrawMeta = withdraw.getItemMeta();
        withdrawMeta.setDisplayName("§c§lСнять");
        withdrawMeta.setLore(Arrays.asList("§7Нажмите и введите", "§7количество в чат"));
        withdraw.setItemMeta(withdrawMeta);
        this.inv.setItem(16, withdraw);

        // Слот 18 — штрафы
        ItemStack finesBtn = new ItemStack(Material.PAPER);
        ItemMeta finesMeta = finesBtn.getItemMeta();
        finesMeta.setDisplayName("§c§lАктивные штрафы");
        finesMeta.setLore(Arrays.asList(
            "§7Количество: §e" + this.activeFines.size(),
            "§7Нажмите для просмотра"
        ));
        finesBtn.setItemMeta(finesMeta);
        this.inv.setItem(22, finesBtn);

        // Слот 26 — информация
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§f§lИнформация");
        infoMeta.setLore(Arrays.asList(
            "§7Команды:",
            "§e/bank §7— открыть банкомат",
            "§e/bank pay <ник> <сумма> §7— перевод",
            "§e/bank balance §7— баланс",
            "",
            "§7Пополнение:",
            "§7Возьмите алмазы в руку и",
            "§7нажмите §eПополнить"
        ));
        info.setItemMeta(infoMeta);
        this.inv.setItem(26, info);

        return this.inv;
    }

    public void handleClick(int slot, ItemStack item) {
        switch (slot) {
            case 10: this.handleDeposit(); break;
            case 16: this.handleWithdraw(); break;
            case 22: this.openFinesMenu(); break;
            // 13 (баланс) и 26 (информация) — без действия
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

    public void openFinesMenu() {
        int rows = Math.max(2, (int) Math.ceil((activeFines.size() + 1) / 9.0) + 1);
        Inventory finesInv = plugin.getServer().createInventory(null, rows * 9, "§c§lШтрафы");

        ItemStack gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayMeta = gray.getItemMeta();
        grayMeta.setDisplayName("§r");
        gray.setItemMeta(grayMeta);
        for (int i = 0; i < rows * 9; i++) finesInv.setItem(i, gray);

        if (activeFines.isEmpty()) {
            ItemStack none = new ItemStack(Material.LIME_CONCRETE);
            ItemMeta noneMeta = none.getItemMeta();
            noneMeta.setDisplayName("§aШтрафов нет!");
            none.setItemMeta(noneMeta);
            finesInv.setItem(4, none);
        } else {
            int slot = 0;
            for (FineEntry fine : activeFines) {
                ItemStack fineItem = new ItemStack(Material.PAPER);
                ItemMeta fineMeta = fineItem.getItemMeta();
                fineMeta.setDisplayName("§c§lШтраф #" + fine.id());
                String statusLine;
                if (fine.status().equals("paid")) {
                    statusLine = "§aОплачен";
                } else if (fine.status().equals("overdue")) {
                    statusLine = "§cПросрочен — выдан варн";
                } else {
                    statusLine = "§eНажмите для оплаты";
                }
                fineMeta.setLore(Arrays.asList(
                    "§7Причина: §f" + fine.reason(),
                    "§7Сумма: §e" + fine.amount() + " алмазов",
                    "§7Осталось: §f" + getTimeLeft(fine.deadline()),
                    "",
                    statusLine
                ));
                fineItem.setItemMeta(fineMeta);
                finesInv.setItem(slot, fineItem);
                slot++;
            }
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§f§lНазад");
        back.setItemMeta(backMeta);
        finesInv.setItem(rows * 9 - 1, back);

        this.switchingToFines = true;
        player.openInventory(finesInv);
    }

    public Player getPlayer() { return player; }
    public List<FineEntry> getActiveFines() { return activeFines; }
    public boolean isSwitchingToFines() { return switchingToFines; }
    public void resetSwitchingToFines() { this.switchingToFines = false; }

    public void doPayFine(FineEntry fine) {
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

    private void handleDeposit() {
        player.closeInventory();
        player.sendMessage("§aВведите количество алмазов для пополнения:");
        plugin.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
                if (!event.getPlayer().equals(player)) return;
                event.setCancelled(true);
                org.bukkit.event.HandlerList.unregisterAll(this);
                try {
                    int amount = Integer.parseInt(event.getMessage().trim());
                    if (amount <= 0) {
                        player.sendMessage("§cНеверное количество");
                        return;
                    }
                    doDeposit(amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cВведите число");
                }
            }
        }, plugin);
    }

    private void handleWithdraw() {
        player.closeInventory();
        player.sendMessage("§aВведите количество алмазов для снятия:");

        // Слушаем следующее сообщение игрока
        plugin.getServer().getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
                if (!event.getPlayer().equals(player)) return;
                event.setCancelled(true);
                org.bukkit.event.HandlerList.unregisterAll(this);

                try {
                    int amount = Integer.parseInt(event.getMessage().trim());
                    if (amount <= 0) {
                        player.sendMessage("§cНеверное количество");
                        return;
                    }
                    doWithdraw(amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cВведите число");
                }
            }
        }, plugin);
    }

    private void doWithdraw(int amount) {
        player.sendMessage("§7[Debug] Отправляю запрос: ник=" + player.getName() + " сумма=" + amount);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            ApiClient.ApiResponse resp = plugin.getApiClient().withdraw(player.getName(), amount);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.sendMessage("§7[Debug] код=" + resp.code + " ответ=" + resp.data.toString());
                if (resp.isSuccess()) {
                    // Выдаём алмазы игроку
                    ItemStack diamonds = new ItemStack(Material.DIAMOND, amount);
                    player.getInventory().addItem(diamonds);
                    player.sendMessage("§aВы сняли §e" + amount + " §aалмазов. Новый баланс: §e" + resp.getInt("balance"));
                } else {
                    player.sendMessage("§cОшибка: " + resp.getMessage());
                }
            });
        });
    }

    private String getTimeLeft(String deadline) {
        if (deadline == null) return "§7Без срока";
        try {
            String dlStr = deadline.endsWith("Z") ? deadline : deadline + "Z";
            java.time.Instant dl = java.time.Instant.parse(dlStr);
            long diff = dl.getEpochSecond() - java.time.Instant.now().getEpochSecond();
            if (diff <= 0) return "§cПросрочен";

            long hours = diff / 3600;
            long minutes = (diff % 3600) / 60;

            if (hours >= 24) {
                long days = hours / 24;
                return "§e" + days + " дн. " + (hours % 24) + " ч.";
            } else if (hours > 0) {
                return "§e" + hours + " ч. " + minutes + " мин.";
            } else {
                return "§c" + minutes + " мин.";
            }
        } catch (Exception e) {
            return "§7" + deadline;
        }
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

    public record FineEntry(int id, int amount, String reason, String deadline, String status) {
    }
}

