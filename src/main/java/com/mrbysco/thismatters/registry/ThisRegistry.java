package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.block.OrganicMatterCompressorBlock;
import com.mrbysco.thismatters.blockentity.OrganicMatterCompressorBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ThisRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ThisMatters.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ThisMatters.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ThisMatters.MOD_ID);

	public static final RegistryObject<Block> ORGANIC_MATTER_COMPRESSOR = BLOCKS.register("organic_matter_compressor", () ->
			new OrganicMatterCompressorBlock(Block.Properties.of(Material.HEAVY_METAL, MaterialColor.COLOR_BLACK)
					.requiresCorrectToolForDrops().strength(5.0F, 120.0F).randomTicks().sound(SoundType.STONE).noOcclusion()));

	public static final RegistryObject<BlockEntityType<OrganicMatterCompressorBlockEntity>> ORGANIC_MATTER_COMPRESSOR_BE = BLOCK_ENTITY_TYPES.register("organic_matter_compressor", () ->
			BlockEntityType.Builder.of(OrganicMatterCompressorBlockEntity::new, ORGANIC_MATTER_COMPRESSOR.get()).build(null));

	public static final RegistryObject<Item> ORGANIC_MATTER_COMPRESSOR_ITEM = ITEMS.register("organic_matter_compressor", () ->
			new BlockItem(ORGANIC_MATTER_COMPRESSOR.get(), new Item.Properties().tab(ThisMatters.TAB_MAIN)));
}
