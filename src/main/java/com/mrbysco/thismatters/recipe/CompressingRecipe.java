package com.mrbysco.thismatters.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mrbysco.thismatters.registry.ThisRecipeSerializers;
import com.mrbysco.thismatters.registry.ThisRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class CompressingRecipe implements Recipe<Container> {
	protected final ResourceLocation id;
	protected final String group;
	protected final ItemStack result;
	protected final Ingredient ingredient;
	protected final int compressingTime;

	public CompressingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack resultStack, int compressingTime) {
		this.id = id;
		this.group = group;
		this.ingredient = ingredient;
		this.result = resultStack;
		this.compressingTime = compressingTime;
	}

	public boolean matches(Container container, Level level) {
		return this.ingredient.test(container.getItem(0));
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

	public ItemStack getResultItem() {
		return this.result;
	}

	public String getGroup() {
		return this.group;
	}

	public int getCompressingTime() {
		return this.compressingTime;
	}

	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ThisRecipeSerializers.ORGANIC_MATTER_COMPRESSION_SERIALIZER.get();
	}

	public RecipeType<?> getType() {
		return ThisRecipeTypes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CompressingRecipe> {
		@Override
		public CompressingRecipe fromJson(ResourceLocation recipeId, JsonObject jsonObject) {
			String s = GsonHelper.getAsString(jsonObject, "group", "");
			JsonElement jsonelement = (JsonElement) (GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.getAsJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
			Ingredient ingredient = Ingredient.fromJson(jsonelement);
			//Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
			if (!jsonObject.has("result"))
				throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
			ItemStack itemstack;
			if (jsonObject.get("result").isJsonObject())
				itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
			else {
				String s1 = GsonHelper.getAsString(jsonObject, "result");
				ResourceLocation resourcelocation = new ResourceLocation(s1);
				itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
					return new IllegalStateException("Item: " + s1 + " does not exist");
				}));
			}
			int i = GsonHelper.getAsInt(jsonObject, "compressingtime", 900);
			return new CompressingRecipe(recipeId, s, ingredient, itemstack, i);
		}

		@Nullable
		@Override
		public CompressingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			ItemStack itemstack = buffer.readItem();
			int compressingTime = buffer.readVarInt();
			return new CompressingRecipe(recipeId, s, ingredient, itemstack, compressingTime);
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
