package svinerus.buildtogether.schema;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Material;
import org.bukkit.World;
import svinerus.buildtogether.utils.Utils;

import java.util.HashMap;

public class Layer {
    final public HashMap<BlockVector3, Material> blocks;

    public Layer() {
        this.blocks = new HashMap<>();
    }

    public Layer(HashMap<BlockVector3, Material> blocks) {
        this.blocks = blocks;
    }

    public boolean isFinished(World world) {
        for (var location : blocks.keySet())
            if (!isBlockCorrect(location, world))
                return false;
        return true;
    }

    // is block set in real world equals block from schema
    public boolean isBlockCorrect(BlockVector3 location, World world) {
        return isBlockCorrect(location, Utils.toLocation(world, location).getBlock().getType());
    }

    // is passed block equals block from schema
    public boolean isBlockCorrect(BlockVector3 location, Material material) {
        return blocks.get(location) == material;
    }

}
