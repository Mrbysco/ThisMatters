package com.mrbysco.thismatters.compat.jei;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.compat.jei.compressing.OrganicMatterCompressingCategory;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.recipe.MatterRecipeCache;
import com.mrbysco.thismatters.registry.ThisRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class JeiCompat implements IModPlugin {
	public static final Identifier PLUGIN_UID = Identifier.fromNamespaceAndPath(ThisMatters.MOD_ID, "main");

	public static final IRecipeType<CompressingRecipe> ORGANIC_MATTER_COMPRESSING_TYPE = IRecipeType.create(
			ThisMatters.MOD_ID, "organic_matter_compressing", CompressingRecipe.class);

	@Nullable
	private IRecipeCategory<CompressingRecipe> compressingCategory;

	@Override
	public Identifier getPluginUid() {
		return PLUGIN_UID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(ORGANIC_MATTER_COMPRESSING_TYPE, new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()));
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		compressingCategory = new OrganicMatterCompressingCategory(guiHelper);
		registration.addRecipeCategories(compressingCategory);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		var recipes = MatterRecipeCache.getCompressingRecipes().stream().map(RecipeHolder::value).toList();
		registration.addRecipes(ORGANIC_MATTER_COMPRESSING_TYPE, recipes);
	}
}
