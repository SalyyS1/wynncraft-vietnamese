package net.wynncraft.vi.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.translation.TranslationEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @ModifyVariable(
            method = "setOverlayMessage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text modifyOverlayMessage(Text message) {
        if (message == null || !ConfigManager.getConfig().translateActionBar) {
            return message;
        }
        return TranslationEngine.getInstance().translateTextComponent(message);
    }

    @ModifyVariable(
            method = "setTitle",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text modifyTitle(Text title) {
        if (title == null || !ConfigManager.getConfig().translateTitles) {
            return title;
        }
        return TranslationEngine.getInstance().translateTextComponent(title);
    }

    @ModifyVariable(
            method = "setSubtitle",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text modifySubtitle(Text subtitle) {
        if (subtitle == null || !ConfigManager.getConfig().translateTitles) {
            return subtitle;
        }
        return TranslationEngine.getInstance().translateTextComponent(subtitle);
    }
}