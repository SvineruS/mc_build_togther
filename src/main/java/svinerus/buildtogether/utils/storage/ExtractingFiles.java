package svinerus.buildtogether.utils.storage;

import svinerus.buildtogether.BuildTogether;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

public class ExtractingFiles {

    public static void ensureFilesExist() {
        try {
            copyResourceDirectory("assets", StorageUtils.getPluginPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // todo create config
    }

    private static void copyResourceDirectory(String jarPath, final Path target) throws IOException {
        var home = BuildTogether.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(5);
        var source = new JarFile(home);
        var entries = source.entries();
        jarPath = String.format("%s/", jarPath);


        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            if (!entry.getName().startsWith(jarPath) || entry.isDirectory()) continue;

            var dest = target.resolve(entry.getName().substring(jarPath.length()));
            if (dest.toFile().exists()) continue;

            StorageUtils.createPath(dest.getParent());
            Files.copy(source.getInputStream(entry), dest);
        }
    }

}

