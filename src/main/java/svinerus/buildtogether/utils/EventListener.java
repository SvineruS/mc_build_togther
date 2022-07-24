package svinerus.buildtogether.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import svinerus.buildtogether.building.BlockPlacement;
import svinerus.buildtogether.building.BuildingsManager;
import svinerus.buildtogether.events.BlockPlaced;

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


    // cancel the event if false returned
    boolean onChangeBlock(BlockEvent event, Material newMat) {
        var loc = event.getBlock().getLocation();
        var building = BuildingsManager.instance.getBuilding(loc);
        if (building == null) return false;

        var blockPlacement = building.blockPlaced(loc, newMat);

        BlockPlaced newEvent = new BlockPlaced(building, event, blockPlacement);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);

        if (blockPlacement == BlockPlacement.INCORRECT) {
            Utils.spawnParticles(loc, Particle.ELECTRIC_SPARK);
            return true;
        } else {
            Utils.spawnParticles(loc, Particle.HEART);
            return false;
        }


    }

}
