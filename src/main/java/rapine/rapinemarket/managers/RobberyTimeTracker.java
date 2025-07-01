package rapine.rapinemarket.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import rapine.rapinemarket.RapineMarket;

public class RobberyTimeTracker {
    private final RapineMarket plugin;
    private final Map<String, Integer> robberyTimes = new HashMap<>();
    
    public RobberyTimeTracker(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Sets the remaining time for a robbery
     * 
     * @param marketName The name of the market
     * @param seconds The remaining seconds
     */
    public void setRemainingTime(String marketName, int seconds) {
        robberyTimes.put(marketName.toLowerCase(), seconds);
    }
    
    /**
     * Decrements the remaining time for a robbery by 1 second
     * 
     * @param marketName The name of the market
     */
    public void decrementTime(String marketName) {
        String key = marketName.toLowerCase();
        if (robberyTimes.containsKey(key)) {
            int time = robberyTimes.get(key);
            if (time > 0) {
                robberyTimes.put(key, time - 1);
            } else {
                robberyTimes.remove(key);
            }
        }
    }
    
    /**
     * Gets the remaining time for a robbery
     * 
     * @param marketName The name of the market
     * @return The remaining seconds, or 0 if no robbery is in progress
     */
    public int getRemainingTime(String marketName) {
        String key = marketName.toLowerCase();
        return robberyTimes.getOrDefault(key, 0);
    }
    
    /**
     * Removes a robbery from tracking
     * 
     * @param marketName The name of the market
     */
    public void removeRobbery(String marketName) {
        robberyTimes.remove(marketName.toLowerCase());
    }
    
    /**
     * Gets the remaining time for a player's robbery
     * 
     * @param player The player
     * @return The remaining seconds, or 0 if the player is not in a robbery
     */
    public int getRemainingTimeForPlayer(Player player) {
        RobberyManager.RobberyData robbery = plugin.getRobberyManager().getRobberyForPlayer(player);
        if (robbery != null) {
            return getRemainingTime(robbery.getRegion().getName());
        }
        return 0;
    }
    
    /**
     * Gets the formatted remaining time for a player's robbery
     * 
     * @param player The player
     * @return A formatted string with the remaining time, or "Rapina non in corso" if no robbery is in progress
     */
    public String getFormattedTimeForPlayer(Player player) {
        int seconds = getRemainingTimeForPlayer(player);
        
        if (seconds <= 0) {
            return "ʀᴀᴘɪɴᴀ ɴᴏɴ ɪɴ ᴄᴏʀꜱᴏ";
        }
        
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        
        return String.format("%d" + "§7" + "ᴍ " + "§f" + "%02d" + "§7" + "s", minutes, remainingSeconds);
    }
} 