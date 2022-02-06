package com.mrbysco.thismatters.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mrbysco.thismatters.registry.ThisRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class MatterRecipe implements Recipe<Container> {
	protected final ResourceLocation id;
	protected final String group;
	protected final ItemStack result;
	protected final NonNullList<Ingredient> ingredients;
	protected final int matterAmount;

	public MatterRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, int compressingTime) {
		this.id = id;
		this.group = group;
		this.ingredients = ingredients;
		this.result = ItemStack.EMPTY;
		this.matterAmount = compressingTime;
	}

	public boolean matches(Container container, Level level) {
		java.util.List<ItemStack> inputs = new java.util.ArrayList<>();

		for(int j = 0; j < container.getContainerSize(); ++j) {
			ItemStack itemstack = container.getItem(j);
			if (!itemstack.isEmpty()) {
				inputs.add(itemstack);
			}
		}

		return (net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, this.ingredients) != null);
	}

	public ItemStack assemble(Container container) {
		return this.result.copy();
	}

	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public NonNullList<Ingredient> getIngredients() {
		return this.ingredients;
	}

	public ItemStack getResultItem() {
		return this.result;
	}

	public String getGroup() {
		return this.group;
	}

	public int getMatterAmount() {
		return this.matterAmount;
	}

	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ThisRecipeTypes.MATTER_SERIALIZER.get();
	}

	public RecipeType<?> getType() {
		return ThisRecipeTypes.MATTER_RECIPE_TYPE;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MatterRecipe> {
		@Override
		public MatterRecipe fromJson(ResourceLocation recipeId, JsonObject jsonObject) {
			String s = GsonHelper.getAsString(jsonObject, "group", "");
			NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
			if (nonnulllist.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else {
				int matterValue = GsonHelper.getAsInt(jsonObject, "matter", 1);
				return new MatterRecipe(recipeId, s, nonnulllist, matterValue);
			}
		}

		private static NonNullList<Ingredient> itemsFromJson(JsonArray jsonArray) {
			NonNullList<Ingredient> nonnulllist = NonNullList.create();

			for(int i = 0; i < jsonArray.size(); ++i) {
				Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
				if (!ingredient.isEmpty()) {
					nonnulllist.add(ingredient);
				}
			}

			return nonnulllist;
		}

		@Nullable
		@Override
		public MatterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			String s = buffer.readUtf();
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

			for(int j = 0; j < nonnulllist.size(); ++j) {
				nonnulllist.set(j, Ingredient.fromNetwork(buffer));
			}

			int matterValue = buffer.readVarInt();
			return new MatterRecipe(recipeId, s, nonnulllist, matterValue);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, MatterRecipe recipe) {
			buffer.writeUtf(recipe.group);
			buffer.writeVarInt(recipe.ingredients.size());

			for(Ingredient ingredient : recipe.ingredients) {
				ingredient.toNetwork(buffer);
			}

			buffer.writeVarInt(recipe.matterAmount);
		}
	}
}
