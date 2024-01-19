package com.mrbysco.thismatters.compat.jei;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.compat.jei.compressing.OrganicMatterCompressingCategory;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.registry.ThisRecipes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import javax.annotation.Nullable;
import java.util.Objects;

@JeiPlugin
public class JeiCompat implements IModPlugin {
	public static final ResourceLocation PLUGIN_UID = new ResourceLocation(ThisMatters.MOD_ID, "main");

	public static final ResourceLocation ORGANIC_MATTER_COMPRESSING = new ResourceLocation(ThisMatters.MOD_ID, "organic_matter_compressing");
	public static final RecipeType<CompressingRecipe> ORGANIC_MATTER_COMPRESSING_TYPE = RecipeType.create(ThisMatters.MOD_ID, "organic_matter_compressing", CompressingRecipe.class);

	@Nullable
	private IRecipeCategory<CompressingRecipe> compressingCategory;

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_UID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()), ORGANIC_MATTER_COMPRESSING_TYPE);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registration.addRecipeCategories(
				compressingCategory = new OrganicMatterCompressingCategory(guiHelper)
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		assert ORGANIC_MATTER_COMPRESSING_TYPE != null;

		ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
		var recipes = world.getRecipeManager().getAllRecipesFor(ThisRecipes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
		registration.addRecipes(ORGANIC_MATTER_COMPRESSING_TYPE, recipes);
	}
}
