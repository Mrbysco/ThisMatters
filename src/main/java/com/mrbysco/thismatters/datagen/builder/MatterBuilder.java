package com.mrbysco.thismatters.datagen.builder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface MatterBuilder {
	MatterBuilder group(@Nullable String group);

	ResourceLocation getName();

	void save(RecipeOutput recipeOutput, ResourceLocation id);

	default void save(RecipeOutput recipeOutput) {
		this.save(recipeOutput, getName());
	}

	default void save(RecipeOutput recipeOutput, String id) {
		ResourceLocation name = getName();
		ResourceLocation location = new ResourceLocation(id);
		if (location.equals(name)) {
			throw new IllegalStateException("Recipe " + id + " should remove its 'save' argument as it is equal to default one");
		} else {
			this.save(recipeOutput, location);
		}
	}
}
