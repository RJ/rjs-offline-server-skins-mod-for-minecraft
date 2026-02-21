package com.metabrew.offlineserverskins.mixin;

import com.metabrew.offlineserverskins.RJsOfflineServerSkins;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ConnectedClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ConnectedClientData.class)
public class ConnectedClientDataMixin {
	@ModifyVariable(method = "createDefault", at = @At("HEAD"), argsOnly = true)
	private static GameProfile rjsOfflineServerSkins$injectCreateDefault(GameProfile profile) {
		return RJsOfflineServerSkins.injectTextures(profile);
	}
}
