package rapine.rapinemarket.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class MarketRegion implements ConfigurationSerializable {
    private String name;
    private final String worldName;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private int safeX, safeY, safeZ;
    private boolean hasSafe;
    
    public MarketRegion(String name, String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.name = name;
        this.worldName = worldName;
        
        
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
        this.hasSafe = false;
    }
    
    public MarketRegion(String name, String worldName, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int safeX, int safeY, int safeZ, boolean hasSafe) {
        this.name = name;
        this.worldName = worldName;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.safeX = safeX;
        this.safeY = safeY;
        this.safeZ = safeZ;
        this.hasSafe = hasSafe;
    }
    
    public boolean contains(Location location) {
        if (location == null || !location.getWorld().getName().equals(worldName)) {
            return false;
        }
        
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        return x >= minX && x <= maxX && 
               y >= minY && y <= maxY && 
               z >= minZ && z <= maxZ;
    }
    
    public void setSafeLocation(int x, int y, int z) {
        this.safeX = x;
        this.safeY = y;
        this.safeZ = z;
        this.hasSafe = true;
    }
    
    public boolean hasSafe() {
        return hasSafe;
    }
    
    public Location getSafeLocation() {
        if (!hasSafe) {
            return null;
        }
        World world = getWorld();
        if (world == null) {
            return null;
        }
        return new Location(world, safeX, safeY, safeZ);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
    
    public int getMinX() {
        return minX;
    }
    
    public int getMinY() {
        return minY;
    }
    
    public int getMinZ() {
        return minZ;
    }
    
    public int getMaxX() {
        return maxX;
    }
    
    public int getMaxY() {
        return maxY;
    }
    
    public int getMaxZ() {
        return maxZ;
    }
    
    public int getSafeX() {
        return safeX;
    }
    
    public int getSafeY() {
        return safeY;
    }
    
    public int getSafeZ() {
        return safeZ;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("world", worldName);
        result.put("min-x", minX);
        result.put("min-y", minY);
        result.put("min-z", minZ);
        result.put("max-x", maxX);
        result.put("max-y", maxY);
        result.put("max-z", maxZ);
        result.put("safe-x", safeX);
        result.put("safe-y", safeY);
        result.put("safe-z", safeZ);
        result.put("has-safe", hasSafe);
        return result;
    }
    
    public static MarketRegion deserialize(Map<String, Object> map) {
        String name = (String) map.get("name");
        String worldName = (String) map.get("world");
        int minX = (int) map.get("min-x");
        int minY = (int) map.get("min-y");
        int minZ = (int) map.get("min-z");
        int maxX = (int) map.get("max-x");
        int maxY = (int) map.get("max-y");
        int maxZ = (int) map.get("max-z");
        int safeX = (int) map.get("safe-x");
        int safeY = (int) map.get("safe-y");
        int safeZ = (int) map.get("safe-z");
        boolean hasSafe = (boolean) map.get("has-safe");
        
        return new MarketRegion(name, worldName, minX, minY, minZ, maxX, maxY, maxZ, safeX, safeY, safeZ, hasSafe);
    }
    
    public static MarketRegion valueOf(Map<String, Object> map) {
        return deserialize(map);
    }
} 