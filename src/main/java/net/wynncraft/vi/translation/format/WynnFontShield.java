package net.wynncraft.vi.translation.format;

import java.util.regex.Pattern;

public class WynnFontShield {
    // Unicode Private Use Area (PUA) range used by Wynncraft for custom textures & negative spaces
    private static final Pattern PUA_PATTERN = Pattern.compile("[\uE000-\uF8FF]");
    private static final Pattern LATIN_ALPHANUM = Pattern.compile("[a-zA-Z0-9]");

    public static boolean containsPuaGlyphs(String text) {
        if (text == null) return false;
        return PUA_PATTERN.matcher(text).find();
    }

    public static String stripPuaGlyphs(String text) {
        if (text == null) return "";
        return PUA_PATTERN.matcher(text).replaceAll("");
    }

    /**
     * Returns true if text is exclusively PUA glyphs, spaces, or Minecraft color formatting codes.
     * These strings are Wynncraft custom UI textures, borders, or negative space shifts and must NEVER be translated.
     */
    public static boolean isPurePuaGlyphs(String text) {
        if (text == null || text.isEmpty()) return true;
        // If it doesn't contain any alphanumeric letters or digits, it's either formatting/PUA/punctuation
        return !LATIN_ALPHANUM.matcher(text).find();
    }
}