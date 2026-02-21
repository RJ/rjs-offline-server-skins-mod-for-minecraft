package com.metabrew.offlineserverskins;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;

public final class OfflineSkinsService {
	private static final long DEFAULT_CACHE_TTL_MILLIS = 5 * 60 * 1000L;

	private final OfflineSkinsConfig config;
	private final TexturesPayloadBuilder payloadBuilder;
	private final TexturesCache cache;
	private final Logger logger;

	public OfflineSkinsService(OfflineSkinsConfig config, Logger logger) {
		this.config = Objects.requireNonNull(config, "config");
		this.logger = Objects.requireNonNull(logger, "logger");
		this.payloadBuilder = new TexturesPayloadBuilder();
		this.cache = new TexturesCache(DEFAULT_CACHE_TTL_MILLIS);
	}

	public GameProfile applyTexturesProperty(GameProfile profile) {
		if (profile == null || profile.name() == null) {
			return profile;
		}

		String resolvedUrl = payloadBuilder.resolveUrl(config.skinUrlTemplate(), profile.name());
		OfflineSkinsConfig.SkinModel model = config.defaultModel();
		TexturesCache.Key cacheKey = new TexturesCache.Key(
			profile.name(),
			uuidString(profile.id()),
			model.id().toLowerCase(Locale.ROOT),
			resolvedUrl
		);

		String encodedPayload = cache.getOrCompute(
			cacheKey,
			() -> payloadBuilder.buildEncodedPayload(profile, resolvedUrl, model)
		);

		Multimap<String, Property> copiedProperties = LinkedHashMultimap.create(profile.properties());
		copiedProperties.removeAll("textures");
		copiedProperties.put("textures", new Property("textures", encodedPayload));
		PropertyMap mutableProperties = new PropertyMap(copiedProperties);
		logger.info("Applying offline skin for {} using URL {}", profile.name(), resolvedUrl);
		return new GameProfile(profile.id(), profile.name(), mutableProperties);
	}

	private static String uuidString(UUID uuid) {
		return uuid == null ? "" : uuid.toString();
	}
}
