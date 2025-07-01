package rapine.rapinemarket.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import rapine.rapinemarket.RapineMarket;


public class RapineExpansion {

    private final RapineMarket plugin;

    /**
     * Constructor for the expansion
     *
     * @param plugin 
     */
    public RapineExpansion(RapineMarket plugin) {
        this.plugin = plugin;
    }

   
    public void register() {
        
        Plugin placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderAPI == null) {
            plugin.getLogger().warning("PlaceholderAPI not found! Placeholders will not be available.");
            return;
        }
        
        
        try {
            
            Object expansion = Class.forName("rapine.rapinemarket.placeholders.RapineExpansionImpl")
                .getConstructor(RapineMarket.class)
                .newInstance(plugin);
            
            
            expansion.getClass().getMethod("register").invoke(expansion);
            
            plugin.getLogger().info("PlaceholderAPI expansion registered successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register PlaceholderAPI expansion: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 