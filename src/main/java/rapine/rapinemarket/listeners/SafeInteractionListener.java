package rapine.rapinemarket.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.models.MarketRegion;

public class SafeInteractionListener implements Listener {
    private final RapineMarket plugin;
    
    public SafeInteractionListener(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();
        
        
        if (plugin.getItemManager().isSafe(item)) {
            
            MarketRegion region = plugin.getRegionManager().getRegionAt(block.getLocation());
            if (region == null) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "La cassaforte può essere piazzata solo all'interno di un market.");
                return;
            }
            
            
            region.setSafeLocation(block.getX(), block.getY(), block.getZ());
            plugin.getRegionManager().saveRegion(region);
            
            player.sendMessage(ChatColor.GREEN + "Cassaforte piazzata con successo!");
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        
        if (block != null && block.getType() == Material.PRISMARINE_STAIRS) {
            
            MarketRegion region = plugin.getRegionManager().getRegionAt(block.getLocation());
            if (region == null) {
                return;
            }
            
            
            if (!plugin.getRobberyManager().canStartRobbery(region.getName())) {
                player.sendMessage(ChatColor.RED + "ɴᴏɴ È ᴘᴏꜱꜱɪʙɪʟᴇ ᴀᴠᴠɪᴀʀᴇ ᴜɴᴀ ʀᴀᴘɪɴᴀ ɪɴ Qᴜᴇꜱᴛᴏ ᴍᴏᴍᴇɴᴛᴏ. ᴘᴏᴛʀᴇʙʙᴇ ᴇꜱꜱᴇʀᴄɪ ᴜɴ ᴄᴏᴏʟᴅᴏᴡɴ ᴀᴛᴛɪᴠᴏ (30 ᴍɪɴᴜᴛɪ) ᴏ ᴜɴᴀ ʀᴀᴘɪɴᴀ ɢɪÀ ɪɴ ᴄᴏʀꜱᴏ.");
                event.setCancelled(true);
                return;
            }
            
            
            openRobberyConfirmationGUI(player, region);
            event.setCancelled(true);
        }
    }
    
    private void openRobberyConfirmationGUI(Player player, MarketRegion region) {
        
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.RED + "" + ChatColor.BOLD + "Vuoi rapinare il market?");
        
        
        ItemStack yesItem = plugin.getItemManager().createStartRobberyButton();
        ItemStack noItem = plugin.getItemManager().createCancelRobberyButton();
        ItemStack infoItem = plugin.getItemManager().createInfoRobberyButton();
        
        
        inventory.setItem(11, yesItem);
        inventory.setItem(15, noItem);
        inventory.setItem(13, infoItem);
        
        
        player.setMetadata("robbery_region", new org.bukkit.metadata.FixedMetadataValue(plugin, region.getName()));
        
        
        player.openInventory(inventory);
    }
} 