package rapine.rapinemarket.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import rapine.rapinemarket.RapineMarket;

public class RegionSelectionListener implements Listener {
    private final RapineMarket plugin;
    
    public RegionSelectionListener(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        
        if (item != null && plugin.getItemManager().isMarketSelector(item)) {
            event.setCancelled(true); 
            
            
            Block block = event.getClickedBlock();
            if (block == null || block.getType() == Material.AIR) {
                return;
            }
            
            Location location = block.getLocation();
            
            
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                
                plugin.getRegionManager().setSelectionPoint(player, location, false);
                player.sendMessage(ChatColor.AQUA + "Primo punto selezionato: " + 
                        ChatColor.GREEN + location.getBlockX() + ", " + 
                        location.getBlockY() + ", " + location.getBlockZ());
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                
                plugin.getRegionManager().setSelectionPoint(player, location, true);
                player.sendMessage(ChatColor.AQUA + "Secondo punto selezionato: " + 
                        ChatColor.GREEN + location.getBlockX() + ", " + 
                        location.getBlockY() + ", " + location.getBlockZ());
                
                
                if (plugin.getRegionManager().hasCompleteSelection(player)) {
                    player.sendMessage(ChatColor.GREEN + "Selezione completa! " + 
                            ChatColor.AQUA + "Usa " + ChatColor.GREEN + "/market create <nome_market>" + 
                            ChatColor.AQUA + " per creare il market.");
                }
            }
        }
    }
} 