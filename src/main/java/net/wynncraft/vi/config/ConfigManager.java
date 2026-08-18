package net.wynncraft.vi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("wynncraft_vi");
    private static final File CONFIG_FILE = CONFIG_DIR.resolve("config.json").toFile();

    private static ModConfig config = new ModConfig();

    public static ModConfig getConfig() {
        return config;
    }

    public static void load() {
        if (!CONFIG_DIR.toFile().exists()) {
            CONFIG_DIR.toFile().mkdirs();
        }

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    config = loaded;
                    LOGGER.info("Config loaded successfully.");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load config file, creating default", e);
                save();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            if (!CONFIG_DIR.toFile().exists()) {
                CONFIG_DIR.toFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
                LOGGER.info("Config saved successfully.");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config file", e);
        }
    }
}