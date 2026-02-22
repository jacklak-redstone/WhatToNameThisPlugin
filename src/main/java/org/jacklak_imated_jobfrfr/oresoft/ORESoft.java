package org.jacklak_imated_jobfrfr.oresoft;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Barrel;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import wueffi.MiniGameCore.api.GameOverEvent;
import wueffi.MiniGameCore.api.GameStartEvent;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

import java.util.HashMap;
import java.util.Random;

public final class ORESoft extends JavaPlugin implements Listener {
    private LobbyManager lobbyManager;
    private HashMap<String, GameState> gameStates = new HashMap<>();
    private Random rand = new Random();

    @Override
    public void onEnable() {
        getLogger().info("Starting up ORESoft Plugin...");
        getServer().getPluginManager().registerEvents(this, this);

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
        PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1, true, false);
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();

        if (bowMeta != null) {
            bowMeta.addEnchant(Enchantment.INFINITY, 1, true);
            bowMeta.addEnchant(Enchantment.POWER, 2, true);
        }

        bow.setItemMeta(bowMeta);

        for (Player player : lobby.getPlayers()) {
            player.sendMessage("§cWelcome to ORESoft! Your goal: be the last player standing. Good luck, and have fun!");

            int red = rand.nextInt(256);
            int green = rand.nextInt(256);
            int blue = rand.nextInt(256);

            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
            helmetMeta.setColor(Color.fromRGB(red, green, blue));
            helmet.setItemMeta(helmetMeta);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            chestplateMeta.setColor(Color.fromRGB(red, green, blue));
            chestplate.setItemMeta(chestplateMeta);

            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
            leggingsMeta.setColor(Color.fromRGB(red, green, blue));
            leggings.setItemMeta(leggingsMeta);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
            bootsMeta.setColor(Color.fromRGB(red, green, blue));
            boots.setItemMeta(bootsMeta);

            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chestplate);
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);

            player.give(bow);
            player.addPotionEffect(invis);
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
            if (tileEntity instanceof Chest chest) {
                var inv = chest.getInventory();
                if (inv.isEmpty()) {
                    inv.addItem(LootItem.chooseItem(rand), LootItem.chooseItem(rand));
                }
            } else if (tileEntity instanceof Barrel barrel) {
                var inv = barrel.getInventory();
                if (inv.isEmpty()) {
                    inv.addItem(LootItem.chooseItem(rand), LootItem.chooseItem(rand));
                }
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye from ORESoft...");
    }
}
