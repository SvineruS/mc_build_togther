package svinerus.buildtogether.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Chat {


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
