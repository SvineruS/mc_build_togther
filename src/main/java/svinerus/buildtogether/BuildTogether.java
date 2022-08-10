package svinerus.buildtogether;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import svinerus.buildtogether.aux.PlaceholderApi;
import svinerus.buildtogether.aux.Rewards;
import svinerus.buildtogether.building.BlockTip;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingsManager;
import svinerus.buildtogether.commands.CommandListener;
import svinerus.buildtogether.events.EventListener;
import svinerus.buildtogether.utils.storage.Buildings;
import svinerus.buildtogether.utils.storage.ExtractingFiles;
import svinerus.buildtogether.utils.Localization;

import java.io.IOException;
import java.util.HashMap;

public final class BuildTogether extends JavaPlugin {

    public static BuildTogether instance;

    public static WorldEditPlugin WEPlugin;
    public static BuildingsManager buildingsManager;
    public static Localization localization;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        // extract files from jar if not exist
        ExtractingFiles.ensureFilesExist();


        // load locale
        var locale = this.getConfig().getString("locale");
        localization = new Localization("locales", locale);

        // load required WorldEdit plugin
        WEPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

        // create buildings manager
        HashMap<String, Building> building = Buildings.loadBuildings();
        buildingsManager = new BuildingsManager(building);


        // register events and commands
        EventListener.register(this);
        CommandListener.register(this);

        // register reward manager
        if (this.getConfig().getBoolean("rewards.enabled"))
            Rewards.register(this);

        // Register PlaceholderAPI expansion
        // https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/PlaceholderExpansion#register-the-expansion
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new PlaceholderApi(locale).register();
    }


    @Override
    public void onDisable() {
        try {
            buildingsManager.shutdown();
            buildingsManager = null;
            BlockTip.killAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
