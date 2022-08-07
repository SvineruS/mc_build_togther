package svinerus.buildtogether.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CmdReload implements ICommand{

    public String getPerms() {
        return "reload";
    }

    public void run(CommandSender sender, String[] args) throws Exception {
        // todo
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
