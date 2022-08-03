package svinerus.buildtogether.utils.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.jar.JarFile;

public class Storage {
    public static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(World.class, new GsonHelper.WorldEditWorld())
      .registerTypeAdapter(BukkitWorld.class, new GsonHelper.WorldEditWorld())
      .enableComplexMapKeySerialization()
      .create();



    public static class Buildings {

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
            createPath(getBuildingsPath());
            for (var building : buildings.values())
                saveBuilding(building);
        }

        static void saveBuilding(Building building) throws IOException {
            var path = getBuildingsPath().resolve(building.getName() + ".json");
            Files.write(path, gson.toJson(building).getBytes());
        }

        private static Path getBuildingsPath() {
            return getPluginPath().resolve("buildings");
        }

    }

    public static class ExtractingFiles {

        public static void ensureFilesExist() {
            try {
                copyResourceDirectory("assets", getPluginPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // todo create config
        }

        public static void copyResourceDirectory(String jarPath, final Path target) throws IOException {
            var home = BuildTogether.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(5);
            var source = new JarFile(home);
            var entries = source.entries();
            jarPath = String.format("%s/", jarPath);


            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (!entry.getName().startsWith(jarPath) || entry.isDirectory()) continue;

                var dest = target.resolve(entry.getName().substring(jarPath.length()));
                if (dest.toFile().exists()) continue;

                createPath(dest.getParent());
                Files.copy(source.getInputStream(entry), dest);
            }
        }

    }


    private static Path getPluginPath() {
        return BuildTogether.instance.getDataFolder().toPath();
    }


    private static void createPath(Path p) throws IOException {
        if (!Files.exists(p)) Files.createDirectories(p);
    }
}
