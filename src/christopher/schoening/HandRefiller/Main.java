package christopher.schoening.HandRefiller;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import christopher.schoening.HandRefiller.Events.EventListener;

public class Main extends JavaPlugin {
	
	public static final boolean ENV_DEBUG = false;	// build env var to toggle debug mode
	
	private static Plugin plugin;	// the plugin itself
	
	/**
	 * onEnable() - method which gets triggered when the plugin gets enabled.
	 */
	public void onEnable() {
		plugin = this;
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "HandRefiller enabled.");
		registerEvents(plugin, new EventListener(/*this*/));
	}
	
	/**
	 * onDisable() - method which gets triggered when the plugin gets disabled.
	 */
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "HandRefiller disabled.");
	}
	
	/**
	 * registerEvents() - helper method to register multiple events more conveniently.
	 * 
	 * @author JPG2000
	 * @see https://bukkit.org/threads/tutorial-using-multiple-classes.179833/
	 * 
	 * @param plugin
	 * @param listeners
	 */
	public static void registerEvents(Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
	
	/**
	 * getPlugin() - Getter for the plugin.
	 * 
	 * @return plugin
	 */
	public static Plugin getPlugin() {
		return plugin;
	}
}
