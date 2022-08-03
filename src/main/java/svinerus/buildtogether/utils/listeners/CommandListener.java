package svinerus.buildtogether.utils.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener implements CommandExecutor, TabCompleter {

    public static void register(JavaPlugin plugin) {
        var cmd = plugin.getCommand("bt");
        assert cmd != null;
        var executor = new CommandListener();
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (args.length < 1) throw new Exception("Enter subcommand");
            switch (args[0]) {
                case "create" -> create(sender, args);
                case "remove" -> remove(sender, args);
                case "where" -> where(sender, args);
                case "list" -> list(sender, args);
                default -> throw new Exception("Unknown subcommand");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            BuildTogether.chat.sendMsg(sender, Component.text(e.toString()).color(NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length <= 1) return new ArrayList<>(List.of(
          "create", "remove", "where", "list"
        ));
        switch (args[0]) {
            case "create" -> {
                if (args.length == 2) return new ArrayList<>(List.of("[name_of_new_building]"));
                if (args.length == 3) return schematicNames();
            }
            case "remove" -> {
                if (args.length == 2) return buildingNames();
            }
            case "where" -> {
                if (args.length == 2) return buildingNames();
                if (args.length == 3) return new ArrayList<>(List.of("-t", ""));
            }
        }
        return new ArrayList<>();
    }

    void create(CommandSender sender, String[] args) throws Exception {
        if (args.length != 3) throw new Exception("Wrong number of arguments");
        if (!(sender instanceof Entity)) throw new Exception("Only entity can use this command");

        String buildingName = args[1];
        String schematicName = args[2];

        var building = BuildTogether.buildingsManager.create(buildingName, schematicName, ((Entity) sender).getLocation());
        BuildTogether.chat.sendMsg(sender,
          Component.text("blocks to build:").color(NamedTextColor.WHITE)
            .append(blocksCount(building.getBuildingSchema().getSchemaBlocks()))
        );
        BuildTogether.chat.sendMsg(sender,
          Component.text("blocks to remove:").color(NamedTextColor.WHITE)
            .append(blocksCount(building.getBuildingSchema().getWorldBlocks())));
        BuildTogether.chat.sendMsg(sender, "building created!");
    }

    void remove(CommandSender sender, String[] args) throws Exception {
        if (args.length != 2) throw new Exception("Wrong number of arguments");

        String buildingName = args[1];
        BuildTogether.buildingsManager.remove(buildingName);
        BuildTogether.chat.sendMsg(sender, "building removed!");
    }

    void list(CommandSender sender, String[] args) {
        var names = buildingNames();
        var namesText = names.isEmpty() ? "Empty" :
          String.join(",", names.toArray(new String[0]));

        BuildTogether.chat.sendMsg(sender, namesText);
    }


    void where(CommandSender sender, String[] args) throws Exception {
        if (args.length < 2) throw new Exception("Wrong number of arguments");
        if (!(sender instanceof Entity)) throw new Exception("Only entity can use this command");

        String buildingName = args[1];

        var where = BuildTogether.buildingsManager.getBuilding(buildingName).where(((Entity) sender).getLocation());
        if (args.length >= 3 && args[2].equals("-t")) {
            // todo check perms
            ((Entity) sender).teleport(where);
        } else {
            BuildTogether.chat.sendMsg(sender, Component.text("Nearest block is ", NamedTextColor.WHITE).append(
              Component.text("[" + where.getBlockX() + ", " + where.getBlockY() + ", " + where.getBlockZ() + "]")
                .color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/bt " + String.join(" ", args) + " -t"))
            ));
        }
    }

    static List<String> buildingNames() {
        return BuildTogether.buildingsManager.getNames();
    }

    static List<String> schematicNames() {
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


    private TextComponent.Builder blocksCount(Stream<Material> materials) {
        var result = Component.text();

        var matCounts = materials.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        matCounts.remove(Material.AIR);

        matCounts.entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue())
          .forEach(e -> result
            .append(Component.text("\n"))
            .append(Component.translatable(e.getKey()).color(NamedTextColor.BLUE))
            .append(Component.text(": ").color(NamedTextColor.GRAY))
            .append(Component.text(e.getValue())).color(NamedTextColor.WHITE));

        return result;
    }

}
