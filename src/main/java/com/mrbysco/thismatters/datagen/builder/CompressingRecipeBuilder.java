package com.mrbysco.thismatters.datagen.builder;

import com.google.gson.JsonObject;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

	public void save(RecipeOutput recipeConsumer, ResourceLocation location) {
		this.ensureValid(location);
		Advancement.Builder advancement$builder = recipeConsumer.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location))
				.rewards(AdvancementRewards.Builder.recipe(location))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement$builder::addCriterion);
		recipeConsumer.accept(new CompressingRecipeBuilder.Result(location, this.group == null ? "" : this.group,
				this.ingredient, this.result, this.compressingTime,
				advancement$builder.build(location.withPrefix("recipes/misc/"))));
	}

	private void ensureValid(ResourceLocation location) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + location);
		}
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final Ingredient ingredient;
		private final Item result;
		private final int compressingTime;
		private final AdvancementHolder advancement;

		public Result(ResourceLocation id, String group, Ingredient input, Item result, int compressingTime, AdvancementHolder advancement) {
			this.id = id;
			this.group = group;
			this.ingredient = input;
			this.result = result;
			this.compressingTime = compressingTime;
			this.advancement = advancement;
		}

		public void serializeRecipeData(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			jsonObject.add("ingredient", this.ingredient.toJson(false));
			JsonObject resultObject = new JsonObject();
			resultObject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());

			jsonObject.add("result", resultObject);
			jsonObject.addProperty("compressingtime", this.compressingTime);
		}

		public RecipeSerializer<?> type() {
			return ThisRecipes.ORGANIC_MATTER_COMPRESSION_SERIALIZER.get();
		}

		public ResourceLocation id() {
			return this.id;
		}

		@Nullable
		public AdvancementHolder advancement() {
			return this.advancement;
		}
	}
}