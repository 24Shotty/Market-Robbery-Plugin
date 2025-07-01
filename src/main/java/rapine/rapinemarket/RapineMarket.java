package rapine.rapinemarket;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;
import rapine.rapinemarket.commands.CassaforteCommand;
import rapine.rapinemarket.commands.MarketCommand;
import rapine.rapinemarket.commands.MarketSelectorCommand;
import rapine.rapinemarket.commands.OstaggioCommand;
import rapine.rapinemarket.commands.RapinaOlogrammaCommand;
import rapine.rapinemarket.commands.RapinaTimeCommand;
import rapine.rapinemarket.commands.RapineHelpCommand;
import rapine.rapinemarket.commands.SoldiSporchiCommand;
import rapine.rapinemarket.listeners.PlayerMovementListener;
import rapine.rapinemarket.listeners.RegionSelectionListener;
import rapine.rapinemarket.listeners.RobberyListener;
import rapine.rapinemarket.listeners.SafeInteractionListener;
import rapine.rapinemarket.managers.HologramManager;
import rapine.rapinemarket.managers.ItemManager;
import rapine.rapinemarket.managers.RegionManager;
import rapine.rapinemarket.managers.RobberyManager;
import rapine.rapinemarket.models.MarketRegion;
import rapine.rapinemarket.placeholders.RapineExpansion;

public final class RapineMarket extends JavaPlugin {
    
    private static RapineMarket instance;
    private LuckPerms luckPermsAPI;
    private RegionManager regionManager;
    private RobberyManager robberyManager;
    private ItemManager itemManager;
    private HologramManager hologramManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        registerSerializableClasses();
        
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        
        File regionsDir = new File(getDataFolder(), "regions");
        if (!regionsDir.exists()) {
            regionsDir.mkdir();
        }
        
        saveDefaultConfig();
        
        setupLuckPerms();
        
        regionManager = new RegionManager(this);
        hologramManager = new HologramManager(this);
        robberyManager = new RobberyManager(this);
        itemManager = new ItemManager(this);
        
        registerCommands();
        
        registerListeners();
        
        registerPlaceholders();
        
        getLogger().info("RapineMarket enabled successfully!");
    }
    
    private void registerSerializableClasses() {
        ConfigurationSerialization.registerClass(MarketRegion.class);
    }
    
    @Override
    public void onDisable() {
        if (regionManager != null) {
            regionManager.saveAllRegions();
        }
        
        if (hologramManager != null) {
            hologramManager.removeAllHolograms();
        }
        
        getLogger().info("RapineMarket disabled successfully!");
    }
    
    private void setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
            getLogger().info("LuckPerms API hooked successfully!");
        } else {
            getLogger().severe("LuckPerms API not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RapineExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered successfully!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not be available.");
        }
    }
    
    private void registerCommands() {
        getCommand("marketselector").setExecutor(new MarketSelectorCommand(this));
        getCommand("market").setExecutor(new MarketCommand(this));
        getCommand("cassaforte").setExecutor(new CassaforteCommand(this));
        getCommand("soldisporchi").setExecutor(new SoldiSporchiCommand(this));
        getCommand("ostaggio").setExecutor(new OstaggioCommand(this));
        getCommand("rapinatime").setExecutor(new RapinaTimeCommand(this));
        getCommand("rapinehelp").setExecutor(new RapineHelpCommand(this));
        
        RapinaOlogrammaCommand rapinaOlogrammaCommand = new RapinaOlogrammaCommand(this);
        getCommand("rapinaologramma").setExecutor(rapinaOlogrammaCommand);
        getCommand("rapinaologramma").setTabCompleter(rapinaOlogrammaCommand);
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new RegionSelectionListener(this), this);
        getServer().getPluginManager().registerEvents(new SafeInteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new RobberyListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(this), this);
    }
    
    public static RapineMarket getInstance() {
        return instance;
    }
    
    public LuckPerms getLuckPermsAPI() {
        return luckPermsAPI;
    }
    
    public RegionManager getRegionManager() {
        return regionManager;
    }
    
    public RobberyManager getRobberyManager() {
        return robberyManager;
    }
    
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    public HologramManager getHologramManager() {
        return hologramManager;
    }
}
