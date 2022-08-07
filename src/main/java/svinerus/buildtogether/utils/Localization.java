package svinerus.buildtogether.utils;


import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.configuration.InvalidConfigurationException;
import svinerus.buildtogether.BuildTogether;
import svinerus.buildtogether.utils.storage.StorageUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class Localization {

    private static final String default_lang = "en_us";
    private HashMap<String, String> locale;
    private final Path filePath;

    public Localization(String path, String localeName) {
        filePath = StorageUtils.getPluginPath().resolve(path).resolve(localeName + ".yaml");
        locale = loadLocaleSafe(path, localeName);
        if (locale == null)
            locale = loadLocaleSafe(path, default_lang);
        if (locale == null)
            throw new IllegalArgumentException("Failed to load " + localeName + " locale. Default locale also cannot be loaded.");
    }

    public static String lt(String key) {
        return BuildTogether.localization.localize(key);
    }

    public String localize(String key) {
        var r = locale.get(key);
        if (r == null) {
            Utils.logger().warning("Failed to localize " + key);
            addLocalizationPlaceholder(key);
            return key;
        }
        return r;
    }

    private HashMap<String, String> loadLocaleSafe(String path, String localeName) {
        try {
            var res = new HashMap<String, String>();
            var cfg = StorageUtils.readYaml(filePath);
            cfg.getKeys(true).forEach(key -> res.put(key, cfg.getString(key)));
            return res;
        } catch (IOException | InvalidConfigurationException e) {
            Utils.exception(e, "Failed to load locale from " + filePath);
        }
        return null;
    }
    private void addLocalizationPlaceholder(String key) {
        try {
            var cfg = StorageUtils.readYaml(filePath);
            cfg.options().pathSeparator('!');  // hack to not split path by '.'
            cfg.set(key, key);
            cfg.save(filePath.toFile());
        } catch (IOException | InvalidConfigurationException e) {
            Utils.exception(e, "Failed to save or load locale from " + filePath);
        }
    }
}
