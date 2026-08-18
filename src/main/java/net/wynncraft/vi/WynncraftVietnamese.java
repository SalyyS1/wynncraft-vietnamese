package net.wynncraft.vi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.config.ModConfig;
import net.wynncraft.vi.config.ModMenuIntegration;
import net.wynncraft.vi.translation.TranslationEngine;
import net.wynncraft.vi.translation.WynnRpgLexicon;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WynncraftVietnamese implements ClientModInitializer {
    public static final String MOD_ID = "wynncraft_vi";
    public static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI");

    private static KeyBinding toggleKey;
    private static KeyBinding configKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starting Wynncraft Vietnamese Translation Mod (By SalyVn)...");

        // 1. Load Configurations & Engine
        ConfigManager.load();
        TranslationEngine.getInstance().init();

        // 2. Register Item Tooltip Callback (Fabric API 1.21.4)
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            List<Text> modified = TranslationEngine.getInstance().processItemTooltip(lines);
            if (modified != lines) {
                lines.clear();
                lines.addAll(modified);
            }
        });

        // 3. Register Keybindings
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wynncraft_vi.toggle",
                GLFW.GLFW_KEY_V,
                "category.wynncraft_vi"
        ));

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wynncraft_vi.config",
                GLFW.GLFW_KEY_O,
                "category.wynncraft_vi"
        ));

        // 4. Register Key Listeners
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                ModConfig config = ConfigManager.getConfig();
                config.enabled = !config.enabled;
                ConfigManager.save();

                String status = config.enabled ? "§aBẬT" : "§cTẮT";
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§6[Wynncraft VI] §fDịch Tiếng Việt: " + status), false);
                }
            }

            while (configKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ModMenuIntegration().getModConfigScreenFactory().create(null));
                }
            }
        });

        // 5. Welcome & Credit message upon joining server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (ConfigManager.getConfig().showCreditBadge && client.player != null) {
                client.execute(() -> {
                    client.player.sendMessage(Text.literal("§6========================================"), false);
                    client.player.sendMessage(Text.literal("§e✦ Wynncraft Tiếng Việt (RPG Edition) §aĐã kích hoạt!"), false);
                    client.player.sendMessage(Text.literal("§b✦ Bản dịch RPG thực hiện bởi: §d" + WynnRpgLexicon.TRANSLATOR_CREDIT), false);
                    client.player.sendMessage(Text.literal("§7✦ Phím tắt: §fV §7(Bật/Tắt) | §fO §7(Cài đặt) | Lệnh: §e/wynnvi"), false);
                    client.player.sendMessage(Text.literal("§6========================================"), false);
                });
            }
        });

        // 6. Register Client Commands (/wynnvi)
        registerCommands();

        // 7. Save on Stop
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            LOGGER.info("Saving Wynncraft Vietnamese caches and configurations...");
            TranslationEngine.getInstance().shutdown();
            ConfigManager.save();
        });

        LOGGER.info("Wynncraft Vietnamese Translation Mod loaded successfully! Credit: SalyVn");
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("wynnvi")
                    .then(ClientCommandManager.literal("toggle").executes(context -> {
                        ModConfig config = ConfigManager.getConfig();
                        config.enabled = !config.enabled;
                        ConfigManager.save();
                        String status = config.enabled ? "§aBẬT" : "§cTẮT";
                        context.getSource().sendFeedback(Text.literal("§6[Wynncraft VI] §fDịch Tiếng Việt: " + status));
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("reload").executes(context -> {
                        ConfigManager.load();
                        TranslationEngine.getInstance().getDictionaryManager().init();
                        context.getSource().sendFeedback(Text.literal("§6[Wynncraft VI] §aĐã tải lại toàn bộ từ điển RPG và cấu hình thành công!"));
                        return 1;
                    }))
                    .then(ClientCommandManager.literal("cache")
                            .then(ClientCommandManager.literal("size").executes(context -> {
                                int count = TranslationEngine.getInstance().getCache().size();
                                context.getSource().sendFeedback(Text.literal("§6[Wynncraft VI] §fSố câu đã lưu trong bộ nhớ đệm cache: §e" + count));
                                return 1;
                            }))
                            .then(ClientCommandManager.literal("clear").executes(context -> {
                                TranslationEngine.getInstance().getCache().clear();
                                context.getSource().sendFeedback(Text.literal("§6[Wynncraft VI] §aĐã xóa toàn bộ bộ nhớ đệm cache!"));
                                return 1;
                            }))
                    )
                    .then(ClientCommandManager.literal("config").executes(context -> {
                        MinecraftClient client = MinecraftClient.getInstance();
                        client.send(() -> client.setScreen(new ModMenuIntegration().getModConfigScreenFactory().create(client.currentScreen)));
                        return 1;
                    }))
                    .executes(context -> {
                        context.getSource().sendFeedback(Text.literal("§6=== [Wynncraft Vietnamese RPG v1.0.0] ==="));
                        context.getSource().sendFeedback(Text.literal("§b✦ Bản dịch RPG thực hiện bởi: §d" + WynnRpgLexicon.TRANSLATOR_CREDIT));
                        context.getSource().sendFeedback(Text.literal("§e/wynnvi toggle §7- Bật/Tắt dịch nhanh"));
                        context.getSource().sendFeedback(Text.literal("§e/wynnvi config §7- Mở bảng cài đặt"));
                        context.getSource().sendFeedback(Text.literal("§e/wynnvi reload §7- Tải lại dữ liệu từ điển"));
                        context.getSource().sendFeedback(Text.literal("§e/wynnvi cache size/clear §7- Quản lý bộ nhớ đệm"));
                        context.getSource().sendFeedback(Text.literal("§7Phím tắt: §bV §7(Bật/Tắt), §bO §7(Cài đặt)"));
                        return 1;
                    })
            );
        });
    }
}