package svinerus.buildtogether.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.building.BuildingsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
                case "delete" -> delete(sender, args);
                case "where" -> where(sender, args);
                case "list" -> list(sender, args);
                default -> throw new Exception("Unknown subcommand");
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            sender.sendMessage(ChatColor.GOLD + "[BuildTogether] " + ChatColor.RED + e);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length <= 1) return new ArrayList<>(List.of(
          "create", "delete", "where", "list"
        ));
        switch (args[0]) {
            case "create" -> {
                if (args.length == 2) return new ArrayList<>(List.of("[name_of_new_building]"));
                if (args.length == 3) return schematicNames();
                return new ArrayList<>();
            }
            case "delete" -> {
                if (args.length == 2) return buildingNames();
                return new ArrayList<>();
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

        var building = BuildingsManager.instance.create(buildingName, schematicName, ((Entity) sender).getLocation());
        sendPluginMsg(sender, "block to build: " + blocksCount(building.getBuildingSchema().getSchemaBlocks()));
        sendPluginMsg(sender, "blocks to remove: " + blocksCount(building.getBuildingSchema().getWorldBlocks()));
        sendPluginMsg(sender, "building created!");
    }

    void delete(CommandSender sender, String[] args) throws Exception {
        if (args.length != 2) throw new Exception("Wrong number of arguments");

        String buildingName = args[1];
        BuildingsManager.instance.remove(buildingName);
        sendPluginMsg(sender, "building deleted!");
    }

    void list(CommandSender sender, String[] args) {
        var names = buildingNames();
        var namesText = names.isEmpty() ? "Empty" :
          String.join(",", names.toArray(new String[0]));

        sendPluginMsg(sender, namesText);
    }


    void where(CommandSender sender, String[] args) throws Exception {
        if (args.length < 2) throw new Exception("Wrong number of arguments");
        if (!(sender instanceof Entity)) throw new Exception("Only entity can use this command");

        String buildingName = args[1];

        var where = BuildingsManager.instance.getBuilding(buildingName).where(((Entity) sender).getLocation());
        if (args.length >= 3 && args[2].equals("-t")) {
            // todo check perms
            ((Entity) sender).teleport(where);
        } else {
            sendPluginMsg(sender, "Nearest block is " +
              where.getBlockX() + " " + where.getBlockY() + " " + where.getBlockZ());
        }
    }

    static List<String> buildingNames() {
        return BuildingsManager.instance.getNames();
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

    private void sendPluginMsg(CommandSender sender, String text) {
        sender.sendMessage(ChatColor.GOLD + "[BuildTogether] " + ChatColor.GREEN + text);
    }

    private String blocksCount(Stream<Material> materials) {
        var matCounts = materials.collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        var result = new StringBuilder();
        matCounts.entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue())
          .forEach(e -> result
            .append(e.getKey())
            .append(": ")
            .append(e.getValue())
            .append("\n"));
        return result.toString();
    }


}
