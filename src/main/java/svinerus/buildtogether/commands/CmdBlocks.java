package svinerus.buildtogether.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;

import java.util.ArrayList;
import java.util.List;

import static svinerus.buildtogether.utils.Localization.lt;

public class CmdBlocks implements ICommand {

    public String getPerms() {
        return "blocks";
    }

    public void run(CommandSender sender, String[] args) throws Exception {
        if (args.length < 2) throw new Exception("Wrong number of arguments");
        if (!(sender instanceof Player)) throw new Exception("Only player can use this command");

        String buildingName = args[1];
        var building = BuildTogether.buildingsManager.getBuilding(buildingName);

        needBlocksInv((Player) sender, building);
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        if (args.length == 2) return CommandListener.buildingNames();
        return new ArrayList<>();
    }


    // open inv with needed blocks
    private static void needBlocksInv(Player player, Building building) {
        var invTitle = Component.text(lt("need_blocks_inv.title"), NamedTextColor.GOLD);
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
        List<Component> lore = List.of(Component.text(lt("need_blocks_inv.have_it"), NamedTextColor.GREEN));
        item.addUnsafeEnchantment(Enchantment.LURE, 1);
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
    }

}
