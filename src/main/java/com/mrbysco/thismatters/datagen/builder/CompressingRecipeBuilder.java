package com.mrbysco.thismatters.datagen.builder;

import com.mrbysco.thismatters.recipe.CompressingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompressingRecipeBuilder implements RecipeBuilder {
	private final Item result;
	private final Ingredient ingredient;
	private final int compressingTime;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	@Nullable
	private String group;

	private CompressingRecipeBuilder(ItemLike result, Ingredient input, int compressingTime) {
		this.result = result.asItem();
		this.ingredient = input;
		this.compressingTime = compressingTime;
	}

	public static CompressingRecipeBuilder compressing(Ingredient input, ItemLike result, int compressingTime) {
		return new CompressingRecipeBuilder(result, input, compressingTime);
	}

	public CompressingRecipeBuilder unlockedBy(String id, Criterion<?> criterion) {
		this.criteria.put(id, criterion);
		return this;
	}

	public CompressingRecipeBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	public Item getResult() {
		return this.result;
	}

	public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> recipeResourceKey) {
		this.ensureValid(recipeResourceKey);
		Advancement.Builder requirements = recipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeResourceKey))
				.rewards(AdvancementRewards.Builder.recipe(recipeResourceKey))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(requirements::addCriterion);
		CompressingRecipe recipe = new CompressingRecipe(this.group == null ? "" : this.group, this.ingredient, new ItemStack(result), this.compressingTime);
		recipeOutput.accept(recipeResourceKey, recipe, requirements.build(recipeResourceKey.location().withPrefix("recipes/misc/")));
	}

	private void ensureValid(ResourceKey<Recipe<?>> recipe) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipe.location());
		}
	}

}