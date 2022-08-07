package svinerus.buildtogether.utils.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
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
import svinerus.buildtogether.utils.Localization;
import svinerus.buildtogether.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener implements CommandExecutor, TabCompleter {

    private final Chat chat;

    public CommandListener(Chat chat) {
        this.chat = chat;
    }

    public static void register(JavaPlugin plugin) {
        var cmd = plugin.getCommand("bt");
        assert cmd != null;
        var executor = new CommandListener(new Chat(BuildTogether.localization));
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
            chat.sendMsg(sender, Component.text(e.toString()).color(NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length <= 1) {
            return Arrays.stream(new String[]{"create", "remove", "where", "list"})
              .filter(e -> havePerms(sender, e)).toList();
        }
        switch (args[0]) {
            case "create" -> {
                if (args.length == 2) return new ArrayList<>(List.of("[name_of_new_building]"));
                if (args.length == 3) return schematicNames();
            }
            case "list", "remove", "where" -> {
                if (args.length == 2) return buildingNames();
            }
        }
        return new ArrayList<>();
    }

    void create(CommandSender sender, String[] args) throws Exception {
        requirePerms(sender, "create");
        if (args.length != 3) throw new Exception("Wrong number of arguments");
        if (!(sender instanceof Entity)) throw new Exception("Only entity can use this command");

        String buildingName = args[1];
        String schematicName = args[2];

        var building = BuildTogether.buildingsManager.create(buildingName, schematicName, ((Entity) sender).getLocation());
        chat.sendMsg(sender,
          Component.text("create.blocks_add_list", NamedTextColor.WHITE)
            .append(blocksCount(building.getBuildingSchema().getSchemaBlocks()))
        );
        chat.sendMsg(sender,
          Component.text("create.blocks_remove_list", NamedTextColor.WHITE)
            .append(blocksCount(building.getBuildingSchema().getWorldBlocks())));
        chat.sendMsg(sender, "create.success");
    }

    void remove(CommandSender sender, String[] args) throws Exception {
        requirePerms(sender, "remove");
        if (args.length != 2) throw new Exception("Wrong number of arguments");

        String buildingName = args[1];
        BuildTogether.buildingsManager.remove(buildingName);
        chat.sendMsg(sender, "remove.success");
    }

    void list(CommandSender sender, String[] args) throws Exception {
        requirePerms(sender, "list");
        var names = buildingNames();
        var namesText = names.isEmpty() ? "list.empty" :
          String.join(",", names.toArray(new String[0]));

        chat.sendMsg(sender, namesText);
    }


    void where(CommandSender sender, String[] args) throws Exception {
        requirePerms(sender, "where");
        if (args.length < 2) throw new Exception("Wrong number of arguments");
        if (!(sender instanceof Entity)) throw new Exception("Only entity can use this command");

        String buildingName = args[1];

        var where = BuildTogether.buildingsManager.getBuilding(buildingName).where(((Entity) sender).getLocation());
        chat.sendMsg(sender, Component.text("where.nearest_block ", NamedTextColor.WHITE).append(
          Component.text("[" + where.getBlockX() + ", " + where.getBlockY() + ", " + where.getBlockZ() + "]", NamedTextColor.GREEN)
            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp  " + where.getBlockX() + " " + where.getBlockY() + " " + where.getBlockZ()))
        ));
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
            .append(Component.translatable(e.getKey(), NamedTextColor.BLUE))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(Component.text(e.getValue(), NamedTextColor.WHITE)));

        return result;
    }

    private boolean havePerms(CommandSender sender, String perm) {
        return sender.hasPermission("buildtogether." + perm) || sender.isOp();
    }

    private void requirePerms(CommandSender sender, String perm) throws Exception {
        if (!havePerms(sender, perm)) throw new Exception("no_perms");
    }

    public static class Chat {
        private static final TextComponent text_ = Component.text("[BuildTogether] ").color(NamedTextColor.GOLD);
        private final TextReplacementConfig replacer;

        public Chat(Localization locale) {
            replacer = locale.toTextReplacementConfig();
        }

        public void sendMsg(CommandSender sender, String text) {
            sendMsg(sender, Component.text(text).color(NamedTextColor.WHITE));
        }

        public void sendMsg(CommandSender sender, TextComponent textComponent) {
            sender.sendMessage(text_.append(localizeText(textComponent)));
        }

        private @NotNull Component localizeText(Component textComponent) {
            return textComponent.replaceText(replacer);
        }

    }
}
