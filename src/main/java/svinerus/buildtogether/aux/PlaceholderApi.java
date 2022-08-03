package svinerus.buildtogether.aux;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.building.BuildingsManager;

import java.util.*;
import java.util.stream.Collectors;

public class PlaceholderApi extends PlaceholderExpansion {

    BuildingsCache buildingsCache = BuildingsCache.instance;

    @Override
    public boolean register() {
        BuildTogether.instance.getLogger().info("Registering PlaceholderAPI expansion");
        return super.register();
    }

    @Override
    public String getAuthor() {
        return "svinerus";
    }

    @Override
    public String getIdentifier() {
        return "bt";
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
            if (p.length < 2) return "bt_timemod_<mod>_[seconds]";
            var div = p.length == 3 ? Integer.parseInt(p[2]) : 1;
            return String.valueOf(System.currentTimeMillis() / 1000L / div % Integer.parseInt(p[1]));
        }


        if ("isinside".equals(p[0]))
            return BuildTogether.buildingsManager.getBuilding(player.getLocation()) != null ? "yes" : "no";


        if (p.length < 2) return "bt_<command>_<buildingname>";
        var building = "inside".equals(p[1]) ?
          BuildTogether.buildingsManager.getBuilding(player.getLocation()) :
          BuildTogether.buildingsManager.getBuilding(p[1]);

        if (building == null) return "No such building";

        switch (p[0]) {

            case "progress":
                return String.valueOf((int) (buildingsCache.get(building.getName()).progress() * 100));
            case "need-blocks-count":
                return String.valueOf(buildingsCache.get(building.getName()).needBlocksMap());
            case "need-blocks-uniq-count":
                return String.valueOf(buildingsCache.get(building.getName()).needBlocksUniqCount());
            case "need-blocks-map":
                if (p.length != 4) return "bt_need-blocks-map_<buildingname>_<index>_<block|count>";
                var index = Integer.parseInt(p[2]);
                var blocksCount = buildingsCache.get(building.getName()).needBlocksMap().get(index);
                var res = "block".equals(p[3]) ?
                  blocksCount.getKey() : blocksCount.getValue();
                return String.valueOf(res);


            case "is-need-block-in-hand":
                var handBlock = player.getInventory().getItemInMainHand().getType();
                return buildingsCache.get(building.getName()).needBlocksSet().contains(handBlock) ? "yes" : "no";

            case "is-need-block-in-env":
                var invBlocks = Arrays.stream(player.getInventory().getStorageContents())
                  .filter(Objects::nonNull).map(ItemStack::getType).collect(Collectors.toSet());
                invBlocks.retainAll(buildingsCache.get(building.getName()).needBlocksSet());
                return !invBlocks.isEmpty() ? "yes" : "no";


        }

        return "unknown param" + p[0];
    }

    static class BuildingsCache {
        public static BuildingsCache instance = new BuildingsCache();
        private final HashMap<String, BuildingCache> cache = new HashMap<>();

        public BuildingCache get(String buildingName) {
            return cache.computeIfAbsent(buildingName, k -> new BuildingCache(BuildTogether.buildingsManager.getBuilding(buildingName)));
        }

        public void invalidate(String buildingName) {
            cache.remove(buildingName);
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