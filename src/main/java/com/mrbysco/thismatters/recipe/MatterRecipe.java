package com.mrbysco.thismatters.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MatterRecipe implements Recipe<RecipeInput> {
	public static final MapCodec<MatterRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
							Codec.lazyInitialized(Ingredient.CODEC::listOf)
									.fieldOf("ingredients")
									.forGetter(matterRecipe -> matterRecipe.ingredients),
							Codec.INT.optionalFieldOf("matter", 1).forGetter(recipe -> recipe.matterAmount)
					)
					.apply(instance, MatterRecipe::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, MatterRecipe> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			recipe -> recipe.group,
			Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
			recipe -> recipe.ingredients,
			ByteBufCodecs.INT,
			recipe -> recipe.matterAmount,
			MatterRecipe::new
	);
	public static final RecipeSerializer<MatterRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);


	protected final String group;
	protected final List<Ingredient> ingredients;
	protected final ItemStackTemplate result;
	protected final int matterAmount;
	@Nullable
	private PlacementInfo placementInfo;

	public MatterRecipe(String group, List<Ingredient> ingredients, int matterAmount) {
		this.group = group;
		this.ingredients = ingredients;
		this.result = null;
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
	public ItemStack assemble(RecipeInput input) {
		return getResult().copy();
	}

	public ItemStack getResult() {
		return ItemStack.EMPTY;
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

	@Override
	public boolean showNotification() {
		return false;
	}
}
