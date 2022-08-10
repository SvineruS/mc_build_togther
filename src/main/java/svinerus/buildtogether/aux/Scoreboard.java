package svinerus.buildtogether.aux;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import svinerus.buildtogether.events.BlockPlacedEvent;

import java.util.HashMap;

public class Scoreboard {
    private final org.bukkit.scoreboard.Scoreboard scoreboard;

    public Scoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public void addScoreboardPoints(BlockPlacedEvent event) {
        addScore(getObjective("all_buildings"), event.getPlayer(), 1);
        addScore(getObjective("" + event.getBuilding().getName()), event.getPlayer(), 1);
    }

    public void addScoreboardPointsWorth(BlockPlacedEvent event, int worth) {
        addScore(getObjective("worth_all_buildings"), event.getPlayer(), worth);
        addScore(getObjective("worth_" + event.getBuilding().getName()), event.getPlayer(), worth);
    }

    public HashMap<String, Integer> getScores(Objective objective) {
        var scores = new HashMap<String, Integer>();
        var players = scoreboard.getEntries();
        for (var player : players) {
            var score = objective.getScore(player).getScore();
            if (score == 0) continue;
            scores.put(player, score);
        }
        return scores;
    }


    private Objective getObjective(String name) {
        name = "bt_" + name;
        var scoreboardObjective = scoreboard.getObjective(name);
        if (scoreboardObjective == null)
            scoreboardObjective = scoreboard.registerNewObjective(name, "dummy");
        return scoreboardObjective;
    }

    private void addScore(Objective objective, Player player, int amount) {
        var score = objective.getScore(player);
        score.setScore(score.getScore() + amount);
    }


}
