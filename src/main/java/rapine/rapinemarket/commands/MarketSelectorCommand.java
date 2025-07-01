package rapine.rapinemarket.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import rapine.rapinemarket.RapineMarket;

public class MarketSelectorCommand implements CommandExecutor {
    private final RapineMarket plugin;
    
    public MarketSelectorCommand(RapineMarket plugin) {
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
        
        if (args.length != 1 || !args[0].equalsIgnoreCase("get")) {
            player.sendMessage(ChatColor.RED + "Uso: /marketselector get");
            return true;
        }
        
        
        ItemStack selector = plugin.getItemManager().createMarketSelector();
        player.getInventory().addItem(selector);
        
        player.sendMessage(ChatColor.AQUA + "Hai ricevuto il " + ChatColor.GREEN + "Market Selector" + ChatColor.AQUA + ".");
        player.sendMessage(ChatColor.AQUA + "Usa il " + ChatColor.GREEN + "click sinistro" + ChatColor.AQUA + " per selezionare il primo punto.");
        player.sendMessage(ChatColor.AQUA + "Usa il " + ChatColor.GREEN + "click destro" + ChatColor.AQUA + " per selezionare il secondo punto.");
        
        return true;
    }
} 