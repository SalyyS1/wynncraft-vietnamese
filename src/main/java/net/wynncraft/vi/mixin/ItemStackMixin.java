package net.wynncraft.vi.mixin;

import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.translation.TranslationEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void modifyItemName(CallbackInfoReturnable<Text> cir) {
        if (!ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateItems) {
            return;
        }
        Text original = cir.getReturnValue();
        if (original != null) {
            String str = original.getString();
            // Preserve Wynntils activity parser tags so Wynntils recognizes all quests
            if (str.contains("[Quest]") || str.contains("[Mini-Quest]") || str.contains("[Discovery]") || str.contains("[World Event]")) {
                return;
            }
            Text translated = TranslationEngine.getInstance().translateTextComponent(original);
            if (translated != original) {
                cir.setReturnValue(translated);
            }
        }
    }

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    private void modifyItemTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (!ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateItems) {
            return;
        }
        List<Text> original = cir.getReturnValue();
        if (original != null && !original.isEmpty()) {
            cir.setReturnValue(TranslationEngine.getInstance().processItemTooltip(original));
        }
    }
}
