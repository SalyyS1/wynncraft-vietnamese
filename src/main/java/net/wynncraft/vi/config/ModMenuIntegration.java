package net.wynncraft.vi.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.wynncraft.vi.translation.TranslationEngine;
import net.wynncraft.vi.translation.WynnRpgLexicon;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = ConfigManager.getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("§6Wynncraft Tiếng Việt §7- §eBản dịch bởi " + WynnRpgLexicon.TRANSLATOR_CREDIT));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // 1. General Category
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("Cài Đặt Chung"));

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Bật Dịch Tiếng Việt"), config.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Bật hoặc tắt toàn bộ tính năng dịch thuật trong game"))
                    .setSaveConsumer(newValue -> config.enabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startEnumSelector(Text.literal("Chế độ hiển thị Vật phẩm"), ModConfig.ItemTooltipMode.class, config.itemTooltipMode)
                    .setDefaultValue(ModConfig.ItemTooltipMode.APPEND)
                    .setTooltip(Text.literal("Cách hiển thị bản dịch tiếng Việt cho trang bị và vật phẩm"))
                    .setSaveConsumer(newValue -> config.itemTooltipMode = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Hiện Credit Người Dịch (SalyVn)"), config.showCreditBadge)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Hiển thị thông tin dịch giả SalyVn khi vào game"))
                    .setSaveConsumer(newValue -> config.showCreditBadge = newValue)
                    .build());

            // 2. Scopes Category
            ConfigCategory scopes = builder.getOrCreateCategory(Text.literal("Phạm Vi Dịch"));

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Nhiệm Vụ (Quests)"), config.translateQuests)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.translateQuests = newValue)
                    .build());

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Hội Thoại NPC"), config.translateNpcDialogue)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.translateNpcDialogue = newValue)
                    .build());

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Thông Tin Vật Phẩm (Lore/Stats)"), config.translateItems)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.translateItems = newValue)
                    .build());

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Giao Diện GUI & Wynntils Overlays"), config.translateGuiAndWynntils)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Tự động dịch Quest Tracker của Wynntils, màn hình Quest Book, Bank, Shop..."))
                    .setSaveConsumer(newValue -> config.translateGuiAndWynntils = newValue)
                    .build());

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Action Bar"), config.translateActionBar)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.translateActionBar = newValue)
                    .build());

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Tiêu Đề & Phụ Đề"), config.translateTitles)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.translateTitles = newValue)
                    .build());

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Tin Nhắn Hệ Thống"), config.translateSystemChat)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.translateSystemChat = newValue)
                    .build());

            // 3. Online Translation & API Category
            ConfigCategory apiCategory = builder.getOrCreateCategory(Text.literal("Dịch Trực Tuyến & AI"));

            apiCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Bật Dịch Trực Tuyến (Khi từ điển chưa có)"), config.onlineTranslationEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Tự động gọi API dịch câu mới kèm bộ lọc thuật ngữ RPG chuẩn"))
                    .setSaveConsumer(newValue -> config.onlineTranslationEnabled = newValue)
                    .build());

            apiCategory.addEntry(entryBuilder.startEnumSelector(Text.literal("Nhà Cung Cấp Dịch (Provider)"), ModConfig.TranslationProviderType.class, config.provider)
                    .setDefaultValue(ModConfig.TranslationProviderType.GOOGLE)
                    .setSaveConsumer(newValue -> config.provider = newValue)
                    .build());

            apiCategory.addEntry(entryBuilder.startStrField(Text.literal("API Key (DeepL / OpenAI)"), config.apiKey)
                    .setDefaultValue("")
                    .setTooltip(Text.literal("Không bắt buộc nếu dùng Google Dịch miễn phí"))
                    .setSaveConsumer(newValue -> config.apiKey = newValue)
                    .build());

            apiCategory.addEntry(entryBuilder.startStrField(Text.literal("AI Custom Endpoint"), config.customEndpoint)
                    .setDefaultValue("https://api.openai.com/v1/chat/completions")
                    .setSaveConsumer(newValue -> config.customEndpoint = newValue)
                    .build());

            apiCategory.addEntry(entryBuilder.startStrField(Text.literal("AI Model"), config.customModel)
                    .setDefaultValue("gpt-4o-mini")
                    .setSaveConsumer(newValue -> config.customModel = newValue)
                    .build());

            // 4. Cache Category
            ConfigCategory cacheCategory = builder.getOrCreateCategory(Text.literal("Bộ Nhớ Đệm"));

            cacheCategory.addEntry(entryBuilder.startBooleanToggle(Text.literal("Bật Lưu Cache (Tránh dịch trùng lặp)"), config.cacheEnabled)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.cacheEnabled = newValue)
                    .build());

            builder.setSavingRunnable(() -> {
                ConfigManager.save();
                TranslationEngine.getInstance().getCache().save();
            });

            return builder.build();
        };
    }
}