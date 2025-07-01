package rapine.rapinemarket.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.models.MarketRegion;

public class RapinaOlogrammaCommand implements CommandExecutor, TabCompleter {
    private final RapineMarket plugin;
    
    public RapinaOlogrammaCommand(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Questo comando può essere eseguito solo da un giocatore.");
            return true;
        }
        
        Player player = (Player) sender;
        
        
        if (!player.hasPermission("rapinemarket.admin") && !player.hasPermission("rapina.ologramma")) {
            player.sendMessage(ChatColor.RED + "Non hai il permesso di eseguire questo comando.");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "set":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Utilizzo: /rapinaologramma set <nome_market>");
                    return true;
                }
                return handleSetCommand(player, args[1]);
                
            case "remove":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Utilizzo: /rapinaologramma remove <nome_market>");
                    return true;
                }
                return handleRemoveCommand(player, args[1]);
                
            case "move":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Utilizzo: /rapinaologramma move <nome_market>");
                    return true;
                }
                return handleMoveCommand(player, args[1]);
                
            case "list":
                return handleListCommand(player);
                
            default:
                sendHelpMessage(player);
                return true;
        }
    }
    
    private boolean handleSetCommand(Player player, String marketName) {
        
        MarketRegion region = plugin.getRegionManager().getRegionAt(player.getLocation());
        if (region == null) {
            player.sendMessage(ChatColor.RED + "Devi essere all'interno di un market per impostare un ologramma.");
            return true;
        }
        
        
        if (!region.getName().equalsIgnoreCase(marketName)) {
            player.sendMessage(ChatColor.RED + "Il nome del market non corrisponde alla regione in cui ti trovi.");
            return true;
        }
        
        
        boolean success = plugin.getHologramManager().setHologramLocation(marketName, player.getLocation());
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Ologramma impostato con successo per il market " + marketName + ".");
            
            
            if (plugin.getRobberyManager().getActiveRobberies().containsKey(marketName)) {
                plugin.getHologramManager().createRobberyHologram(marketName);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Non è stato possibile impostare l'ologramma per il market " + marketName + ".");
        }
        
        return true;
    }
    
    private boolean handleRemoveCommand(Player player, String marketName) {
        boolean success = plugin.getHologramManager().removeHologramLocation(marketName);
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Ologramma rimosso con successo per il market " + marketName + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Non è stato trovato alcun ologramma per il market " + marketName + ".");
        }
        
        return true;
    }
    
    private boolean handleMoveCommand(Player player, String marketName) {
        
        MarketRegion region = plugin.getRegionManager().getRegionAt(player.getLocation());
        if (region == null) {
            player.sendMessage(ChatColor.RED + "Devi essere all'interno di un market per spostare un ologramma.");
            return true;
        }
        
        
        if (!region.getName().equalsIgnoreCase(marketName)) {
            player.sendMessage(ChatColor.RED + "Il nome del market non corrisponde alla regione in cui ti trovi.");
            return true;
        }
        
        
        boolean success = plugin.getHologramManager().moveHologramLocation(marketName, player.getLocation());
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + "Ologramma spostato con successo per il market " + marketName + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Non è stato trovato alcun ologramma per il market " + marketName + ".");
        }
        
        return true;
    }
    
    private boolean handleListCommand(Player player) {
        List<String> markets = plugin.getHologramManager().getHologramMarkets();
        
        if (markets.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Non ci sono ologrammi impostati.");
            return true;
        }
        
        player.sendMessage(ChatColor.GREEN + "Ologrammi impostati:");
        for (String market : markets) {
            player.sendMessage(ChatColor.AQUA + "- " + market);
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GREEN + "=== Comandi Ologramma Rapina ===");
        player.sendMessage(ChatColor.AQUA + "/rapinaologramma set <nome_market>" + ChatColor.WHITE + " - Imposta un ologramma nel punto in cui ti trovi");
        player.sendMessage(ChatColor.AQUA + "/rapinaologramma remove <nome_market>" + ChatColor.WHITE + " - Rimuove un ologramma");
        player.sendMessage(ChatColor.AQUA + "/rapinaologramma move <nome_market>" + ChatColor.WHITE + " - Sposta un ologramma nel punto in cui ti trovi");
        player.sendMessage(ChatColor.AQUA + "/rapinaologramma list" + ChatColor.WHITE + " - Mostra la lista degli ologrammi impostati");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("set");
            completions.add("remove");
            completions.add("move");
            completions.add("list");
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("set") || subCommand.equals("remove") || subCommand.equals("move")) {
                
                for (MarketRegion region : plugin.getRegionManager().getRegions().values()) {
                    completions.add(region.getName());
                }
            }
        }
        
        return completions;
    }
} 