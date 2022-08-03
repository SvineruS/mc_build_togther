package svinerus.buildtogether.aux;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import svinerus.buildtogether.events.BlockPlacedEvent;

public class Scoreboard {
    private final org.bukkit.scoreboard.Scoreboard scoreboard;

    public Scoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public void addScoreboardPoints(BlockPlacedEvent event) {
        addScore(getObjective("worth_all_buildings"), event.getPlayer(), 1);
        addScore(getObjective("worth_" + event.getBuilding().getName()), event.getPlayer(), 1);
    }

    public void addScoreboardPointsWorth(BlockPlacedEvent event, int worth) {
        addScore(getObjective("worth_all_buildings"), event.getPlayer(), worth);
        addScore(getObjective("worth_" + event.getBuilding().getName()), event.getPlayer(), worth);
    }

    private Objective getObjective(String name) {
        var scoreboardObjective = scoreboard.getObjective("bt_" + name);
        if (scoreboardObjective == null)
            scoreboardObjective = scoreboard.registerNewObjective("BuildTogether", "dummy");
        return scoreboardObjective;
    }

    private void addScore(Objective objective, Player player, int amount) {
        var score = objective.getScore(player);
        score.setScore(score.getScore() + amount);
    }

}
