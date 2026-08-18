# Wynncraft Vietnamese

Mod chuyển ngữ Tiếng Việt chuẩn MMORPG cho máy chủ Wynncraft, hoạt động trên Fabric Loader 1.20.4+ và hỗ trợ tương thích với Wynntils.

Dự án và bản dịch được phát triển bởi **SalyVn**.

---

## Tổng quan

Wynncraft Vietnamese là bản mod client-side giúp người chơi trải nghiệm cốt truyện, hội thoại NPC, hệ thống nhiệm vụ và thông tin trang bị của Wynncraft hoàn toàn bằng Tiếng Việt.

Mod sử dụng cơ chế xử lý chuỗi và ngữ cảnh RPG chuyên biệt, tránh hiện tượng dịch theo nghĩa đen từng từ (word-by-word) của các công cụ dịch máy thông thường, đồng thời giữ nguyên toàn bộ mã màu và định dạng hiển thị gốc của game.

---

## Tính năng chính

1. **Cơ chế dịch thuật Hybrid:**
   - **Từ điển offline tích hợp:** Chứa sẵn hàng trăm thuật ngữ, tên trang bị, độ hiếm, chỉ số thuộc tính và mẫu câu hội thoại thông dụng, phản hồi tức thì và không phụ thuộc vào kết nối mạng ngoài.
   - **Bộ lọc thuật ngữ RPG:** Tự động bảo vệ và chuẩn hóa các thuật ngữ đặc thù của Wynncraft (5 hệ nguyên tố Địa/Lôi/Thủy/Hỏa/Phong, các chỉ số Spell Damage, Mana Regen, Life Steal, Soul Points...) trước và sau khi dịch.
   - **Hỗ trợ dịch trực tuyến:** Tự động kết nối Google Translate, DeepL hoặc endpoint OpenAI/LLM tùy chỉnh đối với các đoạn thoại mới chưa có trong từ điển.
   - **Bộ nhớ đệm lưu trữ (Disk Cache):** Tự động lưu các chuỗi đã dịch vào file `cache.json`, đảm bảo mỗi câu chỉ cần dịch một lần duy nhất.

2. **Tương thích giao diện & Wynntils:**
   - Hỗ trợ dịch các thành phần UI của Wynntils như Quest Tracker.
   - Hỗ trợ dịch tiêu đề và nội dung trên các màn hình GUI tùy biến của Wynncraft (Sách Nhiệm Vụ, Ngân Hàng, Đại Sư Thẩm Định, Thợ Rèn, Cây Kỹ Năng).
   - Tự động tách tên NPC và lời thoại: `[NPC] Tên: Lời thoại` giúp giữ nguyên tên riêng của nhân vật và địa danh.

3. **Tùy biến hiển thị thông tin vật phẩm (Item Tooltips):**
   - **APPEND (Mặc định):** Hiển thị dòng dịch Tiếng Việt kèm ký hiệu bên dưới dòng gốc Tiếng Anh.
   - **REPLACE:** Thay thế trực tiếp dòng Tiếng Anh bằng Tiếng Việt.
   - **HOVER_OR_SHIFT:** Chỉ hiển thị nội dung Tiếng Việt khi giữ phím Shift.

4. **Giao diện cấu hình In-game:**
   - Tích hợp màn hình cài đặt trực quan qua Cloth Config và Mod Menu.
   - Cho phép bật/tắt riêng biệt từng phạm vi dịch: Nhiệm vụ, Hội thoại, Vật phẩm, Action Bar, Tiêu đề, Chat hệ thống, Giao diện GUI.

---

## Phím tắt và Lệnh điều khiển

### Phím tắt mặc định
- `V`: Bật hoặc tắt nhanh chế độ dịch Tiếng Việt.
- `O`: Mở màn hình cài đặt cấu hình (Cloth Config).

### Lệnh trong game (`/wynnvi`)
- `/wynnvi toggle`: Bật / Tắt mod dịch.
- `/wynnvi config`: Mở giao diện cài đặt.
- `/wynnvi reload`: Tải lại toàn bộ dữ liệu từ điển và file cấu hình.
- `/wynnvi cache size`: Kiểm tra số lượng mục trong bộ nhớ đệm.
- `/wynnvi cache clear`: Xóa toàn bộ bộ nhớ đệm.

---

## Hướng dẫn cài đặt

### Yêu cầu hệ thống
- Minecraft: `1.20.4`
- Fabric Loader: `>= 0.15.0`
- Fabric API: Khuyến nghị phiên bản mới nhất cho 1.20.4
- (Tùy chọn) Cloth Config API & Mod Menu để sử dụng giao diện cấu hình trong game.
- (Tùy chọn) Wynntils để có trải nghiệm chơi Wynncraft tốt nhất.

### Cài đặt
1. Tải file `wynncraft-vietnamese-1.0.0.jar` từ mục [Releases](../../releases) hoặc thư mục `build/libs`.
2. Đặt file mod vào thư mục `.minecraft/mods`.
3. Khởi động Minecraft bằng Fabric Profile và kết nối vào máy chủ `play.wynncraft.com`.

---

## Mở rộng từ điển tùy chỉnh

Người dùng có thể tự bổ sung hoặc chỉnh sửa bản dịch cá nhân thông qua file:
`.minecraft/config/wynncraft_vi/custom_dict.json`

Cấu trúc file hỗ trợ cả khớp chuỗi chính xác (`exact`) và biểu thức chính quy (`regex`):

```json
{
  "exact": {
    "Hello traveler": "Xin chào người lữ hành",
    "Select an option": "Chọn một phương án"
  },
  "regex": {
    "Level (\\d+)": "Cấp độ $1",
    "Requires (\\d+) Strength": "Yêu cầu $1 Sức Mạnh"
  }
}
```

---

## Hướng dẫn biên dịch từ mã nguồn

Dự án sử dụng Gradle Wrapper. Yêu cầu cài đặt JDK 17 hoặc JDK 21.

```bash
# Clone repository
git clone https://github.com/SalyyS1/wynncraft-vietnamese.git
cd wynncraft-vietnamese

# Biên dịch dự án
./gradlew build
```

File mod hoàn chỉnh sau khi build sẽ nằm tại đường dẫn: `build/libs/wynncraft-vietnamese-1.0.0.jar`.

---

## Bản quyền & Tác giả

- **Tác giả & Dịch giả:** SalyVn
- **Giấy phép:** MIT License. Chi tiết xem tại file [LICENSE](LICENSE).