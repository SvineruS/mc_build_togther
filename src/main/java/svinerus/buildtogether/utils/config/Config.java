package svinerus.buildtogether.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Config {
    public static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(World.class, new WEWorldGsonHelper())
      .registerTypeAdapter(BukkitWorld.class, new WEWorldGsonHelper())
      .enableComplexMapKeySerialization()
      .create();


    public static HashMap<String, Building> loadBuildings() throws IOException {
        var buildings = new HashMap<String, Building>();
        for (var file : Utils.allFiles(getBuildingsPath())) {
            var building = loadBuilding(file);
            buildings.put(building.getName(), building);
        }
        return buildings;
    }
    static Building loadBuilding(Path filePath) throws IOException {
        var fileReader = Files.newBufferedReader(filePath);
        return gson.fromJson(fileReader, Building.class);
    }


    public static void saveBuildings(HashMap<String, Building> buildings) throws IOException {
        if (!Files.exists(getBuildingsPath())) Files.createDirectories(getBuildingsPath());
        for (var building : buildings.values())
            saveBuilding(building);
    }
    static void saveBuilding(Building building) throws IOException {
        var path = getBuildingsPath().resolve(building.getName() + ".json");
        Files.write(path, gson.toJson(building).getBytes());
    }




    private static Path getBuildingsPath() {
        return BuildTogether.instance.getDataFolder().toPath().resolve("buildings");
    }

}
