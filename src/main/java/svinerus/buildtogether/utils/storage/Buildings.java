package svinerus.buildtogether.utils.storage;

import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class Buildings {


    // load

    public static HashMap<String, Building> loadBuildings() {
        var buildings = new HashMap<String, Building>();
        List<Path> allFiles = null;
        try {
            allFiles = Utils.allFiles(getBuildingsPath());
        } catch (IOException e) {
            Utils.exception(e,"Failed to scan files in " + getBuildingsPath());
        }

        for (var file : allFiles) {
            var building = loadBuilding(file);
            if (building != null)
                buildings.put(building.getName(), building);
        }
        return buildings;
    }


    public static Building loadBuilding(String buildingName) {
        return loadBuilding(getBuildingPath(buildingName));
    }

    public static Building loadBuilding(Path filePath) {
        try {
            var fileReader = Files.newBufferedReader(filePath);
            return StorageUtils.gson.fromJson(fileReader, Building.class);
        } catch (IOException e) {
            Utils.exception(e, "Failed to load building from " + filePath);
        }
        return null;
    }


    // save

    public static void saveBuilding(Building building) {
        var filePath = getBuildingPath(building.getName());
        try {
            StorageUtils.createPath(filePath);
            Files.write(filePath, StorageUtils.gson.toJson(building).getBytes());
        } catch (IOException e) {
            Utils.exception(e,"Failed to save config to " + filePath);
        }
    }

    // utils

    private static Path getBuildingPath(String buildingName) {
        return getBuildingsPath().resolve(buildingName + ".json");
    }

    private static Path getBuildingsPath() {
        return StorageUtils.getPluginPath().resolve("buildings");
    }

}
