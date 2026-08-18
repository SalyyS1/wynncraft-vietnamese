package net.wynncraft.vi.translation;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.config.ModConfig;
import net.wynncraft.vi.translation.format.WynnDialogueParser;
import net.wynncraft.vi.translation.format.WynnTextFormatter;
import net.wynncraft.vi.translation.provider.DeepLTranslateProvider;
import net.wynncraft.vi.translation.provider.GoogleTranslateProvider;
import net.wynncraft.vi.translation.provider.ITranslationProvider;
import net.wynncraft.vi.translation.provider.OpenAITranslateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Engine");
    private static TranslationEngine INSTANCE;

    private final DictionaryManager dictionaryManager;
    private final TranslationCache cache;
    private final Map<ModConfig.TranslationProviderType, ITranslationProvider> providers;
    private final Set<String> pendingTranslations = ConcurrentHashMap.newKeySet();

    private TranslationEngine() {
        this.dictionaryManager = new DictionaryManager();
        this.cache = new TranslationCache();
        this.providers = new EnumMap<>(ModConfig.TranslationProviderType.class);

        providers.put(ModConfig.TranslationProviderType.GOOGLE, new GoogleTranslateProvider());
        providers.put(ModConfig.TranslationProviderType.DEEPL, new DeepLTranslateProvider());
        providers.put(ModConfig.TranslationProviderType.OPENAI, new OpenAITranslateProvider());
    }

    public static synchronized TranslationEngine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TranslationEngine();
        }
        return INSTANCE;
    }

    public void init() {
        dictionaryManager.init();
        cache.load();
        LOGGER.info("TranslationEngine initialized.");
    }

    public void shutdown() {
        cache.save();
    }

    public DictionaryManager getDictionaryManager() {
        return dictionaryManager;
    }

    public TranslationCache getCache() {
        return cache;
    }

    public ITranslationProvider getCurrentProvider() {
        ModConfig config = ConfigManager.getConfig();
        return providers.getOrDefault(config.provider, providers.get(ModConfig.TranslationProviderType.GOOGLE));
    }

    public String translateSync(String rawText) {
        if (!ConfigManager.getConfig().enabled || rawText == null || rawText.trim().isEmpty()) {
            return rawText;
        }

        String clean = WynnTextFormatter.stripFormatting(rawText).trim();
        if (clean.isEmpty()) {
            return rawText;
        }

        // 1. Check Offline Dictionary
        String dictMatch = dictionaryManager.findTranslation(clean);
        if (dictMatch != null) {
            return WynnTextFormatter.preserveFormatting(rawText, dictMatch);
        }

        // 2. Check Disk / Memory Cache
        String cached = cache.get(clean);
        if (cached != null) {
            return WynnTextFormatter.preserveFormatting(rawText, cached);
        }

        // 3. Queue async online translation if enabled
        if (ConfigManager.getConfig().onlineTranslationEnabled && !pendingTranslations.contains(clean)) {
            requestAsyncTranslation(clean);
        }

        return rawText;
    }

    public CompletableFuture<String> translateAsync(String rawText) {
        if (!ConfigManager.getConfig().enabled || rawText == null || rawText.trim().isEmpty()) {
            return CompletableFuture.completedFuture(rawText);
        }

        String clean = WynnTextFormatter.stripFormatting(rawText).trim();
        if (clean.isEmpty()) {
            return CompletableFuture.completedFuture(rawText);
        }

        // 1. Check Dictionary
        String dictMatch = dictionaryManager.findTranslation(clean);
        if (dictMatch != null) {
            return CompletableFuture.completedFuture(WynnTextFormatter.preserveFormatting(rawText, dictMatch));
        }

        // 2. Check Cache
        String cached = cache.get(clean);
        if (cached != null) {
            return CompletableFuture.completedFuture(WynnTextFormatter.preserveFormatting(rawText, cached));
        }

        // 3. Request from Online Provider
        if (ConfigManager.getConfig().onlineTranslationEnabled) {
            return getCurrentProvider().translate(clean, "en", "vi").thenApply(translated -> {
                if (translated != null && !translated.equalsIgnoreCase(clean)) {
                    cache.put(clean, translated);
                }
                return WynnTextFormatter.preserveFormatting(rawText, translated != null ? translated : rawText);
            });
        }

        return CompletableFuture.completedFuture(rawText);
    }

    private void requestAsyncTranslation(String cleanText) {
        pendingTranslations.add(cleanText);
        getCurrentProvider().translate(cleanText, "en", "vi").thenAccept(translated -> {
            pendingTranslations.remove(cleanText);
            if (translated != null && !translated.equalsIgnoreCase(cleanText)) {
                cache.put(cleanText, translated);
            }
        }).exceptionally(ex -> {
            pendingTranslations.remove(cleanText);
            LOGGER.error("Async translation error for '{}': {}", cleanText, ex.getMessage());
            return null;
        });
    }

    public Text translateDialogueOrChat(Text original) {
        if (!ConfigManager.getConfig().enabled || original == null) {
            return original;
        }

        String rawString = original.getString();
        WynnDialogueParser.ParsedDialogue parsed = WynnDialogueParser.parse(rawString);

        if (parsed.isDialogue && ConfigManager.getConfig().translateNpcDialogue) {
            String translatedBody = translateSync(parsed.dialogueBody);
            String fullTranslated = parsed.prefix + translatedBody;

            MutableText result = Text.literal(fullTranslated);
            if (original.getStyle() != null) {
                result.setStyle(original.getStyle());
            }
            return result;
        }

        if (ConfigManager.getConfig().translateSystemChat) {
            return translateTextComponent(original);
        }

        return original;
    }

    public Text translateTextComponent(Text original) {
        if (!ConfigManager.getConfig().enabled || original == null) {
            return original;
        }

        String textStr = original.getString();
        if (textStr == null || textStr.trim().isEmpty()) {
            return original;
        }

        String translated = translateSync(textStr);
        if (translated.equals(textStr)) {
            return original;
        }

        MutableText result = Text.literal(translated);
        if (original.getStyle() != null) {
            result.setStyle(original.getStyle());
        }
        return result;
    }

    public List<Text> processItemTooltip(List<Text> originalTooltip) {
        if (!ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateItems || originalTooltip == null || originalTooltip.isEmpty()) {
            return originalTooltip;
        }

        ModConfig.ItemTooltipMode mode = ConfigManager.getConfig().itemTooltipMode;
        if (mode == ModConfig.ItemTooltipMode.HOVER_OR_SHIFT && !Screen.hasShiftDown()) {
            return originalTooltip;
        }

        List<Text> processed = new ArrayList<>();

        for (int i = 0; i < originalTooltip.size(); i++) {
            Text line = originalTooltip.get(i);
            String rawLine = line.getString();
            String clean = WynnTextFormatter.stripFormatting(rawLine).trim();

            if (clean.isEmpty()) {
                processed.add(line);
                continue;
            }

            String translated = translateSync(clean);
            boolean hasTranslation = !translated.equalsIgnoreCase(clean);

            if (mode == ModConfig.ItemTooltipMode.REPLACE) {
                if (hasTranslation) {
                    MutableText translatedLine = Text.literal(WynnTextFormatter.preserveFormatting(line.getString(), translated));
                    translatedLine.setStyle(line.getStyle());
                    processed.add(translatedLine);
                } else {
                    processed.add(line);
                }
            } else if (mode == ModConfig.ItemTooltipMode.APPEND) {
                processed.add(line);
                if (hasTranslation) {
                    MutableText viLine = Text.literal("§7↳ §e" + translated);
                    processed.add(viLine);
                }
            } else if (mode == ModConfig.ItemTooltipMode.HOVER_OR_SHIFT) {
                if (hasTranslation) {
                    MutableText translatedLine = Text.literal(WynnTextFormatter.preserveFormatting(line.getString(), translated));
                    translatedLine.setStyle(line.getStyle());
                    processed.add(translatedLine);
                } else {
                    processed.add(line);
                }
            }
        }

        return processed;
    }
}