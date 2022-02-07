package com.mrbysco.thismatters.compat.jei.compressing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.compat.jei.JeiCompat;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.registry.ThisRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class OrganicMatterCompressingCategory implements IRecipeCategory<CompressingRecipe> {
	public static final ResourceLocation RECIPE_COMPRESSING_JEI = new ResourceLocation(ThisMatters.MOD_ID, "textures/gui/jei/organic_matter_compressing.png");

	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable slotDrawable;
	private final Component title;

	public OrganicMatterCompressingCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(RECIPE_COMPRESSING_JEI, 0, 0, 112, 58);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()));
		this.title = new TranslatableComponent("thismatters.gui.jei.category.organic_matter_compressing");

		this.slotDrawable = guiHelper.getSlotDrawable();
	}

	@Override
	public ResourceLocation getUid() {
		return JeiCompat.ORGANIC_MATTER_COMPRESSING;
	}

	@Override
	public Class<? extends CompressingRecipe> getRecipeClass() {
		return CompressingRecipe.class;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(CompressingRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());

		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
	}
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CompressingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 94, 1);
		guiItemStacks.init(1, false, 94, 37);

		guiItemStacks.set(ingredients);
	}

	@Override
	public void draw(CompressingRecipe recipe, PoseStack poseStack, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;
		TextComponent component = new TextComponent((int)(recipe.getCompressingTime() / 20f) + "s");
		font.draw(poseStack, component, 76 - font.width(component) / 2, 50, 16777215);

		TranslatableComponent matterComponent = new TranslatableComponent("thismatters.gui.jei.compressing.matter");
		font.draw(poseStack, matterComponent, 18 - font.width(component) / 2, 24, 0xAAAAAA);
	}
}