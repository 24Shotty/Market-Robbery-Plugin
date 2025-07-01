package rapine.rapinemarket.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.models.MarketRegion;

public class RegionManager {
    private final RapineMarket plugin;
    private final Map<String, MarketRegion> regions;
    private final Map<UUID, Location[]> playerSelections;
    private final Gson gson;
    private final File regionsDir;
    
    public RegionManager(RapineMarket plugin) {
        this.plugin = plugin;
        this.regions = new HashMap<>();
        this.playerSelections = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.regionsDir = new File(plugin.getDataFolder(), "regions");
        
        loadAllRegions();
    }
    
    public void setSelectionPoint(Player player, Location location, boolean isSecondPoint) {
        UUID playerUUID = player.getUniqueId();
        Location[] selection = playerSelections.getOrDefault(playerUUID, new Location[2]);
        
        if (isSecondPoint) {
            selection[1] = location;
        } else {
            selection[0] = location;
        }
        
        playerSelections.put(playerUUID, selection);
    }
    
    public boolean hasCompleteSelection(Player player) {
        UUID playerUUID = player.getUniqueId();
        Location[] selection = playerSelections.get(playerUUID);
        return selection != null && selection[0] != null && selection[1] != null;
    }
    
    public Location[] getPlayerSelection(Player player) {
        return playerSelections.get(player.getUniqueId());
    }
    
    public boolean createMarketRegion(String name, Player player) {
        if (!hasCompleteSelection(player)) {
            return false;
        }
        
        Location[] selection = getPlayerSelection(player);
        World world = selection[0].getWorld();
        
        if (!world.equals(selection[1].getWorld())) {
            return false;
        }
        
        MarketRegion region = new MarketRegion(
                name,
                world.getName(),
                selection[0].getBlockX(),
                selection[0].getBlockY(),
                selection[0].getBlockZ(),
                selection[1].getBlockX(),
                selection[1].getBlockY(),
                selection[1].getBlockZ()
        );
        
        regions.put(name.toLowerCase(), region);
        
        saveRegion(region);
        
        return true;
    }
    
    public boolean isInMarketRegion(Location location) {
        for (MarketRegion region : regions.values()) {
            if (region.contains(location)) {
                return true;
            }
        }
        return false;
    }
    
    public MarketRegion getRegionAt(Location location) {
        for (MarketRegion region : regions.values()) {
            if (region.contains(location)) {
                return region;
            }
        }
        return null;
    }
    
    public Set<Player> getPlayersInRegion(MarketRegion region) {
        Set<Player> players = new HashSet<>();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (region.contains(player.getLocation())) {
                players.add(player);
            }
        }
        
        return players;
    }
    
    public void loadAllRegions() {
        regions.clear();
        
        if (!regionsDir.exists()) {
            regionsDir.mkdirs();
            return;
        }
        
        File[] files = regionsDir.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                MarketRegion region = gson.fromJson(reader, MarketRegion.class);
                if (region != null) {
                    regions.put(region.getName().toLowerCase(), region);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load region from file: " + file.getName());
                e.printStackTrace();
            }
        }
        
        plugin.getLogger().info("Loaded " + regions.size() + " market regions.");
    }
    
    public void saveRegion(MarketRegion region) {
        if (!regionsDir.exists()) {
            regionsDir.mkdirs();
        }
        
        File file = new File(regionsDir, region.getName().toLowerCase() + ".json");
        
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(region, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save region to file: " + region.getName());
            e.printStackTrace();
        }
    }
    
    public void saveAllRegions() {
        for (MarketRegion region : regions.values()) {
            saveRegion(region);
        }
    }
    
    public Map<String, MarketRegion> getRegions() {
        return regions;
    }
    
    public boolean renameMarket(String oldName, String newName) {
        String oldNameLower = oldName.toLowerCase();
        String newNameLower = newName.toLowerCase();
        
        if (!regions.containsKey(oldNameLower)) {
            return false;
        }
        
        if (regions.containsKey(newNameLower)) {
            return false;
        }
        
        MarketRegion region = regions.get(oldNameLower);
        
        region.setName(newName);
        
        regions.remove(oldNameLower);
        regions.put(newNameLower, region);
        
        File oldFile = new File(regionsDir, oldNameLower + ".json");
        if (oldFile.exists()) {
            oldFile.delete();
        }
        
        saveRegion(region);
        
        return true;
    }
} 