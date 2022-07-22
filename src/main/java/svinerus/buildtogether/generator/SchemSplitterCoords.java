package svinerus.buildtogether.generator;

import com.sk89q.worldedit.math.BlockVector3;
import svinerus.buildtogether.building.BuildingSchema;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

class SchemSplitterCoords {

    private List<BuildingSchema.Layer> layers = new ArrayList<>();

    SchemSplitterCoords(BuildingSchema.Layer layerFrom) {
        this.layers.add(layerFrom);
    }

    public SchemSplitterCoords splitX(boolean asc) {
        return split(BlockVector3::getBlockX, asc);
    }

    public SchemSplitterCoords splitY(boolean asc) {
        return split(BlockVector3::getBlockY, asc);
    }

    public SchemSplitterCoords splitZ(boolean asc) {
        return split(BlockVector3::getBlockZ, asc);
    }

    public List<BuildingSchema.Layer> collect() {
        return this.layers;
    }


    // if asc is true, then the order is from lesser to greater
    private SchemSplitterCoords split(Function<BlockVector3, Integer> keyFunc, boolean ascSort) {
        this.layers = splitLayers(this.layers, keyFunc, ascSort);
        return this;
    }

    private static List<BuildingSchema.Layer> splitLayers(List<BuildingSchema.Layer> layersFrom, Function<BlockVector3, Integer> keyFunc, boolean ascSort) {
        ArrayList<BuildingSchema.Layer> result = new ArrayList<>();
        Comparator<Integer> sort = ascSort ? Comparator.naturalOrder() : Comparator.reverseOrder();
        layersFrom.stream().map(layer -> splitLayer(layer, keyFunc, sort)).forEach(result::addAll);
        return result;
    }

    private static List<BuildingSchema.Layer> splitLayer(BuildingSchema.Layer layerFrom, Function<BlockVector3, Integer> keyFunc, Comparator<Integer> sort) {
        HashMap<Integer, BuildingSchema.Layer> layers = new HashMap<>();
        for (var blockLoc : layerFrom.blocks().keySet()) {

            Integer key = keyFunc.apply(blockLoc);
            if (!layers.containsKey(key)) layers.put(key, new BuildingSchema.Layer(new HashMap<>()));

            layers.get(key).blocks().put(blockLoc, layerFrom.blocks().get(blockLoc));

        }
        return layers.keySet().stream()
          .sorted(sort)
          .map(layers::get).toList();
    }
}
