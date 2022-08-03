package svinerus.buildtogether;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.aux.PlaceholderApi;
import svinerus.buildtogether.aux.Rewards;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingsManager;
import svinerus.buildtogether.utils.CommandListener;
import svinerus.buildtogether.utils.storage.Buildings;
import svinerus.buildtogether.utils.storage.ExtractingFiles;
import svinerus.buildtogether.utils.EventListener;

import java.io.IOException;
import java.util.HashMap;

public final class BuildTogether extends JavaPlugin {

    public static BuildTogether instance;

    public static WorldEditPlugin WEPlugin;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        // load required WorldEdit plugin
        WEPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

        // create buildings manager
        HashMap<String, Building> building = Buildings.loadBuildingsSafe();
        BuildingsManager.instance = new BuildingsManager(building);

        // extract files from jar if not exist
        ExtractingFiles.ensureFilesExist();

        // register events and commands
        EventListener.register(this);
        CommandListener.register(this);

        // register reward manager
        Rewards.register(this);

        // Register PlaceholderAPI expansion
        // https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/PlaceholderExpansion#register-the-expansion
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderApi().register();
    }


    @Override
    public void onDisable() {
        try {
            BuildingsManager.instance.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
