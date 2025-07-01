package rapine.rapinemarket.managers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.models.MarketRegion;

public class RobberyManager {
    private final RapineMarket plugin;
    private final Map<String, Long> lastRobberyTimes = new HashMap<>();
    private final Map<UUID, Long> hostageTimeouts = new HashMap<>();
    private final Map<String, RobberyData> activeRobberies = new HashMap<>();
    private final RobberyTimeTracker timeTracker;
    private final List<String> lawEnforcementGroups = Arrays.asList(
            "smd-colonnello", "smd-generale-comandante", "smd-generale-vicecomandante",
            "gdf-appuntato", "gdf-appuntatoscelto", "gdf-brigadiere", "gdf-brigadierecapo",
            "gdf-brigadierecapoqs", "gdf-capitano", "gdf-colonnello", "gdf-comandante",
            "gdf-finanziere", "gdf-finanzierescelto", "gdf-generalearmata", "gdf-generalebrigata",
            "gdf-generaledivisione", "gdf-maggiore", "gdf-maresciallo", "gdf-maresciallocapo",
            "gdf-sottotenente", "gdf-tenente", "gdf-tenentecolonnello", "gdf-vicebrigadiere",
            "gdf-vicecomandante", "pol-agente", "pol-agentescelto", "pol-assistente",
            "pol-assistentecapo", "pol-assistentecapocoord", "pol-commissario", "pol-commissariocapo",
            "pol-dirigentegenerale", "pol-dirigentesuperiore", "pol-ispettore", "pol-ispettorecapo",
            "pol-ispettoresuperiore", "pol-nocs", "pol-primodirigente", "pol-sovr", "pol-sovrcapo",
            "pol-sovrcapocoord", "pol-vicecommissario", "pol-viceispettore", "pol-vicequestore", "pol-vicesovr"
    );
    
    public RobberyManager(RapineMarket plugin) {
        this.plugin = plugin;
        this.timeTracker = new RobberyTimeTracker(plugin);
    }
    
    public boolean canStartRobbery(String marketName) {
        if (lastRobberyTimes.containsKey(marketName)) {
            long lastRobbery = lastRobberyTimes.get(marketName);
            long currentTime = System.currentTimeMillis();
            long cooldown = 30 * 60 * 1000;
            
            if (currentTime - lastRobbery < cooldown) {
                return false;
            }
        }
        
        return !activeRobberies.containsKey(marketName);
    }
    
    public boolean hasWeapon(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.CROSSBOW) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasEnoughLawEnforcement() {
        int lawEnforcementCount = 0;
        LuckPerms luckPerms = plugin.getLuckPermsAPI();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                for (String group : lawEnforcementGroups) {
                    if (user.getInheritedGroups(user.getQueryOptions()).stream()
                            .anyMatch(g -> g.getName().equalsIgnoreCase(group))) {
                        lawEnforcementCount++;
                        break;
                    }
                }
            }
            
            if (lawEnforcementCount >= 2) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isLawEnforcement(Player player) {
        LuckPerms luckPerms = plugin.getLuckPermsAPI();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        
        if (user != null) {
            for (String group : lawEnforcementGroups) {
                if (user.getInheritedGroups(user.getQueryOptions()).stream()
                        .anyMatch(g -> g.getName().equalsIgnoreCase(group))) {
                    return true;
                }
            }
            
            String primaryGroup = user.getPrimaryGroup();
            if (primaryGroup != null) {
                for (String group : lawEnforcementGroups) {
                    if (primaryGroup.equalsIgnoreCase(group)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public boolean startRobbery(Player initiator, MarketRegion region) {
        String marketName = region.getName();
        
        if (!canStartRobbery(marketName) || !hasWeapon(initiator) || !hasEnoughLawEnforcement()) {
            return false;
        }
        
        RobberyData robbery = new RobberyData(initiator.getUniqueId(), region);
        activeRobberies.put(marketName, robbery);
        
        for (Player player : plugin.getRegionManager().getPlayersInRegion(region)) {
            robbery.addParticipant(player.getUniqueId());
        }
        
        BossBar bossBar = Bukkit.createBossBar(
                ChatColor.AQUA + "ʀᴀᴘɪɴᴀ ɪɴ ᴄᴏʀꜱᴏ: " + marketName,
                BarColor.BLUE,
                BarStyle.SOLID
        );
        
        for (UUID uuid : robbery.getParticipants()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && !isLawEnforcement(player)) {
                bossBar.addPlayer(player);
            }
        }
        
        robbery.setBossBar(bossBar);
        
        timeTracker.setRemainingTime(marketName, 300);
        
        plugin.getHologramManager().createRobberyHologram(marketName);
        
        BukkitTask task = new BukkitRunnable() {
            private int secondsRemaining = 5 * 60;
            
            @Override
            public void run() {
                if (secondsRemaining <= 0) {
                    completeRobbery(marketName);
                    cancel();
                    return;
                }
                
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                bossBar.setTitle(ChatColor.AQUA + "ʀᴀᴘɪɴᴀ ɪɴ ᴄᴏʀꜱᴏ: " + marketName + " - " + 
                        ChatColor.AQUA + String.format("%d:%02d", minutes, seconds));
                bossBar.setProgress(secondsRemaining / (5.0 * 60.0));
                
                timeTracker.setRemainingTime(marketName, secondsRemaining);
                
                secondsRemaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        robbery.setTask(task);
        
        Location safeLocation = region.getSafeLocation();
        String locationInfo = "";
        
        if (safeLocation != null) {
            locationInfo = String.format(" [x: %d, ʏ: %d, ᴢ: %d]", 
                    safeLocation.getBlockX(), safeLocation.getBlockY(), safeLocation.getBlockZ());
        }
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isLawEnforcement(player)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ᴀᴛᴛᴇɴᴢɪᴏɴᴇ " + 
                        ChatColor.AQUA + "ꜱᴛᴀ ᴠᴇɴᴇɴᴅᴏ ᴇꜱᴇɢᴜɪᴛᴀ ᴜɴᴀ ʀᴀᴘɪɴᴀ ᴀʟ ᴍᴀʀᴋᴇᴛ " + 
                        ChatColor.AQUA + marketName + locationInfo + 
                        ChatColor.RED + " ɪɴᴛᴇʀᴠɪᴇɴɪ ᴘᴇʀ ᴄᴏɴᴛʀᴀᴛᴛᴀʀᴇ ᴄᴏɴ ɪ ᴄʀɪᴍɪɴᴀʟɪ");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            }
        }
        
        lastRobberyTimes.put(marketName, System.currentTimeMillis());
        
        return true;
    }
    
    public void cancelRobbery(String marketName) {
        RobberyData robbery = activeRobberies.get(marketName);
        if (robbery != null) {
            if (robbery.getTask() != null) {
                robbery.getTask().cancel();
            }
            
            if (robbery.getBossBar() != null) {
                robbery.getBossBar().removeAll();
            }
            
            if (robbery.getHostage() != null) {
                Player hostage = Bukkit.getPlayer(robbery.getHostage());
                if (hostage != null && hostage.isOnline()) {
                    plugin.getHologramManager().removeHostageHologram(hostage);
                }
            }
            
            plugin.getHologramManager().removeRobberyHologram(marketName);
            
            timeTracker.removeRobbery(marketName);
            
            activeRobberies.remove(marketName);
            
            for (UUID uuid : robbery.getParticipants()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "ʟᴀ ʀᴀᴘɪɴᴀ È ꜱᴛᴀᴛᴀ ᴀɴɴᴜʟʟᴀᴛᴀ!");
                }
            }
        }
    }
    
    public void completeRobbery(String marketName) {
        RobberyData robbery = activeRobberies.get(marketName);
        if (robbery != null) {
            if (robbery.getTask() != null) {
                robbery.getTask().cancel();
            }
            
            if (robbery.getBossBar() != null) {
                robbery.getBossBar().removeAll();
            }
            
            if (robbery.getHostage() != null) {
                Player hostage = Bukkit.getPlayer(robbery.getHostage());
                if (hostage != null && hostage.isOnline()) {
                    plugin.getHologramManager().removeHostageHologram(hostage);
                }
            }
            
            plugin.getHologramManager().removeRobberyHologram(marketName);
            
            timeTracker.removeRobbery(marketName);
            
            Player initiator = Bukkit.getPlayer(robbery.getInitiator());
            if (initiator != null && initiator.isOnline()) {
                initiator.getWorld().playSound(initiator.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                
                ItemStack dirtyMoney = plugin.getItemManager().createDirtyMoney(20);
                HashMap<Integer, ItemStack> leftover = initiator.getInventory().addItem(dirtyMoney);
                
                for (ItemStack item : leftover.values()) {
                    initiator.getWorld().dropItemNaturally(initiator.getLocation(), item);
                }
                
                initiator.sendMessage(ChatColor.GREEN + "" + ChatColor.GREEN + "ʀᴀᴘɪɴᴀ ᴄᴏᴍᴘʟᴇᴛᴀᴛᴀ! " + 
                        ChatColor.AQUA + "ʜᴀɪ ʀɪᴄᴇᴠᴜᴛᴏ 20 ʙᴀɴᴄᴏɴᴏᴛᴇ ᴅɪ ꜱᴏʟᴅɪ ꜱᴘᴏʀᴄʜɪ.");
            }
            
            activeRobberies.remove(marketName);
            
            for (UUID uuid : robbery.getParticipants()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && !player.getUniqueId().equals(robbery.getInitiator()) && !isLawEnforcement(player)) {
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.GREEN + "ʀᴀᴘɪɴᴀ ᴄᴏᴍᴘʟᴇᴛᴀᴛᴀ!");
                }
            }
        }
    }
    
    public boolean isInActiveRobbery(Player player) {
        for (RobberyData robbery : activeRobberies.values()) {
            if (robbery.getParticipants().contains(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    
    public RobberyData getRobberyForPlayer(Player player) {
        for (RobberyData robbery : activeRobberies.values()) {
            if (robbery.getParticipants().contains(player.getUniqueId())) {
                return robbery;
            }
        }
        return null;
    }
    
    public boolean canLeaveRobbery(Player player) {
        RobberyData robbery = getRobberyForPlayer(player);
        if (robbery != null) {
            if (player.getUniqueId().equals(robbery.getInitiator())) {
                return false;
            }
            
            return player.getUniqueId().equals(robbery.getHostage());
        }
        return true;
    }
    
    public boolean setHostage(Player initiator, Player hostage) {
        RobberyData robbery = getRobberyForPlayer(initiator);
        if (robbery == null || !initiator.getUniqueId().equals(robbery.getInitiator())) {
            return false;
        }
        
        if (!robbery.getParticipants().contains(hostage.getUniqueId())) {
            return false;
        }
        
        if (hostageTimeouts.containsKey(hostage.getUniqueId())) {
            long timeout = hostageTimeouts.get(hostage.getUniqueId());
            if (System.currentTimeMillis() < timeout) {
                return false;
            }
        }
        
        if (robbery.getHostage() != null) {
            Player previousHostage = Bukkit.getPlayer(robbery.getHostage());
            if (previousHostage != null && previousHostage.isOnline()) {
                plugin.getHologramManager().removeHostageHologram(previousHostage);
                
                if (robbery.getParticipants().contains(robbery.getHostage()) && 
                        robbery.getBossBar() != null && 
                        !isLawEnforcement(previousHostage)) {
                    robbery.getBossBar().addPlayer(previousHostage);
                }
            }
        }
        
        robbery.setHostage(hostage.getUniqueId());
        
        plugin.getHologramManager().createHostageHologram(hostage);
        
        hostageTimeouts.put(hostage.getUniqueId(), System.currentTimeMillis() + (2 * 60 * 60 * 1000));
        
        if (robbery.getBossBar() != null) {
            robbery.getBossBar().removePlayer(hostage);
        }
        
        return true;
    }
    
    public boolean canPlayerEnterRobbery(Player player, MarketRegion region) {
        String marketName = region.getName();
        RobberyData robbery = activeRobberies.get(marketName);
        
        if (robbery != null) {
            if (isLawEnforcement(player)) {
                return true;
            }
            
            if (robbery.getParticipants().contains(player.getUniqueId())) {
                return true;
            }
            
            return robbery.isDamageTaken();
        }
        
        return true;
    }
    
    public void setDamageTaken(String marketName, boolean damageTaken) {
        RobberyData robbery = activeRobberies.get(marketName);
        if (robbery != null) {
            robbery.setDamageTaken(damageTaken);
        }
    }
    
    public Map<String, RobberyData> getActiveRobberies() {
        return activeRobberies;
    }
    
    public RobberyTimeTracker getTimeTracker() {
        return timeTracker;
    }
    
    public static class RobberyData {
        private final UUID initiator;
        private final MarketRegion region;
        private final Set<UUID> participants;
        private UUID hostage;
        private BossBar bossBar;
        private BukkitTask task;
        private boolean damageTaken;
        
        public RobberyData(UUID initiator, MarketRegion region) {
            this.initiator = initiator;
            this.region = region;
            this.participants = new HashSet<>();
            this.damageTaken = false;
        }
        
        public UUID getInitiator() {
            return initiator;
        }
        
        public MarketRegion getRegion() {
            return region;
        }
        
        public Set<UUID> getParticipants() {
            return participants;
        }
        
        public void addParticipant(UUID uuid) {
            participants.add(uuid);
        }
        
        public UUID getHostage() {
            return hostage;
        }
        
        public void setHostage(UUID hostage) {
            this.hostage = hostage;
        }
        
        public BossBar getBossBar() {
            return bossBar;
        }
        
        public void setBossBar(BossBar bossBar) {
            this.bossBar = bossBar;
        }
        
        public BukkitTask getTask() {
            return task;
        }
        
        public void setTask(BukkitTask task) {
            this.task = task;
        }
        
        public boolean isDamageTaken() {
            return damageTaken;
        }
        
        public void setDamageTaken(boolean damageTaken) {
            this.damageTaken = damageTaken;
        }
    }
} 