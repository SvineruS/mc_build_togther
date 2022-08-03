package svinerus.buildtogether.utils.storage;


import org.bukkit.configuration.InvalidConfigurationException;
import svinerus.buildtogether.BuildTogether;

import java.io.IOException;
import java.util.HashMap;

public class Locale {

    public final HashMap<String, HashMap<String, String>> locales = new HashMap<>();

    public String localize(String userLocale, String key) {
        String serverDefaultLocale = BuildTogether.instance.getConfig().getString("locale");

        for (String localeName : new String[]{userLocale, serverDefaultLocale, "en_us"}) {
            var locale = getLocaleCached(localeName);
            if (locale != null && locale.containsKey(key))
                return locale.get(key);
        }
        return key;
    }

    private HashMap<String, String> getLocaleCached(String localeName) {
        if (!locales.containsKey(localeName))
            locales.put(localeName, getLocaleSafe(localeName));
        return locales.get(localeName);
    }

    private static HashMap<String, String> getLocaleSafe(String locale) {
        try {
            return loadLocale(locale);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<String, String> loadLocale(String locale) throws IOException, InvalidConfigurationException {
        var res = new HashMap<String, String>();

        var cfg = StorageUtils.readYaml(StorageUtils.getPluginPath().resolve("locales").resolve(locale + ".json"));
        for (String key : cfg.getKeys(false))
            res.put(key, cfg.getString(key));

        // block names
        cfg = StorageUtils.readYaml(StorageUtils.getPluginPath().resolve("locales/blocks").resolve(locale + ".json"));
        for (String key : cfg.getKeys(false))
            res.put(key, cfg.getString(key));


        return res;
    }
}
