package com.mrbysco.thismatters;

import com.mrbysco.thismatters.client.ClientHandler;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.registry.ThisMenus;
import com.mrbysco.thismatters.registry.ThisRecipes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThisMatters.MOD_ID)
public class ThisMatters {
	public static final String MOD_ID = "thismatters";
	public static final Logger LOGGER = LogManager.getLogger();

	public ThisMatters() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(Type.COMMON, ThisConfig.commonSpec);
		eventBus.register(ThisConfig.class);

		ThisRegistry.BLOCKS.register(eventBus);
		ThisRegistry.BLOCK_ENTITY_TYPES.register(eventBus);
		ThisRegistry.ITEMS.register(eventBus);
		ThisRegistry.CREATIVE_MODE_TABS.register(eventBus);
		ThisRecipes.RECIPE_TYPES.register(eventBus);
		ThisRecipes.RECIPE_SERIALIZERS.register(eventBus);
		ThisMenus.MENU_TYPES.register(eventBus);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
		});
	}
}
