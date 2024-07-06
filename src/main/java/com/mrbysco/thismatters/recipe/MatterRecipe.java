package com.mrbysco.thismatters.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class MatterRecipe implements Recipe<RecipeInput> {
	protected final String group;
	protected final ItemStack result;
	protected final NonNullList<Ingredient> ingredients;
	protected final int matterAmount;

	public MatterRecipe(String group, NonNullList<Ingredient> ingredients, int matterAmount) {
		this.group = group;
		this.ingredients = ingredients;
		this.result = ItemStack.EMPTY;
		this.matterAmount = matterAmount;
	}

	@Override
	public boolean matches(RecipeInput input, Level level) {
		for (int j = 0; j < input.size(); ++j) {
			ItemStack itemstack = input.getItem(j);
			if (!itemstack.isEmpty()) {
				return this.getIngredients().stream().anyMatch(ingredient -> ingredient.test(itemstack));
			}
		}

		return false;
	}

	@Override
	public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
		return getResultItem(registries).copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return this.result;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	public int getMatterAmount() {
		return this.matterAmount;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ThisRecipes.MATTER_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ThisRecipes.MATTER_RECIPE_TYPE.get();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<MatterRecipe> {
		public static final MapCodec<MatterRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
								Ingredient.CODEC_NONEMPTY
										.listOf()
										.fieldOf("ingredients")
										.flatXmap(
												list -> {
													Ingredient[] aingredient = list
															.toArray(Ingredient[]::new); //Forge skip the empty check and immediatly create the array.
													if (aingredient.length == 0) {
														return DataResult.error(() -> "No ingredients for shapeless recipe");
													} else {
														return DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
													}
												},
												DataResult::success
										)
										.forGetter(recipe -> recipe.ingredients),
								Codec.INT.optionalFieldOf("matter", 1).forGetter(recipe -> recipe.matterAmount)
						)
						.apply(instance, MatterRecipe::new)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, MatterRecipe> STREAM_CODEC = StreamCodec.of(
				MatterRecipe.Serializer::toNetwork, MatterRecipe.Serializer::fromNetwork
		);

		@Override
		public MapCodec<MatterRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MatterRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		public static MatterRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
			nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));

			int matterValue = buffer.readVarInt();
			return new MatterRecipe(s, nonnulllist, matterValue);
		}

		public static void toNetwork(RegistryFriendlyByteBuf buffer, MatterRecipe recipe) {
			buffer.writeUtf(recipe.group);
			buffer.writeVarInt(recipe.ingredients.size());

			for (Ingredient ingredient : recipe.ingredients) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
			}

			buffer.writeVarInt(recipe.matterAmount);
		}
	}
}
