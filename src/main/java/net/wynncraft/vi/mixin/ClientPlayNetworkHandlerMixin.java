package net.wynncraft.vi.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import net.wynncraft.vi.config.ConfigManager;
import net.wynncraft.vi.translation.TranslationEngine;
import net.wynncraft.vi.translation.format.WynnFontShield;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @ModifyVariable(
            method = "onGameMessage",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private GameMessageS2CPacket modifyGameMessagePacket(GameMessageS2CPacket packet) {
        if (!ConfigManager.getConfig().enabled || packet == null) {
            return packet;
        }
        Text original = packet.content();
        if (original == null) return packet;

        Text translated = TranslationEngine.getInstance().translateDialogueOrChat(original);
        if (translated != original) {
            return new GameMessageS2CPacket(translated, packet.overlay());
        }
        return packet;
    }

    @ModifyVariable(
            method = "onProfilelessChatMessage",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private ProfilelessChatMessageS2CPacket modifyProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet) {
        if (!ConfigManager.getConfig().enabled || packet == null) {
            return packet;
        }
        Text original = packet.message();
        if (original == null) return packet;

        Text translated = TranslationEngine.getInstance().translateDialogueOrChat(original);
        if (translated != original) {
            return new ProfilelessChatMessageS2CPacket(translated, packet.chatType());
        }
        return packet;
    }

    @ModifyVariable(
            method = "onTitle",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private TitleS2CPacket modifyTitlePacket(TitleS2CPacket packet) {
        if (!ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateTitles || packet == null) {
            return packet;
        }
        Text original = packet.text();
        if (original == null || WynnFontShield.containsPuaGlyphs(original.getString())) {
            return packet;
        }

        Text translated = TranslationEngine.getInstance().translateTextComponent(original);
        if (translated != original) {
            return new TitleS2CPacket(translated);
        }
        return packet;
    }

    @ModifyVariable(
            method = "onSubtitle",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private SubtitleS2CPacket modifySubtitlePacket(SubtitleS2CPacket packet) {
        if (!ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateTitles || packet == null) {
            return packet;
        }
        Text original = packet.text();
        if (original == null || WynnFontShield.containsPuaGlyphs(original.getString())) {
            return packet;
        }

        Text translated = TranslationEngine.getInstance().translateTextComponent(original);
        if (translated != original) {
            return new SubtitleS2CPacket(translated);
        }
        return packet;
    }

    @ModifyVariable(
            method = "onOverlayMessage",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private OverlayMessageS2CPacket modifyOverlayMessagePacket(OverlayMessageS2CPacket packet) {
        if (!ConfigManager.getConfig().enabled || !ConfigManager.getConfig().translateActionBar || packet == null) {
            return packet;
        }
        Text original = packet.text();
        if (original == null || WynnFontShield.containsPuaGlyphs(original.getString())) {
            return packet; // Do not touch Action Bar messages that contain custom font textures / negative spaces
        }

        Text translated = TranslationEngine.getInstance().translateTextComponent(original);
        if (translated != original) {
            return new OverlayMessageS2CPacket(translated);
        }
        return packet;
    }
}
