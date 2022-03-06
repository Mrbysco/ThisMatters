package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.recipe.MatterRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class ThisRecipeTypes {
	public static final RecipeType<CompressingRecipe> ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE = RecipeType.register(new ResourceLocation(ThisMatters.MOD_ID, "organic_matter_compression").toString());
	public static final RecipeType<MatterRecipe> MATTER_RECIPE_TYPE = RecipeType.register(new ResourceLocation(ThisMatters.MOD_ID, "matter_recipe").toString());

	public static void init() {
		//For initializing the static final fields
	}
}
