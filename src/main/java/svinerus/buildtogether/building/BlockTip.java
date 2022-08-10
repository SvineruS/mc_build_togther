package svinerus.buildtogether.building;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

class BlockTips {
    final HashMap<Location, BlockTip> tips = new HashMap<>();

    public void show(Location location, Material material) {
        if (!tips.containsKey(location))
            tips.put(location, new BlockTip(location, material));
    }

    public void hide(Location location) {
        if (tips.containsKey(location)) {
            tips.get(location).die();
            tips.remove(location);
        }
    }

    public void hideAll() {
        for (BlockTip tip : tips.values())
            tip.die();
        tips.clear();
    }
}

public class BlockTip {
    final ArmorStand stand;

    public BlockTip(Location location, Material material) {
        if (material == Material.AIR) {
            material = Material.BARRIER;
        }
        var blockItem = new ItemStack(material);

        var loc = location.toCenterLocation().subtract(0, 1.5, 0);  // armor stand too high
        loc.setYaw(45);

        stand = location.getWorld().spawn(loc, ArmorStand.class);
//        stand.customName(Component.translatable(material.translationKey()));
//        stand.setCustomNameVisible(true);  // looks bad (
        stand.addScoreboardTag("bttip");
        stand.setItem(EquipmentSlot.HEAD, blockItem);
        stand.setCollidable(false);
        stand.setMarker(true);
        stand.setInvisible(true);
        stand.setAI(false);
        stand.setCanTick(false);
        stand.setInvulnerable(true);
    }

    public void die() {
        stand.remove();
    }

    public static void killAll() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:kill @e[tag=bttip]");
    }
}
