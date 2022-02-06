package com.mrbysco.thismatters.datagen.builder;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface MatterBuilder {
	MatterBuilder group(@Nullable String group);

	ResourceLocation getName();

	void save(Consumer<FinishedRecipe> recipeConsumer, ResourceLocation id);

	default void save(Consumer<FinishedRecipe> recipeConsumer) {
		this.save(recipeConsumer, getName());
	}

	default void save(Consumer<FinishedRecipe> recipeConsumer, String id) {
		ResourceLocation name = getName();
		ResourceLocation location = new ResourceLocation(id);
		if (location.equals(name)) {
			throw new IllegalStateException("Recipe " + id + " should remove its 'save' argument as it is equal to default one");
		} else {
			this.save(recipeConsumer, location);
		}
	}
}
