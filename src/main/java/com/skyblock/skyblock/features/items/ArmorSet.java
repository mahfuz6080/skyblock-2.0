package com.skyblock.skyblock.features.items;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public abstract class ArmorSet {

    protected final static ItemHandler handler = Skyblock.getPlugin(Skyblock.class).getItemHandler();
    private HashMap<Player, BukkitRunnable> runnables;
    private ItemStack helmet;
    private ItemStack chest;
    private ItemStack legs;
    private ItemStack boots;
    private String id;

    public ArmorSet(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots, String id) {
        this.helmet = helmet;
        this.chest = chest;
        this.legs = legs;
        this.boots = boots;
        this.id = id;

        this.runnables = new HashMap<>();
    }

    public void fullSetBonus(Player player) {
        if (runnables.get(player) == null) {
            runnables.put(player, new BukkitRunnable() {
                @Override
                public void run() {
                    tick(player);
                }
            });
        }

        runnables.get(player).runTaskTimerAsynchronously(Skyblock.getPlugin(Skyblock.class), 5L, 1);
    }

    public void stopFullSetBonus(Player player) {
        runnables.get(player).cancel();
    }

    public void tick(Player player) {}

    public List<ItemStack> toList() {
        return Arrays.asList(helmet, chest, legs, boots);
    }
}