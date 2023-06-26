package com.mrbysco.thismatters.util;

import com.mrbysco.thismatters.recipe.MatterRecipe;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
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
			for (MatterRecipe matterRecipe : matterRecipeList) {
				List<ItemStack> ingredientList = matterMap.getOrDefault(matterRecipe.getMatterAmount(), new ArrayList<>());
				for (Ingredient ingredient : matterRecipe.getIngredients()) {
					ingredientList.addAll(Arrays.asList(ingredient.getItems()));
				}
				Collections.shuffle(ingredientList);

				matterMap.put(matterRecipe.getMatterAmount(), ingredientList);
			}

			for (Item item : ForgeRegistries.ITEMS.getValues()) {
				if (item instanceof BlockItem blockItem) {
					Block block = blockItem.getBlock();
					BlockState state = block.defaultBlockState();
					int defaultValue = 0;

					if (state.is(BlockTags.WARPED_STEMS)) {
						defaultValue = 5;
					} else if (block instanceof CactusBlock || block instanceof TallGrassBlock || state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS) ||
							block instanceof StemGrownBlock || block instanceof CarvedPumpkinBlock) {
						defaultValue = 4;
					} else if (state.is(BlockTags.LEAVES) || state.is(BlockTags.CROPS) || state.is(BlockTags.REPLACEABLE_BY_TREES) ||
							block instanceof SeagrassBlock || block instanceof WebBlock || state.is(BlockTags.WOOL) || block instanceof CakeBlock) {
						defaultValue = 3;
					} else if (state.is(BlockTags.WOOL_CARPETS)) {
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
