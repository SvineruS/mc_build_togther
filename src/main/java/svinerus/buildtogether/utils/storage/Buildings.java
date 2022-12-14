package svinerus.buildtogether.utils.storage;

import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
            Utils.exception(e, "Failed to scan files in " + getBuildingsPath());
            return buildings;
        }

        for (var file : allFiles) {
            if (file.getFileName().toString().endsWith(".off")) continue;
            var building = loadBuilding(file);
            if (building != null)
                buildings.put(building.getName(), building);
        }
        return buildings;
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
            StorageUtils.createPath(filePath.getParent());
            Files.write(filePath, StorageUtils.gson.toJson(building).getBytes());
        } catch (IOException e) {
            Utils.exception(e, "Failed to save config to " + filePath);
        }
    }

    public static void off(String buildingName) throws IOException {
        var filePath = getBuildingPath(buildingName);
        Files.move(filePath, filePath.resolveSibling(buildingName + ".off"), StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean isExist(String buildingName) {
        return getBuildingPath(buildingName).toFile().exists();
    }

    // utils

    private static Path getBuildingPath(String buildingName) {
        return getBuildingsPath().resolve(buildingName + ".json");
    }

    private static Path getBuildingsPath() {
        return StorageUtils.getPluginPath().resolve("buildings");
    }

}
