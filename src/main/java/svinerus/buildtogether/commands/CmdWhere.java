package svinerus.buildtogether.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import svinerus.buildtogether.BuildTogether;

import java.util.ArrayList;
import java.util.List;

import static svinerus.buildtogether.utils.Localization.lt;

public class CmdWhere implements ICommand{

    public String getPerms() {
        return "where";
    }

    public void run(CommandSender sender, String[] args) throws Exception {
        if (args.length < 2) throw new Exception("Wrong number of arguments");
        if (!(sender instanceof Entity)) throw new Exception("Only entity can use this command");

        String buildingName = args[1];

        var where = BuildTogether.buildingsManager.getBuilding(buildingName).where(((Entity) sender).getLocation());
        CommandListener.chat.sendMsg(sender, Component.text(lt("where.nearest_block"), NamedTextColor.WHITE).append(
          Component.text(" [" + where.getBlockX() + ", " + where.getBlockY() + ", " + where.getBlockZ() + "]", NamedTextColor.GREEN)
            .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("Listener.chat.coordinates.tooltip")))
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp  " + where.getBlockX() + " " + where.getBlockY() + " " + where.getBlockZ()))
        ));
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        if (args.length == 2) return CommandListener.buildingNames();
        return new ArrayList<>();
    }
}
