package com.metabrew.offlineserverskins;

import net.fabricmc.api.ClientModInitializer;

public class RJsOfflineServerSkinsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RJsOfflineServerSkins.LOGGER.info("Client-side unsigned skin support is enabled.");
	}
}
