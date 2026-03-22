/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package dev.serverpanel.plugin.managers;

import dev.serverpanel.plugin.ServerPanelPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AtmManager {
    private final ServerPanelPlugin plugin;
    private final Set<String> atmLocations = new HashSet<String>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public AtmManager(ServerPanelPlugin plugin) {
        this.plugin = plugin;
        this.load();
    }

    private void load() {
        this.dataFile = new File(this.plugin.getDataFolder(), "atm_locations.yml");
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration((File)this.dataFile);
        if (this.dataConfig.getStringList("locations") != null) {
            this.atmLocations.addAll(this.dataConfig.getStringList("locations"));
        }
    }

    private void save() {
        this.dataConfig.set("locations", this.atmLocations.stream().toList());
        try {
            this.dataConfig.save(this.dataFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().warning("Failed to save ATM locations: " + e.getMessage());
        }
    }

    public void addAtm(Location loc) {
        this.atmLocations.add(this.locKey(loc));
        this.save();
    }

    public void removeAtm(Location loc) {
        this.atmLocations.remove(this.locKey(loc));
        this.save();
    }

    public boolean isAtm(Location loc) {
        return this.atmLocations.contains(this.locKey(loc));
    }

    private String locKey(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}

