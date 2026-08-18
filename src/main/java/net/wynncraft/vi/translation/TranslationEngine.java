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

public class TranslationEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI-Engine");
    private static TranslationEngine INSTANCE;

    private final DictionaryManager dictionaryManager;

    // Stat and UI token replacements for item tooltips and quest screens
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
            {"Storyline – Currently in progress", "Cốt truyện – Đang thực hiện"},
            {"Currently in progress", "Đang thực hiện"},
            {"Solve this riddle in the tomb:", "Giải câu đố sau trong lăng mộ:"},
            {"Throw tribute an item to the wood that stands out, the right angle will open the way", "Dâng vật phẩm tế lễ cho khúc gỗ nổi bật, góc vuông chuẩn xác sẽ mở lối đi"},
            {"“Throw tribute an item to the wood that stands out, the right angle will open the way”", "\"Dâng vật phẩm tế lễ cho khúc gỗ nổi bật, góc vuông chuẩn xác sẽ mở lối đi\""},
            {"Length: Short", "Độ dài: Ngắn"},
            {"Length: Medium", "Độ dài: Trung bình"},
            {"Length: Long", "Độ dài: Dài"},
            {"Difficulty: Easy", "Độ khó: Dễ"},
            {"Difficulty: Medium", "Độ khó: Trung bình"},
            {"Difficulty: Hard", "Độ khó: Khó"},
            {"Click To Track", "Nhấp Để Theo Dõi"},
            {"Click to track", "Nhấp để theo dõi"},
            {"Click to view on map", "Nhấp để xem trên bản đồ"},
            {"Click to open on the wiki", "Nhấp để mở trên wiki"},
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
            {"Combat Lv. Min:", "Cấp Chiến Đấu Tối Thiểu:"},
            {"Combat Level Min:", "Cấp Chiến Đấu Tối Thiểu:"},
            {"Combat Level", "Cấp Chiến Đấu"},
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
            {"Yellow Crystal", "Pha Lê Vàng (Yellow Crystal)"},
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
        if (translated != null && !translated.equalsIgnoreCase(textStr)) {
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
     * Translates Item Tooltip line in-place cleanly without touching font matrices.
     */
    public Text translateTooltipLine(Text line) {
        if (line == null) return null;
        String raw = line.getString();
        if (raw == null || raw.trim().isEmpty() || WynnFontShield.isPurePuaGlyphs(raw)) {
            return line;
        }

        String clean = WynnTextFormatter.stripFormatting(raw).trim();

        // 1. Direct dictionary match
        String match = dictionaryManager.findTranslation(clean);
        if (match != null && !match.equalsIgnoreCase(clean)) {
            MutableText res = Text.literal(WynnTextFormatter.preserveFormatting(raw, match));
            if (line.getStyle() != null) res.setStyle(line.getStyle());
            return res;
        }

        // 2. Normalized quotes match
        String normalized = clean.replace('“', '"').replace('”', '"').replace('‘', '\'').replace('’', '\'');
        if (!normalized.equals(clean)) {
            String matchQ = dictionaryManager.findTranslation(normalized);
            if (matchQ != null && !matchQ.equalsIgnoreCase(clean)) {
                MutableText res = Text.literal(WynnTextFormatter.preserveFormatting(raw, matchQ));
                if (line.getStyle() != null) res.setStyle(line.getStyle());
                return res;
            }
        }

        // 3. In-line token replacement for stats and quest descriptions
        String replaced = raw;
        boolean changed = false;
        for (Object[] pair : STAT_REPLACEMENTS) {
            String eng = (String) pair[0];
            String vie = (String) pair[1];
            if (replaced.contains(eng)) {
                replaced = replaced.replace(eng, vie);
                changed = true;
            }
        }

        if (changed) {
            MutableText res = Text.literal(replaced);
            if (line.getStyle() != null) res.setStyle(line.getStyle());
            return res;
        }

        return line;
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
            Text translated = translateTooltipLine(line);
            processed.add(translated != null ? translated : line);
        }

        return processed;
    }
}