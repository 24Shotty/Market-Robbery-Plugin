package rapine.rapinemarket.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.managers.RobberyManager;
import rapine.rapinemarket.models.MarketRegion;

public class RobberyListener implements Listener {
    private final RapineMarket plugin;
    
    public RobberyListener(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.RED + "" + ChatColor.BOLD + "Vuoi rapinare il market?")) {
            event.setCancelled(true);
            
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }
            
            String regionName = null;
            List<MetadataValue> metaValues = player.getMetadata("robbery_region");
            if (!metaValues.isEmpty()) {
                regionName = metaValues.get(0).asString();
            }
            
            if (regionName == null) {
                player.closeInventory();
                return;
            }
            
            MarketRegion region = plugin.getRegionManager().getRegions().get(regionName.toLowerCase());
            if (region == null) {
                player.closeInventory();
                return;
            }
            
            if (clickedItem.getType() == Material.STICK && clickedItem.getItemMeta().hasCustomModelData()) {
                int modelData = clickedItem.getItemMeta().getCustomModelData();
                
                if (modelData == plugin.getItemManager().getStartRobberyModelData()) {
                    player.closeInventory();
                    
                    if (!plugin.getRobberyManager().hasWeapon(player)) {
                        player.sendMessage(ChatColor.RED + "ᴅᴇᴠɪ ᴀᴠᴇʀᴇ ᴜɴ'ᴀʀᴍᴀ ɴᴇʟʟ'ɪɴᴠᴇɴᴛᴀʀɪᴏ ᴘᴇʀ ɪɴɪᴢɪᴀʀᴇ ᴜɴᴀ ʀᴀᴘɪɴᴀ.");
                        return;
                    }
                    
                    if (!plugin.getRobberyManager().hasEnoughLawEnforcement()) {
                        player.sendMessage(ChatColor.RED + "ɴᴏɴ ᴄɪ ꜱᴏɴᴏ ᴀʙʙᴀꜱᴛᴀɴᴢᴀ ꜰᴏʀᴢᴇ ᴅᴇʟʟ'ᴏʀᴅɪɴᴇ ᴏɴʟɪɴᴇ (ᴍɪɴɪᴍᴏ 2).");
                        return;
                    }
                    
                    boolean success = plugin.getRobberyManager().startRobbery(player, region);
                    
                    if (success) {
                        player.sendMessage(ChatColor.GREEN + "ʀᴀᴘɪɴᴀ ɪɴɪᴢɪᴀᴛᴀ! " + 
                                ChatColor.GREEN + "ᴜꜱᴀ " + ChatColor.GREEN + "/ᴏꜱᴛᴀɢɢɪᴏ <ᴘʟᴀʏᴇʀ>" + 
                                ChatColor.GREEN + " ᴘᴇʀ ᴅᴇꜱɪɢɴᴀʀᴇ ᴜɴ ᴏꜱᴛᴀɢɢɪᴏ.");
                    } else {
                        player.sendMessage(ChatColor.RED + "ɴᴏɴ È ꜱᴛᴀᴛᴏ ᴘᴏꜱꜱɪʙɪʟᴇ ᴀᴠᴠɪᴀʀᴇ ʟᴀ ʀᴀᴘɪɴᴀ.");
                    }
                } else if (modelData == plugin.getItemManager().getCancelRobberyModelData()) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "ʀᴀᴘɪɴᴀ ᴀɴɴᴜʟʟᴀᴛᴀ.");
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(ChatColor.RED + "" + ChatColor.BOLD + "Vuoi rapinare il market?")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getRobberyManager().isInActiveRobbery(player)) {
            RobberyManager.RobberyData robbery = plugin.getRobberyManager().getRobberyForPlayer(player);
            
            if (robbery != null && !plugin.getRobberyManager().canLeaveRobbery(player)) {
                plugin.getRobberyManager().cancelRobbery(robbery.getRegion().getName());
            }
            
            if (robbery != null && player.getUniqueId().equals(robbery.getHostage())) {
                plugin.getHologramManager().removeHostageHologram(player);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        if (plugin.getRobberyManager().isInActiveRobbery(player)) {
            RobberyManager.RobberyData robbery = plugin.getRobberyManager().getRobberyForPlayer(player);
            
            if (robbery != null && player.getUniqueId().equals(robbery.getHostage())) {
                plugin.getHologramManager().removeHostageHologram(player);
            }
            
            if (robbery != null && player.getUniqueId().equals(robbery.getInitiator())) {
                plugin.getRobberyManager().cancelRobbery(robbery.getRegion().getName());
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        if (plugin.getRobberyManager().isInActiveRobbery(player)) {
            RobberyManager.RobberyData robbery = plugin.getRobberyManager().getRobberyForPlayer(player);
            
            if (robbery != null) {
                plugin.getRobberyManager().setDamageTaken(robbery.getRegion().getName(), true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getRobberyManager().isInActiveRobbery(player)) {
            RobberyManager.RobberyData robbery = plugin.getRobberyManager().getRobberyForPlayer(player);
            
            if (robbery != null && player.getUniqueId().equals(robbery.getInitiator())) {
                Location destination = event.getTo();
                MarketRegion destinationRegion = plugin.getRegionManager().getRegionAt(destination);
                
                if (destinationRegion == null || !destinationRegion.getName().equalsIgnoreCase(robbery.getRegion().getName())) {
                    plugin.getRobberyManager().cancelRobbery(robbery.getRegion().getName());
                    player.sendMessage(ChatColor.RED + "ꜱᴇɪ ꜱᴛᴀᴛᴏ ᴍᴇꜱꜱᴏ ɪɴ ᴘʀɪɢɪᴏɴᴇ, ʟᴀ ʀᴀᴘɪɴᴀ È ꜱᴛᴀᴛᴀ ᴀɴɴᴜʟʟᴀᴛᴀ!");
                }
            }
            
            if (robbery != null && player.getUniqueId().equals(robbery.getHostage())) {
                plugin.getHologramManager().removeHostageHologram(player);
                plugin.getHologramManager().createHostageHologram(player);
            }
        }
    }
} 