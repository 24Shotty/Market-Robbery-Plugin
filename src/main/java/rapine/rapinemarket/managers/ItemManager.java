package rapine.rapinemarket.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import rapine.rapinemarket.RapineMarket;

public class ItemManager {
    private final RapineMarket plugin;
    private final NamespacedKey selectorKey;
    private final NamespacedKey safeKey;
    private final NamespacedKey dirtyMoneyKey;
    private final int safeModelData;
    private final int dirtyMoneyModelData = 30; 
    private final int startRobberyModelData = 258; 
    private final int infoRobberyModelData = 153; 
    private final int cancelRobberyModelData = 257; 
    
    public ItemManager(RapineMarket plugin) {
        this.plugin = plugin;
        this.selectorKey = new NamespacedKey(plugin, "market_selector");
        this.safeKey = new NamespacedKey(plugin, "safe");
        this.dirtyMoneyKey = new NamespacedKey(plugin, "dirty_money");
        
        
        Random random = new Random();
        this.safeModelData = random.nextInt(90000) + 10000;
        
        
        plugin.getConfig().set("items.safe.model_data", safeModelData);
        plugin.getConfig().set("items.dirty_money.model_data", dirtyMoneyModelData);
        plugin.getConfig().set("items.start_robbery.model_data", startRobberyModelData);
        plugin.getConfig().set("items.info_robbery.model_data", infoRobberyModelData);
        plugin.getConfig().set("items.cancel_robbery.model_data", cancelRobberyModelData);
        plugin.saveConfig();
    }
    
    public ItemStack createMarketSelector() {
        ItemStack selector = new ItemStack(Material.WOODEN_AXE);
        ItemMeta meta = selector.getItemMeta();
        
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Market Selector");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Left-click to select the first position");
        lore.add(ChatColor.GREEN + "Right-click to select the second position");
        meta.setLore(lore);
        
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(selectorKey, PersistentDataType.BYTE, (byte) 1);
        
        selector.setItemMeta(meta);
        return selector;
    }
    
    public ItemStack createSafe() {
        ItemStack safe = new ItemStack(Material.PRISMARINE_STAIRS);
        ItemMeta meta = safe.getItemMeta();
        
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cassaforte");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Piazza questa cassaforte in un market");
        lore.add(ChatColor.YELLOW + "per permettere le rapine.");
        meta.setLore(lore);
        
        meta.setCustomModelData(safeModelData);
        meta.getPersistentDataContainer().set(safeKey, PersistentDataType.BYTE, (byte) 1);
        
        safe.setItemMeta(meta);
        return safe;
    }
    
    public ItemStack createDirtyMoney(int amount) {
        ItemStack money = new ItemStack(Material.STICK, amount);
        ItemMeta meta = money.getItemMeta();
        
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "ꜱᴏʟᴅɪ ꜱᴘᴏʀᴄʜɪ");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "ꜱᴏʟᴅɪ ᴏᴛᴛᴇɴᴜᴛɪ ᴅᴀ ᴜɴᴀ ʀᴀᴘɪɴᴀ.");
        meta.setLore(lore);
        
        meta.setCustomModelData(dirtyMoneyModelData);
        meta.getPersistentDataContainer().set(dirtyMoneyKey, PersistentDataType.BYTE, (byte) 1);
        
        money.setItemMeta(meta);
        return money;
    }
    
    public ItemStack createStartRobberyButton() {
        ItemStack button = new ItemStack(Material.STICK);
        ItemMeta meta = button.getItemMeta();
        
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "SI");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "ᴄʟɪᴄᴄᴀ ᴘᴇʀ ɪɴɪᴢɪᴀʀᴇ ʟᴀ ʀᴀᴘɪɴᴀ");
        lore.add("");
        lore.add(ChatColor.RED + "ʀᴇQᴜɪꜱɪᴛɪ:");
        lore.add(ChatColor.GOLD + "- ᴅᴇᴠɪ ᴀᴠᴇʀᴇ ᴜɴ'ᴀʀᴍᴀ ɴᴇʟʟ'ɪɴᴠᴇɴᴛᴀʀɪᴏ");
        lore.add(ChatColor.GOLD + "- ᴅᴇᴠᴏɴᴏ ᴇꜱꜱᴇʀᴇ ᴏɴʟɪɴᴇ ᴀʟᴍᴇɴᴏ 2 ꜰᴏʀᴢᴇ ᴅᴇʟʟ'ᴏʀᴅɪɴᴇ");
        lore.add(ChatColor.GOLD + "- ᴅᴇᴠɪ ᴅᴇꜱɪɢɴᴀʀᴇ ᴜɴ ᴏꜱᴛᴀɢɢɪᴏ ᴄᴏɴ /ᴏꜱᴛᴀɢɢɪᴏ <ᴘʟᴀʏᴇʀ>");
        meta.setLore(lore);
        
        meta.setCustomModelData(startRobberyModelData);
        
        button.setItemMeta(meta);
        return button;
    }
    
    public ItemStack createInfoRobberyButton() {
        ItemStack button = new ItemStack(Material.STICK);
        ItemMeta meta = button.getItemMeta();
        
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Informazioni");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "ʟᴀ ʀᴀᴘɪɴᴀ ᴅᴜʀᴀ 5 ᴍɪɴᴜᴛɪ.");
        lore.add(ChatColor.GOLD + "ʟᴇ ꜰᴏʀᴢᴇ ᴅᴇʟʟ'ᴏʀᴅɪɴᴇ ᴠᴇʀʀᴀɴɴᴏ ᴀᴠᴠɪꜱᴀᴛᴇ.");
        lore.add(ChatColor.GOLD + "ꜱᴇ ᴜɴ ʀᴀᴘɪɴᴀᴛᴏʀᴇ ᴇꜱᴄᴇ ᴅᴀʟʟᴀ ʀᴇɢɪᴏɴ, ʟᴀ ʀᴀᴘɪɴᴀ ꜰᴀʟʟɪꜱᴄᴇ.");
        lore.add(ChatColor.GOLD + "È ᴏʙʙʟɪɢᴀᴛᴏʀɪᴏ ᴀᴠᴇʀᴇ ᴜɴ ᴏꜱᴛᴀɢɢɪᴏ, ᴀʟᴛʀɪᴍᴇɴᴛɪ");
        lore.add(ChatColor.GOLD + "ꜱɪ ʀɪꜱᴄʜɪᴀ ᴅɪ ᴇꜱꜱᴇʀᴇ ꜱᴀɴᴢɪᴏɴᴀᴛɪ.");
        lore.add("");
        lore.add(ChatColor.RED + "ᴜꜱᴀ /ᴏꜱᴛᴀɢɢɪᴏ <ᴘʟᴀʏᴇʀ> ᴘᴇʀ ᴅᴇꜱɪɢɴᴀʀᴇ ᴜɴ ᴏꜱᴛᴀɢɢɪᴏ.");
        meta.setLore(lore);
        
        meta.setCustomModelData(infoRobberyModelData);
        
        button.setItemMeta(meta);
        return button;
    }
    
    public ItemStack createCancelRobberyButton() {
        ItemStack button = new ItemStack(Material.STICK);
        ItemMeta meta = button.getItemMeta();
        
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "NO");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "ᴄʟɪᴄᴄᴀ ᴘᴇʀ ᴀɴɴᴜʟʟᴀʀᴇ");
        meta.setLore(lore);
        
        meta.setCustomModelData(cancelRobberyModelData);
        
        button.setItemMeta(meta);
        return button;
    }
    
    public boolean isMarketSelector(ItemStack item) {
        if (item == null || item.getType() != Material.WOODEN_AXE) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(selectorKey, PersistentDataType.BYTE);
    }
    
    public boolean isSafe(ItemStack item) {
        if (item == null || item.getType() != Material.PRISMARINE_STAIRS) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(safeKey, PersistentDataType.BYTE);
    }
    
    public boolean isDirtyMoney(ItemStack item) {
        if (item == null || item.getType() != Material.STICK) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(dirtyMoneyKey, PersistentDataType.BYTE);
    }
    
    public int getSafeModelData() {
        return safeModelData;
    }
    
    public int getDirtyMoneyModelData() {
        return dirtyMoneyModelData;
    }
    
    public int getStartRobberyModelData() {
        return startRobberyModelData;
    }
    
    public int getInfoRobberyModelData() {
        return infoRobberyModelData;
    }
    
    public int getCancelRobberyModelData() {
        return cancelRobberyModelData;
    }
} 