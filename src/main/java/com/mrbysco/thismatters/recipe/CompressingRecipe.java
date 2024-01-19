package com.mrbysco.thismatters.recipe;

import com.mojang.serialization.Codec;
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

public class CompressingRecipe implements Recipe<Container> {
	protected final String group;
	protected final ItemStack result;
	protected final Ingredient ingredient;
	protected final int compressingTime;

	public CompressingRecipe(String group, Ingredient ingredient, ItemStack resultStack, int compressingTime) {
		this.group = group;
		this.ingredient = ingredient;
		this.result = resultStack;
		this.compressingTime = compressingTime;
	}

	public boolean matches(Container container, Level level) {
		return this.ingredient.test(container.getItem(0));
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess registryAccess) {
		return getResultItem(registryAccess).copy();
	}

	public ItemStack assemble(Container container) {
		return this.result.copy();
	}

	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(this.ingredient);
		return nonnulllist;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return this.result;
	}

	public String getGroup() {
		return this.group;
	}

	public int getCompressingTime() {
		return this.compressingTime;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ThisRecipes.ORGANIC_MATTER_COMPRESSION_SERIALIZER.get();
	}

	public RecipeType<?> getType() {
		return ThisRecipes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE.get();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static class Serializer implements RecipeSerializer<CompressingRecipe> {
		public static final Codec<CompressingRecipe> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
								ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
								Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
								ItemStack.SINGLE_ITEM_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
								Codec.INT.optionalFieldOf("compressingtime", 900).forGetter(recipe -> recipe.compressingTime)
						)
						.apply(instance, CompressingRecipe::new)
		);

		@Override
		public Codec<CompressingRecipe> codec() {
			return CODEC;
		}

		@Nullable
		@Override
		public CompressingRecipe fromNetwork(FriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			ItemStack itemstack = buffer.readItem();
			int compressingTime = buffer.readVarInt();
			return new CompressingRecipe(s, ingredient, itemstack, compressingTime);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, CompressingRecipe recipe) {
			buffer.writeUtf(recipe.group);
			recipe.ingredient.toNetwork(buffer);
			buffer.writeItem(recipe.result);
			buffer.writeVarInt(recipe.compressingTime);
		}
	}
}
