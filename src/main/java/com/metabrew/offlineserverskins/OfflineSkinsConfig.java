package com.metabrew.offlineserverskins;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;

public final class OfflineSkinsConfig {
	public static final String FILE_NAME = "rjs_offline_server_skins.txt";
	private static final String KEY_SKIN_URL_TEMPLATE = "skin_url_template";
	private static final String KEY_DEFAULT_MODEL = "default_model";

	private final String skinUrlTemplate;
	private final SkinModel defaultModel;

	private OfflineSkinsConfig(String skinUrlTemplate, SkinModel defaultModel) {
		this.skinUrlTemplate = skinUrlTemplate;
		this.defaultModel = defaultModel;
	}

	public static OfflineSkinsConfig load(Path configDir, Logger logger) {
		Objects.requireNonNull(configDir, "configDir");
		Objects.requireNonNull(logger, "logger");

		Path configPath = configDir.resolve(FILE_NAME);
		ensureConfigExists(configPath, logger);

		Properties properties = new Properties();
		try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
			properties.load(reader);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read config at " + configPath, e);
		}

		String template = properties.getProperty(KEY_SKIN_URL_TEMPLATE, "").trim();
		if (template.isEmpty()) {
			throw new IllegalStateException(
				"Missing required key '" + KEY_SKIN_URL_TEMPLATE + "' in " + configPath
			);
		}

		String modelString = properties.getProperty(KEY_DEFAULT_MODEL, SkinModel.CLASSIC.id()).trim();
		SkinModel model = SkinModel.fromConfigValue(modelString);

		return new OfflineSkinsConfig(template, model);
	}

	private static void ensureConfigExists(Path configPath, Logger logger) {
		if (Files.exists(configPath)) {
			return;
		}

		try {
			Files.createDirectories(configPath.getParent());
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create config directory for " + configPath, e);
		}

		try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
			for (String line : defaultConfigLines()) {
				writer.write(line);
				writer.write('\n');
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create default config at " + configPath, e);
		}

		logger.info("Created default config at {}", configPath);
	}

	public String skinUrlTemplate() {
		return this.skinUrlTemplate;
	}

	public SkinModel defaultModel() {
		return this.defaultModel;
	}

	private static List<String> defaultConfigLines() {
		return List.of(
			"# RJ's Offline Server Skins config",
			"# Required: supports %name%",
			"# Optional token: %rev% (random cache-busting path segment)",
			"skin_url_template=http://127.0.0.1/skins/%rev%/%name%.png",
			"",
			"# Optional: classic or slim",
			"default_model=classic"
		);
	}

	public enum SkinModel {
		CLASSIC("classic"),
		SLIM("slim");

		private final String id;

		SkinModel(String id) {
			this.id = id;
		}

		public String id() {
			return this.id;
		}

		public static SkinModel fromConfigValue(String raw) {
			String value = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
			if (value.isEmpty() || CLASSIC.id.equals(value)) {
				return CLASSIC;
			}
			if (SLIM.id.equals(value)) {
				return SLIM;
			}
			throw new IllegalStateException(
				"Invalid default_model '" + raw + "'. Expected 'classic' or 'slim'."
			);
		}
	}
}
