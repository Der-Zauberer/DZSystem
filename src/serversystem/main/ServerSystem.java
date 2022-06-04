package serversystem.main;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import serversystem.commands.AdminCommand;
import serversystem.commands.BuildCommand;
import serversystem.commands.EnderchestCommand;
import serversystem.commands.InventoryCommand;
import serversystem.commands.LobbyCommand;
import serversystem.commands.PermissionCommand;
import serversystem.commands.PermissionReloadComnmand;
import serversystem.commands.SpeedCommand;
import serversystem.commands.VanishCommand;
import serversystem.commands.WTPCommand;
import serversystem.commands.WarpCommand;
import serversystem.commands.WorldCommand;
import serversystem.config.Config;
import serversystem.config.SaveConfig;
import serversystem.events.CommandPreprocessListener;
import serversystem.events.EntityDamageListener;
import serversystem.events.ExplotionListener;
import serversystem.events.HungerListener;
import serversystem.events.PlayerDeathListener;
import serversystem.events.PlayerInteractListener;
import serversystem.events.PlayerJoinListener;
import serversystem.events.PlayerQuitListener;
import serversystem.events.PlayerRespawnListener;
import serversystem.events.PlayerTeleportListener;
import serversystem.handler.ChatHandler;
import serversystem.handler.InventoryHandler;
import serversystem.handler.PermissionHandler;
import serversystem.handler.SignHandler;
import serversystem.handler.TeamHandler;
import serversystem.handler.WorldGroupHandler;
import serversystem.signs.WarpSign;
import serversystem.signs.WorldSign;

public class ServerSystem extends JavaPlugin {

	private static ServerSystem instance;

	@Override
	public void onEnable() {
		setInstance(this);
		new Config();
		new SaveConfig();
		registerEvents();
		registerCommands();
		registerWorldSigns();
		repeatAutoSave();
		for (String world : Config.getLoadWorlds()) {
			if (Bukkit.getWorld(world) == null) {
				Bukkit.getWorlds().add(new WorldCreator(world).createWorld());
			}
		}
		WorldGroupHandler.autoCreateWorldGroups();
		for (Player player : Bukkit.getOnlinePlayers()) {
			TeamHandler.addRoleToPlayer(player);
			if (Config.lobbyExists() && Config.getLobbyWorld() != null) {
				player.teleport(Config.getLobbyWorld().getSpawnLocation());
			}
		}
		WorldGroupHandler.autoRemoveWorldGroups();
		if (Config.lobbyExists() && Config.getLobbyWorld() != null) {
			Config.getLobbyWorld().setMonsterSpawnLimit(0);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					PermissionHandler.loadPlayerPermissions(player);
					TeamHandler.addRoleToPlayer(player);
				}
			}

		}.runTaskLater(ServerSystem.getInstance(), 100);
	}

	@Override
	public void onDisable() {
		WorldGroupHandler.autoSavePlayerStats();
		TeamHandler.resetTeams();
	}

	private static void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new CommandPreprocessListener(), instance);
		Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), instance);
		Bukkit.getPluginManager().registerEvents(new ExplotionListener(), instance);
		Bukkit.getPluginManager().registerEvents(new HungerListener(), instance);
		Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), instance);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), instance);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), instance);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), instance);
		Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), instance);
		Bukkit.getPluginManager().registerEvents(new PlayerTeleportListener(), instance);
		
		Bukkit.getPluginManager().registerEvents(new ChatHandler(), instance);
		Bukkit.getPluginManager().registerEvents(new PermissionHandler(), instance);
		Bukkit.getPluginManager().registerEvents(new InventoryHandler(), instance);
		Bukkit.getPluginManager().registerEvents(new SignHandler(), instance);
		
		Bukkit.getPluginManager().registerEvents(new BuildCommand(), instance);
	}

	private static void registerCommands() {
		instance.getCommand("admin").setExecutor(new AdminCommand());
		instance.getCommand("build").setExecutor(new BuildCommand());
		instance.getCommand("enderchest").setExecutor(new EnderchestCommand());
		instance.getCommand("inventory").setExecutor(new InventoryCommand());
		instance.getCommand("lobby").setExecutor(new LobbyCommand());
		instance.getCommand("permission").setExecutor(new PermissionCommand());
		instance.getCommand("permissionreload").setExecutor(new PermissionReloadComnmand());
		instance.getCommand("speed").setExecutor(new SpeedCommand());
		instance.getCommand("vanish").setExecutor(new VanishCommand());
		instance.getCommand("warp").setExecutor(new WarpCommand());
		instance.getCommand("world").setExecutor(new WorldCommand());
		instance.getCommand("wtp").setExecutor(new WTPCommand());
	}

	private static void registerWorldSigns() {
		SignHandler.registerServerSign(new WorldSign());
		SignHandler.registerServerSign(new WarpSign());
	}

	private static void repeatAutoSave() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			@Override
			public void run() {
				WorldGroupHandler.autoSavePlayerStats();
			}
		}, 1L, (long) 120 * 20);
	}

	public static ServerSystem getInstance() {
		return instance;
	}

	public static void setInstance(ServerSystem instance) {
		ServerSystem.instance = instance;
	}

}
