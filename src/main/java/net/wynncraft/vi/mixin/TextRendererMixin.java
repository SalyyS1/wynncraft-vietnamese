package net.wynncraft.vi.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.wynncraft.vi.translation.TranslationEngine;
import net.wynncraft.vi.translation.format.WynnFontShield;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @ModifyVariable(
            method = "draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private Text modifyDrawText(Text text) {
        if (text == null) return null;
        String raw = text.getString();
        // Strictly protect all custom font glyphs, negative space offsets, and UI textures
        if (WynnFontShield.containsPuaGlyphs(raw)) {
            return text;
        }
        return TranslationEngine.getInstance().translateTextComponent(text);
    }

    @ModifyVariable(
            method = "draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private String modifyDrawString(String text) {
        if (text == null || text.isEmpty()) return text;
        // Strictly protect all custom font glyphs, negative space offsets, and UI textures
        if (WynnFontShield.containsPuaGlyphs(text)) {
            return text;
        }
        String translated = TranslationEngine.getInstance().translateManual(text);
        return translated != null ? translated : text;
    }
}
