package com.mrbysco.thismatters.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.menu.OrganicMatterCompressorMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OrganicMatterCompressorScreen extends AbstractContainerScreen<OrganicMatterCompressorMenu> {
	private static final ResourceLocation SCREEN_LOCATION = new ResourceLocation(ThisMatters.MOD_ID, "textures/gui/organic_matter_compressor.png");

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
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, SCREEN_LOCATION);

		int i = this.leftPos;
		int j = this.topPos;
		this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.hasMatter()) {
			int l = this.menu.getCompressionProgress();
			this.blit(poseStack, i + 94, j + 36, 176, 0, l + 1, 16);
		}

		this.drawCenteredString(poseStack, this.font, new TextComponent(this.menu.getMatterPercentage() + "%"), i + 134, j + 72, 16777215);
	}
}
