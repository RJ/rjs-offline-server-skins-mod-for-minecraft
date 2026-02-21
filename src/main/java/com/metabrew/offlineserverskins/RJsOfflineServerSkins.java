package com.metabrew.offlineserverskins;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RJsOfflineServerSkins implements ModInitializer {
	public static final String MOD_ID = "rjs-offline-server-skins";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static volatile OfflineSkinsService skinsService;

	@Override
	public void onInitialize() {
		try {
			OfflineSkinsConfig config = OfflineSkinsConfig.load(FabricLoader.getInstance().getConfigDir(), LOGGER);
			skinsService = new OfflineSkinsService(config, LOGGER);
			LOGGER.info("Loaded offline skins config from {}", FabricLoader.getInstance().getConfigDir().resolve(OfflineSkinsConfig.FILE_NAME));
		} catch (Exception e) {
			LOGGER.error("Failed to initialize offline skins config. Skin injection is disabled.", e);
			skinsService = null;
		}

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			if (server.isOnlineMode()) {
				LOGGER.error("==============================================================");
				LOGGER.error("{} is running in ONLINE MODE.", MOD_ID);
				LOGGER.error("This mod is intended for OFFLINE-MODE servers and unsigned");
				LOGGER.error("textures will not behave as expected in online mode.");
				LOGGER.error("==============================================================");
			}
		});
	}

	public static GameProfile injectTextures(GameProfile profile) {
		OfflineSkinsService service = skinsService;
		if (service == null) {
			return profile;
		}
		try {
			return service.applyTexturesProperty(profile);
		} catch (Exception e) {
			LOGGER.error("Failed to inject textures for {}", profile == null ? "<null>" : profile.name(), e);
			return profile;
		}
	}
}