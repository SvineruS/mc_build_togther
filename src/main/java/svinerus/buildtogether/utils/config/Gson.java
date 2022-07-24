package svinerus.buildtogether.utils.config;

import com.google.gson.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;

class WEWorldGsonHelper implements JsonDeserializer<World>, JsonSerializer<BukkitWorld> {


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
