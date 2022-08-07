package svinerus.buildtogether.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.utils.Localization;

import java.util.*;
import java.util.stream.Collectors;

public class CommandListener implements CommandExecutor, TabCompleter {

    static Chat chat;  // todo
    private final HashMap<String, ICommand> commands;

    public CommandListener(Chat chat) {
        CommandListener.chat = chat;
        this.commands = new HashMap<>();
        this.commands.put("reload", new CmdReload());
        this.commands.put("create", new CmdCreate());
        this.commands.put("list", new CmdList());
        this.commands.put("remove", new CmdRemove());
        this.commands.put("where", new CmdWhere());
        this.commands.put("blocks", new CmdBlocks());

    }

    public static void register(JavaPlugin plugin) {
        var cmd = plugin.getCommand("bt");
        assert cmd != null;
        var executor = new CommandListener(new Chat());
        cmd.setExecutor(executor);
        cmd.setTabCompleter(executor);
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (args.length < 1) throw new Exception("Enter subcommand");
            if (!commands.containsKey(args[0])) throw new Exception("Unknown subcommand");
            var cmd = commands.get(args[0]);
            if (!havePerms(sender, cmd.getPerms()))
                throw new Exception("no_perms");
            cmd.run(sender, args);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            chat.sendMsg(sender, Component.text(e.toString()).color(NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length <= 1) {
            return commands.keySet().stream()
              .filter(e -> havePerms(sender, commands.get(e).getPerms()))
              .collect(Collectors.toList());
        }
        if (!commands.containsKey(args[0])) return new ArrayList<>();

        return commands.get(args[0]).tabCompletion(sender, args);
    }


    static java.util.List<String> buildingNames() {
        return BuildTogether.buildingsManager.getNames();
    }


    private boolean havePerms(CommandSender sender, String perm) {
        return sender.hasPermission("buildtogether." + perm) || sender.isOp();
    }

    public static class Chat {
        private static final TextComponent text_ = Component.text("[BuildTogether] ").color(NamedTextColor.GOLD);

        public void sendMsg(CommandSender sender, String text) {
            sendMsg(sender, Component.text(text).color(NamedTextColor.WHITE));
        }

        public void sendMsg(CommandSender sender, TextComponent textComponent) {
            sender.sendMessage(text_.append(textComponent));
        }

    }
}
