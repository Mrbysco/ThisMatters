package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.menu.OrganicMatterCompressorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ThisMenus {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ThisMatters.MOD_ID);

	public static final Supplier<MenuType<OrganicMatterCompressorMenu>> ORGANIC_MATTER_COMPRESSOR = MENU_TYPES.register("organic_matter_compressor", () -> IMenuTypeExtension.create(OrganicMatterCompressorMenu::new));
}
