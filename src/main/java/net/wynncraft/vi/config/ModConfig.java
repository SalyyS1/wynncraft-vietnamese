package net.wynncraft.vi.config;

public class ModConfig {
    public boolean enabled = true;

    // Translation scopes
    public boolean translateQuests = true;
    public boolean translateNpcDialogue = true;
    public boolean translateItems = true;
    public boolean translateActionBar = true;
    public boolean translateTitles = true;
    public boolean translateSystemChat = true;

    // Display & Credit preferences
    public ItemTooltipMode itemTooltipMode = ItemTooltipMode.APPEND;
    public boolean showCreditBadge = true;

    public enum ItemTooltipMode {
        REPLACE("Thay thế tiếng Anh bằng tiếng Việt"),
        APPEND("Hiển thị thêm dòng tiếng Việt bên dưới"),
        HOVER_OR_SHIFT("Chỉ hiện tiếng Việt khi giữ phím Shift");

        private final String displayName;

        ItemTooltipMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}