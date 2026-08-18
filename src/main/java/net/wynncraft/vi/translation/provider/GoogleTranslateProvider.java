package net.wynncraft.vi.translation.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.wynncraft.vi.translation.SmartRpgTranslator;
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

public class GoogleTranslateProvider implements ITranslationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Google");
    private final HttpClient httpClient;

    public GoogleTranslateProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public String getName() {
        return "Google Dịch (Kèm Bộ Lọc RPG SalyVn)";
    }

    @Override
    public CompletableFuture<String> translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.trim().isEmpty()) {
            return CompletableFuture.completedFuture(text);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String masked = SmartRpgTranslator.maskTerms(text);
                String encodedText = URLEncoder.encode(masked, StandardCharsets.UTF_8);
                String urlStr = String.format(
                        "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
                        sourceLang, targetLang, encodedText
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlStr))
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .timeout(Duration.ofSeconds(6))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonElement jsonElement = JsonParser.parseString(response.body());
                    if (jsonElement.isJsonArray()) {
                        JsonArray rootArray = jsonElement.getAsJsonArray();
                        if (!rootArray.isEmpty() && rootArray.get(0).isJsonArray()) {
                            JsonArray sentences = rootArray.get(0).getAsJsonArray();
                            StringBuilder result = new StringBuilder();
                            for (JsonElement sentenceElem : sentences) {
                                if (sentenceElem.isJsonArray()) {
                                    JsonArray sentence = sentenceElem.getAsJsonArray();
                                    if (!sentence.isEmpty() && !sentence.get(0).isJsonNull()) {
                                        result.append(sentence.get(0).getAsString());
                                    }
                                }
                            }
                            String rawTranslated = result.toString().trim();
                            if (!rawTranslated.isEmpty()) {
                                return SmartRpgTranslator.unmaskAndPolish(rawTranslated);
                            }
                        }
                    }
                } else {
                    LOGGER.warn("Google Translate returned status: {}", response.statusCode());
                }
            } catch (Exception e) {
                LOGGER.error("Error during Google Translation: {}", e.getMessage());
            }
            return text;
        });
    }
}