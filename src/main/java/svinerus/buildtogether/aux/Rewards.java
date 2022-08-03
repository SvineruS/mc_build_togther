package svinerus.buildtogether.aux;

import com.earth2me.essentials.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.BlockPlacement;
import svinerus.buildtogether.events.BlockPlacedEvent;

import javax.annotation.Nullable;
import java.math.BigDecimal;

public class Rewards  implements Listener {


    @Nullable
    private EssentialsApi essApi;
    private final Scoreboard scoreboard;

    public Rewards() {
        essApi = null;
        var essPlugin = (IEssentials) Bukkit.getPluginManager().getPlugin("EssentialsX");
        if (essPlugin == null) BuildTogether.instance.getLogger().warning("Working without EssentialsX");
        else essApi = new EssentialsApi(essPlugin);

        scoreboard = new Scoreboard();
    }

    public static void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new Rewards(), plugin);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlacedEvent event) {
        if (event.getBlockPlacement() != BlockPlacement.CORRECT) return;


        scoreboard.addScoreboardPoints(event);

        if (essApi != null) {
            var worth = essApi.getWorth(event.getBlockEvent().getBlock().getType());
            if (worth == null) worth = BigDecimal.valueOf(1);  // todo

            essApi.giveMoney(event.getPlayer().getName(), worth);
            scoreboard.addScoreboardPointsWorth(event, worth.intValue());
        }


    }


}
