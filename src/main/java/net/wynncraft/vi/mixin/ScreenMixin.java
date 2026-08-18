package net.wynncraft.vi.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.translation.TranslationEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(
            method = "getTitle",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyScreenTitle(CallbackInfoReturnable<Text> cir) {
        Text original = cir.getReturnValue();
        if (original != null && ConfigManager.getConfig().enabled && ConfigManager.getConfig().translateGuiAndWynntils) {
            cir.setReturnValue(TranslationEngine.getInstance().translateTextComponent(original));
        }
    }
}