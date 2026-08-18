package net.wynncraft.vi.translation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Advanced Context-Aware RPG Translator by SalyVn.
 * Solves word-by-word mistranslations by shielding RPG terms and polishing grammar.
 */
public class SmartRpgTranslator {

    private static final Map<Pattern, String> PRE_TRANSLATION_MASKS = new LinkedHashMap<>();
    private static final Map<String, String> POST_TRANSLATION_REPLACEMENTS = new LinkedHashMap<>();

    static {
        // Protect stats & gameplay terms from Google word-by-word errors
        registerTerm("Spell Damage", "Sát thương Phép", "SPEL_DMG");
        registerTerm("Main Attack Damage", "Sát thương Đòn đánh thường", "MAIN_DMG");
        registerTerm("Melee Damage", "Sát thương Cận chiến", "MEL_DMG");
        registerTerm("Attack Speed", "Tốc độ Đánh", "ATK_SPD");
        registerTerm("Mana Regen", "Hồi phục Năng lượng", "MANA_REG");
        registerTerm("Mana Steal", "Hút Năng lượng", "MANA_STL");
        registerTerm("Life Steal", "Hút Máu", "LIFE_STL");
        registerTerm("Health Regen", "Hồi phục Máu", "HP_REG");
        registerTerm("Walk Speed", "Tốc độ Di chuyển", "WALK_SPD");
        registerTerm("Soul Points", "Điểm Linh hồn", "SOUL_PTS");
        registerTerm("Soul Point", "Điểm Linh hồn", "SOUL_PT");
        registerTerm("Emerald Blocks", "Khối Lục bảo", "EM_BLKS");
        registerTerm("Emerald Block", "Khối Lục bảo", "EM_BLK");
        registerTerm("Liquid Emeralds", "Lục bảo Lỏng", "LIQ_EMS");
        registerTerm("Liquid Emerald", "Lục bảo Lỏng", "LIQ_EM");
        registerTerm("Emeralds", "Ngọc Lục bảo", "EMS");
        registerTerm("Emerald", "Ngọc Lục bảo", "EM");

        // Lore & Dialogues post-processing polish (make it sound like an epic RPG)
        POST_TRANSLATION_REPLACEMENTS.put("bạn phải", "ngươi cần phải");
        POST_TRANSLATION_REPLACEMENTS.put("Bạn phải", "Ngươi cần phải");
        POST_TRANSLATION_REPLACEMENTS.put("bạn có thể", "ngươi có thể");
        POST_TRANSLATION_REPLACEMENTS.put("Bạn có thể", "Ngươi có thể");
        POST_TRANSLATION_REPLACEMENTS.put("hãy nói chuyện với", "hãy diện kiến");
        POST_TRANSLATION_REPLACEMENTS.put("Hãy nói chuyện với", "Hãy diện kiến");
        POST_TRANSLATION_REPLACEMENTS.put("Đánh vần", "Phép thuật");
        POST_TRANSLATION_REPLACEMENTS.put("đánh vần", "phép thuật");
        POST_TRANSLATION_REPLACEMENTS.put("Thiệt hại", "Sát thương");
        POST_TRANSLATION_REPLACEMENTS.put("thiệt hại", "sát thương");
        POST_TRANSLATION_REPLACEMENTS.put("Cúi đầu", "Cung tên");
        POST_TRANSLATION_REPLACEMENTS.put("cúi đầu", "cung tên");
    }

    private static void registerTerm(String englishTerm, String vietnameseMeaning, String tag) {
        String placeholder = "%%" + tag + "%%";
        Pattern pattern = Pattern.compile("(?i)\\b" + Pattern.quote(englishTerm) + "\\b");
        PRE_TRANSLATION_MASKS.put(pattern, placeholder);
        POST_TRANSLATION_REPLACEMENTS.put(placeholder, vietnameseMeaning);
    }

    public static String maskTerms(String input) {
        if (input == null) return null;
        String text = input;
        for (Map.Entry<Pattern, String> entry : PRE_TRANSLATION_MASKS.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }
        return text;
    }

    public static String unmaskAndPolish(String translated) {
        if (translated == null) return null;
        String text = translated;
        for (Map.Entry<String, String> entry : POST_TRANSLATION_REPLACEMENTS.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }
}