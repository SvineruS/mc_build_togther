package svinerus.buildtogether.aux;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;

import java.util.List;

public class NeedBlocksInv {


    // open inv with needed blocks
    public static void needBlocksInv(Player player, Building building) {
        var invTitle = Component.text(BuildTogether.localization.localize("need_blocks_inv_title"), NamedTextColor.GOLD);
        Inventory inv = Bukkit.createInventory(null, 27, invTitle);


        var needBlocks = building.needBlocksSorted();
        var playerInv = player.getInventory();

        for (var entry : needBlocks) {
            var item = new ItemStack(entry.getKey(), entry.getValue().intValue());

            if (playerInv.contains(entry.getKey()))
                setLoreAndEnchant(item);

            inv.addItem(item);
        }

        player.openInventory(inv);
    }


    private static void setLoreAndEnchant(ItemStack item) {
        List<Component> lore = List.of(Component.text(BuildTogether.localization.localize("need_blocks_inv_have_it"), NamedTextColor.GREEN));
        item.addUnsafeEnchantment(Enchantment.LURE, 1);
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
    }
}
