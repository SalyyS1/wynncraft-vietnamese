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

import java.lang.reflect.Constructor;
import java.util.List;

public class WynncraftVietnamese implements ClientModInitializer {
    public static final String MOD_ID = "wynncraft_vi";
    public static final Logger LOGGER = LoggerFactory.getLogger("WynncraftVI");

    private static KeyBinding toggleKey;
    private static KeyBinding configKey;
    private static boolean hasWelcomed = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Starting Wynncraft Vietnamese Translation Mod (By SalyVn)...");

        // 1. Load Configurations & Engine
        try {
            ConfigManager.load();
            TranslationEngine.getInstance().init();
        } catch (Throwable t) {
            LOGGER.error("Failed to initialize translation engine", t);
        }

        // 2. Register Item Tooltip Callback
        try {
            ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
                List<Text> modified = TranslationEngine.getInstance().processItemTooltip(lines);
                if (modified != lines) {
                    lines.clear();
                    lines.addAll(modified);
                }
            });
        } catch (Throwable t) {
            LOGGER.warn("ItemTooltipCallback registration error: {}", t.getMessage());
        }

        // 3. Register Keybindings safely via dynamic constructor resolution
        try {
            toggleKey = createKeyBinding("key.wynncraft_vi.toggle", GLFW.GLFW_KEY_V, "category.wynncraft_vi");
            if (toggleKey != null) {
                KeyBindingHelper.registerKeyBinding(toggleKey);
            }

            configKey = createKeyBinding("key.wynncraft_vi.config", GLFW.GLFW_KEY_O, "category.wynncraft_vi");
            if (configKey != null) {
                KeyBindingHelper.registerKeyBinding(configKey);
            }
        } catch (Throwable t) {
            LOGGER.warn("Keybinding registration error: {}", t.getMessage());
        }

        // 4. Register Key Listeners
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey != null) {
                while (toggleKey.wasPressed()) {
                    ModConfig config = ConfigManager.getConfig();
                    config.enabled = !config.enabled;
                    ConfigManager.save();

                    String status = config.enabled ? "§aBẬT" : "§cTẮT";
                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("§6[Wynncraft VI] §fDịch Tiếng Việt: " + status), false);
                    }
                }
            }

            if (configKey != null) {
                while (configKey.wasPressed()) {
                    if (client.currentScreen == null) {
                        client.setScreen(new ModMenuIntegration().getModConfigScreenFactory().create(null));
                    }
                }
            }
        });

        // 5. Welcome & Credit message upon joining server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (ConfigManager.getConfig().showCreditBadge && client.player != null && !hasWelcomed) {
                hasWelcomed = true;
                client.execute(() -> {
                    client.player.sendMessage(Text.literal("§6========================================"), false);
                    client.player.sendMessage(Text.literal("§e✦ Wynncraft Tiếng Việt (RPG Edition) §aĐã kích hoạt!"), false);
                    client.player.sendMessage(Text.literal("§b✦ Bản dịch RPG thực hiện bởi: §d" + WynnRpgLexicon.TRANSLATOR_CREDIT), false);
                    client.player.sendMessage(Text.literal("§7✦ Phím tắt: §fV §7(Bật/Tắt) | §fO §7(Cài đặt) | Lệnh: §e/wynnvi"), false);
                    client.player.sendMessage(Text.literal("§6========================================"), false);
                });
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            hasWelcomed = false;
        });

        // 6. Register Client Commands (/wynnvi)
        registerCommands();

        // 7. Save on Stop
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            LOGGER.info("Saving Wynncraft Vietnamese configurations...");
            TranslationEngine.getInstance().shutdown();
            ConfigManager.save();
        });

        LOGGER.info("Wynncraft Vietnamese Translation Mod loaded successfully! Credit: SalyVn");
    }

    private static KeyBinding createKeyBinding(String id, int keyCode, String category) {
        try {
            for (Constructor<?> c : KeyBinding.class.getConstructors()) {
                Class<?>[] params = c.getParameterTypes();

                // 4-parameter constructor: (String, InputUtil.Type, int, String)
                if (params.length == 4 && params[0] == String.class && (params[2] == int.class || params[2] == Integer.class) && params[3] == String.class) {
                    Object typeEnum = null;
                    if (params[1].isEnum()) {
                        for (Object constant : params[1].getEnumConstants()) {
                            if (constant.toString().equalsIgnoreCase("KEYSYM")) {
                                typeEnum = constant;
                                break;
                            }
                        }
                        if (typeEnum == null && params[1].getEnumConstants().length > 0) {
                            typeEnum = params[1].getEnumConstants()[0];
                        }
                    }
                    return (KeyBinding) c.newInstance(id, typeEnum, keyCode, category);
                }

                // 3-parameter constructor: (String, int, String)
                if (params.length == 3 && params[0] == String.class && (params[1] == int.class || params[1] == Integer.class) && params[2] == String.class) {
                    return (KeyBinding) c.newInstance(id, keyCode, category);
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Could not reflectively construct KeyBinding for {}", id, t);
        }
        return null;
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
                        context.getSource().sendFeedback(Text.literal("§6[Wynncraft VI] §aĐã tải lại toàn bộ từ điển RPG thủ công thành công!"));
                        return 1;
                    }))
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
                        context.getSource().sendFeedback(Text.literal("§7Phím tắt: §bV §7(Bật/Tắt), §bO §7(Cài đặt)"));
                        return 1;
                    })
            );
        });
    }
}