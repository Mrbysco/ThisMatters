package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.recipe.CompressingRecipe.Serializer;
import com.mrbysco.thismatters.recipe.MatterRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ThisRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ThisMatters.MOD_ID);

	public static final RegistryObject<Serializer> ORGANIC_MATTER_COMPRESSION_SERIALIZER = RECIPE_SERIALIZERS.register("organic_matter_compression", CompressingRecipe.Serializer::new);
	public static final RegistryObject<MatterRecipe.Serializer> MATTER_SERIALIZER = RECIPE_SERIALIZERS.register("matter_recipe", MatterRecipe.Serializer::new);
}
