package serversystem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle.EnumTitleAction;
import serversystem.config.Config;
import serversystem.handler.ChatHandler;
import serversystem.handler.PlayerPacketHandler;
import serversystem.handler.PlayerPermission;
import serversystem.handler.WorldGroupHandler;

public class PlayerJoinListener implements Listener {
	
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		if(Config.isJoinMessageActiv()) {
			event.setJoinMessage(ChatHandler.getPlayerJoinMessage(event));
		} else {
			event.setJoinMessage("");
		}
		Config.addPlayer(event.getPlayer());
		PlayerPermission.removeConfigDisablePermissions(event.getPlayer());
		PlayerPermission.addConfigPermissions(event.getPlayer());
		PlayerPermission.reloadPlayerPermissions(event.getPlayer());
		WorldGroupHandler.getWorldGroup(event.getPlayer()).onPlayerJoin(event.getPlayer());
		if(Config.lobbyExists() && Config.getLobbyWorld() != null) {
			event.getPlayer().teleport(Config.getLobbyWorld().getSpawnLocation());
		}
		event.getPlayer().setGameMode(Config.getWorldGamemode(event.getPlayer().getWorld().getName()));
		if(Config.getTitle() != null) {PlayerPacketHandler.sendTitle(event.getPlayer(), EnumTitleAction.TITLE, Config.getTitle(), Config.getTitleColor(), 100);}
		if(Config.getSubtitle() != null) {PlayerPacketHandler.sendTitle(event.getPlayer(), EnumTitleAction.SUBTITLE, Config.getSubtitle(), Config.getSubtitleColor(), 100);}
		event.getPlayer().setPlayerListHeader(ChatHandler.parseColor(Config.getTablistTitleColor()) + Config.getTablistTitle());
		event.getPlayer().setPlayerListFooter(ChatHandler.parseColor(Config.getTablistSubtitleColor()) + Config.getTablistSubtitle());
	}
}
