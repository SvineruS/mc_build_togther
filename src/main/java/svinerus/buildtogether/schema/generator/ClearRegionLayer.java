package svinerus.buildtogether.schema.generator;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import svinerus.buildtogether.schema.Layer;

import java.util.HashMap;

public class ClearRegionLayer {
    public static Layer Create(Region reg) {
        HashMap<BlockVector3, Material> layer = new HashMap<>();

        for (int x = reg.getMinimumPoint().getBlockX(); x < reg.getMaximumPoint().getBlockX(); x++) {
            for (int y = reg.getMinimumPoint().getBlockY(); y < reg.getMaximumPoint().getBlockY(); y++) {

                for (int z = reg.getMinimumPoint().getBlockZ(); z < reg.getMaximumPoint().getBlockZ(); z++) {

                    var loc = BlockVector3.at(x, y, z);
                    layer.put(loc, Material.AIR);

                }

            }
        }
        return new Layer(layer);
    }

}