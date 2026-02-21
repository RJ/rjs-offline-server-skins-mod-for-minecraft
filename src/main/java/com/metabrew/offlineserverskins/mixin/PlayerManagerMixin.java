package com.metabrew.offlineserverskins.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.metabrew.offlineserverskins.RJsOfflineServerSkins;
import com.mojang.authlib.properties.Property;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(method = "onPlayerConnect", at = @At("HEAD"))
	private void rjsOfflineServerSkins$logFinalProfile(
		ClientConnection connection,
		ServerPlayerEntity player,
		ConnectedClientData clientData,
		CallbackInfo ci
	) {
		Collection<Property> textures = player.getGameProfile().properties().get("textures");
		if (textures.isEmpty()) {
			RJsOfflineServerSkins.LOGGER.warn("No textures property present for {} at onPlayerConnect", player.getNameForScoreboard());
			return;
		}

		Property first = textures.iterator().next();
		String url = extractSkinUrl(first.value());
		RJsOfflineServerSkins.LOGGER.info(
			"Final profile textures for {} at onPlayerConnect -> {}",
			player.getNameForScoreboard(),
			url == null ? "<unreadable>" : url
		);
	}

	private static String extractSkinUrl(String encodedTextures) {
		try {
			String json = new String(Base64.getDecoder().decode(encodedTextures), StandardCharsets.UTF_8);
			JsonObject root = JsonParser.parseString(json).getAsJsonObject();
			JsonObject textures = root.getAsJsonObject("textures");
			if (textures == null) {
				return null;
			}
			JsonObject skin = textures.getAsJsonObject("SKIN");
			if (skin == null || !skin.has("url")) {
				return null;
			}
			return skin.get("url").getAsString();
		} catch (Exception ignored) {
			return null;
		}
	}
}
