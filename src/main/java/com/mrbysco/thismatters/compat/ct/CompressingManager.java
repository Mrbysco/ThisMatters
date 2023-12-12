//package com.mrbysco.thismatters.compat.ct;
//
//import com.blamejared.crafttweaker.api.CraftTweakerAPI;
//import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
//import com.blamejared.crafttweaker.api.annotation.ZenRegister;
//import com.blamejared.crafttweaker.api.ingredient.IIngredient;
//import com.blamejared.crafttweaker.api.item.IItemStack;
//import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
//import com.mrbysco.thismatters.recipe.CompressingRecipe;
//import com.mrbysco.thismatters.registry.ThisRecipes;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.RecipeType;
//import org.openzen.zencode.java.ZenCodeType.Method;
//import org.openzen.zencode.java.ZenCodeType.Name;
//
//@ZenRegister
//@Name("mods.thismatters.CompressingManager")
//public class CompressingManager implements IRecipeManager<CompressingRecipe> {
//
//	public static final CompressingManager INSTANCE = new CompressingManager();
//
//	private CompressingManager() {
//	}
//
//	@Method
//	public void addCompressing(String name, IIngredient ingredient, IItemStack resultStack, int time) {
//		final ResourceLocation id = new ResourceLocation("crafttweaker", name);
//		final Ingredient foodIngredient = ingredient.asVanillaIngredient();
//		final ItemStack resultItemStack = resultStack.getInternal();
//		final CompressingRecipe recipe = new CompressingRecipe(id, "", foodIngredient, resultItemStack, time);
//		CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe));
//	}
//
//	@Override
//	public RecipeType<CompressingRecipe> getRecipeType() {
//		return ThisRecipes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE.get();
//	}
//}
