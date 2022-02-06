package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.menu.OrganicMatterCompressorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ThisMenus {
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ThisMatters.MOD_ID);

	public static final RegistryObject<MenuType<OrganicMatterCompressorMenu>> ORGANIC_MATTER_COMPRESSOR = MENUS.register("organic_matter_compressor", () -> IForgeMenuType.create(OrganicMatterCompressorMenu::new));
}
