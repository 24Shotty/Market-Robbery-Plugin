package rapine.rapinemarket.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import rapine.rapinemarket.RapineMarket;

public class CassaforteCommand implements CommandExecutor {
    private final RapineMarket plugin;
    
    public CassaforteCommand(RapineMarket plugin) {
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
            player.sendMessage(ChatColor.RED + "Uso: /cassaforte get");
            return true;
        }
        
        
        ItemStack safe = plugin.getItemManager().createSafe();
        player.getInventory().addItem(safe);
        
        player.sendMessage(ChatColor.AQUA + "Hai ricevuto una " + ChatColor.RED + "Cassaforte" + ChatColor.AQUA + ".");
        player.sendMessage(ChatColor.AQUA + "Piazzala all'interno di un market per permettere le rapine.");
        
        return true;
    }
} 