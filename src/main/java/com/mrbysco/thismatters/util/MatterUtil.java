package com.mrbysco.thismatters.util;

import com.mrbysco.thismatters.recipe.MatterRecipe;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MatterUtil {
	public static final List<MatterInfo> matterList = new ArrayList<>();

	public static void reloadMatterList(Level level) {
		if (level != null) {
			final List<MatterRecipe> matterRecipeList = level.getRecipeManager().getAllRecipesFor(ThisRecipes.MATTER_RECIPE_TYPE.get());
			Map<Integer, List<ItemStack>> matterMap = new HashMap<>();
			List<ItemLike> items = new ArrayList<>();
			for (MatterRecipe matterRecipe : matterRecipeList) {
				List<ItemStack> ingredientList = matterMap.getOrDefault(matterRecipe.getMatterAmount(), new ArrayList<>());
				for (Ingredient ingredient : matterRecipe.getIngredients()) {
					ingredientList.addAll(Arrays.asList(ingredient.getItems()));
				}
				ingredientList.forEach((stack) -> items.add(stack.getItem()));
				Collections.shuffle(ingredientList);

				matterMap.put(matterRecipe.getMatterAmount(), ingredientList);
			}

			for (Item item : ForgeRegistries.ITEMS.getValues()) {
				if (item instanceof BlockItem blockItem) {
					final Material material = blockItem.getBlock().defaultBlockState().getMaterial();

					int defaultValue = 0;
					if (material == Material.CACTUS || material == Material.GRASS || material == Material.WOOD ||
							material == Material.VEGETABLE) {
						defaultValue = 4;
					} else if (material == Material.LEAVES || material == Material.PLANT || material == Material.REPLACEABLE_PLANT ||
							material == Material.REPLACEABLE_WATER_PLANT || material == Material.REPLACEABLE_FIREPROOF_PLANT ||
							material == Material.WEB || material == Material.WOOL || material == Material.CAKE) {
						defaultValue = 3;
					} else if (material == Material.CLOTH_DECORATION) {
						defaultValue = 2;
					}

					if (defaultValue > 0) {
						List<ItemStack> ingredientList = matterMap.getOrDefault(defaultValue, new ArrayList<>());
						ingredientList.add(new ItemStack(blockItem));
						matterMap.put(defaultValue, ingredientList);
					}
				}
			}

			if (!matterMap.isEmpty()) {
				matterList.clear();
				for (Entry<Integer, List<ItemStack>> entry : matterMap.entrySet()) {
					matterList.add(new MatterInfo(entry.getKey(), entry.getValue()));
				}
			}
		}
	}

	public record MatterInfo(int matterAmount, List<ItemStack> matterStacks) {
	}
}
