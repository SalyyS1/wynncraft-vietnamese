package net.wynncraft.vi.translation;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.translation.format.WynnDialogueParser;
import net.wynncraft.vi.translation.format.WynnFontShield;
import net.wynncraft.vi.translation.format.WynnTextFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Engine");
    private static TranslationEngine INSTANCE;

    private final DictionaryManager dictionaryManager;

    // Stat token replacements for PUA / formatted item stats
    private static final Object[][] STAT_REPLACEMENTS = {
            {"Left-Click to play", "Chuột trái để chơi"},
            {"Right-Click to switch", "Chuột phải để đổi nhân vật"},
            {"Left-Click to select", "Chuột trái để chọn"},
            {"Right-Click to delete", "Chuột phải để xóa"},
            {"Shift-Right-Click to delete", "Shift-Chuột phải để xóa"},
            {"Content Completion", "Tiến Độ Nội Dung"},
            {"SHIFT to continue", "SHIFT để tiếp tục"},
            {"Press SHIFT to continue", "Nhấn SHIFT để tiếp tục"},
            {"Press SPACE to skip", "Nhấn SPACE để bỏ qua"},
            {"to continue", "để tiếp tục"},
            {"Mana Regen", "Hồi Phục Năng Lượng"},
            {"Mana Steal", "Hút Năng Lượng"},
            {"Life Steal", "Hút Sinh Lực"},
            {"Health Regen Raw", "Hồi Máu Cơ Bản"},
            {"Health Regen", "Hồi Phục Sinh Lực"},
            {"Walk Speed", "Tốc Độ Di Chuyển"},
            {"Sprint Regen", "Hồi Thể Lực Chạy"},
            {"Sprint", "Tốc Độ Chạy"},
            {"Jump Height", "Độ Cao Nhảy"},
            {"Spell Damage Raw", "Sát Thương Phép Cơ Bản"},
            {"Spell Damage", "Sát Thương Phép"},
            {"Main Attack Damage Raw", "Sát Thương Đòn Thường Cơ Bản"},
            {"Main Attack Damage", "Sát Thương Đòn Thường"},
            {"Melee Damage Raw", "Sát Thương Cận Chiến Cơ Bản"},
            {"Melee Damage", "Sát Thương Cận Chiến"},
            {"Weaken Enemy", "Làm Suy Yếu Kẻ Địch"},
            {"Slow Enemy", "Làm Chậm Kẻ Địch"},
            {"Thunder Defence", "Phòng Ngự Lôi"},
            {"Thunder Defense", "Phòng Ngự Lôi"},
            {"Water Defence", "Phòng Ngự Thủy"},
            {"Water Defense", "Phòng Ngự Thủy"},
            {"Earth Defence", "Phòng Ngự Địa"},
            {"Earth Defense", "Phòng Ngự Địa"},
            {"Fire Defence", "Phòng Ngự Hỏa"},
            {"Fire Defense", "Phòng Ngự Hỏa"},
            {"Air Defence", "Phòng Ngự Phong"},
            {"Air Defense", "Phòng Ngự Phong"},
            {"Thunder Damage", "Sát Thương Lôi"},
            {"Water Damage", "Sát Thương Thủy"},
            {"Earth Damage", "Sát Thương Địa"},
            {"Fire Damage", "Sát Thương Hỏa"},
            {"Air Damage", "Sát Thương Phong"},
            {"Neutral Damage", "Sát Thương Vật Lý"},
            {"XP Bonus", "Tăng Kinh Nghiệm (XP)"},
            {"Loot Bonus", "Tăng Tỉ Lệ Rớt Đồ"},
            {"Loot Quality", "Chất Lượng Đồ Rớt"},
            {"Stealing", "Tỉ Lệ Trộm Lục Bảo"},
            {"Soul Point Regen", "Hồi Điểm Linh Hồn"},
            {"Reflection", "Phản Sát Thương"},
            {"Thorns", "Gai Phản Đòn"},
            {"Exploding", "Nổ Lan Khi Tiêu Diệt"},
            {"Poison", "Độc Tố"},
            {"Combat Level", "Cấp Chiến Đấu"},
            {"Combat Lv. Min:", "Cấp Chiến Đấu Tối Thiểu:"},
            {"Class Type", "Yêu Cầu Hệ Phái"},
            {"Intelligence", "Trí Lực"},
            {"Strength", "Sức Mạnh"},
            {"Dexterity", "Thân Pháp"},
            {"Defense", "Thủ Hộ"},
            {"Agility", "Nhanh Nhẹn"},
            {"Very Fast", "Rất Nhanh"},
            {"Super Fast", "Siêu Tốc"},
            {"Fast", "Nhanh"},
            {"Normal", "Bình Thường"},
            {"Slow", "Chậm"},
            {"Very Slow", "Rất Chậm"},
            {"Super Slow", "Cực Chậm"},
            {"hits/s", "đòn/giây"},
            {"DPS", "DPS"}
    };

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
        if (clean.isEmpty() || WynnFontShield.isPurePuaGlyphs(clean)) {
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

        // 4. Token substring replacement for stats and UI phrases
        String tokenReplaced = clean;
        boolean modified = false;
        for (Object[] pair : STAT_REPLACEMENTS) {
            String eng = (String) pair[0];
            String vie = (String) pair[1];
            if (tokenReplaced.contains(eng)) {
                tokenReplaced = tokenReplaced.replace(eng, vie);
                modified = true;
            }
        }

        if (modified) {
            return tokenReplaced;
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
            return translateTextComponent(original);
        }

        if (ConfigManager.getConfig().translateSystemChat) {
            return translateTextComponent(original);
        }

        return original;
    }

    /**
     * Translates a Minecraft Text component deeply and recursively.
     */
    public Text translateTextComponent(Text original) {
        if (!ConfigManager.getConfig().enabled || original == null) {
            return original;
        }

        String textStr = original.getString();
        if (textStr == null || textStr.trim().isEmpty() || WynnFontShield.isPurePuaGlyphs(textStr)) {
            return original;
        }

        String translated = translateManual(textStr);
        if (translated != null) {
            MutableText result = Text.literal(WynnTextFormatter.preserveFormatting(textStr, translated));
            if (original.getStyle() != null) {
                result.setStyle(original.getStyle());
            }
            return result;
        }

        // Recursive translation of siblings
        if (!original.getSiblings().isEmpty()) {
            MutableText copy = original.copyContentOnly();
            if (original.getStyle() != null) {
                copy.setStyle(original.getStyle());
            }
            boolean anyChanged = false;
            for (Text sibling : original.getSiblings()) {
                Text transSibling = translateTextComponent(sibling);
                if (transSibling != sibling) {
                    anyChanged = true;
                }
                copy.append(transSibling);
            }
            if (anyChanged) {
                return copy;
            }
        }

        return original;
    }

    /**
     * Translates Item Tooltips in-place.
     */
    public List<Text> processItemTooltip(List<Text> originalTooltip) {
        if (!ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateItems || originalTooltip == null || originalTooltip.isEmpty()) {
            return originalTooltip;
        }

        List<Text> processed = new ArrayList<>();

        for (int i = 0; i < originalTooltip.size(); i++) {
            Text line = originalTooltip.get(i);
            String rawLine = line.getString();
            String clean = WynnTextFormatter.stripFormatting(rawLine).trim();

            if (clean.isEmpty() || WynnFontShield.isPurePuaGlyphs(clean)) {
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