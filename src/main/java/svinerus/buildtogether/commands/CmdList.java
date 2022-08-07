package svinerus.buildtogether.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static svinerus.buildtogether.utils.Localization.lt;

public class CmdList implements ICommand{

    public String getPerms() {
        return "list";
    }

    public  void run(CommandSender sender, String[] args) throws Exception {
        var names = CommandListener.buildingNames();
        var namesText = names.isEmpty() ? lt("list.empty") :
          String.join(",", names.toArray(new String[0]));

        CommandListener.chat.sendMsg(sender, namesText);
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        if (args.length == 2) return CommandListener.buildingNames();
        return new ArrayList<>();
    }
}
