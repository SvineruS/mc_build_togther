package svinerus.buildtogether.building;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;


public class BuildingSchema {
    final public CuboidRegion region;
    final public ArrayList<Layer> layers;

    public BuildingSchema(CuboidRegion region, ArrayList<Layer> layers) {
        this.region = region;
        this.layers = layers;
    }

    public BuildingSchema(CuboidRegion region, Layer layer) {
        this.region = region;
        this.layers = new ArrayList<>();
        this.layers.add(layer);
    }

    public HashMap<Material, Integer> blocksCount() {
        HashMap<Material, Integer> result = new HashMap<>();
        for (var layer : layers) {
            for (var material : layer.blocks.values()) {
                if (!result.containsKey(material))
                    result.put(material, 0);
                result.put(material, result.get(material) + 1);
            }
        }
        return result;
    }

    public Set<BlockVector3> blocksLocation() {
        Set<BlockVector3> result = new HashSet<>();
        for (var layer : layers)
            result.addAll(layer.blocks.keySet());
        return result;
    }

    public World world() {
        return BukkitAdapter.adapt(Objects.requireNonNull(region.getWorld()));
    }

    public static class Layer {
        HashMap<BlockVector3, Material> blocks;

        public Layer(HashMap<BlockVector3, Material> blocks) {
            this.blocks = blocks;
        }

        public HashMap<BlockVector3, Material> blocks() {
            return blocks;
        }
    }

}


