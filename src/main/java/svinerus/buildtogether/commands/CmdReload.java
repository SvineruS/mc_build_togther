package svinerus.buildtogether.commands;

import org.bukkit.command.CommandSender;
import svinerus.buildtogether.BuildTogether;

import java.util.ArrayList;
import java.util.List;

public class CmdReload implements ICommand{

    public String getPerms() {
        return "reload";
    }

    public void run(CommandSender sender, String[] args) throws Exception {
        BuildTogether.instance.onDisable();
        BuildTogether.instance.onEnable();
        CommandListener.chat.sendMsg(sender, "reloaded");
    }

    public List<String> tabCompletion(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
