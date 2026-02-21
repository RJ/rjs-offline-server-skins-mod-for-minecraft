package com.metabrew.offlineserverskins.mixin.client;

import java.util.UUID;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.texture.PlayerSkinProvider$1")
public class PlayerSkinProviderLoadHandlerMixin {
	@Redirect(
		method = "method_65882",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
		)
	)
	private static void rjsOfflineServerSkins$clean404Log(Logger logger, String message, Object arg1, Object arg2) {
		if (arg2 instanceof Throwable throwable) {
			String missingUrl = find404Url(throwable);
			if (missingUrl != null) {
				String profileId = arg1 instanceof UUID uuid ? uuid.toString() : String.valueOf(arg1);
				logger.warn("Custom skin URL returned 404 for profile {}: {} (using default skin)", profileId, missingUrl);
				return;
			}
		}

		logger.warn(message, arg1, arg2);
	}

	private static String find404Url(Throwable throwable) {
		Throwable current = throwable;
		while (current != null) {
			String text = current.getMessage();
			if (text != null && text.contains("HTTP error code: 404")) {
				String prefix = "Failed to open ";
				int prefixIndex = text.indexOf(prefix);
				int suffixIndex = text.indexOf(", HTTP error code: 404");
				if (prefixIndex >= 0 && suffixIndex > prefixIndex) {
					return text.substring(prefixIndex + prefix.length(), suffixIndex);
				}
				return "<unknown-url>";
			}
			current = current.getCause();
		}
		return null;
	}
}
