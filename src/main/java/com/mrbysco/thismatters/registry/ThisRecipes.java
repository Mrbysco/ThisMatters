package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.recipe.MatterRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ThisRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ThisMatters.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ThisMatters.MOD_ID);

	public static final Supplier<RecipeType<CompressingRecipe>> ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE = RECIPE_TYPES.register("organic_matter_compression", () -> new RecipeType<>() {
	});
	public static final Supplier<RecipeType<MatterRecipe>> MATTER_RECIPE_TYPE = RECIPE_TYPES.register("matter_recipe", () -> new RecipeType<>() {
	});

	public static final Supplier<CompressingRecipe.Serializer> ORGANIC_MATTER_COMPRESSION_SERIALIZER = RECIPE_SERIALIZERS.register("organic_matter_compression", CompressingRecipe.Serializer::new);
	public static final Supplier<MatterRecipe.Serializer> MATTER_SERIALIZER = RECIPE_SERIALIZERS.register("matter_recipe", MatterRecipe.Serializer::new);
}
