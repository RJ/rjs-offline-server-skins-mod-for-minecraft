package com.metabrew.offlineserverskins;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class TexturesPayloadBuilder {
	public String resolveUrl(String template, String playerName) {
		Objects.requireNonNull(template, "template");
		Objects.requireNonNull(playerName, "playerName");
		String encodedName = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
		return template.replace("%name%", encodedName);
	}

	public String buildEncodedPayload(GameProfile profile, String url, OfflineSkinsConfig.SkinModel model) {
		Objects.requireNonNull(profile, "profile");
		Objects.requireNonNull(url, "url");
		Objects.requireNonNull(model, "model");

		JsonObject root = new JsonObject();
		root.addProperty("timestamp", System.currentTimeMillis());
		root.addProperty("profileId", uuidNoDashes(profile.id()));
		root.addProperty("profileName", profile.name());

		JsonObject textures = new JsonObject();
		JsonObject skin = new JsonObject();
		skin.addProperty("url", url);

		if (model == OfflineSkinsConfig.SkinModel.SLIM) {
			JsonObject metadata = new JsonObject();
			metadata.addProperty("model", model.id().toLowerCase(Locale.ROOT));
			skin.add("metadata", metadata);
		}

		textures.add("SKIN", skin);
		root.add("textures", textures);

		String json = root.toString();
		return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
	}

	private static String uuidNoDashes(UUID uuid) {
		return uuid == null ? "" : uuid.toString().replace("-", "");
	}
}
