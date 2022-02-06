package com.mrbysco.thismatters.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class CapabilityHelper {
	public static ItemStack removeItem(ItemStackHandler handler, int index, int count) {
		return index >= 0 && index < handler.getSlots() && !handler.getStackInSlot(index).isEmpty() && count > 0 ? handler.getStackInSlot(index).split(count) : ItemStack.EMPTY;
	}

	public static ItemStack takeItem(ItemStackHandler handler, int index) {
		return index >= 0 && index < handler.getSlots() ? setItemAndGetPrevious(handler, index, ItemStack.EMPTY) : ItemStack.EMPTY;
	}

	public static ItemStack setItemAndGetPrevious(ItemStackHandler handler, int index, ItemStack stack) {
		ItemStack oldStack = handler.getStackInSlot(index);
		handler.setStackInSlot(index, stack);
		return oldStack;
	}
}
