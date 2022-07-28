package svinerus.buildtogether.building;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.events.BuildingFinished;
import svinerus.buildtogether.events.LayerFinished;
import svinerus.buildtogether.utils.Utils;

import java.util.Comparator;

public class Building {
    private final String name;
    private final BuildingSchema buildingSchema;

    // don't save this fields in json
    private transient BlockTips tips;
    private transient int activeLayerIndex = 0;


    public Building(String name, BuildingSchema buildingSchema) {
        this.name = name;
        this.buildingSchema = buildingSchema;
        onEnable();
    }

    public void onEnable() {
        this.tips = new BlockTips();
        // todo if this.visible
        show();
    }

    public void show() {
        this.checkLayerFinish();
    }


    public BlockPlacement blockPlaced(Location loc, Material newMat) {
        var locVec = Utils.toVector(loc);

        if (isBlockCorrect(locVec, newMat)) {
            tips.hide(loc);
            Bukkit.getScheduler().runTaskLater(BuildTogether.instance, this::onCorrectBlockPlacement, 1);
            return BlockPlacement.CORRECT;
        }

        if (newMat == Material.AIR && !isBlockCorrect(locVec, world()))
            return BlockPlacement.REMOVE_INCORRECT;

        tips.show(loc, activeLayer().blocks().get(locVec));
        return BlockPlacement.INCORRECT;
    }

    public void shutdown() {
        if (tips != null) tips.hideAll();
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
                .filter(l -> isBlockCorrect(l, location.getWorld()))
                .min(Comparator.comparingDouble(locationVec::distance)).orElse(null);

        if (whereVec == null) throw new IllegalArgumentException("No incorrect blocks");
        return Utils.toLocation(world(), whereVec);
    }

    public double progress() {
        return (double) activeLayerIndex / this.buildingSchema.layers.size();
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

        LayerFinished newEvent = new LayerFinished(this);
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


