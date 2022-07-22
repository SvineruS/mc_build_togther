package svinerus.buildtogether.building;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.events.BuildingFinished;
import svinerus.buildtogether.events.LayerFinished;
import svinerus.buildtogether.schema.BuildingSchema;
import svinerus.buildtogether.schema.Layer;
import svinerus.buildtogether.utils.Utils;

import java.util.Comparator;

public class Building {
    final String name;
    final BuildingSchema buildingSchema;
    final BlockTips tips = new BlockTips();
    int activeLayerIndex = 0;


    public Building(String name, BuildingSchema buildingSchema) {
        this.name = name;
        this.buildingSchema = buildingSchema;

        this.checkLayerFinish();
    }


    public BlockPlacement blockPlaced(Location loc, Material newMat) {
        var locVec = Utils.toVector(loc);

        if (activeLayer().isBlockCorrect(locVec, newMat)) {
            tips.hide(loc);
            Bukkit.getScheduler().runTaskLater(BuildTogether.instance, this::onCorrectBlockPlacement, 1);
            return BlockPlacement.CORRECT;
        }

        if (newMat == Material.AIR && !activeLayer().isBlockCorrect(locVec, world()))
            return BlockPlacement.REMOVE_INCORRECT;

        return BlockPlacement.INCORRECT;
    }

    public void shutdown() {
        tips.hideAll();
    }

    public boolean isInside(Location location) {
        // todo check using region
        var locVec = Utils.toVector(location);
        return activeLayer().blocks.containsKey(locVec);
    }

    // find the closest incorrect block to this location
    public Location where(Location location) throws IllegalArgumentException {
        if (!location.getWorld().equals(world())) throw new IllegalArgumentException("Wrong world");
        var locationVec = Utils.toVector(location);

        var whereVec = activeLayer().blocks.keySet().stream()
          .filter(l -> !activeLayer().isBlockCorrect(l, location.getWorld()))
          .min(Comparator.comparingDouble(locationVec::distance)).orElse(null);

        if (whereVec == null) throw new IllegalArgumentException("No incorrect blocks");
        return Utils.toLocation(world(), whereVec);
    }


    private void onCorrectBlockPlacement() {
        if (!checkLayerFinish()) return;

        var loc = Utils.toLocation(world(), buildingSchema.region.getCenter().toBlockPoint());
        Utils.spawnFireworks(loc, 2);
    }


    private void onFinished() {
        BuildingFinished newEvent = new BuildingFinished(this);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);

        BuildingsManager.instance.remove(name);
    }


    // return true on layer finished
    private boolean checkLayerFinish() {
        if (!activeLayer().isFinished(world())) return false;
        tips.hideAll();

        do {
            activeLayerIndex++;
            if (activeLayerIndex >= buildingSchema.layers.size()) { // this was the last layer
                onFinished();
                return true;
            }
        } while (activeLayer().isFinished(world()));

        setBlockTips();

        LayerFinished newEvent = new LayerFinished(this);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);

        return true;
    }

    private void setBlockTips() {
        for (var locVec : activeLayer().blocks.keySet()) {
            var loc = Utils.toLocation(world(), locVec);
            if (activeLayer().isBlockCorrect(locVec, world()))
                tips.hide(loc);
            else
                tips.show(loc, activeLayer().blocks.get(locVec));
        }
    }

    private Layer activeLayer() {
        return buildingSchema.layers.get(activeLayerIndex);
    }


    private World world() {
        return buildingSchema.world();
    }
}


