package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.recipe.CompressingRecipe.Serializer;
import com.mrbysco.thismatters.recipe.MatterRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ThisRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ThisMatters.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ThisMatters.MOD_ID);

	public static final RegistryObject<RecipeType<CompressingRecipe>> ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE = RECIPE_TYPES.register("organic_matter_compression", () -> new RecipeType<>() {
	});
	public static final RegistryObject<RecipeType<MatterRecipe>> MATTER_RECIPE_TYPE = RECIPE_TYPES.register("matter_recipe", () -> new RecipeType<>() {
	});

	public static final RegistryObject<Serializer> ORGANIC_MATTER_COMPRESSION_SERIALIZER = RECIPE_SERIALIZERS.register("organic_matter_compression", CompressingRecipe.Serializer::new);
	public static final RegistryObject<MatterRecipe.Serializer> MATTER_SERIALIZER = RECIPE_SERIALIZERS.register("matter_recipe", MatterRecipe.Serializer::new);
}
