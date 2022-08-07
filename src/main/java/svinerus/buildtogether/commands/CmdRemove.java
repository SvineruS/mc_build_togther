package svinerus.buildtogether.commands;

import org.bukkit.command.CommandSender;
import svinerus.buildtogether.BuildTogether;

import java.util.ArrayList;
import java.util.List;

import static svinerus.buildtogether.utils.Localization.lt;

public class CmdRemove implements ICommand{

    public String getPerms() {
        return "remove";
    }

    public void run(CommandSender sender, String[] args) throws Exception {
        if (args.length != 2) throw new Exception("Wrong number of arguments");

        String buildingName = args[1];
        BuildTogether.buildingsManager.remove(buildingName);
        CommandListener.chat.sendMsg(sender, lt("remove.success"));
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        if (args.length == 2) return CommandListener.buildingNames();
        return new ArrayList<>();
    }}
