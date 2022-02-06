package com.mrbysco.thismatters.client;

import com.mrbysco.thismatters.client.screen.OrganicMatterCompressorScreen;
import com.mrbysco.thismatters.registry.ThisMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		MenuScreens.register(ThisMenus.ORGANIC_MATTER_COMPRESSOR.get(), OrganicMatterCompressorScreen::new);
	}
}
