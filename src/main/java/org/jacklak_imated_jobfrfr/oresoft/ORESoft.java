package org.jacklak_imated_jobfrfr.oresoft;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

import java.util.HashMap;

public final class ORESoft extends JavaPlugin implements Listener {
    public LobbyManager lobbyManager;
    private HashMap<String, GameState> gameStates;

    @Override
    public void onEnable() {
        getLogger().info("Starting up ORESoft Plugin...");

        LoadWorld.setup(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDmgPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged && event.getDamager() instanceof Player damager)) return;
        Lobby lobby = LobbyManager.getLobbyByPlayer(damaged);
        if (!"ORESoft".equals(lobby.getGameName())) return;
        GameState gameState = gameStates.get(lobby.getLobbyId());
        if (gameState == GameState.GRACE_PERIOD) {
            damager.sendMessage("§cYou are in the grace period!");
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {

    }
}
