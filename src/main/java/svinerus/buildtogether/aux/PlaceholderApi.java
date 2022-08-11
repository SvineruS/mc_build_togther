package svinerus.buildtogether.aux;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.building.Building;
import svinerus.buildtogether.utils.Utils;
import svinerus.buildtogether.utils.Localization;

import java.util.*;

public class PlaceholderApi extends PlaceholderExpansion {

    Localization blocksLocalisation;

    public PlaceholderApi(String locale) {
        this.blocksLocalisation = new Localization("locales/blocks", locale);
    }

    @Override
    public boolean register() {
        Utils.logger().info("Registering PlaceholderAPI expansion");
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
        try {
            return onPlaceholderRequest(player, p);
        } catch (Exception e) {
            Utils.exception(e, "PlaceholderAPI exception;");
        }
        return "internal error";
    }


    private String onPlaceholderRequest(Player player, String[] p) {
        if ("isinside".equals(p[0]))
            return BuildTogether.buildingsManager.getBuilding(player.getLocation()) != null ? "yes" : "no";

        if (p.length < 2) return "bt_<command>_<buildingname>";
        var building = "inside".equals(p[1]) ?
          BuildTogether.buildingsManager.getBuilding(player.getLocation()) :
          BuildTogether.buildingsManager.getBuilding(p[1]);

        if (building == null) return "No such building";

        switch (p[0]) {

            case "correct-blocks":
                return String.valueOf(building.correctBlocks());

            case "total-blocks":
                return String.valueOf(building.getTotalBlocks());

            case "progress":
                return String.valueOf((int) (building.progress() * 100));

            case "need-blocks-map":
                if (p.length != 4) return "bt_need-blocks-map_<buildingname>_<index>_<block|count>";
                var index = Integer.parseInt(p[2]);
                var isCount = "count".equals(p[3]);
                return getBlockInfo(building, index, isCount);

            case "need-blocks-map-auto":
                if (p.length != 4) return "bt_need-blocks-map-auto_<buildingname>_<index>_<block|count>";
                index = Integer.parseInt(p[2]);
                isCount = "count".equals(p[3]);
                var blocksCount = building.needBlocksSorted().size();
                if (blocksCount == 0) return "";
                var autoIndex = System.currentTimeMillis() / 1000L / blocksCount %
                  BuildTogether.instance.getConfig().getInt("placeholderapi.block_change_time");
                index = (index + (int) autoIndex) % (blocksCount + 1);
                return getBlockInfo(building, index, isCount);

            case "is-need-block-in-env":
                var playerInv = player.getInventory();
                var isContains = building.needBlocks().stream().anyMatch(playerInv::contains);
                return isContains ? "yes" : "no";


        }

        return "unknown param: " + p[0];
    }

    private String getBlockInfo(Building building, int index, boolean isCount) {
        var needBlocks = building.needBlocksSorted();
        if (index >= needBlocks.size()) return "";

        var block = needBlocks.get(index);
        if (isCount)
            return block.getValue().toString();
        var localizationKey = block.getKey().translationKey();
        return blocksLocalisation.localize(localizationKey.substring(16));  // remove 'block.minecraft.' prefix
    }


}