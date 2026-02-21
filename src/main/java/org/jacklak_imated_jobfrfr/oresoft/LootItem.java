package org.jacklak_imated_jobfrfr.oresoft;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootItem {
    public Material item;
    public int maxAmount;
    public float percentRarity;

    public LootItem(Material i, int max, float percent) {
        item = i;
        maxAmount = max;
        percentRarity = percent;
    }

    public static ItemStack chooseItem(Random random) {
        var items = new LootItem[] {
                new LootItem(Material.WIND_CHARGE, 8, 20),
                new LootItem(Material.FIRE_CHARGE, 3, 15),
                new LootItem(Material.BAKED_POTATO, 16, 30),
                new LootItem(Material.STONE_SWORD, 1, 14),
                new LootItem(Material.IRON_SWORD, 1, 7),
                new LootItem(Material.STONE_AXE, 1, 9),
                new LootItem(Material.IRON_AXE, 1, 2),
                new LootItem(Material.WOODEN_AXE, 1, 18),
        };

        var total = 0f;
        for (var loot : items)
            total += loot.percentRarity;

        var rand = random.nextFloat() * total;

        LootItem picked = items[items.length - 1];
        for (var loot : items) {
            rand -= loot.percentRarity;
            if (rand <= 0) {
                picked = loot;
                break;
            }
        }

        var amount = random.nextInt(picked.maxAmount) + 1;
        return new ItemStack(picked.item, amount);
    }
}
