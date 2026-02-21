package com.metabrew.offlineserverskins.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerSkinProvider.class)
public class PlayerSkinProviderMixin {
	@ModifyVariable(method = "supplySkinTextures", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private boolean rjsOfflineServerSkins$allowUnsignedTextures(boolean requireSecure, GameProfile profile) {
		if (profile == null) {
			return requireSecure;
		}

		if (!profile.properties().get("textures").isEmpty()) {
			return false;
		}

		return requireSecure;
	}
}
