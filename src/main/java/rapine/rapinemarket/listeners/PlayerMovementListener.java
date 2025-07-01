package rapine.rapinemarket.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.managers.RobberyManager;
import rapine.rapinemarket.models.MarketRegion;

public class PlayerMovementListener implements Listener {
    private final RapineMarket plugin;
    
    public PlayerMovementListener(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
       
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        
        
        MarketRegion fromRegion = plugin.getRegionManager().getRegionAt(from);
        MarketRegion toRegion = plugin.getRegionManager().getRegionAt(to);
        
        
        if (fromRegion != null && toRegion == null) {
            
            RobberyManager.RobberyData robbery = null;
            for (String marketName : plugin.getRegionManager().getRegions().keySet()) {
                if (marketName.equalsIgnoreCase(fromRegion.getName())) {
                    robbery = plugin.getRobberyManager().getRobberyForPlayer(player);
                    break;
                }
            }
            
           
            if (robbery != null) {
                
                if (player.getUniqueId().equals(robbery.getInitiator()) && !robbery.isDamageTaken()) {
                    plugin.getRobberyManager().cancelRobbery(fromRegion.getName());
                    player.sendMessage(ChatColor.RED + "ʜᴀɪ ʟᴀꜱᴄɪᴀᴛᴏ ɪʟ ᴍᴀʀᴋᴇᴛ. ʟᴀ ʀᴀᴘɪɴᴀ È ꜱᴛᴀᴛᴀ ᴀɴɴᴜʟʟᴀᴛᴀ!");
                    return;
                }
                
                
                if (robbery.getParticipants().contains(player.getUniqueId()) && 
                        !player.getUniqueId().equals(robbery.getHostage()) && 
                        !robbery.isDamageTaken()) {
                    plugin.getRobberyManager().cancelRobbery(fromRegion.getName());
                    player.sendMessage(ChatColor.RED + "ʜᴀɪ ʟᴀꜱᴄɪᴀᴛᴏ ɪʟ ᴍᴀʀᴋᴇᴛ. ʟᴀ ʀᴀᴘɪɴᴀ È ꜱᴛᴀᴛᴀ ᴀɴɴᴜʟʟᴀᴛᴀ!");
                    return;
                }
                
               
                if (robbery.getParticipants().contains(player.getUniqueId()) && 
                        !player.getUniqueId().equals(robbery.getHostage()) && 
                        robbery.isDamageTaken()) {
                    return;
                }
                
                
                if (player.getUniqueId().equals(robbery.getHostage())) {
                    plugin.getHologramManager().updateHostageHologramReleased(player);
                    player.sendMessage(ChatColor.GREEN + "ꜱᴇɪ ꜱᴛᴀᴛᴏ ʟɪʙᴇʀᴀᴛᴏ ᴄᴏᴍᴇ ᴏꜱᴛᴀɢɢɪᴏ!");
                    return;
                }
            }
        }
        
        
        if (fromRegion == null && toRegion != null) {
            
            RobberyManager.RobberyData robbery = null;
            String marketName = toRegion.getName().toLowerCase();
            
            for (String name : plugin.getRegionManager().getRegions().keySet()) {
                if (name.equalsIgnoreCase(marketName)) {
                    for (RobberyManager.RobberyData r : plugin.getRobberyManager().getActiveRobberies().values()) {
                        if (r.getRegion().getName().equalsIgnoreCase(marketName)) {
                            robbery = r;
                            break;
                        }
                    }
                    break;
                }
            }
            
            
            if (robbery != null) {
                
                if (plugin.getRobberyManager().isLawEnforcement(player)) {
                    return;
                }
                
                
                if (robbery.isDamageTaken()) {
                    return;
                }
                
                
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "ɴᴏɴ ᴘᴜᴏɪ ᴇɴᴛʀᴀʀᴇ ɴᴇʟ ᴍᴀʀᴋᴇᴛ ᴅᴜʀᴀɴᴛᴇ ᴜɴᴀ ʀᴀᴘɪɴᴀ!");
                return;
            }
        }
    }
} 