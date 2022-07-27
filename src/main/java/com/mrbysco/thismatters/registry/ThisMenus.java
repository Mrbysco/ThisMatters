package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.menu.OrganicMatterCompressorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ThisMenus {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ThisMatters.MOD_ID);

	public static final RegistryObject<MenuType<OrganicMatterCompressorMenu>> ORGANIC_MATTER_COMPRESSOR = MENU_TYPES.register("organic_matter_compressor", () -> IForgeMenuType.create(OrganicMatterCompressorMenu::new));
}
