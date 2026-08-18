package net.wynncraft.vi.translation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.wynncraft.vi.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TranslationCache {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Cache");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("wynncraft_vi");
    private static final File CACHE_FILE = CONFIG_DIR.resolve("cache.json").toFile();

    private final Map<String, String> cacheMap = new ConcurrentHashMap<>();
    private final AtomicBoolean dirty = new AtomicBoolean(false);

    public TranslationCache() {
        load();
    }

    public String get(String key) {
        if (!ConfigManager.getConfig().cacheEnabled || key == null) {
            return null;
        }
        return cacheMap.get(key.trim());
    }

    public void put(String key, String translation) {
        if (!ConfigManager.getConfig().cacheEnabled || key == null || translation == null) {
            return;
        }
        String cleanKey = key.trim();
        if (!cleanKey.isEmpty() && !cleanKey.equalsIgnoreCase(translation.trim())) {
            cacheMap.put(cleanKey, translation.trim());
            dirty.set(true);
        }
    }

    public void load() {
        if (!CACHE_FILE.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(CACHE_FILE, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> loaded = GSON.fromJson(reader, type);
            if (loaded != null) {
                cacheMap.putAll(loaded);
                LOGGER.info("Loaded {} cached translations.", cacheMap.size());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load translation cache: {}", e.getMessage());
        }
    }

    public void save() {
        if (!dirty.get() && CACHE_FILE.exists()) {
            return;
        }
        try {
            if (!CONFIG_DIR.toFile().exists()) {
                CONFIG_DIR.toFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(CACHE_FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(cacheMap, writer);
                dirty.set(false);
                LOGGER.info("Saved {} translations to cache.", cacheMap.size());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save translation cache: {}", e.getMessage());
        }
    }

    public int size() {
        return cacheMap.size();
    }

    public void clear() {
        cacheMap.clear();
        dirty.set(true);
        save();
    }
}