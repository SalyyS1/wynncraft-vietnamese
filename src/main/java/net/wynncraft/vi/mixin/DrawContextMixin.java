package net.wynncraft.vi.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.wynncraft.vi.translation.TranslationEngine;
import net.wynncraft.vi.translation.format.WynnFontShield;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @ModifyVariable(
            method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Text modifyDrawText(Text text) {
        if (text == null) return null;
        String raw = text.getString();
        if (WynnFontShield.isPurePuaGlyphs(raw)) {
            return text;
        }
        return TranslationEngine.getInstance().translateTextComponent(text);
    }

    @ModifyVariable(
            method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"),
            argsOnly = true
    )
    private String modifyDrawTextString(String text) {
        if (text == null || text.isEmpty()) return text;
        if (WynnFontShield.isPurePuaGlyphs(text)) {
            return text;
        }
        String translated = TranslationEngine.getInstance().translateManual(text);
        return translated != null ? translated : text;
    }
}
