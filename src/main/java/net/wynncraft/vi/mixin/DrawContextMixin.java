package net.wynncraft.vi.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.translation.TranslationEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @ModifyVariable(
            method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
            at = @At("HEAD"),
            argsOnly = true,
            index = 2,
            require = 0
    )
    private Text modifyDrawText(Text text) {
        if (text == null || !ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateGuiAndWynntils) {
            return text;
        }
        return TranslationEngine.getInstance().translateTextComponent(text);
    }

    @ModifyVariable(
            method = "drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I",
            at = @At("HEAD"),
            argsOnly = true,
            index = 2,
            require = 0
    )
    private Text modifyDrawTextWithShadow(Text text) {
        if (text == null || !ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateGuiAndWynntils) {
            return text;
        }
        return TranslationEngine.getInstance().translateTextComponent(text);
    }
}