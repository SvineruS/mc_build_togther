package svinerus.buildtogether.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Config {
    final static Gson gson = new Gson();


    public static HashMap<String, Building> loadBuildings() throws IOException {
        var fileReader = Files.newBufferedReader(getConfigPath());
        var type = new TypeToken<HashMap<String, Building>>() {
        }.getType();
        return gson.fromJson(fileReader, type);
    }


    public static void saveBuildings(HashMap<String, Building> buildings) throws IOException {
        var cfgPath = getConfigPath();
        if (!Files.exists(cfgPath.getParent())) Files.createDirectories(cfgPath.getParent());

        Files.write(cfgPath, gson.toJson(buildings).getBytes());
    }

    private static Path getConfigPath() {
        return BuildTogether.instance.getDataFolder().toPath().resolve("cfg.json");
    }

}
