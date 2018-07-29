package fr.florent.nicknames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

public class NickCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Commande joueur uniquement !");
		} else if (args.length != 3){
			sender.sendMessage("Usage : /nick <PseudoMC> <Couleur> <PseudoRP>");
		} else if (!Plugin.colors.containsKey(args[1].toUpperCase())){
			sender.sendMessage(ChatColor.RED + "La couleur " + args[1] + " est inconnue."
					+ "\nLes couleurs disponibles sont les suivantes : " + Plugin.colorsString);
		} else if (!hasColorPermission((Player)sender, args[1].toLowerCase())){
			sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de donner la couleur " + args[1] + ".");
		} else if(getPlayerCase(args[0]) == 3){
			sender.sendMessage(ChatColor.RED + "Le joueur " + args[0] + " n'a pas été trouvé.");
		} else {
			try {
				renameThisPlayer(args[0], args[2], getColorCode(args[1].toUpperCase()), getPlayerCase(args[0]));
				sender.sendMessage(ChatColor.GREEN + "Le joueur " + args[0] + " a été renommé avec succès.");
				return true;
			} catch (IOException e) {
				sender.sendMessage("Le plugin n'est pas parvenu à ajouter votre surnom au fichier de configuration."
						+ "\nMerci de prévenir un opérateur.");
				e.printStackTrace();	
			}	
		}		
		return false;
	}
	
	/**
	 * Build the full nickname thanks to the basic nickname and the color code.
	 * Next save the information in the memory of the plugin (the nicknames hashmap).
	 * Next update the string list in the configuration file.
	 * Finally if the player is connected change his current name immediately. 
	 * @param playerName
	 * @param nickName
	 * @param colorCode
	 * @param playerCase
	 * @throws IOException
	 */
	
	public void renameThisPlayer(String playerName, String nickName, String colorCode, int playerCase) throws IOException{
		nickName = new String(colorCode + nickName + "§r");
		Plugin.nicknames.put(playerName, nickName);
		List<String> playersList = Plugin.nicknamesConfig.getStringList("nicknames");
		List<String> playersListUpdate = new ArrayList<String>();
		
		for(String line : playersList){
			if(!line.contains(playerName)){
				playersListUpdate.add(line);
			}
		}
		
		playersListUpdate.add(playerName + ":" + nickName);
		Plugin.nicknamesConfig.set("nicknames", playersListUpdate);
		Plugin.nicknamesConfig.save(Plugin.file);
		
		if(playerCase == 1){
			Player player = Bukkit.getPlayer(playerName);
			player.setPlayerListName(nickName);
			player.setDisplayName(nickName);
			player.setCustomName(nickName);
		}
	}
	
	/**
	 * Return the code of the color or an empty String if it not exists.
	 * @param color
	 * @return String
	 */
	
	public String getColorCode(String color){
		if(Plugin.colors.containsKey(color)){
			return Plugin.colors.get(color);
		} else {
			return new String("");
		}
	}
	
	/**
	 * Return an integer depends on the player situation (1 if connected, 2 if not connected, 3 if not exists).
	 * @param playerName
	 * @return int
	 */
	
	public int getPlayerCase(String playerName){
		if(Bukkit.getPlayer(playerName) != null){
			return 1;
		} else if(this.getOfflinePlayers().contains(playerName)){
			return 2;
		} else {
			return 3;
		}
	}
	
	
	/**
	 * Return true if the player is OP or has the permission for the following color.
	 * @param player
	 * @param color
	 * @return boolean
	 */
	
	public boolean hasColorPermission(Player player, String color){
		if(player.isOp() || player.hasPermission("nicknames.manage." + color.toLowerCase())){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Return a list of offline players names.
	 * @return List<String>
	 */
	
	public List<String> getOfflinePlayers(){
		List<String> list = new ArrayList<String>();
		for(OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()){
			list.add(player.getName());
		}
		return list;
	}

}
