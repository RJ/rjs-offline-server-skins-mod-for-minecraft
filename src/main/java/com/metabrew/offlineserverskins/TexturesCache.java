package com.metabrew.offlineserverskins;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class TexturesCache {
	private final long ttlMillis;
	private final Map<Key, Entry> entries = new ConcurrentHashMap<>();

	public TexturesCache(long ttlMillis) {
		if (ttlMillis <= 0L) {
			throw new IllegalArgumentException("ttlMillis must be > 0");
		}
		this.ttlMillis = ttlMillis;
	}

	public String getOrCompute(Key key, Supplier<String> supplier) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(supplier, "supplier");

		long now = System.currentTimeMillis();
		Entry existing = entries.get(key);
		if (existing != null && existing.expiresAtMillis > now) {
			return existing.value;
		}

		String value = supplier.get();
		entries.put(key, new Entry(value, now + ttlMillis));
		return value;
	}

	public record Key(String playerName, String uuid, String model, String url) {}

	private record Entry(String value, long expiresAtMillis) {}
}
