package rapine.rapinemarket.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.models.MarketRegion;

public class MarketCommand implements CommandExecutor {
    private final RapineMarket plugin;
    
    public MarketCommand(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo i player possono usare questo comando.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("rapinemarket.admin")) {
            player.sendMessage(ChatColor.RED + "Non hai il permesso di usare questo comando.");
            return true;
        }
        
        if (args.length == 0) {
            sendUsage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                return handleCreate(player, args);
            case "delete":
                return handleDelete(player, args);
            case "list":
                return handleList(player);
            case "rename":
                return handleRename(player, args);
            default:
                sendUsage(player);
                return true;
        }
    }
    
    private boolean handleCreate(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Uso: /market create <nome_market>");
            return true;
        }
        
        String marketName = args[1];
        
        if (!plugin.getRegionManager().hasCompleteSelection(player)) {
            player.sendMessage(ChatColor.RED + "Devi prima selezionare una regione con il Market Selector.");
            return true;
        }
        
        boolean success = plugin.getRegionManager().createMarketRegion(marketName, player);
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Market " + ChatColor.AQUA + marketName + ChatColor.GREEN + " creato con successo!");
        } else {
            player.sendMessage(ChatColor.RED + "Errore durante la creazione del market. Assicurati che i punti selezionati siano nello stesso mondo.");
        }
        
        return true;
    }
    
    private boolean handleDelete(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Uso: /market delete <nome_market>");
            return true;
        }
        
        String marketName = args[1].toLowerCase();
        
        if (!plugin.getRegionManager().getRegions().containsKey(marketName)) {
            player.sendMessage(ChatColor.RED + "Il market " + ChatColor.AQUA + args[1] + ChatColor.RED + " non esiste.");
            return true;
        }
        
        if (plugin.getRobberyManager().getActiveRobberies().containsKey(marketName)) {
            player.sendMessage(ChatColor.RED + "Non puoi eliminare un market durante una rapina attiva.");
            return true;
        }
        
        plugin.getRegionManager().getRegions().remove(marketName);
        
        File regionFile = new File(plugin.getDataFolder(), "regions/" + marketName + ".json");
        if (regionFile.exists()) {
            regionFile.delete();
        }
        
        player.sendMessage(ChatColor.GREEN + "Market " + ChatColor.AQUA + args[1] + ChatColor.GREEN + " eliminato con successo!");
        
        return true;
    }
    
    private boolean handleList(Player player) {
        if (plugin.getRegionManager().getRegions().isEmpty()) {
            player.sendMessage(ChatColor.RED + "Non ci sono market registrati.");
            return true;
        }
        
        player.sendMessage(ChatColor.GREEN + "Lista dei market:");
        
        for (MarketRegion region : plugin.getRegionManager().getRegions().values()) {
            String safeInfo = region.hasSafe() ? ChatColor.GREEN + " [Cassaforte: SI]" : ChatColor.RED + " [Cassaforte: NO]";
            player.sendMessage(ChatColor.AQUA + "- " + region.getName() + safeInfo);
        }
        
        return true;
    }
    
    private boolean handleRename(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(ChatColor.RED + "Uso: /market rename <vecchio_nome> <nuovo_nome>");
            return true;
        }
        
        String oldName = args[1];
        String newName = args[2];
        
        if (!plugin.getRegionManager().getRegions().containsKey(oldName.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Il market " + ChatColor.AQUA + oldName + ChatColor.RED + " non esiste.");
            return true;
        }
        
        if (plugin.getRegionManager().getRegions().containsKey(newName.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Esiste già un market con il nome " + ChatColor.AQUA + newName + ChatColor.RED + ".");
            return true;
        }
        
        if (plugin.getRobberyManager().getActiveRobberies().containsKey(oldName.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Non puoi rinominare un market durante una rapina attiva.");
            return true;
        }
        
        boolean success = plugin.getRegionManager().renameMarket(oldName, newName);
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Market rinominato da " + ChatColor.AQUA + oldName + 
                    ChatColor.GREEN + " a " + ChatColor.AQUA + newName + ChatColor.GREEN + " con successo!");
            
            if (plugin.getHologramManager().getHologramMarkets().contains(oldName.toLowerCase())) {
                Location location = plugin.getHologramManager().getHologramLocation(oldName);
                if (location != null) {
                    plugin.getHologramManager().removeHologramLocation(oldName);
                    
                    plugin.getHologramManager().setHologramLocation(newName, location);
                    
                    player.sendMessage(ChatColor.GREEN + "Hologram location aggiornata.");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Errore durante la rinomina del market.");
        }
        
        return true;
    }
    
    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Uso:");
        player.sendMessage(ChatColor.RED + "/market create <nome_market> - Crea un nuovo market");
        player.sendMessage(ChatColor.RED + "/market delete <nome_market> - Elimina un market esistente");
        player.sendMessage(ChatColor.RED + "/market list - Mostra la lista dei market");
        player.sendMessage(ChatColor.RED + "/market rename <vecchio_nome> <nuovo_nome> - Rinomina un market esistente");
    }
} 