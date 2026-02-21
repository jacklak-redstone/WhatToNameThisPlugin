package org.jacklak_imated.oresoft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LoadWorld {
    public static void setup(Plugin plugin) {
        try {
            copyWorldFromResources(plugin);
            updateMiniGameCoreConfig(plugin);
            plugin.getLogger().info("ORE-Soft world and config setup!");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error setting up ORE-Soft world and config", e);
        }
    }

    private static void copyWorldFromResources(Plugin plugin) throws IOException {
        File serverRoot = plugin.getServer().getWorldContainer().getAbsoluteFile().getParentFile();
        File miniGamesDir = new File(serverRoot, "plugins/MiniGameCore/MiniGames");
        File targetWorldDir = new File(miniGamesDir, "map_1");

        if (!miniGamesDir.exists()) {
            miniGamesDir.mkdirs();
        }

        if (targetWorldDir.exists()) {
            plugin.getLogger().info("map_1 already exists, skipping copy.");
            return;
        }

        targetWorldDir.mkdirs();

        InputStream zipStream = plugin.getResource("map_1.zip");
        if (zipStream == null) {
            throw new IOException("map_1.zip not found in plugin resources");
        }

        unzip(zipStream, targetWorldDir);
        zipStream.close();

        plugin.getLogger().info("Copied map_1 to " + targetWorldDir.getAbsolutePath());
    }

    private static void unzip(InputStream zipStream, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(zipStream);
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);

            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }

            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target directory: " + zipEntry.getName());
        }

        return destFile;
    }

    private static void updateMiniGameCoreConfig(Plugin plugin) throws IOException {
        File serverRoot = plugin.getServer().getWorldContainer().getAbsoluteFile().getParentFile();
        File configFile = new File(serverRoot, "plugins/MiniGameCore/config.yml");

        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        FileConfiguration config;
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            config = new YamlConfiguration();
        }

        List<String> availableGames = config.getStringList("available-games");

        if (!availableGames.contains("ORE-Soft")) {
            availableGames.add("ORE-Soft");
            config.set("available-games", availableGames);
            config.save(configFile);
            plugin.getLogger().info("Added ORE-Soft to MiniGameCore config.yml");
        } else {
            plugin.getLogger().info("ORE-Soft already exists in MiniGameCore config.yml");
        }
    }
}
