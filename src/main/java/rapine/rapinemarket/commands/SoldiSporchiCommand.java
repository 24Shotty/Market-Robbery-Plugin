package rapine.rapinemarket.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import rapine.rapinemarket.RapineMarket;

public class SoldiSporchiCommand implements CommandExecutor {
    private final RapineMarket plugin;
    
    public SoldiSporchiCommand(RapineMarket plugin) {
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
        
        if (args.length != 2 || !args[0].equalsIgnoreCase("get")) {
            player.sendMessage(ChatColor.RED + "Uso: /soldisporchi get <quantità>");
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "La quantità deve essere maggiore di zero.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "La quantità deve essere un numero valido.");
            return true;
        }
        
        
        ItemStack dirtyMoney = plugin.getItemManager().createDirtyMoney(amount);
        player.getInventory().addItem(dirtyMoney);
        
        player.sendMessage(ChatColor.AQUA + "Hai ricevuto " + ChatColor.GREEN + amount + " Soldi Sporchi" + ChatColor.AQUA + ".");
        
        return true;
    }
} 