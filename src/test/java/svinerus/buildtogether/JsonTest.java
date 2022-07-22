package svinerus.buildtogether;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Material;
import org.junit.Assert;
import org.junit.Test;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingSchema;
import svinerus.buildtogether.building.Layer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonTest {

    @Test
    public void building() {
        var gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        var locVec = BlockVector3.at(0, 0, 0);
        var mat = Material.AIR;

        var layer = new Layer();
        layer.blocks.put(locVec, mat);

        var region = new CuboidRegion(BlockVector3.at(0, 0, 0), BlockVector3.at(10, 10, 10));
        var schema = new BuildingSchema(region, new ArrayList<>(List.of(layer)));

        var building = new Building("name", schema);

        var buildingJ1 = gson.toJson(building);
        var building2 = gson.fromJson(buildingJ1, Building.class);
        var buildingJ2 = gson.toJson(building2);
        System.out.println(buildingJ1);
        Assert.assertEquals(buildingJ2, buildingJ1);
    }


}