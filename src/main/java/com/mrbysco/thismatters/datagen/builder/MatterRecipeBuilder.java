package com.mrbysco.thismatters.datagen.builder;

import com.mrbysco.thismatters.recipe.MatterRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

public class MatterRecipeBuilder implements RecipeBuilder {
	private final ResourceLocation name;
	private final int matterAmount;
	private final NonNullList<Ingredient> ingredients = NonNullList.create();
	@Nullable
	private String group;

	public MatterRecipeBuilder(ResourceLocation name, int matterAmount) {
		this.name = name;
		this.matterAmount = matterAmount;
	}

	public static MatterRecipeBuilder matter(ResourceLocation location, int matterAmount) {
		return new MatterRecipeBuilder(location, matterAmount);
	}

	public MatterRecipeBuilder requires(TagKey<Item> itemTag) {
		return this.requires(Ingredient.of(itemTag));
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

	@Override
	public Item getResult() {
		return Items.AIR;
	}

	public MatterRecipeBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		MatterRecipe recipe = new MatterRecipe(this.group == null ? "" : this.group, this.ingredients, matterAmount);
		ResourceLocation usedID = name.equals(id) ? id : name;
		recipeOutput.accept(usedID, recipe, null);
	}
}