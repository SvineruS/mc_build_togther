package svinerus.buildtogether.aux;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.BlockPlacement;
import svinerus.buildtogether.events.BlockPlacedEvent;
import svinerus.buildtogether.utils.Utils;
import svinerus.buildtogether.utils.storage.StorageUtils;

import javax.annotation.Nullable;
import java.io.IOException;

public class Rewards implements Listener {


    @Nullable
    private final Scoreboard scoreboard;
    private YamlConfiguration worth;

    public Rewards() {
        scoreboard = new Scoreboard();
        try {
            worth = StorageUtils.readYaml(StorageUtils.getPluginPath().resolve("materials_worth.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            Utils.exception(e, "Could not read materials_worth.yml; Working without worth!");
        }
    }

    public static void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new Rewards(), plugin);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlacedEvent event) {
        if (event.getBlockPlacement() != BlockPlacement.CORRECT) return;


        scoreboard.addScoreboardPoints(event);

        if (worth != null) {
            var material = event.getBlockEvent().getBlock().getType().toString();
            var blockWorth = worth.getDouble(material) * BuildTogether.instance.getConfig().getDouble("rewards.worth_multiplier");

//            essApi.giveMoney(event.getPlayer().getName(), worth);
            scoreboard.addScoreboardPointsWorth(event, (int)Math.ceil(blockWorth));
        }
    }


}
