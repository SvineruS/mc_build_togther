package svinerus.buildtogether.generator;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import svinerus.buildtogether.building.BuildingSchema;
import svinerus.buildtogether.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class ParseSchematic {

    public static BuildingSchema Parse(Location worldOrigin, String schemaName) throws IOException {
        return parseSchematic(worldOrigin, loadSchematic(schemaName));
    }

    private static BuildingSchema parseSchematic(Location worldOrigin, Clipboard schema) {
        var minimumPoint = schema.getMinimumPoint();
        var schemaOrigin = schema.getOrigin();
        var dimensions = schema.getDimensions();


        HashMap<BlockVector3, Material> layer = new HashMap<>();

        for (int y = 0; y < dimensions.getBlockY(); y++) {
            for (int x = 0; x < dimensions.getBlockX(); x++) {
                for (int z = 0; z < dimensions.getBlockZ(); z++) {

                    var blockSchemPos = minimumPoint.add(x, y, z);
                    var block = schema.getBlock(blockSchemPos);


                    // wo + min + i - so
                    var blockWorldPos = blockSchemPos.subtract(schemaOrigin).add(Utils.toVector(worldOrigin));
                    var mat = BukkitAdapter.adapt(block.toBaseBlock().getBlockType());

                    layer.put(blockWorldPos, mat);

                }
            }
        }

        var worldMinimumPoint = BukkitAdapter.asBlockVector(worldOrigin).add(minimumPoint).subtract(schemaOrigin);
        var region = new CuboidRegion(worldMinimumPoint, worldMinimumPoint.add(dimensions));
        region.setWorld(BukkitAdapter.adapt(worldOrigin.getWorld()));
        return new BuildingSchema(region, new BuildingSchema.Layer(layer));
    }


    private static Clipboard loadSchematic(String schematicName) throws IOException {
        var file = new File(Utils.schematicsPath + schematicName + ".schem");

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        assert format != null;
        ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
        return reader.read();
    }
}
