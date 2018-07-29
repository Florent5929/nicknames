package fr.florent.nicknames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This plugin allows to rename (in color) others players if you have the permission or if you are an operator. 
 * @author Florent
 */

public class Plugin extends JavaPlugin {
	
	public static Map<String, String> nicknames = new HashMap<String, String>();
	public static FileConfiguration nicknamesConfig;
	public static File file;
	public static Map<String, String> colors = new HashMap<String, String>();
	public static String[] colorsTab = {"noir", "bleuSombre", "vertSombre", "aquaSombre", "rougeSombre", "violetSombre", "or", "gris", "grisSombre", "bleu", "vert", "aqua", "rouge", "violet", "jaune", "blanc"};
	public static String[] colorsCodeTab = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"};
	public static String colorsString = new String("");
	
	@Override
	public void onEnable() {
		if (!this.getDataFolder().exists()) { 
			 this.getDataFolder().mkdir();
		}
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new PluginListener(), this);
		this.getCommand("nick").setExecutor(new NickCommandExecutor());
		
		file = getFile("nicknames");
		nicknamesConfig = getFileConfig(file);
		
		if(file.length() == 0){
			try {
				initConfigNicknames();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		loadNicknames();
		loadColors();
		
	}
	
	/**
	 * Create a hashmap from two arrays and prepare a String with the colors for the user.
	 */
	
	public static void loadColors(){
		int i = 0;
		for(String color : colorsTab){
			colors.put(color.toUpperCase(), colorsCodeTab[i]);
			colorsString = new String(colorsString + color.toUpperCase() + ", ");
			i++;
		}
		
		if(colorsString.length()>= 3){
			colorsString = new String(colorsString.substring(0, colorsString.length()-2) + ".");
		}
	}
	
	/**
	 * Fill the nicknames hashmap from the configuration file.
	 */

	public static void loadNicknames() {
		List<String> listconfig = nicknamesConfig.getStringList("nicknames");

			for (String line : listconfig) {
				String[] words = line.split(":");
				nicknames.put(words[0], words[1]);
			}
	}
	
	/**
	 * Create a nicknames configuration file if it not exists.
	 * @throws IOException
	 */

	private void initConfigNicknames() throws IOException {
		String header = new String("On the left, the normal name, on the right, the nickname.\n"
				+ "Possibility to use minecraft colors codes, example :\n"
				+ "https://codepen.io/Rundik/pen/ggVemP");
		nicknamesConfig.options().header(header);
		nicknamesConfig.options().copyHeader(true);
		nicknamesConfig.set("nicknames", new ArrayList<String>());
		List<String> playersList = nicknamesConfig.getStringList("nicknames");
		playersList.add("Florent5929:§4Florent§r");
		nicknamesConfig.set("nicknames", playersList);
		nicknamesConfig.save(file);
	}
	
	/**
	 * Return an instance of the Plugin.
	 * @return Plugin
	 */

	public static Plugin plugin() {
		return Plugin.getPlugin(Plugin.class);
	}
	
	/**
	 * Return the configuration file as a File and create it if it not exists.
	 * @param name
	 * @return File
	 */
	
	public static File getFile(String name) {
		name = name + ".yml";
		File file = new File(Plugin.plugin().getDataFolder(), name);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				Log.error(Level.SEVERE, "Could not create file " + name);
			}
		}
		return file;
	}
	
	/**
	 * Return the configuration file as FileConfiguration from a file.
	 * @param file
	 * @return FileConfiguration
	 */
	
	public static FileConfiguration getFileConfig(File file) {
		return YamlConfiguration.loadConfiguration(file);
	}

}
