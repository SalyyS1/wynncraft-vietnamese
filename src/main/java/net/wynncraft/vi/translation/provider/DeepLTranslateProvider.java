package net.wynncraft.vi.translation.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.wynncraft.vi.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class DeepLTranslateProvider implements ITranslationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-DeepL");
    private final HttpClient httpClient;

    public DeepLTranslateProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public String getName() {
        return "DeepL API";
    }

    @Override
    public CompletableFuture<String> translate(String text, String sourceLang, String targetLang) {
        String apiKey = ConfigManager.getConfig().apiKey;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            LOGGER.warn("DeepL API Key is missing. Falling back to original text.");
            return CompletableFuture.completedFuture(text);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String host = apiKey.endsWith(":fx") ? "api-free.deepl.com" : "api.deepl.com";
                String body = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                        + "&source_lang=" + sourceLang.toUpperCase()
                        + "&target_lang=" + targetLang.toUpperCase();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://" + host + "/v2/translate"))
                        .header("Authorization", "DeepL-Auth-Key " + apiKey.trim())
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .timeout(Duration.ofSeconds(8))
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    return json.getAsJsonArray("translations")
                            .get(0).getAsJsonObject()
                            .get("text").getAsString();
                } else {
                    LOGGER.warn("DeepL API returned HTTP {}", response.statusCode());
                }
            } catch (Exception e) {
                LOGGER.error("DeepL translation failed: {}", e.getMessage());
            }
            return text;
        });
    }
}