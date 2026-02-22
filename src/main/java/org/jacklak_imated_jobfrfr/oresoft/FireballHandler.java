package org.jacklak_imated_jobfrfr.oresoft;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FireballHandler implements Listener {
    private Set<UUID> fireBallRateLimits = new HashSet<>();
    private final ORESoft oreSoft;

    public FireballHandler(ORESoft oreSoft) {
        this.oreSoft = oreSoft;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Lobby lobby = LobbyManager.getLobbyByPlayer(player);

        if (lobby == null) return;
        if (!"ORESoft".equals(lobby.getGameName())) return;
        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.FIRE_CHARGE) return;

        event.setCancelled(true);
        if (fireBallRateLimits.contains(player.getUniqueId())) return; // after cancel so they don't accidentally place fire

        fireBallRateLimits.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(oreSoft, () -> { fireBallRateLimits.remove(player.getUniqueId()); }, 10L);

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.getInventory().removeItem(new ItemStack(Material.FIRE_CHARGE, 1));
        }
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setYield(2);
        fireball.setVelocity(fireball.getVelocity().multiply(2));
    }
}
