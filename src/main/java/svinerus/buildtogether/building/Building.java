package svinerus.buildtogether.building;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.events.BuildingFinishedEvent;
import svinerus.buildtogether.events.LayerFinishedEvent;
import svinerus.buildtogether.utils.Utils;
import svinerus.buildtogether.utils.storage.Buildings;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Building {
    private final String name;
    private final BuildingSchema buildingSchema;
    private int activeLayerIndex = 0;
//    private boolean enabled = true;  // TODO

    // don't save this fields in json
    private transient BlockTips tips;


    public Building(String name, BuildingSchema buildingSchema) {
        this.name = name;
        this.buildingSchema = buildingSchema;
    }

    public void onEnable() {
        try {
            this._onEnable();
        } catch (Exception e) {
            Utils.exception(e, "Failed to load building " + name);
        }
    }

    private void _onEnable() {
        this.tips = new BlockTips();
        // todo if this.visible
        this.setBlockTips();
        this.checkLayerFinish();
    }


    public BlockPlacement blockPlaced(Location loc, Material newMat) {
        var locVec = Utils.toVector(loc);

        if (isBlockCorrect(locVec, newMat)) {
            tips.hide(loc);
            Bukkit.getScheduler().runTaskLater(BuildTogether.instance, this::onCorrectBlockPlacement, 1);
            return BlockPlacement.CORRECT;
        }

        if (newMat == Material.AIR && !isBlockCorrect(locVec, world())) {
            tips.show(loc, activeLayer().blocks().get(locVec));
            return BlockPlacement.REMOVE_INCORRECT;
        }

        // todo almost correct blocks

        return BlockPlacement.INCORRECT;
    }

    public void shutdown() {
        if (tips != null) tips.hideAll();
        Buildings.saveBuilding(this);
    }

    public boolean isInside(Location location) {
        // todo check using region
        var locVec = Utils.toVector(location);
        return activeLayer().blocks().containsKey(locVec);
    }

    // find the closest incorrect block to this location
    public Location where(Location location) throws IllegalArgumentException {
        if (!location.getWorld().equals(world())) throw new IllegalArgumentException("Wrong world");
        var locationVec = Utils.toVector(location);

        var whereVec = activeLayer().blocks().keySet().stream()
          .filter(l -> !isBlockCorrect(l, location.getWorld()))
          .min(Comparator.comparingDouble(locationVec::distance)).orElse(null);

        if (whereVec == null) throw new IllegalArgumentException("No incorrect blocks");
        return Utils.toLocation(world(), whereVec);
    }

    // blocks in current layer that are incorrect
    public List<Material> what() {
        return activeLayer().blocks().entrySet().stream()
          .filter(l -> !isBlockCorrect(l.getKey(), world()) && !l.getValue().isAir())
          .map(Map.Entry::getValue).toList();
    }

    public double progress() {
        return (double) activeLayerIndex / this.buildingSchema.layers.size();
    }


    private void onCorrectBlockPlacement() {
        if (!checkLayerFinish()) return;
        onLayerFinished();
    }

    private void onLayerFinished() {
        // spawn firework
        var loc = Utils.toLocation(world(), buildingSchema.region.getCenter().toBlockPoint());
        Utils.spawnFirework(loc);

        // save building state
        Buildings.saveBuilding(this);
    }


    private void onFinished() {
        BuildingFinishedEvent newEvent = new BuildingFinishedEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);

        // todo set disabled
//        BuildingsManager.instance.remove(name);
    }


    // return true on layer finished
    private boolean checkLayerFinish() {
        if (!isActiveLayerFinished(world())) return false;
        tips.hideAll();

        do {
            activeLayerIndex++;
            if (activeLayerIndex >= buildingSchema.layers.size()) { // this was the last layer
                onFinished();
                return true;
            }
        } while (isActiveLayerFinished(world()));

        setBlockTips();

        LayerFinishedEvent newEvent = new LayerFinishedEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(newEvent);

        return true;
    }

    private void setBlockTips() {
        for (var locVec : activeLayer().blocks().keySet()) {
            var loc = Utils.toLocation(world(), locVec);
            if (isBlockCorrect(locVec, world()))
                tips.hide(loc);
            else
                tips.show(loc, activeLayer().blocks().get(locVec));
        }
    }


    private boolean isActiveLayerFinished(World world) {
        for (var location : activeLayer().blocks().keySet())
            if (!isBlockCorrect(location, world))
                return false;
        return true;
    }

    // is block set in real world equals block from schema
    private boolean isBlockCorrect(BlockVector3 location, World world) {
        return isBlockCorrect(location, Utils.toLocation(world, location).getBlock().getType());
    }

    // is passed block equals block from schema
    private boolean isBlockCorrect(BlockVector3 location, Material material) {
        return activeLayer().blocks().get(location) == material;
    }

    private BuildingSchema.Layer activeLayer() {
        return buildingSchema.layers.get(activeLayerIndex);
    }


    private World world() {
        return buildingSchema.world();
    }

    public String getName() {
        return name;
    }

    public BuildingSchema getBuildingSchema() {
        return buildingSchema;
    }
}


