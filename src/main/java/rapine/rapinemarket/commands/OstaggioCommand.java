package rapine.rapinemarket.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import rapine.rapinemarket.RapineMarket;
import rapine.rapinemarket.managers.RobberyManager;

public class OstaggioCommand implements CommandExecutor {
    private final RapineMarket plugin;
    
    public OstaggioCommand(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo i player possono usare questo comando.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "ᴜꜱᴏ: /ᴏꜱᴛᴀɢɢɪᴏ <ᴘʟᴀʏᴇʀ>");
            return true;
        }
        
        
        RobberyManager robberyManager = plugin.getRobberyManager();
        if (!robberyManager.isInActiveRobbery(player)) {
            player.sendMessage(ChatColor.RED + "ɴᴏɴ ꜱᴇɪ ɪɴ ᴜɴᴀ ʀᴀᴘɪɴᴀ ᴀᴛᴛɪᴠᴀ.");
            return true;
        }
        
        
        RobberyManager.RobberyData robbery = robberyManager.getRobberyForPlayer(player);
        if (robbery == null || !player.getUniqueId().equals(robbery.getInitiator())) {
            player.sendMessage(ChatColor.RED + "ꜱᴏʟᴏ ᴄʜɪ ʜᴀ ɪɴɪᴢɪᴀᴛᴏ ʟᴀ ʀᴀᴘɪɴᴀ ᴘᴜÒ ɪᴍᴘᴏꜱᴛᴀʀᴇ ᴜɴ ᴏꜱᴛᴀɢɢɪᴏ.");
            return true;
        }
        
        
        if (robbery.getHostage() != null) {
            Player currentHostage = Bukkit.getPlayer(robbery.getHostage());
            String hostageName = currentHostage != null ? currentHostage.getName() : "ꜱᴄᴏɴᴏꜱᴄɪᴜᴛᴏ";
            player.sendMessage(ChatColor.RED + "ᴄ'È ɢɪÀ ᴜɴ ᴏꜱᴛᴀɢɢɪᴏ ɪɴ Qᴜᴇꜱᴛᴀ ʀᴀᴘɪɴᴀ: " + ChatColor.GOLD + hostageName);
            player.sendMessage(ChatColor.RED + "ᴘᴜᴏɪ ꜱᴄᴇɢʟɪᴇʀᴇ ꜱᴏʟᴏ ᴜɴ ᴏꜱᴛᴀɢɢɪᴏ ᴘᴇʀ ʀᴀᴘɪɴᴀ.");
            return true;
        }
        
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player non trovato o non online.");
            return true;
        }
        
        
        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "ɴᴏɴ ᴘᴜᴏɪ ɪᴍᴘᴏꜱᴛᴀʀᴇ ᴛᴇ ꜱᴛᴇꜱꜱᴏ ᴄᴏᴍᴇ ᴏꜱᴛᴀɢɢɪᴏ!");
            return true;
        }
        
        
        if (!robbery.getParticipants().contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "ɪʟ ᴘʟᴀʏᴇʀ ᴅᴇᴠᴇ ᴇꜱꜱᴇʀᴇ ᴘʀᴇꜱᴇɴᴛᴇ ɴᴇʟʟᴀ ꜱᴛᴇꜱꜱᴀ ʀᴀᴘɪɴᴀ.");
            return true;
        }
        
        
        boolean success = robberyManager.setHostage(player, target);
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + "ʜᴀɪ ɪᴍᴘᴏꜱᴛᴀᴛᴏ " + ChatColor.GREEN + target.getName() + ChatColor.GREEN + " ᴄᴏᴍᴇ ᴏꜱᴛᴀɢɢɪᴏ.");
            target.sendMessage(ChatColor.RED + "ꜱᴇɪ ꜱᴛᴀᴛᴏ ᴘʀᴇꜱᴏ ᴄᴏᴍᴇ ᴏꜱᴛᴀɢɢɪᴏ"  + ChatColor.RED + "!");
            
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (robbery.getParticipants().contains(p.getUniqueId()) && 
                        !p.equals(player) && !p.equals(target)) {
                    p.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + " È ꜱᴛᴀᴛᴏ ᴘʀᴇꜱᴏ ᴄᴏᴍᴇ ᴏꜱᴛᴀɢɢɪᴏ!");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "ɴᴏɴ È ᴘᴏꜱꜱɪʙɪʟᴇ ɪᴍᴘᴏꜱᴛᴀʀᴇ Qᴜᴇꜱᴛᴏ ᴘʟᴀʏᴇʀ ᴄᴏᴍᴇ ᴏꜱᴛᴀɢɢɪᴏ.");
            player.sendMessage(ChatColor.RED + "ɪʟ ᴘʟᴀʏᴇʀ ᴘᴏᴛʀᴇʙʙᴇ ᴇꜱꜱᴇʀᴇ ɪɴ ᴄᴏᴏʟᴅᴏᴡɴ (2 ᴏʀᴇ) ᴄᴏᴍᴇ ᴏꜱᴛᴀɢɢɪᴏ.");
        }
        
        return true;
    }
} 