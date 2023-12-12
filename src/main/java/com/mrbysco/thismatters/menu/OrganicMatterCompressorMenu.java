package com.mrbysco.thismatters.menu;

import com.mrbysco.thismatters.blockentity.OrganicMatterCompressorBlockEntity;
import com.mrbysco.thismatters.registry.ThisMenus;
import com.mrbysco.thismatters.registry.ThisRecipes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.Objects;

public class OrganicMatterCompressorMenu extends AbstractContainerMenu {
	private final OrganicMatterCompressorBlockEntity blockEntity;
	private final ContainerData data;
	protected final Level level;

	public OrganicMatterCompressorMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
		this(windowId, playerInventory, getBlockEntity(playerInventory, data));
	}

	private static OrganicMatterCompressorBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
		Objects.requireNonNull(data, "data cannot be null!");
		final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());

		if (tileAtPos instanceof OrganicMatterCompressorBlockEntity compressorBlockEntity) {
			return compressorBlockEntity;
		}

		throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
	}

	public OrganicMatterCompressorMenu(int id, Inventory playerInventoryIn, OrganicMatterCompressorBlockEntity compressorBlockEntity) {
		super(ThisMenus.ORGANIC_MATTER_COMPRESSOR.get(), id);

		this.blockEntity = compressorBlockEntity;
		this.data = compressorBlockEntity.getDataAccess();
		this.level = playerInventoryIn.player.level();
		checkContainerDataCount(data, 4);

		this.addSlot(new SlotItemHandler(blockEntity.getInputInventory(), 0, 126, 17));
		this.addSlot(new SlotItemHandler(blockEntity.getResultInventory(), 0, 126, 53));

		this.bindMatterInventory(blockEntity.getMatterInventory());

		this.bindPlayerInventory(playerInventoryIn);
	}

	private void bindMatterInventory(IItemHandler itemHandler) {
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 3; ++col) {
				this.addSlot(new SlotItemHandler(itemHandler, col + row * 3, 34 + col * 18, 18 + row * 18));
			}
		}
	}

	private void bindPlayerInventory(Inventory inventory) {
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return this.blockEntity.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			final int blockInventorySize = 11;

			if (index < blockInventorySize) {
				if (!this.moveItemStackTo(itemstack1, blockInventorySize, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (this.canCompress(itemstack1)) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.isMatter(itemstack1)) {
					if (!this.moveItemStackTo(itemstack1, 2, 11, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	protected boolean canCompress(ItemStack compress) {
		return level.getRecipeManager().getRecipeFor(ThisRecipes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE.get(), new SimpleContainer(compress), level).isPresent();
	}

	protected boolean isMatter(ItemStack stack) {
		return OrganicMatterCompressorBlockEntity.getMatterValue(this.level, stack) > 0;
	}

	public int getCompressionProgress() {
		int compressingProgress = this.data.get(2);
		int compressingTotalTime = this.data.get(3);
		return compressingTotalTime != 0 && compressingProgress != 0 ? compressingProgress * 24 / compressingTotalTime : 0;
	}

	public int getMatterPercentage() {
		int matterAmount = this.data.get(0);
		int maxMatter = this.data.get(1);
		return Mth.floor((matterAmount * 100f / maxMatter));
	}

	public boolean hasMatter() {
		return this.data.get(0) >= this.data.get(1);
	}
}
