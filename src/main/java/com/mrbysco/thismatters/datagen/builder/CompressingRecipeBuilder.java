package com.mrbysco.thismatters.datagen.builder;

import com.google.gson.JsonObject;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CompressingRecipeBuilder implements RecipeBuilder {
	private final Item result;
	private final Ingredient ingredient;
	private final int compressingTime;
	private final Advancement.Builder advancement = Advancement.Builder.advancement();
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

	public CompressingRecipeBuilder unlockedBy(String id, CriterionTriggerInstance triggerInstance) {
		this.advancement.addCriterion(id, triggerInstance);
		return this;
	}

	public CompressingRecipeBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	public Item getResult() {
		return this.result;
	}

	public void save(Consumer<FinishedRecipe> recipeConsumer, ResourceLocation location) {
		this.ensureValid(location);
		this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe",
				RecipeUnlockedTrigger.unlocked(location)).rewards(AdvancementRewards.Builder.recipe(location)).requirements(RequirementsStrategy.OR);
		recipeConsumer.accept(new CompressingRecipeBuilder.Result(location, this.group == null ? "" : this.group,
				this.ingredient, this.result, this.compressingTime,
				this.advancement, new ResourceLocation(location.getNamespace(), "recipes/" + RecipeCategory.MISC.getFolderName() + "/" + location.getPath())));
	}

	private void ensureValid(ResourceLocation location) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + location);
		}
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final String group;
		private final Ingredient ingredient;
		private final Item result;
		private final int compressingTime;
		private final Advancement.Builder advancement;
		private final ResourceLocation advancementId;

		public Result(ResourceLocation id, String group, Ingredient input, Item result, int compressingTime, Advancement.Builder advancement, ResourceLocation advancementID) {
			this.id = id;
			this.group = group;
			this.ingredient = input;
			this.result = result;
			this.compressingTime = compressingTime;
			this.advancement = advancement;
			this.advancementId = advancementID;
		}

		public void serializeRecipeData(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			jsonObject.add("ingredient", this.ingredient.toJson());
			jsonObject.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
			jsonObject.addProperty("compressingtime", this.compressingTime);
		}

		public RecipeSerializer<?> getType() {
			return ThisRecipes.ORGANIC_MATTER_COMPRESSION_SERIALIZER.get();
		}

		public ResourceLocation getId() {
			return this.id;
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			return this.advancement.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advancementId;
		}
	}
}