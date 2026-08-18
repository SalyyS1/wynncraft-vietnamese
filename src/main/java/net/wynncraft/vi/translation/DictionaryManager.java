package net.wynncraft.vi.translation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Dict");
    private static final Gson GSON = new Gson();

    private final Map<String, String> exactDictionary = new HashMap<>();
    private final List<RegexEntry> regexDictionary = new ArrayList<>();

    public static class RegexEntry {
        public final Pattern pattern;
        public final String replacement;

        public RegexEntry(Pattern pattern, String replacement) {
            this.pattern = pattern;
            this.replacement = replacement;
        }
    }

    public void init() {
        exactDictionary.clear();
        regexDictionary.clear();

        loadBundledResource("/assets/wynncraft_vi/translations/terms.json");
        loadBundledResource("/assets/wynncraft_vi/translations/items.json");
        loadBundledResource("/assets/wynncraft_vi/translations/quests.json");
        loadBundledResource("/assets/wynncraft_vi/translations/dialogues.json");

        loadUserDictionary();

        LOGGER.info("Dictionary loaded: {} exact entries, {} regex patterns.",
                exactDictionary.size(), regexDictionary.size());
    }

    private void loadBundledResource(String resourcePath) {
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                LOGGER.debug("Resource not found: {}", resourcePath);
                return;
            }
            try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                parseDictionaryJson(reader, "bundled:" + resourcePath);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load bundled dictionary {}: {}", resourcePath, e.getMessage());
        }
    }

    private void loadUserDictionary() {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("wynncraft_vi");
        File customFile = configDir.resolve("custom_dict.json").toFile();

        if (!customFile.exists()) {
            createSampleCustomDict(customFile);
            return;
        }

        try (FileReader reader = new FileReader(customFile, StandardCharsets.UTF_8)) {
            parseDictionaryJson(reader, "custom:" + customFile.getName());
        } catch (Exception e) {
            LOGGER.error("Failed to load custom dictionary: {}", e.getMessage());
        }
    }

    private void createSampleCustomDict(File file) {
        try {
            file.getParentFile().mkdirs();
            JsonObject sample = new JsonObject();
            JsonObject exact = new JsonObject();
            exact.addProperty("Hello", "Xin chào");
            exact.addProperty("Goodbye", "Tạm biệt");
            sample.add("exact", exact);

            JsonObject regex = new JsonObject();
            regex.addProperty("Level (\\d+)", "Cấp độ $1");
            sample.add("regex", regex);

            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(sample, writer);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create sample custom dict: {}", e.getMessage());
        }
    }

    private void parseDictionaryJson(Reader reader, String source) {
        try {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            if (json.has("exact")) {
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> map = GSON.fromJson(json.get("exact"), type);
                if (map != null) {
                    exactDictionary.putAll(map);
                }
            } else {
                for (String key : json.keySet()) {
                    if (!key.equals("regex") && json.get(key).isJsonPrimitive()) {
                        exactDictionary.put(key, json.get(key).getAsString());
                    }
                }
            }

            if (json.has("regex")) {
                JsonObject regexObj = json.getAsJsonObject("regex");
                for (String patternStr : regexObj.keySet()) {
                    String replacement = regexObj.get(patternStr).getAsString();
                    try {
                        Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                        regexDictionary.add(new RegexEntry(p, replacement));
                    } catch (Exception e) {
                        LOGGER.warn("Invalid regex pattern [{}] in {}: {}", patternStr, source, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error parsing JSON dictionary {}: {}", source, e.getMessage());
        }
    }

    public String findTranslation(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        String clean = text.trim();

        String match = exactDictionary.get(clean);
        if (match != null) {
            return match;
        }

        for (Map.Entry<String, String> entry : exactDictionary.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(clean)) {
                return entry.getValue();
            }
        }

        for (RegexEntry entry : regexDictionary) {
            Matcher m = entry.pattern.matcher(clean);
            if (m.matches()) {
                return m.replaceAll(entry.replacement);
            }
        }

        return null;
    }
}