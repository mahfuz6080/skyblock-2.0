package com.skyblock.skyblock.commands.menu;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.collections.Collection;
import com.skyblock.skyblock.features.collections.CollectionCategory;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiresPlayer
@Usage(usage = "/sb collection")
@Description(description = "Open the collection menu")
public class CollectionCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        Inventory inventory;

        SkyblockPlayer skyblockPlayer = new SkyblockPlayer(player.getUniqueId());

        if (args.length == 0) {
            inventory = Bukkit.createInventory(null, 54, "Collection");

            fillEmpty(inventory);

            AtomicInteger collectionUnlocked = new AtomicInteger();
            AtomicInteger collectionTotal = new AtomicInteger();

            Collection.getCollections().forEach(col -> {
                if (skyblockPlayer.getValue("collection." + col.getName().toLowerCase() + ".unlocked").equals(true)) collectionUnlocked.getAndIncrement();

                collectionTotal.getAndIncrement();
            });

            double collectionPercentage = Math.round((double) collectionUnlocked.get() / (double) collectionTotal.get() * 1000) / 10.0;

            int collectionPercentageBar = (int) Math.round(collectionPercentage / 5);
            int collectionPercentageBarEmpty = 20 - collectionPercentageBar;

            String bar = "&2" + StringUtils.repeat("-", collectionPercentageBar) + "&7" + StringUtils.repeat("-", collectionPercentageBarEmpty);

            inventory.setItem(4, new ItemBuilder(ChatColor.GREEN + "Collection", Material.ITEM_FRAME).addLore(Util.buildLore("&7View all of the items available\n&7in SkyBlock. Collect more of an\n&7item to unlock rewards on your\n&7way to becoming the master of\n&7SkyBlock!\n\n&7Collection Unlocked: &e" + collectionPercentage + "&6%\n" + bar + " &e" + collectionUnlocked + "&6/&e" + collectionTotal + "\n\n&eClick to show rankings!")).toItemStack());

            inventory.setItem(20, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(0)));
            inventory.setItem(21, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(1)));
            inventory.setItem(22, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(2)));
            inventory.setItem(23, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(3)));
            inventory.setItem(24, generateCollectionCategory(skyblockPlayer, Collection.getCollectionCategories().get(4)));

            player.openInventory(inventory);

            return;
        }

        String inv = args[0];

        if (Collection.getCollectionCategories().stream().anyMatch(cat -> cat.getName().equalsIgnoreCase(inv))) {
            CollectionCategory category = Collection.getCollectionCategories().stream().filter(cat -> cat.getName().equalsIgnoreCase(inv)).findFirst().get();

            inventory = Bukkit.createInventory(null, 54, category.getName());

            fillEmpty(inventory);

            player.openInventory(inventory);
        } else if (Collection.getCollections().stream().anyMatch(col -> col.getName().equalsIgnoreCase(inv))) {
            Collection collection = Collection.getCollections().stream().filter(col -> col.getName().equalsIgnoreCase(inv)).findFirst().get();

            inventory = Bukkit.createInventory(null, 54, collection.getName());

            fillEmpty(inventory);

            player.openInventory(inventory);
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid collection category or collection");
        }

    }

    public void fillEmpty(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, new ItemBuilder(" ", Material.STAINED_GLASS_PANE, 1, (short) 15).toItemStack());
    }

    public ItemStack generateCollectionCategory(SkyblockPlayer player, CollectionCategory category) {
        ItemBuilder builder = new ItemBuilder(ChatColor.GREEN + category.getName() + " Collection", category.getIcon()).setDamage(category.getData());

        builder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<Collection> collections = Collection.getCollections().stream().filter(col -> col.getCategory().equals(category.getName())).collect(Collectors.toList());

        int unlocked = 0;
        int total = collections.size();

        for (Collection collection : collections) {
            if (player.getValue("collection." + collection.getName().toLowerCase() + ".unlocked").equals(true)) unlocked++;
        }

        double percentUnlocked = Math.round((double) unlocked / (double) total * 1000) / 10.0;

        double barMax = 20;
        double barCharPerPercent = 5;

        int barFilled = (int) Math.round(percentUnlocked / barCharPerPercent);

        int barEmpty = (int) (barMax - barFilled);

        String bar = "&2" + StringUtils.repeat("-", barFilled) + "&7" + StringUtils.repeat("-", barEmpty);

        builder.addLore(
                Util.buildLore("&7View your " + category.getName() + " collection!\n\n&7Collection Unlocked: &e" + percentUnlocked + "&6%\n" + bar + " &e" + unlocked + "&6/&e" + total + "\n\n&eClick to view!")
        );

        return builder.toItemStack();
    }

}