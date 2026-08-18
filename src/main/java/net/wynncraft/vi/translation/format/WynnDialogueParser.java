package net.wynncraft.vi.translation.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WynnDialogueParser {
    private static final Pattern NPC_DIALOGUE_PATTERN = Pattern.compile(
            "^(§[0-9a-fk-or]|\\s)*(\\[\\d+/\\d+\\]\\s*)?(\\[NPC\\]\\s*)([^:]+:\\s*)(.+)$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern QUEST_STEP_PATTERN = Pattern.compile(
            "^(\\[\\d+/\\d+\\]\\s*)(.+)$"
    );

    private static final Pattern DIALOGUE_OPTION_PATTERN = Pattern.compile(
            "^(\\[\\d+\\]\\s*)(.+)$"
    );

    public static class ParsedDialogue {
        public final boolean isDialogue;
        public final String prefix;
        public final String dialogueBody;

        public ParsedDialogue(boolean isDialogue, String prefix, String dialogueBody) {
            this.isDialogue = isDialogue;
            this.prefix = prefix;
            this.dialogueBody = dialogueBody;
        }
    }

    public static ParsedDialogue parse(String rawText) {
        String clean = WynnTextFormatter.stripFormatting(rawText);
        if (clean == null || clean.trim().isEmpty()) {
            return new ParsedDialogue(false, "", rawText);
        }

        Matcher npcMatcher = NPC_DIALOGUE_PATTERN.matcher(clean);
        if (npcMatcher.find()) {
            String step = npcMatcher.group(2) != null ? npcMatcher.group(2) : "";
            String npcTag = npcMatcher.group(3) != null ? npcMatcher.group(3) : "";
            String nameColon = npcMatcher.group(4) != null ? npcMatcher.group(4) : "";
            String body = npcMatcher.group(5);

            String prefix = step + npcTag + nameColon;
            return new ParsedDialogue(true, prefix, body);
        }

        Matcher stepMatcher = QUEST_STEP_PATTERN.matcher(clean);
        if (stepMatcher.find()) {
            String step = stepMatcher.group(1);
            String body = stepMatcher.group(2);
            return new ParsedDialogue(true, step, body);
        }

        Matcher optionMatcher = DIALOGUE_OPTION_PATTERN.matcher(clean);
        if (optionMatcher.find()) {
            String optionNumber = optionMatcher.group(1);
            String body = optionMatcher.group(2);
            return new ParsedDialogue(true, optionNumber, body);
        }

        // Generic Named NPC Dialogue (e.g. "Enzan: Hello!", "Dr. Picard: Look here", "King of Ragni: Welcome")
        int colonIdx = clean.indexOf(':');
        if (colonIdx > 0 && colonIdx < 35) {
            String speaker = clean.substring(0, colonIdx).trim();
            if (!speaker.contains("http") && !speaker.contains("/") && speaker.matches("^[A-Z][a-zA-Z0-9' ._-]{1,30}$")) {
                String prefix = clean.substring(0, colonIdx + 1) + " ";
                String body = clean.substring(colonIdx + 1).trim();
                if (!body.isEmpty()) {
                    return new ParsedDialogue(true, prefix, body);
                }
            }
        }

        return new ParsedDialogue(false, "", rawText);
    }
}