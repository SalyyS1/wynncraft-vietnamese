package net.wynncraft.vi.translation.format;

import java.util.regex.Pattern;

public class WynnFontShield {
    // Unicode Private Use Area (PUA) range used by Wynncraft for custom textures & negative spaces
    private static final Pattern PUA_PATTERN = Pattern.compile("[\uE000-\uF8FF]");

    public static boolean containsPuaGlyphs(String text) {
        if (text == null) return false;
        return PUA_PATTERN.matcher(text).find();
    }

    public static String stripPuaGlyphs(String text) {
        if (text == null) return "";
        return PUA_PATTERN.matcher(text).replaceAll("");
    }
}