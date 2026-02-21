package org.jacklak_imated_jobfrfr.oresoft;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import wueffi.MiniGameCore.api.GameOverEvent;
import wueffi.MiniGameCore.api.GameStartEvent;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

import java.util.HashMap;
import java.util.Random;

public final class ORESoft extends JavaPlugin implements Listener {
    public LobbyManager lobbyManager;
    private HashMap<String, GameState> gameStates = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("Starting up ORESoft Plugin...");

        LoadWorld.setup(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDmgPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;

        Player damager = null;

        if (event.getDamager() instanceof Player p) {
            damager = p;
        }

        else if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player p) {
                damager = p;
            }
        }

        if (damager == null) return;

        Lobby lobby = LobbyManager.getLobbyByPlayer(damaged);
        if (lobby == null) return;

        GameState gameState = gameStates.get(lobby.getLobbyId());
        if (gameState == GameState.GRACE_PERIOD) {
            damager.sendMessage("§cYou are in the grace period!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        String name = event.getGameName();

        if(!name.equals("ORESoft")) {
            return;
        }

        Lobby lobby = event.getLobby();
        String lobbyId = lobby.getLobbyId();

        for (Player player : lobby.getPlayers()) {
            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta bowMeta = bow.getItemMeta();

            if (bowMeta != null) {
                bowMeta.setUnbreakable(true);
                bowMeta.addEnchant(Enchantment.INFINITY, 1, true);
            }

            player.give(bow);
        }

        gameStates.put(lobbyId, GameState.GRACE_PERIOD);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            gameStates.put(lobbyId, GameState.FIGHTING);
            for (Player player : lobby.getPlayers()) {
                player.sendMessage("§cThe grace period is over! You may now PvP!");
            }
        }, 40 * 20L);

        World world = Bukkit.getWorld(lobby.getWorldFolder().getName());
        if (world == null) {
            getLogger().warning("World wasn't loaded for Lobby " + lobbyId + "(" + lobby.getWorldFolder().getName() + ")");
            return;
        }

        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);

        for (var chunk : world.getLoadedChunks()) {
            fillChests(chunk);
        }
    }

    @EventHandler
    public void onGameEnd(GameOverEvent event) {
        String lobbyId = event.getLobby().getLobbyId();
        gameStates.remove(lobbyId);
    }

    public void fillChests(Chunk chunk) {
        for (var tileEntity : chunk.getTileEntities()) {
            if (!(tileEntity instanceof Chest chest))
                continue;
            var inv = chest.getInventory();
            if (inv.isEmpty()) {
                inv.setItem(new Random().nextInt(inv.getSize()), getRandomItem());
            }
        }
    }

    public ItemStack getRandomItem() {
        var items = new Material[] {
                Material.WIND_CHARGE,
                Material.FIRE_CHARGE,
                Material.BAKED_POTATO
        };

        var picked = items[new Random().nextInt(items.length)];
        var amount = new Random().nextInt(8) + 1;
        return new ItemStack(picked, amount);
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye from ORESoft...");
    }
}
