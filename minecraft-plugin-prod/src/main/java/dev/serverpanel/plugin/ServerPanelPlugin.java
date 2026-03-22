package dev.serverpanel.plugin;

import dev.serverpanel.plugin.api.ApiClient;
import dev.serverpanel.plugin.commands.AcceptInviteCommand;
import dev.serverpanel.plugin.commands.AdminBankCommand;
import dev.serverpanel.plugin.commands.BankCommand;
import dev.serverpanel.plugin.commands.DeclineInviteCommand;
import dev.serverpanel.plugin.commands.FineCommand;
import dev.serverpanel.plugin.commands.InviteCommand;
import dev.serverpanel.plugin.commands.SetAtmCommand;
import dev.serverpanel.plugin.commands.UnwarnCommand;
import dev.serverpanel.plugin.commands.WarnCommand;
import dev.serverpanel.plugin.http.BanHttpServer;
import dev.serverpanel.plugin.listeners.AtmListener;
import dev.serverpanel.plugin.listeners.PlayerSessionListener;
import dev.serverpanel.plugin.managers.AtmManager;
import dev.serverpanel.plugin.managers.FineCheckTask;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerPanelPlugin extends JavaPlugin {
    private static ServerPanelPlugin instance;
    private ApiClient apiClient;
    private AtmManager atmManager;
    private BanHttpServer banHttpServer;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        String apiUrl = this.getConfig().getString("api.url", "http://localhost:8000");
        String apiSecret = this.getConfig().getString("api.secret", "changeme");
        this.apiClient = new ApiClient(this, apiUrl, apiSecret);
        this.atmManager = new AtmManager(this);
        this.getCommand("fine").setExecutor((CommandExecutor)new FineCommand(this));
        this.getCommand("warn").setExecutor((CommandExecutor)new WarnCommand(this));
        this.getCommand("unwarn").setExecutor((CommandExecutor)new UnwarnCommand(this));
        this.getCommand("bank").setExecutor((CommandExecutor)new BankCommand(this));
        this.getCommand("setatm").setExecutor((CommandExecutor)new SetAtmCommand(this));
        this.getCommand("adminbank").setExecutor((CommandExecutor)new AdminBankCommand(this));
        this.getCommand("invite").setExecutor((CommandExecutor)new InviteCommand(this));
        this.getCommand("acceptinvite").setExecutor((CommandExecutor)new AcceptInviteCommand(this.apiClient));
        this.getCommand("declineinvite").setExecutor((CommandExecutor)new DeclineInviteCommand(this.apiClient));
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerSessionListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AtmListener(this), (Plugin)this);
        int interval = this.getConfig().getInt("fines.check-interval", 60) * 20;
        new FineCheckTask(this).runTaskTimerAsynchronously((Plugin)this, interval, interval);
        int banPort = this.getConfig().getInt("ban-api.port", 8080);
        this.banHttpServer = new BanHttpServer(this, apiSecret);
        try {
            this.banHttpServer.start(banPort);
        }
        catch (Exception e) {
            this.getLogger().severe("Failed to start ban HTTP server: " + e.getMessage());
        }
        this.getLogger().info("ServerPanel plugin enabled!");
        this.getLogger().info("Connecting to API: " + apiUrl);
    }

    public void onDisable() {
        if (this.banHttpServer != null) {
            this.banHttpServer.stop();
        }
        this.getLogger().info("ServerPanel plugin disabled.");
    }

    public static ServerPanelPlugin getInstance() {
        return instance;
    }

    public ApiClient getApiClient() {
        return this.apiClient;
    }

    public AtmManager getAtmManager() {
        return this.atmManager;
    }

    public String prefix() {
        return this.getConfig().getString("messages.prefix", "&8[&bServerPanel&8] &r").replace("&", "\u00a7");
    }
}
