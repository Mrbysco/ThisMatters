package com.mrbysco.thismatters.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class MatterRecipe implements Recipe<Container> {
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

	public boolean matches(Container container, Level level) {
		for (int j = 0; j < container.getContainerSize(); ++j) {
			ItemStack itemstack = container.getItem(j);
			if (!itemstack.isEmpty()) {
				return this.getIngredients().stream().anyMatch(ingredient -> ingredient.test(itemstack));
			}
		}

		return false;
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess registryAccess) {
		return getResultItem(registryAccess).copy();
	}

	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public NonNullList<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return this.result;
	}

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

	public RecipeType<?> getType() {
		return ThisRecipes.MATTER_RECIPE_TYPE.get();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<MatterRecipe> {
		public static final Codec<MatterRecipe> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
								ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
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

		@Override
		public Codec<MatterRecipe> codec() {
			return CODEC;
		}

		@Nullable
		@Override
		public MatterRecipe fromNetwork(FriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

			for (int j = 0; j < nonnulllist.size(); ++j) {
				nonnulllist.set(j, Ingredient.fromNetwork(buffer));
			}

			int matterValue = buffer.readVarInt();
			return new MatterRecipe(s, nonnulllist, matterValue);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, MatterRecipe recipe) {
			buffer.writeUtf(recipe.group);
			buffer.writeVarInt(recipe.ingredients.size());

			for (Ingredient ingredient : recipe.ingredients) {
				ingredient.toNetwork(buffer);
			}

			buffer.writeVarInt(recipe.matterAmount);
		}
	}
}
