package svinerus.buildtogether.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static svinerus.buildtogether.utils.Localization.lt;

public class CmdCreate implements ICommand{

    public String getPerms() {
        return "create";
    }

    public void run(CommandSender sender, String[] args) throws Exception {
        if (args.length != 3) throw new Exception("error.wrong_args_num");
        if (!(sender instanceof Player)) throw new Exception("error.only_player");

        String buildingName = args[1];
        String schematicName = args[2];

        var building = BuildTogether.buildingsManager.create(buildingName, schematicName, ((Entity) sender).getLocation());
        CommandListener.chat.sendMsg(sender,
          Component.text(lt("create.blocks_add_list"), NamedTextColor.WHITE)
            .append(blocksCount(building.getBuildingSchema().getSchemaBlocks()))
        );
        CommandListener.chat.sendMsg(sender,
          Component.text(lt("create.blocks_remove_list"), NamedTextColor.WHITE)
            .append(blocksCount(building.getBuildingSchema().getWorldBlocks())));
        CommandListener.chat.sendMsg(sender, lt("create.success"));
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        if (args.length == 2) return new ArrayList<>(List.of("[name_of_new_building]"));
        if (args.length == 3) return schematicNames();
        return new ArrayList<>();
    }



    private TextComponent.Builder blocksCount(Stream<Material> materials) {
        var result = Component.text();

        var matCounts = materials.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        matCounts.remove(Material.AIR);

        matCounts.entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue())
          .forEach(e -> result
            .append(Component.text("\n"))
            .append(Component.translatable(e.getKey(), NamedTextColor.BLUE))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(Component.text(e.getValue(), NamedTextColor.WHITE)));

        return result;
    }


    private static List<String> schematicNames() {
        try {
            return Utils.allFiles(Utils.schematicsPath()).stream()
              .map(p -> p.getFileName().toString())
              .filter(p -> p.endsWith(".schem"))
              .map(p -> p.substring(0, p.length() - 6))
              .toList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
