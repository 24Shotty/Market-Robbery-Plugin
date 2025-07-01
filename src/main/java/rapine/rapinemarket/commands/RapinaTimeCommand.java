package rapine.rapinemarket.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.managers.RobberyManager;
import rapine.rapinemarket.managers.RobberyManager.RobberyData;

public class RapinaTimeCommand implements CommandExecutor {
    private final RapineMarket plugin;
    
    public RapinaTimeCommand(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo i giocatori possono usare questo comando.");
            return true;
        }
        
        Player player = (Player) sender;
        RobberyManager robberyManager = plugin.getRobberyManager();
        RobberyData robbery = robberyManager.getRobberyForPlayer(player);
        
        if (robbery == null) {
            player.sendMessage(ChatColor.RED + "ʀᴀᴘɪɴᴀ ɴᴏɴ ɪɴ ᴄᴏʀꜱᴏ");
            return true;
        }
        
        
        int remainingSeconds = robberyManager.getTimeTracker().getRemainingTimeForPlayer(player);
        
        if (remainingSeconds <= 0) {
            player.sendMessage(ChatColor.RED + "ʀᴀᴘɪɴᴀ ɴᴏɴ ɪɴ ᴄᴏʀꜱᴏ");
            return true;
        }
        
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        
        player.sendMessage(ChatColor.GOLD + "ᴛᴇᴍᴘᴏ ʀɪᴍᴀɴᴇɴᴛᴇ: " + 
                ChatColor.RED + String.format("%d:%02d", minutes, seconds));
        
        return true;
    }
} 