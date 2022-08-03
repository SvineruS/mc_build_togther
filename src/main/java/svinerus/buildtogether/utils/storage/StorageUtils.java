package svinerus.buildtogether.utils.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import svinerus.buildtogether.BuildTogether;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageUtils {
    public static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(World.class, new GsonHelper.WorldEditWorld())
      .registerTypeAdapter(BukkitWorld.class, new GsonHelper.WorldEditWorld())
      .enableComplexMapKeySerialization()
      .create();

    static Path getPluginPath() {
        return BuildTogether.instance.getDataFolder().toPath();
    }


    static void createPath(Path p) throws IOException {
        if (!Files.exists(p)) Files.createDirectories(p);
    }

    static YamlConfiguration readYaml(Path path) throws IOException, InvalidConfigurationException {
        var cfg = new YamlConfiguration();
        cfg.load(path.toFile());
        return cfg;
    }
}
