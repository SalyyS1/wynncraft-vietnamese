package net.wynncraft.vi.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.wynncraft.vi.translation.WynnRpgLexicon;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = ConfigManager.getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Wynncraft Tiếng Việt - Bản dịch bởi " + WynnRpgLexicon.TRANSLATOR_CREDIT));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // 1. General Category
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("Cài Đặt Chung"));

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Bật Dịch Tiếng Việt"), config.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Bật hoặc tắt toàn bộ tính năng dịch thuật trong game"))
                    .setSaveConsumer(newValue -> config.enabled = newValue)
                    .build());

            general.addEntry(entryBuilder.startEnumSelector(Text.literal("Chế độ hiển thị Trang bị"), ModConfig.ItemTooltipMode.class, config.itemTooltipMode)
                    .setDefaultValue(ModConfig.ItemTooltipMode.APPEND)
                    .setTooltip(Text.literal("Cách hiển thị bản dịch tiếng Việt cho trang bị và vật phẩm"))
                    .setSaveConsumer(newValue -> config.itemTooltipMode = newValue)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Hiện Thông Báo Khởi Động"), config.showCreditBadge)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Hiển thị thông tin dịch giả khi vào game"))
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

            scopes.addEntry(entryBuilder.startBooleanToggle(Text.literal("Dịch Thuộc Tính Trang Bị (Item Lore/Stats)"), config.translateItems)
                    .setDefaultValue(true)
                    .setSaveConsumer(newValue -> config.translateItems = newValue)
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

            builder.setSavingRunnable(ConfigManager::save);

            return builder.build();
        };
    }
}