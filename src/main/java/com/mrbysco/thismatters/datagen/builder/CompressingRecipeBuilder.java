package com.mrbysco.thismatters.datagen.builder;

import com.mrbysco.thismatters.recipe.CompressingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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

	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		this.ensureValid(id);
		Advancement.Builder advancement$builder = recipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement$builder::addCriterion);
		CompressingRecipe recipe = new CompressingRecipe(this.group == null ? "" : this.group, this.ingredient, new ItemStack(result), this.compressingTime);
		recipeOutput.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/misc/")));
	}

	private void ensureValid(ResourceLocation location) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + location);
		}
	}
}