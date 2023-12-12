package com.mrbysco.thismatters.config;

import com.mrbysco.thismatters.ThisMatters;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class ThisConfig {
	public static class Common {
		public final ModConfigSpec.IntValue minY;
		public final ModConfigSpec.IntValue maxMatter;
		public final ModConfigSpec.BooleanValue useDefaults;

		Common(ModConfigSpec.Builder builder) {
			builder.comment("General settings")
					.push("General");

			minY = builder
					.comment("Defines the minimum y level at which the Matter Compressor can work [default: 15]")
					.defineInRange("minY", 15, Integer.MIN_VALUE, Integer.MAX_VALUE);

			maxMatter = builder
					.comment("Defines the maximum amount of matter until the Matter Compressor displays 100% [default: 150]")
					.defineInRange("maxMatter", 150, Integer.MIN_VALUE, Integer.MAX_VALUE);

			useDefaults = builder
					.comment("Use built-in matter checks when no item match is found using recipes [default: true]")
					.define("useDefaults", true);

			builder.pop();
		}
	}

	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		ThisMatters.LOGGER.debug("Loaded This Matter's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		ThisMatters.LOGGER.debug("This Matter's config just got changed on the file system!");
	}
}
