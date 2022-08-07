package svinerus.buildtogether.utils.storage;

import com.google.gson.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import svinerus.buildtogether.BuildTogether;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class StorageUtils {
    public static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(World.class, new GsonHelper.WorldEditWorld())
      .registerTypeAdapter(BukkitWorld.class, new GsonHelper.WorldEditWorld())
      .enableComplexMapKeySerialization()
      .create();

    public static Path getPluginPath() {
        return BuildTogether.instance.getDataFolder().toPath();
    }


    static void createPath(Path p) throws IOException {
        if (!Files.exists(p)) Files.createDirectories(p);
    }

    public static YamlConfiguration readYaml(Path path) throws IOException, InvalidConfigurationException {
        return YamlConfiguration.loadConfiguration(path.toFile());
    }

    static class GsonHelper {


        static class WorldEditWorld implements JsonDeserializer<World>, JsonSerializer<BukkitWorld> {
            @Override
            public JsonElement serialize(BukkitWorld src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", src.getName());
                return jsonObject;
            }

            @Override
            public World deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                var name = jsonObject.get("name").getAsString();
                return new BukkitWorld(Bukkit.getWorld(name));
            }
        }
    }
}
