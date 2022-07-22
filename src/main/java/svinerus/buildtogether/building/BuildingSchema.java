package svinerus.buildtogether.building;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Objects;


public class BuildingSchema {
    final public Region region;
    final public ArrayList<Layer> layers;

    public BuildingSchema(Region region, ArrayList<Layer> layers) {
        this.region = region;
        this.layers = layers;
    }

    public BuildingSchema(Region region, Layer layer) {
        this.region = region;
        this.layers = new ArrayList<>();
        this.layers.add(layer);
    }

    public World world() {
        return BukkitAdapter.adapt(Objects.requireNonNull(region.getWorld()));
    }
}


