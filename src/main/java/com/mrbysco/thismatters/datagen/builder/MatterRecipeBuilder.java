package com.mrbysco.thismatters.datagen.builder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrbysco.thismatters.registry.ThisRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class MatterRecipeBuilder implements MatterBuilder {
	private final ResourceLocation name;
	private final int matterAmount;
	private final List<Ingredient> ingredients = Lists.newArrayList();
	@Nullable
	private String group;

	public MatterRecipeBuilder(ResourceLocation name, int matterAmount) {
		this.name = name;
		this.matterAmount = matterAmount;
	}

	public static MatterRecipeBuilder matter(ResourceLocation location, int matterAmount) {
		return new MatterRecipeBuilder(location, matterAmount);
	}

	public MatterRecipeBuilder requires(Tag<Item> itemTag) {
		return this.requires(Ingredient.of(itemTag));
	}

	public MatterRecipeBuilder requires(ItemLike itemLike) {
		return this.requires(itemLike, 1);
	}

	public MatterRecipeBuilder requires(ItemLike itemLike, int count) {
		for(int i = 0; i < count; ++i) {
			this.requires(Ingredient.of(itemLike));
		}

		return this;
	}

	public MatterRecipeBuilder requires(Ingredient ingredient) {
		return this.requires(ingredient, 1);
	}

	public MatterRecipeBuilder requires(Ingredient ingredient, int count) {
		for(int i = 0; i < count; ++i) {
			this.ingredients.add(ingredient);
		}

		return this;
	}

	public MatterRecipeBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	@Override
	public ResourceLocation getName() {
		return name;
	}

	public void save(Consumer<FinishedRecipe> recipeConsumer, ResourceLocation id) {
		recipeConsumer.accept(new MatterRecipeBuilder.Result(id, this.matterAmount, this.group == null ? "" : this.group, this.ingredients));
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final int matterAmount;
		private final String group;
		private final List<Ingredient> ingredients;

		public Result(ResourceLocation id, int matterAmount, String group, List<Ingredient> ingredients) {
			this.id = id;
			this.matterAmount = matterAmount;
			this.group = group;
			this.ingredients = ingredients;
		}

		public void serializeRecipeData(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			JsonArray jsonarray = new JsonArray();

			for(Ingredient ingredient : this.ingredients) {
				jsonarray.add(ingredient.toJson());
			}

			jsonObject.add("ingredients", jsonarray);
			jsonObject.addProperty("matter", this.matterAmount);
		}

		public RecipeSerializer<?> getType() {
			return ThisRecipeTypes.MATTER_SERIALIZER.get();
		}

		public ResourceLocation getId() {
			return this.id;
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}