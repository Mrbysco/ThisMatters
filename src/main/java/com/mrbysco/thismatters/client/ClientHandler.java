package com.mrbysco.thismatters.client;

import com.mrbysco.thismatters.client.screen.OrganicMatterCompressorScreen;
import com.mrbysco.thismatters.registry.ThisMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientHandler {
	public static void onRegisterMenu(final RegisterMenuScreensEvent event) {
		event.register(ThisMenus.ORGANIC_MATTER_COMPRESSOR.get(), OrganicMatterCompressorScreen::new);
	}
}
