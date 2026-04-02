package com.mrbysco.thismatters.compat.jei.compressing;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.compat.jei.JeiCompat;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.registry.ThisRegistry;
import com.mrbysco.thismatters.util.MatterUtil;
import com.mrbysco.thismatters.util.MatterUtil.MatterInfo;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class OrganicMatterCompressingCategory implements IRecipeCategory<CompressingRecipe> {
	public static final Identifier RECIPE_COMPRESSING_JEI = Identifier.fromNamespaceAndPath(ThisMatters.MOD_ID, "textures/gui/jei/organic_matter_compressing.png");

	private final IDrawable background;
	private final IDrawable icon;
	private final Component title;

	public OrganicMatterCompressingCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(RECIPE_COMPRESSING_JEI, 0, 0, 112, 58);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()));
		this.title = Component.translatable("thismatters.gui.jei.category.organic_matter_compressing");

		MatterUtil.reloadMatterList(Minecraft.getInstance().level);
	}

	@Override
	public IRecipeType<CompressingRecipe> getRecipeType() {
		return JeiCompat.ORGANIC_MATTER_COMPRESSING_TYPE;
	}

	@Override
	public int getHeight() {
		return background.getHeight();
	}

	@Override
	public int getWidth() {
		return background.getWidth();
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CompressingRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 95, 2).add(recipe.getIngredient());

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				int index = j + i * 3;
				if (index < MatterUtil.matterList.size()) {
					MatterInfo matterInfo = MatterUtil.matterList.get(index);
					builder.addSlot(RecipeIngredientRole.INPUT, 3 + j * 18, 3 + i * 18)
							.addIngredients(VanillaTypes.ITEM_STACK, matterInfo.matterStacks()).addRichTooltipCallback(new MatterTooltip(matterInfo.matterAmount()));
				}
			}
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 38).add(recipe.getResult());
	}

	@Override
	public void draw(CompressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		this.background.draw(guiGraphics, 0, 0);
		Font font = Minecraft.getInstance().font;
		MutableComponent component = Component.literal((int) (recipe.getCompressingTime() / 20f) + "s");
		guiGraphics.text(font, component, 76 - font.width(component) / 2, 50, 16777215, false);
	}

	public static class MatterTooltip implements IRecipeSlotRichTooltipCallback {
		private final int matterAmount;

		public MatterTooltip(int matterAmount) {
			this.matterAmount = matterAmount;
		}

		@Override
		public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
			tooltip.add(Component.translatable("thismatters.gui.jei.compressing.matter_amount", matterAmount).withStyle(ChatFormatting.GOLD));
		}
	}
}