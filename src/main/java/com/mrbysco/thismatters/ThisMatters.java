package com.mrbysco.thismatters;

import com.mrbysco.thismatters.client.ClientHandler;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.registry.ThisMenus;
import com.mrbysco.thismatters.registry.ThisRecipeTypes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThisMatters.MOD_ID)
public class ThisMatters {
    public static final String MOD_ID = "thismatters";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final CreativeModeTab TAB_MAIN = new CreativeModeTab(MOD_ID) {
        public ItemStack makeIcon() {
            return new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get());
        }
    };

    public ThisMatters() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(Type.COMMON, ThisConfig.commonSpec);
        eventBus.register(ThisConfig.class);

        ThisRegistry.BLOCKS.register(eventBus);
        ThisRegistry.BLOCK_ENTITIES.register(eventBus);
        ThisRegistry.ITEMS.register(eventBus);
        ThisRecipeTypes.RECIPE_SERIALIZERS.register(eventBus);
        ThisMenus.MENUS.register(eventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::onClientSetup);
        });
    }
}
