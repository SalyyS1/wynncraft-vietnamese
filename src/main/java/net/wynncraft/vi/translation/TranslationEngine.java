package net.wynncraft.vi.translation;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.config.ModConfig;
import net.wynncraft.vi.translation.format.WynnDialogueParser;
import net.wynncraft.vi.translation.format.WynnFontShield;
import net.wynncraft.vi.translation.format.WynnTextFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TranslationEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Engine");
    private static TranslationEngine INSTANCE;

    private final DictionaryManager dictionaryManager;

    private TranslationEngine() {
        this.dictionaryManager = new DictionaryManager();
    }

    public static synchronized TranslationEngine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TranslationEngine();
        }
        return INSTANCE;
    }

    public void init() {
        dictionaryManager.init();
        LOGGER.info("Manual TranslationEngine initialized.");
    }

    public void shutdown() {
    }

    public DictionaryManager getDictionaryManager() {
        return dictionaryManager;
    }

    /**
     * Looks up manual translation for text. Returns null if not found in dictionary.
     */
    public String translateManual(String rawText) {
        if (!ConfigManager.getConfig().enabled || rawText == null || rawText.trim().isEmpty()) {
            return null;
        }

        String clean = WynnTextFormatter.stripFormatting(rawText).trim();
        if (clean.isEmpty()) {
            return null;
        }

        // 1. Direct dictionary match
        String match = dictionaryManager.findTranslation(clean);
        if (match != null) {
            return match;
        }

        // 2. Normalized unicode quotes (“ ” ‘ ’) match
        String normalizedQuotes = clean.replace('“', '"').replace('”', '"').replace('‘', '\'').replace('’', '\'');
        if (!normalizedQuotes.equals(clean)) {
            String matchQ = dictionaryManager.findTranslation(normalizedQuotes);
            if (matchQ != null) {
                return matchQ;
            }
        }

        // 3. Leading checkmarks (✔, ✓) or PUA icons
        if (clean.startsWith("✔") || clean.startsWith("✓") || clean.startsWith("-") || WynnFontShield.containsPuaGlyphs(clean)) {
            String prefix = "";
            String body = clean;
            if (clean.startsWith("✔") || clean.startsWith("✓")) {
                prefix = clean.substring(0, 1) + " ";
                body = clean.substring(1).trim();
            } else if (WynnFontShield.containsPuaGlyphs(clean)) {
                body = WynnFontShield.stripPuaGlyphs(clean).trim();
            }

            if (!body.isEmpty()) {
                String matchBody = dictionaryManager.findTranslation(body);
                if (matchBody == null) {
                    String normBody = body.replace('“', '"').replace('”', '"');
                    matchBody = dictionaryManager.findTranslation(normBody);
                }
                if (matchBody != null) {
                    return prefix + matchBody;
                }
            }
        }

        return null;
    }

    /**
     * Translates chat and NPC dialogues specifically, separating name tags and dialogue content.
     */
    public Text translateDialogueOrChat(Text original) {
        if (!ConfigManager.getConfig().enabled || original == null) {
            return original;
        }

        String rawString = original.getString();
        WynnDialogueParser.ParsedDialogue parsed = WynnDialogueParser.parse(rawString);

        if (parsed.isDialogue && ConfigManager.getConfig().translateNpcDialogue) {
            String translatedBody = translateManual(parsed.dialogueBody);
            if (translatedBody != null) {
                String fullTranslated = parsed.prefix + translatedBody;
                MutableText result = Text.literal(fullTranslated);
                if (original.getStyle() != null) {
                    result.setStyle(original.getStyle());
                }
                return result;
            }
            return original;
        }

        if (ConfigManager.getConfig().translateSystemChat) {
            return translateTextComponent(original);
        }

        return original;
    }

    /**
     * Translates a Minecraft Text component if an exact manual translation exists.
     */
    public Text translateTextComponent(Text original) {
        if (!ConfigManager.getConfig().enabled || original == null) {
            return original;
        }

        String textStr = original.getString();
        if (textStr == null || textStr.trim().isEmpty()) {
            return original;
        }

        String translated = translateManual(textStr);
        if (translated == null) {
            return original;
        }

        MutableText result = Text.literal(WynnTextFormatter.preserveFormatting(textStr, translated));
        if (original.getStyle() != null) {
            result.setStyle(original.getStyle());
        }
        return result;
    }

    /**
     * Translates Item Tooltips according to configured ItemTooltipMode.
     * Preserves custom font pack textures and icons.
     */
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

            String translated = translateManual(clean);
            boolean hasTranslation = (translated != null && !translated.equalsIgnoreCase(clean));

            if (!hasTranslation) {
                processed.add(line);
                continue;
            }

            MutableText translatedLine = Text.literal(WynnTextFormatter.preserveFormatting(line.getString(), translated));
            if (line.getStyle() != null) {
                translatedLine.setStyle(line.getStyle());
            }
            processed.add(translatedLine);
        }

        return processed;
    }
}