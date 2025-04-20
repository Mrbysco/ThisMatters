package com.mrbysco.thismatters.recipe;

import com.mrbysco.thismatters.blockentity.OrganicMatterCompressorBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatterRecipeCache {
	public static final List<RecipeHolder<CompressingRecipe>> compressingRecipes = new ArrayList<>();
	public static final List<RecipeHolder<MatterRecipe>> matterRecipes = new ArrayList<>();

	public static Optional<RecipeHolder<CompressingRecipe>> getCompressingRecipe(SingleRecipeInput singleRecipeInput, Level level) {
		return compressingRecipes.stream()
				.filter(recipe -> recipe.value().matches(singleRecipeInput, level))
				.findFirst();
	}

	public static int getMatterValue(ItemStack stack, Level level) {
		int itemID = Item.getId(stack.getItem());
		if (OrganicMatterCompressorBlockEntity.cachedValues.containsKey(itemID)) {
			return OrganicMatterCompressorBlockEntity.cachedValues.get(itemID);
		}
		int value = matterRecipes.stream().filter(recipe -> recipe.value().matches(new SingleRecipeInput(stack), level)).findFirst()
				.map(holder -> holder.value().getMatterAmount()).orElse(OrganicMatterCompressorBlockEntity.getDefaultMatterValue(stack));
		OrganicMatterCompressorBlockEntity.cachedValues.put(itemID, value);
		return value;
	}

	public static Optional<RecipeHolder<MatterRecipe>> getMatterRecipe(SingleRecipeInput singleRecipeInput, Level level) {
		return matterRecipes.stream()
				.filter(recipe -> recipe.value().matches(singleRecipeInput, level))
				.findFirst();
	}

	public static void clear() {
		compressingRecipes.clear();
		matterRecipes.clear();
	}
}
