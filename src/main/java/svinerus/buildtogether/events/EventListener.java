package svinerus.buildtogether.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.utils.Utils;

public class EventListener implements Listener {

    public static void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        var eventCancelled = onChangeBlock(event, event.getBlockPlaced().getType());
        event.setCancelled(eventCancelled);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {

        var eventCancelled = onChangeBlock(event, Material.AIR);
        event.setCancelled(eventCancelled);
    }


    // cancel the event if true returned
    boolean onChangeBlock(BlockEvent event, Material newMat) {
        var loc = event.getBlock().getLocation();
        var building = BuildTogether.buildingsManager.getBuilding(loc);
        if (building == null) return false;

        var blockPlacement = building.blockPlaced(loc, newMat);

        BlockPlacedEvent newEvent = new BlockPlacedEvent(building, event, blockPlacement);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);

        if (blockPlacement.isCorrect()) {
            Utils.spawnParticles(loc, Particle.HEART);
            loc.getWorld().playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            return false;
        } else {
            Utils.spawnParticles(loc, Particle.ELECTRIC_SPARK);
            loc.getWorld().playSound(loc, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return true;
        }


    }


    // disable ability to get blocks from custom inventory
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() != null) return;
        e.setCancelled(true);
    }

}
