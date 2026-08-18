package net.wynncraft.vi.translation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Wynncraft RPG Lexicon curated by SalyVn.
 * Contains context-aware RPG terms, towns, classes, elements, and gameplay mechanics.
 */
public class WynnRpgLexicon {
    public static final String TRANSLATOR_CREDIT = "SalyVn";
    public static final String MOD_TITLE = "Wynncraft Tiếng Việt (RPG Edition)";

    public static final Map<String, String> ELEMENT_NAMES;
    public static final Map<String, String> STAT_NAMES;
    public static final Map<String, String> CLASS_NAMES;
    public static final Map<String, String> RARITY_NAMES;
    public static final Map<String, String> PROVINCES_AND_TOWNS;

    static {
        Map<String, String> elem = new HashMap<>();
        elem.put("Earth", "Hệ Địa (Đất)");
        elem.put("Thunder", "Hệ Lôi (Sấm Sét)");
        elem.put("Water", "Hệ Thủy (Nước)");
        elem.put("Fire", "Hệ Hỏa (Lửa)");
        elem.put("Air", "Hệ Phong (Gió)");
        elem.put("Neutral", "Thuộc Tính Cơ Bản");
        ELEMENT_NAMES = Collections.unmodifiableMap(elem);

        Map<String, String> stat = new HashMap<>();
        stat.put("Strength", "Sức Mạnh (Tăng Sát Thương Địa)");
        stat.put("Dexterity", "Thân Pháp (Tăng Tỉ Lệ Chí Mạng & Sát Thương Lôi)");
        stat.put("Intelligence", "Trí Lực (Giảm Tiêu Hao Năng Lượng & Sát Thương Thủy)");
        stat.put("Defense", "Thủ Hộ (Giảm Sát Thương Nhận Vào & Sát Thương Hỏa)");
        stat.put("Agility", "Nhanh Nhẹn (Tăng Tỉ Lệ Né Đòn & Sát Thương Phong)");
        STAT_NAMES = Collections.unmodifiableMap(stat);

        Map<String, String> cls = new HashMap<>();
        cls.put("Warrior", "Chiến Binh");
        cls.put("Knight", "Hiệp Sĩ Hoàng Gia");
        cls.put("Archer", "Xạ Thủ");
        cls.put("Hunter", "Thợ Săn Tiền Thưởng");
        cls.put("Mage", "Pháp Sư");
        cls.put("Dark Wizard", "Thuật Sĩ Hắc Ám");
        cls.put("Assassin", "Sát Thủ");
        cls.put("Ninja", "Bóng Ma Ninja");
        cls.put("Shaman", "Tế Tư Thần Tộc");
        cls.put("Skyseer", "Chiêm Tinh Sư");
        CLASS_NAMES = Collections.unmodifiableMap(cls);

        Map<String, String> rar = new HashMap<>();
        rar.put("Normal", "Thường");
        rar.put("Unique", "Độc Nhất");
        rar.put("Rare", "Quý Hiếm");
        rar.put("Legendary", "Huyền Thoại");
        rar.put("Fabled", "Thần Thoại");
        rar.put("Mythic", "Thần Thánh");
        rar.put("Set", "Trang Bị Bộ");
        rar.put("Crafted", "Tự Chế Tạo");
        RARITY_NAMES = Collections.unmodifiableMap(rar);

        Map<String, String> places = new HashMap<>();
        places.put("Ragni", "Thành Cổ Ragni");
        places.put("Detlas", "Đại Đô Thị Detlas");
        places.put("Almuj", "Ốc Đảo Sa Mạc Almuj");
        places.put("Troms", "Pháo Đài Rừng Troms");
        places.put("Nemract", "Thị Trấn Cảng Nemract");
        places.put("Nivla Woods", "Khu Rừng Cổ Thụ Nivla");
        places.put("Pigman's Ravines", "Hẻm Núi Pigman");
        places.put("Time Valley", "Thung Lũng Thời Gian");
        places.put("Bremminglar", "Làng Nông Bremminglar");
        places.put("Llevigar", "Thành Phố Cảng Llevigar");
        places.put("Olux", "Vùng Đầm Lầy Olux");
        places.put("Cinfras", "Kinh Đô Cinfras");
        places.put("Aldorei Valley", "Thung Lũng Tiên Tộc Aldorei");
        places.put("Ahmsord", "Thành Phố Trên Mây Ahmsord");
        places.put("Lutho", "Vùng Đất Tĩnh Lặng Lutho");
        places.put("Silent Expanse", "Hoang Mạc Tĩnh Lặng");
        places.put("Wynn Province", "Đại Lục Wynn");
        places.put("Gavel Province", "Đại Lục Gavel");
        places.put("Corkus Island", "Hòn Đảo Cơ Giới Corkus");
        places.put("Dern", "Cõi Hư Vô Dern");
        places.put("Fruma", "Đế Chế Khép Kín Fruma");
        places.put("Roots of Corruption", "Cội Nguồn Ô Nhiễm");
        PROVINCES_AND_TOWNS = Collections.unmodifiableMap(places);
    }
}