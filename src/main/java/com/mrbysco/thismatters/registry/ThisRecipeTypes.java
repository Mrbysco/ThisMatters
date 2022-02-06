package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.recipe.MatterRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ThisRecipeTypes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ThisMatters.MOD_ID);

	public static final RecipeType<CompressingRecipe> ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE = RecipeType.register(new ResourceLocation(ThisMatters.MOD_ID, "organic_matter_compression").toString());
	public static final RecipeType<MatterRecipe> MATTER_RECIPE_TYPE = RecipeType.register(new ResourceLocation(ThisMatters.MOD_ID, "matter_recipe").toString());

	public static final RegistryObject<CompressingRecipe.Serializer> ORGANIC_MATTER_COMPRESSION_SERIALIZER = RECIPE_SERIALIZERS.register("organic_matter_compression", CompressingRecipe.Serializer::new);
	public static final RegistryObject<MatterRecipe.Serializer> MATTER_SERIALIZER = RECIPE_SERIALIZERS.register("matter_recipe", MatterRecipe.Serializer::new);
}
