package com.mrbysco.thismatters.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import org.jetbrains.annotations.Nullable;

public class CompressingRecipe implements Recipe<RecipeInput> {
	protected final String group;
	protected final Ingredient ingredient;
	protected final ItemStack result;
	protected final int compressingTime;
	@Nullable
	private PlacementInfo placementInfo;

	public CompressingRecipe(String group, Ingredient ingredient, ItemStack resultStack, int compressingTime) {
		this.group = group;
		this.ingredient = ingredient;
		this.result = resultStack;
		this.compressingTime = compressingTime;
	}

	@Override
	public String group() {
		return this.group;
	}

	@Override
	public boolean matches(RecipeInput input, Level level) {
		return this.ingredient.test(input.getItem(0));
	}

	@Override
	public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
		return this.getResult().copy();
	}

	public ItemStack getResult() {
		return result;
	}

	public int getCompressingTime() {
		return this.compressingTime;
	}

	@Override
	public RecipeSerializer<CompressingRecipe> getSerializer() {
		return ThisRecipes.ORGANIC_MATTER_COMPRESSION_SERIALIZER.get();
	}

	@Override
	public RecipeType<CompressingRecipe> getType() {
		return ThisRecipes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		if (this.placementInfo == null) {
			this.placementInfo = PlacementInfo.create(this.ingredient);
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

	public static class Serializer implements RecipeSerializer<CompressingRecipe> {
		public static final MapCodec<CompressingRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
								Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
								ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
								Codec.INT.optionalFieldOf("compressingtime", 900).forGetter(recipe -> recipe.compressingTime)
						)
						.apply(instance, CompressingRecipe::new)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, CompressingRecipe> STREAM_CODEC = StreamCodec.of(
				CompressingRecipe.Serializer::toNetwork, CompressingRecipe.Serializer::fromNetwork
		);

		@Override
		public MapCodec<CompressingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CompressingRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		public static CompressingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
			ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
			int compressingTime = buffer.readVarInt();
			return new CompressingRecipe(s, ingredient, itemstack, compressingTime);
		}

		public static void toNetwork(RegistryFriendlyByteBuf buffer, CompressingRecipe recipe) {
			buffer.writeUtf(recipe.group);
			Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
			ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
			buffer.writeVarInt(recipe.compressingTime);
		}
	}
}
