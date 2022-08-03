package svinerus.buildtogether.aux;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingsManager;

import java.util.*;
import java.util.stream.Collectors;

public class PlaceholderApi extends PlaceholderExpansion {

    BuildingsCache buildingsCache = BuildingsCache.instance;

    @Override
    public String getAuthor() {
        return "svinerus";
    }

    @Override
    public String getIdentifier() {
        return "buildtogether";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        var p = PlaceholderAPI.setBracketPlaceholders(player, params).split("_");

        if (Objects.equals(p[0], "timemod")) {
            if (p.length != 2) return "bt_timemod_<mod>";
            return String.valueOf(System.currentTimeMillis() / 1000L % Integer.parseInt(p[1]));
        }

        if (p.length < 2) return "bt_needblocksmap_<buildingname>";
        var building = "inside".equals(p[1]) ?
          BuildingsManager.instance.getBuilding(player.getLocation()) :
          BuildingsManager.instance.getBuilding(p[1]);


        switch (p[0]) {
            case "isinside":
                return building != null ? "yes" : "no";

            case "needblockscount":
                return String.valueOf(buildingsCache.get(building.getName()).needBlocksMap());
            case "needblocksuniqcount":
                return String.valueOf(buildingsCache.get(building.getName()).needBlocksUniqCount());
            case "needblocksmap":
                if (p.length != 4) return "bt_needblocksmap_<buildingname>_<index>_<block|count>";
                var index = Integer.parseInt(p[2]);
                var blocksCount = buildingsCache.get(building.getName()).needBlocksMap().get(index);
                var res = "block".equals(p[3]) ?
                  blocksCount.getKey() : blocksCount.getValue();
                return String.valueOf(res);


            case "isneedhandblock":
                var handBlock = player.getInventory().getItemInMainHand().getType();
                return buildingsCache.get(building.getName()).needBlocksSet().contains(handBlock) ? "yes" : "no";

        }

        return "unknown param" + p[0];
    }

    static class BuildingsCache {
        public static BuildingsCache instance = new BuildingsCache();
        private final HashMap<String, BuildingCache> cache = new HashMap<>();

        public BuildingCache get(String buildingName) {
            return cache.computeIfAbsent(buildingName, k -> new BuildingCache(BuildingsManager.instance.getBuilding(buildingName)));
        }
        public BuildingCache invalidate(String buildingName) {
            return cache.remove(buildingName);
        }

        static class BuildingCache {
            private final Building building;
            private Double progress;
            private Integer needBlocksCount;
            private Set<Material> needBlocksSet;
            private List<Map.Entry<Material, Long>> needBlocksCountMap;

            public BuildingCache(Building building) {
                this.building = building;
            }

            public double progress() {
                if (progress == null) recalc();
                return progress;
            }

            public int needBlocksCount() {
                if (needBlocksCount == null) recalc();
                return needBlocksCount;
            }

            public int needBlocksUniqCount() {
                if (needBlocksCount == null) recalc();
                return needBlocksSet.size();
            }

            public Set<Material> needBlocksSet() {
                if (needBlocksSet == null) recalc();
                return needBlocksSet;
            }

            public List<Map.Entry<Material, Long>> needBlocksMap() {
                if (needBlocksCountMap == null) recalc();
                return needBlocksCountMap;
            }

            private void recalc() {
                progress = building.progress();
                var needBlocks = building.what();
                needBlocksCount = needBlocks.size();
                needBlocksSet = new HashSet<>(needBlocks);
                needBlocksCountMap = needBlocks.stream()
                  .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                  .entrySet().stream()
                  .sorted(Map.Entry.comparingByValue())
                  .toList();
            }
        }
    }

}