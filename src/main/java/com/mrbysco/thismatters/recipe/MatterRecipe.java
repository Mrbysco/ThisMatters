package com.mrbysco.thismatters.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MatterRecipe implements Recipe<RecipeInput> {
	protected final String group;
	protected final List<Ingredient> ingredients;
	protected final ItemStack result;
	protected final int matterAmount;
	@Nullable
	private PlacementInfo placementInfo;

	public MatterRecipe(String group, List<Ingredient> ingredients, int matterAmount) {
		this.group = group;
		this.ingredients = ingredients;
		this.result = ItemStack.EMPTY;
		this.matterAmount = matterAmount;
	}

	@Override
	public String group() {
		return this.group;
	}

	@Override
	public boolean matches(RecipeInput input, Level level) {
		for (int j = 0; j < input.size(); ++j) {
			ItemStack itemstack = input.getItem(j);
			if (!itemstack.isEmpty()) {
				return this.ingredients.stream().anyMatch(ingredient -> ingredient.test(itemstack));
			}
		}

		return false;
	}

	@Override
	public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
		return getResult().copy();
	}

	public ItemStack getResult() {
		return result;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public int getMatterAmount() {
		return this.matterAmount;
	}

	@Override
	public RecipeSerializer<MatterRecipe> getSerializer() {
		return ThisRecipes.MATTER_SERIALIZER.get();
	}

	@Override
	public RecipeType<MatterRecipe> getType() {
		return ThisRecipes.MATTER_RECIPE_TYPE.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		if (this.placementInfo == null) {
			this.placementInfo = PlacementInfo.create(this.ingredients);
		}

		return this.placementInfo;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<MatterRecipe> {
		public static final MapCodec<MatterRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
								Codec.lazyInitialized(Ingredient.CODEC::listOf)
										.fieldOf("ingredients")
										.forGetter(p_360071_ -> p_360071_.ingredients),
								Codec.INT.optionalFieldOf("matter", 1).forGetter(recipe -> recipe.matterAmount)
						)
						.apply(instance, MatterRecipe::new)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, MatterRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				p_360074_ -> p_360074_.group,
				Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
				p_360069_ -> p_360069_.ingredients,
				ByteBufCodecs.INT,
				p_360070_ -> p_360070_.matterAmount,
				MatterRecipe::new
		);

		@Override
		public MapCodec<MatterRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MatterRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
