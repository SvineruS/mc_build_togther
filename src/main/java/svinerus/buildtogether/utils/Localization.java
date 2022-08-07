package svinerus.buildtogether.utils;


import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.configuration.InvalidConfigurationException;
import svinerus.buildtogether.utils.storage.StorageUtils;

import java.io.IOException;
import java.util.HashMap;

public class Localization {

    private static final String default_lang = "en_us";
    private HashMap<String, String> locale;

    public Localization(String path, String localeName) {
        locale = loadLocaleSafe(path, localeName);
        if (locale == null)
            locale = loadLocaleSafe(path, default_lang);
        if (locale == null)
            throw new IllegalArgumentException("Failed to load " + localeName + " locale. Default locale also cannot be loaded.");
    }

    private HashMap<String, String> loadLocaleSafe(String path, String localeName) {
        var filePath = StorageUtils.getPluginPath().resolve(path).resolve(localeName + ".yaml");
        try {
            return StorageUtils.readYamlToMap(filePath);
        } catch (IOException | InvalidConfigurationException e) {
            Utils.exception(e, "Failed to load locale from " + filePath);
        }
        return null;
    }

    public String localize(String key) {
        var r = locale.get(key);
        if (r == null) {
            Utils.logger().warning("Failed to localize " + key);
            return key;
        }
        return r;
    }

    public TextReplacementConfig toTextReplacementConfig() {
        var res = TextReplacementConfig.builder();
        for (var entry : this.locale.entrySet())
            res.match(entry.getKey()).replacement(entry.getValue());
        return res.build();
    }
}
