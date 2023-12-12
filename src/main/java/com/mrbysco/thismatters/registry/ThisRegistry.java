package com.mrbysco.thismatters.registry;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.block.OrganicMatterCompressorBlock;
import com.mrbysco.thismatters.blockentity.OrganicMatterCompressorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ThisRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThisMatters.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThisMatters.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThisMatters.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThisMatters.MOD_ID);

	public static final DeferredBlock<OrganicMatterCompressorBlock> ORGANIC_MATTER_COMPRESSOR = BLOCKS.register("organic_matter_compressor", () ->
			new OrganicMatterCompressorBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK)
					.requiresCorrectToolForDrops().strength(5.0F, 120.0F).randomTicks().sound(SoundType.STONE).noOcclusion()));

	public static final Supplier<BlockEntityType<OrganicMatterCompressorBlockEntity>> ORGANIC_MATTER_COMPRESSOR_BE = BLOCK_ENTITY_TYPES.register("organic_matter_compressor", () ->
			BlockEntityType.Builder.of(OrganicMatterCompressorBlockEntity::new, ORGANIC_MATTER_COMPRESSOR.get()).build(null));

	public static final DeferredItem<BlockItem> ORGANIC_MATTER_COMPRESSOR_ITEM = ITEMS.registerSimpleBlockItem(ORGANIC_MATTER_COMPRESSOR);

	public static final Supplier<CreativeModeTab> MATTER_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.thismatters"))
			.displayItems((displayParameters, output) -> {
				List<ItemStack> stacks = ThisRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);
			}).build());
}
