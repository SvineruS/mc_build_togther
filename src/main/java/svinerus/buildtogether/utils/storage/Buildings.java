package svinerus.buildtogether.utils.storage;

import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Buildings {


    public static HashMap<String, Building> loadBuildingsSafe() {
        HashMap<String, Building> building = new HashMap<>();
        try {
            building = loadBuildings();
        } catch (java.nio.file.NoSuchFileException ignored) {
        } catch (Exception e) {
            e.printStackTrace(System.out);
            BuildTogether.instance.getLogger().warning("Failed to load config");
        }
        return building;
    }

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
        return StorageUtils.gson.fromJson(fileReader, Building.class);
    }



    public static void saveBuildings(HashMap<String, Building> buildings) throws IOException {
        StorageUtils.createPath(getBuildingsPath());
        for (var building : buildings.values())
            saveBuilding(building);
    }


    static void saveBuilding(Building building) throws IOException {
        var path = getBuildingsPath().resolve(building.getName() + ".json");
        Files.write(path, StorageUtils.gson.toJson(building).getBytes());
    }

    private static Path getBuildingsPath() {
        return StorageUtils.getPluginPath().resolve("buildings");
    }

}
