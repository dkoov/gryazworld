package ru.gryazworld.portal;

import org.bukkit.plugin.java.JavaPlugin;

public class PortalPlugin extends JavaPlugin {

    private PortalManager portalManager;
    private ApiClient apiClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        apiClient    = new ApiClient(this);
        portalManager = new PortalManager(this);

        getServer().getPluginManager().registerEvents(new PortalListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Команда /portalreload
        getCommand("portalreload").setExecutor(new PortalCommand(this));

        // Загружаем порталы этого сервера из API через 2 секунды после старта
        getServer().getScheduler().runTaskLater(this,
                () -> portalManager.loadPortalsFromApi(), 40L);

        getLogger().info("PortalPlugin включён. Сервер: " + getConfig().getString("server-name"));
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        getLogger().info("PortalPlugin отключён.");
    }

    public PortalManager getPortalManager() { return portalManager; }
    public ApiClient     getApiClient()     { return apiClient; }
}
