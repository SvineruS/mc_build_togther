package svinerus.buildtogether.generator;

import org.bukkit.Location;
import svinerus.buildtogether.building.BuildingSchema;

import java.io.IOException;

public class Creator {


    public static BuildingSchema Create(String schemaName, Location where) throws IOException {
        var schema = ParseSchematic.Parse(where, schemaName);

        var clearLayer = ClearRegionLayer.Create(schema.region);
        var clearLayers = new SchemSplitterCoords(clearLayer).splitY(false).collect();

        var layersSplitByHeight = new SchemSplitterCoords(schema.layers.get(0)).splitY(true).collect();

        schema.layers.clear();
        schema.layers.addAll(clearLayers);
        schema.layers.addAll(layersSplitByHeight);

        return schema;
    }

}
