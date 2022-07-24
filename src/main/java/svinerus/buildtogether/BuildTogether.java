package svinerus.buildtogether;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingsManager;
import svinerus.buildtogether.utils.CommandListener;
import svinerus.buildtogether.utils.config.Config;
import svinerus.buildtogether.utils.EventListener;

import java.io.IOException;
import java.util.HashMap;

public final class BuildTogether extends JavaPlugin {

    public static BuildTogether instance;

    public static WorldEditPlugin WEPlugin;

    @Override
    public void onEnable() {
        instance = this;
        WEPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"); // init world edit

        // register events and commands
        EventListener.register(this);
        CommandListener.register(this);

        // create buildings manager
        HashMap<String, Building> building = loadBuildings();
        BuildingsManager.instance = new BuildingsManager(building);
    }


    @Override
    public void onDisable() {
        try {
            BuildingsManager.instance.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @NotNull
    private HashMap<String, Building> loadBuildings() {
        HashMap<String, Building> building = new HashMap<>();
        try {
            building = Config.loadBuildings();
        } catch (java.nio.file.NoSuchFileException ignored) {
        } catch (Exception e) {
            e.printStackTrace(System.out);
            this.getLogger().warning("Failed to load config");
        }
        return building;
    }
}
