package com.mrbysco.thismatters.client.screen;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.menu.OrganicMatterCompressorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OrganicMatterCompressorScreen extends AbstractContainerScreen<OrganicMatterCompressorMenu> {
	private static final Identifier SCREEN_LOCATION = Identifier.fromNamespaceAndPath(ThisMatters.MOD_ID, "textures/gui/organic_matter_compressor.png");

	public OrganicMatterCompressorScreen(OrganicMatterCompressorMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
		this.inventoryLabelY = this.imageHeight - 92;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		this.extractTooltip(graphics, mouseX, mouseY);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		int i = this.leftPos;
		int j = this.topPos;
		graphics.blit(RenderPipelines.GUI_TEXTURED, SCREEN_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
		if (this.menu.hasMatter()) {
			int l = this.menu.getCompressionProgress();
			graphics.blit(RenderPipelines.GUI_TEXTURED, SCREEN_LOCATION, i + 94, j + 36, 176, 0, l + 1, 16, 256, 256);
		}

		graphics.centeredText(this.font, Component.literal(this.menu.getMatterPercentage() + "%"), i + 134, j + 72, 16777215);
	}
}
