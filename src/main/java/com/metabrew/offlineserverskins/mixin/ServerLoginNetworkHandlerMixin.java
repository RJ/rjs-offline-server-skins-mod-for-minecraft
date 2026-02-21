package com.metabrew.offlineserverskins.mixin;

import com.metabrew.offlineserverskins.RJsOfflineServerSkins;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin {
	@Shadow
	private GameProfile profile;

	@ModifyVariable(method = "startVerify", at = @At("HEAD"), argsOnly = true)
	private GameProfile rjsOfflineServerSkins$injectAtStartVerify(GameProfile profile) {
		GameProfile modified = RJsOfflineServerSkins.injectTextures(profile);
		this.profile = modified;
		return modified;
	}

	@ModifyVariable(method = "sendSuccessPacket", at = @At("HEAD"), argsOnly = true)
	private GameProfile rjsOfflineServerSkins$injectBeforeLoginComplete(GameProfile profile) {
		GameProfile modified = RJsOfflineServerSkins.injectTextures(profile);
		this.profile = modified;
		return modified;
	}
}
