package svinerus.buildtogether.building;

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

class BlockTip {
    final ArmorStand stand;

    public BlockTip(Location location, Material material) {
        if (material == Material.AIR) {
            material = Material.BARRIER;
        }
        var blockItem = new ItemStack(material);

        var loc = location.toCenterLocation().subtract(0, 1.5, 0);  // armor stand too high

        stand = location.getWorld().spawn(loc, ArmorStand.class);
        // todo set name
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
}
