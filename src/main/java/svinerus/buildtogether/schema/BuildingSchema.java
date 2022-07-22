package svinerus.buildtogether.schema;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.World;

import java.util.ArrayList;


public class BuildingSchema {
    public Region region;
    public ArrayList<Layer> layers;

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
        return BukkitAdapter.adapt(region.getWorld());
    }
}


