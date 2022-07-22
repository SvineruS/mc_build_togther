package svinerus.buildtogether;

import com.google.gson.Gson;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Material;
import org.junit.Test;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingSchema;
import svinerus.buildtogether.building.Layer;

import java.util.ArrayList;
import java.util.List;

public class JsonTest {

    @Test
    public void layer() {
        var layer = new Layer();
        layer.blocks.put(BlockVector3.at(0, 0, 0), Material.AIR);
        var j = new Gson().toJson(layer);
        System.out.println(j);
    }



    @Test
    public void building() {
        var layer = new Layer();
        layer.blocks.put(BlockVector3.at(0, 0, 0), Material.AIR);
        System.out.println(new Gson().toJson(layer));

        var region = new CuboidRegion(BlockVector3.at(0, 0, 0), BlockVector3.at(10, 10, 10));
        System.out.println(new Gson().toJson(region));

        var schema = new BuildingSchema(region, new ArrayList<>(List.of(layer)));
        System.out.println(new Gson().toJson(schema));

        var building = new Building("name", schema);
        System.out.println(new Gson().toJson(building));
    }



}