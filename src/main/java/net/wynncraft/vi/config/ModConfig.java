package net.wynncraft.vi.config;

public class ModConfig {
    public boolean enabled = true;

    // Translation scopes
    public boolean translateQuests = true;
    public boolean translateNpcDialogue = true;
    public boolean translateItems = true;
    public boolean translateActionBar = true;
    public boolean translateTitles = true;
    public boolean translateBossBars = true;
    public boolean translateSystemChat = true;
    public boolean translateGuiAndWynntils = true;

    // Online translation engine settings
    public boolean onlineTranslationEnabled = true;
    public TranslationProviderType provider = TranslationProviderType.GOOGLE;
    public String apiKey = "";
    public String customEndpoint = "https://api.openai.com/v1/chat/completions";
    public String customModel = "gpt-4o-mini";

    // Display & Credit preferences
    public boolean showOriginalOnHover = true;
    public ItemTooltipMode itemTooltipMode = ItemTooltipMode.APPEND;
    public boolean cacheEnabled = true;
    public boolean showCreditBadge = true;

    public enum TranslationProviderType {
        GOOGLE("Google Dịch (Kèm bộ lọc RPG SalyVn)"),
        DEEPL("DeepL API"),
        OPENAI("OpenAI / Custom AI API (SalyVn RPG Prompt)");

        private final String displayName;

        TranslationProviderType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ItemTooltipMode {
        REPLACE("Thay thế tiếng Anh bằng tiếng Việt"),
        APPEND("Hiển thị thêm dòng tiếng Việt bên dưới"),
        HOVER_OR_SHIFT("Chỉ hiện tiếng Việt khi giữ phím Shift");

        private final String displayName;

        ItemTooltipMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}