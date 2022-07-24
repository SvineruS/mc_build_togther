package svinerus.buildtogether.utils;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import svinerus.buildtogether.BuildTogether;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static Path schematicsPath() {
        return BuildTogether.WEPlugin.getDataFolder().toPath().resolve("schematics");
    }


    public static void spawnParticles(Location l, Particle particle) {
        l.getWorld().spawnParticle(particle, l.toCenterLocation(), 10, 0.5F, 0.5F, 0.5F);
    }

    public static void spawnFireworks(Location loc, int amount) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
//        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }
    public static List<Path> allFiles(Path root) throws IOException {
        List<Path> pathList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    pathList.addAll(allFiles(path));
                } else {
                    pathList.add(path);
                }
            }
        }
        return pathList;
    }


    public static BlockVector3 toVector(Location location) {
        return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Location toLocation(World world, BlockVector3 vector) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

}
