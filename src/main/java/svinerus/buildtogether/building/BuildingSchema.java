package svinerus.buildtogether.building;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Objects;


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

    public World world() {
        return BukkitAdapter.adapt(Objects.requireNonNull(region.getWorld()));
    }
}


