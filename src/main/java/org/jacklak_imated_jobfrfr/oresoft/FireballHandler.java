package org.jacklak_imated_jobfrfr.oresoft;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

public class FireballHandler implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Lobby lobby = LobbyManager.getLobbyByPlayer(player);

        if (lobby == null) return;
        if (!"ORESoft".equals(lobby.getGameName())) return;
        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.FIRE_CHARGE) return;
        event.setCancelled(true);

        player.getInventory().removeItem(new ItemStack(Material.FIRE_CHARGE, 1));
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setYield(4);
        fireball.setVelocity(fireball.getVelocity().multiply(4));
    }
}
