package com.mrbysco.thismatters.compat.jei;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.compat.jei.compressing.OrganicMatterCompressingCategory;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.registry.ThisRecipeTypes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

@JeiPlugin
public class JeiCompat implements IModPlugin {
	public static final ResourceLocation PLUGIN_UID = new ResourceLocation(ThisMatters.MOD_ID, "main");

	public static final ResourceLocation ORGANIC_MATTER_COMPRESSING = new ResourceLocation(ThisMatters.MOD_ID, "organic_matter_compressing");

	@Nullable
	private IRecipeCategory<CompressingRecipe> compressingCategory;

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_UID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()), ORGANIC_MATTER_COMPRESSING);
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
		ErrorUtil.checkNotNull(compressingCategory, "compressingCategory");

		ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
		registration.addRecipes(world.getRecipeManager().getAllRecipesFor(ThisRecipeTypes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE), ORGANIC_MATTER_COMPRESSING);
	}
}
