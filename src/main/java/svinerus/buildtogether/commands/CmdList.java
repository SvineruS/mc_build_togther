package svinerus.buildtogether.commands;

import org.bukkit.command.CommandSender;

public class CmdList implements ICommand{

    public String getPerms() {
        return "list";
    }

    public  void run(CommandSender sender, String[] args) throws Exception {
        var names = CommandListener.buildingNames();
        var namesText = names.isEmpty() ? "list.empty" :
          String.join(",", names.toArray(new String[0]));

        CommandListener.chat.sendMsg(sender, namesText);
    }

    public java.util.List<String> tabCompletion(CommandSender sender, String[] args) {
        return CommandListener.buildingNames();
    }
}
