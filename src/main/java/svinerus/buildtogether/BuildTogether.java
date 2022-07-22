package svinerus.buildtogether;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingsManager;
import svinerus.buildtogether.utils.CommandListener;
import svinerus.buildtogether.utils.Config;
import svinerus.buildtogether.utils.EventListener;

import java.io.IOException;
import java.util.HashMap;

public final class BuildTogether extends JavaPlugin {

    public static BuildTogether instance;

    public static WorldEditPlugin WEPlugin;
    public static RegionQuery WGRegionQuery;

    @Override
    public void onEnable() {
        instance = this;

        // register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        // register commands
        CommandListener.register(this);


        // read config
        HashMap<String, Building> building = new HashMap<>();
        try {
            building = Config.loadBuildings();
        } catch (java.nio.file.NoSuchFileException ignored) {
        } catch (Exception e) {
            this.getLogger().warning("Failed to load config");
        }

        // create buildings manager
        BuildingsManager.instance = new BuildingsManager(building);

        // init world edit
        initWorldEdit();
    }

    @Override
    public void onDisable() {
        try {
            BuildingsManager.instance.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void initWorldEdit() {
        WEPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WGRegionQuery = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        }

    }
}
