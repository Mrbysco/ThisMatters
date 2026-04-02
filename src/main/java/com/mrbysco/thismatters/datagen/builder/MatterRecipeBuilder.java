package com.mrbysco.thismatters.datagen.builder;

import com.mrbysco.thismatters.recipe.MatterRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class MatterRecipeBuilder implements RecipeBuilder {
	private final HolderGetter<Item> items;
	private final Identifier name;
	private final int matterAmount;
	private final NonNullList<Ingredient> ingredients = NonNullList.create();
	@Nullable
	private String group;

	public MatterRecipeBuilder(HolderGetter<Item> items, Identifier name, int matterAmount) {
		this.items = items;
		this.name = name;
		this.matterAmount = matterAmount;
	}

	public static MatterRecipeBuilder matter(HolderGetter<Item> items, Identifier location, int matterAmount) {
		return new MatterRecipeBuilder(items, location, matterAmount);
	}

	public MatterRecipeBuilder requires(TagKey<Item> itemTag) {
		return this.requires(Ingredient.of(this.items.getOrThrow(itemTag)));
	}

	public MatterRecipeBuilder requires(ItemLike itemLike) {
		return this.requires(itemLike, 1);
	}

	public MatterRecipeBuilder requires(ItemLike itemLike, int count) {
		for (int i = 0; i < count; ++i) {
			this.requires(Ingredient.of(itemLike));
		}

		return this;
	}

	public MatterRecipeBuilder requires(Ingredient ingredient) {
		return this.requires(ingredient, 1);
	}

	public MatterRecipeBuilder requires(Ingredient ingredient, int count) {
		for (int i = 0; i < count; ++i) {
			this.ingredients.add(ingredient);
		}

		return this;
	}

	@Override
	public RecipeBuilder unlockedBy(String id, Criterion<?> criterion) {
		return null;
	}

	public MatterRecipeBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	@Override
	public ResourceKey<Recipe<?>> defaultId() {
		return ResourceKey.create(Registries.RECIPE, name);
	}

	public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> recipeResourceKey) {
		MatterRecipe recipe = new MatterRecipe(this.group == null ? "" : this.group, this.ingredients, matterAmount);
		ResourceKey<Recipe<?>> usedID = name.equals(recipeResourceKey.identifier()) ? recipeResourceKey : ResourceKey.create(Registries.RECIPE, name);
		recipeOutput.accept(usedID, recipe, null);
	}
}