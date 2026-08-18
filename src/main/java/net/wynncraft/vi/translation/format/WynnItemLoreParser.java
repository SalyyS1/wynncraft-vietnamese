package net.wynncraft.vi.translation.format;

import java.util.regex.Pattern;

public class WynnItemLoreParser {
    private static final Pattern TIER_PATTERN = Pattern.compile(
            "^(Normal|Unique|Rare|Legendary|Fabled|Mythic|Set|Crafted)\\s+(.*)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LVL_REQ_PATTERN = Pattern.compile(
            "^(Combat Level Min|Combat Lv\\. Min|Requires Level|Min Level):\\s*(\\d+)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern CLASS_REQ_PATTERN = Pattern.compile(
            "^(Class Req|Requires Class):\\s*(.+)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ATTACK_SPEED_PATTERN = Pattern.compile(
            "^(Attack Speed):\\s*(SUPER_SLOW|VERY_SLOW|SLOW|NORMAL|FAST|VERY_FAST|SUPER_FAST|Super Slow|Very Slow|Slow|Normal|Fast|Very Fast|Super Fast)$",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean isWynncraftItemLore(String rawLine) {
        String clean = WynnTextFormatter.stripFormatting(rawLine).trim();
        return TIER_PATTERN.matcher(clean).find() ||
                LVL_REQ_PATTERN.matcher(clean).find() ||
                CLASS_REQ_PATTERN.matcher(clean).find() ||
                ATTACK_SPEED_PATTERN.matcher(clean).find() ||
                clean.startsWith("Identifications:") ||
                clean.startsWith("Main Attack Damage:") ||
                clean.startsWith("Base Damage:") ||
                clean.startsWith("Powder Slots:") ||
                clean.startsWith("Set Bonus:");
    }
}