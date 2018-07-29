package fr.florent.nicknames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PluginListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();

		if (Plugin.nicknames.containsKey(player.getName())) {
			String nickname = Plugin.nicknames.get(player.getName());
			player.setPlayerListName(nickname);
			player.setDisplayName(nickname);
			player.setCustomName(nickname);
		}
	}
	
}
