package rapine.rapinemarket.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import rapine.rapinemarket.RapineMarket;

public class RapineHelpCommand implements CommandExecutor {
    private final RapineMarket plugin;
    
    public RapineHelpCommand(RapineMarket plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo i player possono usare questo comando.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendMainHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "admin":
                if (player.hasPermission("rapinemarket.admin")) {
                    sendAdminHelp(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Non hai il permesso di visualizzare i comandi admin.");
                }
                break;
            case "player":
                sendPlayerHelp(player);
                break;
            case "rapina":
                sendRobberyHelp(player);
                break;
            default:
                sendMainHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendMainHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "========== " + ChatColor.AQUA + "RapineMarket - Aiuto" + ChatColor.GOLD + " ==========");
        player.sendMessage(ChatColor.AQUA + "Plugin per rapine nei market con ostaggi e interazioni con la polizia");
        player.sendMessage("");
        
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinehelp player" + ChatColor.WHITE + " - Mostra i comandi per i giocatori");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinehelp rapina" + ChatColor.WHITE + " - Guida alle rapine");
        
        if (player.hasPermission("rapinemarket.admin")) {
            player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinehelp admin" + ChatColor.WHITE + " - Mostra i comandi admin");
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Sviluppato da: " + ChatColor.AQUA + "24Shotty");
    }
    
    private void sendPlayerHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "========== " + ChatColor.AQUA + "RapineMarket - Comandi Giocatori" + ChatColor.GOLD + " ==========");
        player.sendMessage("");
        
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/ostaggio <player>" + ChatColor.WHITE + " - Designa un giocatore come ostaggio durante una rapina");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinatime" + ChatColor.WHITE + " - Mostra il tempo rimanente della rapina in corso");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rt" + ChatColor.WHITE + " - Alias per /rapinatime");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/tempo" + ChatColor.WHITE + " - Alias per /rapinatime");
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Per iniziare una rapina, entra in un market con una cassaforte e interagisci con essa.");
    }
    
    private void sendRobberyHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "========== " + ChatColor.AQUA + "RapineMarket - Guida alle Rapine" + ChatColor.GOLD + " ==========");
        player.sendMessage("");
        
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "Come iniziare una rapina:");
        player.sendMessage(ChatColor.WHITE + "  1. Entra in un market con una cassaforte");
        player.sendMessage(ChatColor.WHITE + "  2. Assicurati di avere un'arma nell'inventario");
        player.sendMessage(ChatColor.WHITE + "  3. Interagisci con la cassaforte e conferma");
        player.sendMessage(ChatColor.WHITE + "  4. Usa " + ChatColor.AQUA + "/ostaggio <player>" + ChatColor.WHITE + " per designare un ostaggio");
        player.sendMessage(ChatColor.WHITE + "  5. Rimani nel market fino al completamento della rapina (5 minuti)");
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "Requisiti per una rapina:");
        player.sendMessage(ChatColor.WHITE + "  - Almeno 2 forze dell'ordine online");
        player.sendMessage(ChatColor.WHITE + "  - Un'arma nell'inventario");
        player.sendMessage(ChatColor.WHITE + "  - Puoi scegliere solo un ostaggio per rapina");
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "Durante la rapina:");
        player.sendMessage(ChatColor.WHITE + "  - Se esci dal market, la rapina viene annullata");
        player.sendMessage(ChatColor.WHITE + "  - Se vieni colpito, altri giocatori possono entrare nel market");
        player.sendMessage(ChatColor.WHITE + "  - Se ti teletrasporti fuori dal market, la rapina viene annullata");
        player.sendMessage(ChatColor.WHITE + "  - Una barra blu in alto mostrerà il tempo rimanente");
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "Completamento della rapina:");
        player.sendMessage(ChatColor.WHITE + "  - Dopo 5 minuti riceverai soldi sporchi");
        player.sendMessage(ChatColor.WHITE + "  - Usa i soldi sporchi per ottenere ricompense");
    }
    
    private void sendAdminHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "========== " + ChatColor.AQUA + "RapineMarket - Comandi Admin" + ChatColor.GOLD + " ==========");
        player.sendMessage("");
        
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/marketselector get" + ChatColor.WHITE + " - Ottieni lo strumento per selezionare le regioni");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/market create <nome>" + ChatColor.WHITE + " - Crea un market con la selezione attuale");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/market delete <nome>" + ChatColor.WHITE + " - Elimina un market esistente");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/market list" + ChatColor.WHITE + " - Mostra la lista dei market");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/market rename <vecchio> <nuovo>" + ChatColor.WHITE + " - Rinomina un market");
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/cassaforte get" + ChatColor.WHITE + " - Ottieni un blocco cassaforte da piazzare");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/soldisporchi get <quantità>" + ChatColor.WHITE + " - Ottieni soldi sporchi");
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinaologramma set <market>" + ChatColor.WHITE + " - Imposta un ologramma nella posizione attuale");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinaologramma remove <market>" + ChatColor.WHITE + " - Rimuove un ologramma");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinaologramma move <market>" + ChatColor.WHITE + " - Sposta un ologramma nella posizione attuale");
        player.sendMessage(ChatColor.GOLD + "» " + ChatColor.AQUA + "/rapinaologramma list" + ChatColor.WHITE + " - Mostra la lista degli ologrammi");
    }
} 