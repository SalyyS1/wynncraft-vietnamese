package net.wynncraft.vi.translation.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.wynncraft.vi.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class OpenAITranslateProvider implements ITranslationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-OpenAI");
    private final HttpClient httpClient;

    public OpenAITranslateProvider() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public String getName() {
        return "OpenAI / LLM RPG Context Translator (SalyVn Prompt)";
    }

    @Override
    public CompletableFuture<String> translate(String text, String sourceLang, String targetLang) {
        String apiKey = ConfigManager.getConfig().apiKey;
        String endpoint = ConfigManager.getConfig().customEndpoint;
        String model = ConfigManager.getConfig().customModel;

        if (apiKey == null || apiKey.trim().isEmpty()) {
            LOGGER.warn("OpenAI API Key is missing. Falling back to original text.");
            return CompletableFuture.completedFuture(text);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject payload = new JsonObject();
                payload.addProperty("model", model.isEmpty() ? "gpt-4o-mini" : model);

                JsonArray messages = new JsonArray();
                JsonObject sysMsg = new JsonObject();
                sysMsg.addProperty("role", "system");
                sysMsg.addProperty("content",
                        "Bạn là chuyên gia dịch thuật MMORPG chuyên sâu cho game Wynncraft (Dự án Wynncraft Vietnamese bởi SalyVn).\n" +
                        "QUY TẮC BẮT BUỘC:\n" +
                        "1. Dịch theo ngữ cảnh MMORPG/Fantasy huyền ảo, KHÔNG dịch word-by-word máy móc.\n" +
                        "2. Hội thoại NPC: Xưng hô tự nhiên kiểu hiệp sĩ/thần thoại (ta - ngươi, sứ giả, chiến binh, lữ khách).\n" +
                        "3. Giữ nguyên mọi mã màu Minecraft (§a, §6, §7, §e...) và các ký hiệu dạng [1/4], [NPC].\n" +
                        "4. Giữ nguyên tên riêng của nhân vật, quái vật, địa danh (Ragni, Detlas, Bob, Corkus...).\n" +
                        "5. Chỉ trả về duy nhất chuỗi kết quả dịch tiếng Việt, không thêm lời giải thích hay ngoặc kép."
                );
                messages.add(sysMsg);

                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", text);
                messages.add(userMsg);

                payload.add("messages", messages);
                payload.addProperty("temperature", 0.3);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint.isEmpty() ? "https://api.openai.com/v1/chat/completions" : endpoint))
                        .header("Authorization", "Bearer " + apiKey.trim())
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(10))
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    return json.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString().trim();
                } else {
                    LOGGER.warn("OpenAI API returned HTTP {}", response.statusCode());
                }
            } catch (Exception e) {
                LOGGER.error("OpenAI translation failed: {}", e.getMessage());
            }
            return text;
        });
    }
}