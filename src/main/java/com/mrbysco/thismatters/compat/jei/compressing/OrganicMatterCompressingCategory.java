package com.mrbysco.thismatters.compat.jei.compressing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.compat.jei.JeiCompat;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.registry.ThisRegistry;
import com.mrbysco.thismatters.util.MatterUtil;
import com.mrbysco.thismatters.util.MatterUtil.MatterInfo;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OrganicMatterCompressingCategory implements IRecipeCategory<CompressingRecipe> {
	public static final ResourceLocation RECIPE_COMPRESSING_JEI = new ResourceLocation(ThisMatters.MOD_ID, "textures/gui/jei/organic_matter_compressing.png");

	private final IDrawable background;
	private final IDrawable icon;
	private final Component title;

	public OrganicMatterCompressingCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(RECIPE_COMPRESSING_JEI, 0, 0, 112, 58);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()));
		this.title = new TranslatableComponent("thismatters.gui.jei.category.organic_matter_compressing");

		MatterUtil.reloadMatterList(Minecraft.getInstance().level);
	}

	@Override
	public RecipeType<CompressingRecipe> getRecipeType() {
		return JeiCompat.ORGANIC_MATTER_COMPRESSING_TYPE;
	}

	@SuppressWarnings("removal")
	@Override
	public ResourceLocation getUid() {
		return JeiCompat.ORGANIC_MATTER_COMPRESSING;
	}

	@SuppressWarnings("removal")
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
	public void setRecipe(IRecipeLayoutBuilder builder, CompressingRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 95, 2).addIngredients(recipe.getIngredients().get(0));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				int index = j + i * 3;
				if (index < MatterUtil.matterList.size()) {
					MatterInfo matterInfo = MatterUtil.matterList.get(index);
					builder.addSlot(RecipeIngredientRole.INPUT, 3 + j * 18, 3 + i * 18)
							.addIngredients(VanillaTypes.ITEM, matterInfo.matterStacks()).addTooltipCallback(new MatterTooltip(matterInfo.matterAmount()));
				}
			}
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 38).addItemStack(recipe.getResultItem());
	}

	@Override
	public void draw(CompressingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		Font font = Minecraft.getInstance().font;
		TextComponent component = new TextComponent((int) (recipe.getCompressingTime() / 20f) + "s");
		font.draw(stack, component, 76 - font.width(component) / 2, 50, 16777215);
	}

	public static class MatterTooltip implements IRecipeSlotTooltipCallback {
		private final int matterAmount;

		public MatterTooltip(int matterAmount) {
			this.matterAmount = matterAmount;
		}

		@Override
		public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
			tooltip.add(new TranslatableComponent("thismatters.gui.jei.compressing.matter_amount", matterAmount).withStyle(ChatFormatting.GOLD));
		}
	}
}