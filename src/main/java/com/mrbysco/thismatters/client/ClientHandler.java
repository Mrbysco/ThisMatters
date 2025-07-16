package com.mrbysco.thismatters.client;

import com.mrbysco.thismatters.client.screen.OrganicMatterCompressorScreen;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.recipe.MatterRecipe;
import com.mrbysco.thismatters.recipe.MatterRecipeCache;
import com.mrbysco.thismatters.registry.ThisMenus;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.Collection;

public class ClientHandler {

	public static void onRegisterMenu(final RegisterMenuScreensEvent event) {
		event.register(ThisMenus.ORGANIC_MATTER_COMPRESSOR.get(), OrganicMatterCompressorScreen::new);
	}

	public static void onRecipeReceived(final RecipesReceivedEvent event) {
		MatterRecipeCache.clear();

		Collection<RecipeHolder<CompressingRecipe>> compressingRecipes = event.getRecipeMap().byType(ThisRecipes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE.get());
		MatterRecipeCache.compressingRecipes.addAll(compressingRecipes);

		Collection<RecipeHolder<MatterRecipe>> matterRecipes = event.getRecipeMap().byType(ThisRecipes.MATTER_RECIPE_TYPE.get());
		MatterRecipeCache.matterRecipes.addAll(matterRecipes);
	}

	public static void onPlayerDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		MatterRecipeCache.clear();
	}
}
