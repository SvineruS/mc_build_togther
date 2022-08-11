package svinerus.buildtogether.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    public static Inventory inv;

    public CmdBlocks() {
        var invTitle = Component.text(lt("need_blocks_inv.title"), NamedTextColor.GOLD);
        inv = Bukkit.createInventory(null, 27, invTitle);
    }

    public String getPerms() {
        return "blocks";
    }

    public void run(CommandSender sender, String[] args) throws Exception {
        if (args.length < 2) throw new Exception("error.wrong_args_num");
        if (!(sender instanceof Player)) throw new Exception("error.only_player");

        String buildingName = args[1];
        var building = BuildTogether.buildingsManager.getBuilding(buildingName);

        needBlocksInv((Player) sender, building);
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        if (args.length == 2) return CommandListener.buildingNames();
        return new ArrayList<>();
    }


    // open inv with needed blocks
    private void needBlocksInv(Player player, Building building) {
        var needBlocks = building.needBlocksSorted();
        var playerInv = player.getInventory();

        inv.clear();
        for (var entry : needBlocks)
            inv.addItem(createItem(entry.getKey(), entry.getValue().intValue(), playerInv.contains(entry.getKey())));

        player.openInventory(inv);
    }


    private static ItemStack createItem(Material mat, Integer amount, boolean playerHaveIt) {
        var item = new ItemStack(mat);
        var lore = new ArrayList<Component>();

        lore.add(Component.text(lt("need_blocks_inv.count") + amount, NamedTextColor.WHITE));
        if (playerHaveIt) {
            lore.add(Component.text(lt("need_blocks_inv.have_it"), NamedTextColor.GREEN));
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        }

        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.lore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

}
