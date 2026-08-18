package net.wynncraft.vi.translation.format;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WynnTextFormatter {
    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    private static final Pattern LEADING_FORMATTING_PATTERN = Pattern.compile("^((?:§[0-9a-fk-or])+)(.*)$", Pattern.CASE_INSENSITIVE);

    public static String stripFormatting(String input) {
        if (input == null) return null;
        return FORMATTING_CODE_PATTERN.matcher(input).replaceAll("");
    }

    public static String preserveFormatting(String original, String translated) {
        if (original == null || translated == null) {
            return translated;
        }

        Matcher matcher = LEADING_FORMATTING_PATTERN.matcher(original);
        if (matcher.find()) {
            String formatting = matcher.group(1);
            return formatting + translated;
        }

        return translated;
    }

    public static MutableText createStyledText(String translatedText, Style originalStyle) {
        MutableText text = Text.literal(translatedText);
        if (originalStyle != null) {
            text.setStyle(originalStyle);
        }
        return text;
    }
}