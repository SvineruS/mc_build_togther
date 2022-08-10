package svinerus.buildtogether.utils;


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
        var localesPath = StorageUtils.getPluginPath().resolve(path);

        var filePath = localesPath.resolve(localeName + ".yaml");
        locale = loadLocaleSafe(filePath);

        if (locale == null) {
            filePath = localesPath.resolve(default_lang + ".yaml");
            locale = loadLocaleSafe(filePath);
        }

        if (locale == null)
            throw new IllegalArgumentException("Failed to load " + localeName + " locale. Default locale also cannot be loaded.");

        this.filePath = filePath;
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

    private HashMap<String, String> loadLocaleSafe(Path path) {
        try {
            var res = new HashMap<String, String>();
            var cfg = StorageUtils.readYaml(path);
            cfg.getKeys(true).forEach(key -> res.put(key, cfg.getString(key)));
            return res;
        } catch (IOException | InvalidConfigurationException e) {
            Utils.exception(e, "Failed to load locale from " + path);
        }
        return null;
    }

    private void addLocalizationPlaceholder(String key) {
        try {
            var cfg = StorageUtils.readYaml(filePath);
            cfg.set(key, key);
            cfg.save(filePath.toFile());
        } catch (IOException | InvalidConfigurationException e) {
            Utils.exception(e, "Failed to save or load locale from " + filePath);
        }
    }
}
