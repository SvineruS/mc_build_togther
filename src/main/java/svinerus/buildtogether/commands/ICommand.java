package svinerus.buildtogether.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import svinerus.buildtogether.BuildTogether;

import java.util.List;

interface ICommand {
    String getPerms();

    void run(CommandSender sender, String[] args) throws Exception;

    List<String> tabCompletion(CommandSender sender, String[] args);
}
