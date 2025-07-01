package rapine.rapinemarket.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.models.MarketRegion;

public class HologramManager {
    private final RapineMarket plugin;
    private final Map<UUID, ArmorStand> hostageHolograms;
    private final Map<String, List<ArmorStand>> robberyHolograms;
    private final Map<String, Location> hologramLocations;
    private final File hologramsFile;
    
    public HologramManager(RapineMarket plugin) {
        this.plugin = plugin;
        this.hostageHolograms = new HashMap<>();
        this.robberyHolograms = new HashMap<>();
        this.hologramLocations = new HashMap<>();
        this.hologramsFile = new File(plugin.getDataFolder(), "holograms.yml");
        
        loadHologramLocations();
    }
    
    /**
     * Creates a hologram above a player's head to indicate they are a hostage
     * 
     * @param player The player who is a hostage
     */
    public void createHostageHologram(Player player) {
        removeHostageHologram(player);
        
        ArmorStand hologram = (ArmorStand) player.getWorld().spawnEntity(
                player.getLocation().add(0, 2.2, 0), 
                EntityType.ARMOR_STAND);
        
        hologram.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "ᴏsᴛᴀɢɢɪᴏ");
        hologram.setCustomNameVisible(true);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setMarker(true);
        hologram.setSmall(true);
        
        hostageHolograms.put(player.getUniqueId(), hologram);
        
        startFollowingPlayer(player, hologram);
    }
    
    /**
     * Updates the hostage hologram when the hostage is released
     * 
     * @param player The player who was a hostage
     */
    public void updateHostageHologramReleased(Player player) {
        UUID playerUuid = player.getUniqueId();
        if (hostageHolograms.containsKey(playerUuid)) {
            ArmorStand hologram = hostageHolograms.get(playerUuid);
            if (hologram != null && !hologram.isDead()) {
                hologram.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "ᴏsᴛᴀɢɢɪᴏ ʀɪʟᴀsᴄɪᴀᴛᴏ");
                
                hologram.teleport(player.getLocation().add(0, 2.2, 0));
                
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (!player.isOnline() || hologram.isDead() || !hostageHolograms.containsKey(player.getUniqueId())) {
                        task.cancel();
                        if (!hologram.isDead()) {
                            hologram.remove();
                        }
                        hostageHolograms.remove(player.getUniqueId());
                        return;
                    }
                    
                    hologram.teleport(player.getLocation().add(0, 2.2, 0));
                }, 0L, 1L);
            }
        }
    }
    
    /**
     * Removes the hostage hologram from a player
     * 
     * @param player The player whose hologram should be removed
     */
    public void removeHostageHologram(Player player) {
        UUID playerUuid = player.getUniqueId();
        if (hostageHolograms.containsKey(playerUuid)) {
            ArmorStand hologram = hostageHolograms.get(playerUuid);
            if (hologram != null && !hologram.isDead()) {
                hologram.remove();
            }
            hostageHolograms.remove(playerUuid);
        }
    }
    
    /**
     * Removes all active holograms
     */
    public void removeAllHolograms() {
        for (ArmorStand hologram : hostageHolograms.values()) {
            if (hologram != null && !hologram.isDead()) {
                hologram.remove();
            }
        }
        hostageHolograms.clear();
        
        for (List<ArmorStand> holograms : robberyHolograms.values()) {
            for (ArmorStand hologram : holograms) {
                if (hologram != null && !hologram.isDead()) {
                    hologram.remove();
                }
            }
        }
        robberyHolograms.clear();
    }
    
    /**
     * Makes the hologram follow the player
     * 
     * @param player The player to follow
     * @param hologram The hologram that should follow the player
     */
    private void startFollowingPlayer(Player player, ArmorStand hologram) {
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!player.isOnline() || hologram.isDead() || !hostageHolograms.containsKey(player.getUniqueId())) {
                task.cancel();
                if (!hologram.isDead()) {
                    hologram.remove();
                }
                hostageHolograms.remove(player.getUniqueId());
                return;
            }
            
            hologram.teleport(player.getLocation().add(0, 2.2, 0));
        }, 0L, 1L);
    }
    
    /**
     * Sets the location of a robbery hologram for a specific market
     * 
     * @param marketName The name of the market
     * @param location The location for the hologram
     * @return true if the hologram was set successfully, false otherwise
     */
    public boolean setHologramLocation(String marketName, Location location) {
        MarketRegion region = plugin.getRegionManager().getRegionAt(location);
        if (region == null || !region.getName().equalsIgnoreCase(marketName)) {
            return false;
        }
        
        hologramLocations.put(marketName.toLowerCase(), location.clone());
        saveHologramLocations();
        return true;
    }
    
    /**
     * Moves an existing robbery hologram to a new location
     * 
     * @param marketName The name of the market
     * @param location The new location for the hologram
     * @return true if the hologram was moved successfully, false otherwise
     */
    public boolean moveHologramLocation(String marketName, Location location) {
        String key = marketName.toLowerCase();
        
        if (!hologramLocations.containsKey(key)) {
            return false;
        }
        
        MarketRegion region = plugin.getRegionManager().getRegionAt(location);
        if (region == null || !region.getName().equalsIgnoreCase(marketName)) {
            return false;
        }
        
        hologramLocations.put(key, location.clone());
        saveHologramLocations();
        
        if (robberyHolograms.containsKey(key)) {
            removeRobberyHologram(marketName);
            createRobberyHologram(marketName);
        }
        
        return true;
    }
    
    /**
     * Removes a robbery hologram location
     * 
     * @param marketName The name of the market
     * @return true if the hologram was removed successfully, false otherwise
     */
    public boolean removeHologramLocation(String marketName) {
        String key = marketName.toLowerCase();
        
        if (!hologramLocations.containsKey(key)) {
            return false;
        }
        
        removeRobberyHologram(marketName);
        
        hologramLocations.remove(key);
        saveHologramLocations();
        
        return true;
    }
    
    /**
     * Gets a list of all markets with hologram locations
     * 
     * @return A list of market names
     */
    public List<String> getHologramMarkets() {
        return new ArrayList<>(hologramLocations.keySet());
    }
    
    /**
     * Gets the hologram location for a market
     * 
     * @param marketName The name of the market
     * @return The location of the hologram, or null if not set
     */
    public Location getHologramLocation(String marketName) {
        return hologramLocations.get(marketName.toLowerCase());
    }
    
    /**
     * Creates a robbery hologram for a market
     * 
     * @param marketName The name of the market
     * @return true if the hologram was created successfully, false otherwise
     */
    public boolean createRobberyHologram(String marketName) {
        String key = marketName.toLowerCase();
        
        if (!hologramLocations.containsKey(key)) {
            return false;
        }
        
        removeRobberyHologram(marketName);
        
        Location location = hologramLocations.get(key);
        
        List<ArmorStand> holograms = new ArrayList<>();
        
        ArmorStand line1 = createHologramLine(location.clone().add(0, 0.75, 0), 
                ChatColor.DARK_AQUA + "ʀᴀᴘɪɴᴀ ᴍᴀʀᴋᴇᴛ");
        holograms.add(line1);
        
        ArmorStand line2 = createHologramLine(location.clone().add(0, 0.5, 0), 
                ChatColor.AQUA + marketName);
        holograms.add(line2);
        
        ArmorStand line3 = createHologramLine(location.clone().add(0, 0.25, 0), 
                ChatColor.AQUA + "%tempo_rimanente%");
        holograms.add(line3);
        
        robberyHolograms.put(key, holograms);
        
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!robberyHolograms.containsKey(key)) {
                task.cancel();
                return;
            }
            
            if (!plugin.getRobberyManager().getActiveRobberies().containsKey(marketName)) {
                task.cancel();
                removeRobberyHologram(marketName);
                return;
            }
            
            ArmorStand countdownLine = holograms.get(2);
            int remainingTime = plugin.getRobberyManager().getTimeTracker().getRemainingTime(marketName);
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            countdownLine.setCustomName(ChatColor.WHITE + String.format("%d", minutes) + 
                                       ChatColor.GRAY + "ᴍ " + 
                                       ChatColor.WHITE + String.format("%02d", seconds) + 
                                       ChatColor.GRAY + "s");
        }, 0L, 20L);
        
        return true;
    }
    
    /**
     * Removes a robbery hologram
     * 
     * @param marketName The name of the market
     */
    public void removeRobberyHologram(String marketName) {
        String key = marketName.toLowerCase();
        
        if (!robberyHolograms.containsKey(key)) {
            return;
        }
        
        for (ArmorStand hologram : robberyHolograms.get(key)) {
            if (hologram != null && !hologram.isDead()) {
                hologram.remove();
            }
        }
        
        robberyHolograms.remove(key);
    }
    
    /**
     * Creates a single hologram line
     * 
     * @param location The location for the hologram
     * @param text The text to display
     * @return The created armor stand
     */
    private ArmorStand createHologramLine(Location location, String text) {
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(
                location, 
                EntityType.ARMOR_STAND);
        
        hologram.setCustomName(text);
        hologram.setCustomNameVisible(true);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setMarker(true);
        hologram.setSmall(true);
        
        return hologram;
    }
    
    /**
     * Loads hologram locations from the config file
     */
    private void loadHologramLocations() {
        if (!hologramsFile.exists()) {
            return;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(hologramsFile);
        
        for (String key : config.getKeys(false)) {
            String worldName = config.getString(key + ".world");
            World world = Bukkit.getWorld(worldName);
            
            if (world == null) {
                plugin.getLogger().warning("Could not find world " + worldName + " for hologram " + key);
                continue;
            }
            
            double x = config.getDouble(key + ".x");
            double y = config.getDouble(key + ".y");
            double z = config.getDouble(key + ".z");
            
            Location location = new Location(world, x, y, z);
            hologramLocations.put(key.toLowerCase(), location);
        }
    }
    
    /**
     * Saves hologram locations to the config file
     */
    private void saveHologramLocations() {
        FileConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<String, Location> entry : hologramLocations.entrySet()) {
            String key = entry.getKey();
            Location location = entry.getValue();
            
            config.set(key + ".world", location.getWorld().getName());
            config.set(key + ".x", location.getX());
            config.set(key + ".y", location.getY());
            config.set(key + ".z", location.getZ());
        }
        
        try {
            config.save(hologramsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save hologram locations: " + e.getMessage());
        }
    }
} 