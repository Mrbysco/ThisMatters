package com.mrbysco.thismatters;

import com.mrbysco.thismatters.client.ClientHandler;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.registry.ThisMenus;
import com.mrbysco.thismatters.registry.ThisRecipes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThisMatters.MOD_ID)
public class ThisMatters {
	public static final String MOD_ID = "thismatters";
	public static final Logger LOGGER = LogManager.getLogger();

	public ThisMatters(IEventBus eventBus, Dist dist, ModContainer container) {
		container.registerConfig(Type.COMMON, ThisConfig.commonSpec);
		eventBus.register(ThisConfig.class);

		eventBus.addListener(ThisRegistry::registerCapabilities);

		ThisRegistry.BLOCKS.register(eventBus);
		ThisRegistry.BLOCK_ENTITY_TYPES.register(eventBus);
		ThisRegistry.ITEMS.register(eventBus);
		ThisRegistry.CREATIVE_MODE_TABS.register(eventBus);
		ThisRecipes.RECIPE_TYPES.register(eventBus);
		ThisRecipes.RECIPE_SERIALIZERS.register(eventBus);
		ThisMenus.MENU_TYPES.register(eventBus);

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			eventBus.addListener(ClientHandler::onRegisterMenu);
		}
	}
}
