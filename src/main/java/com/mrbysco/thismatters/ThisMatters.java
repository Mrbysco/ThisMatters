package com.mrbysco.thismatters;

import com.mrbysco.thismatters.client.ClientHandler;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.registry.ThisMenus;
import com.mrbysco.thismatters.registry.ThisRecipes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

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
		ThisRecipes.RECIPE_TYPES.register(eventBus);
		ThisRecipes.RECIPE_SERIALIZERS.register(eventBus);
		ThisMenus.MENU_TYPES.register(eventBus);

		eventBus.addListener(this::registerCreativeTabs);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
		});
	}

	private static CreativeModeTab TAB_MAIN;

	private void registerCreativeTabs(final CreativeModeTabEvent.Register event) {
		TAB_MAIN = event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "tab"), builder ->
				builder.icon(() -> new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()))
						.title(Component.translatable("itemGroup.thismatters"))
						.displayItems((features, output, hasPermissions) -> {
							List<ItemStack> stacks = ThisRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
							output.acceptAll(stacks);
						}));
	}
}
