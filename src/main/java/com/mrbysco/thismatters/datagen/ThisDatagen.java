package com.mrbysco.thismatters.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.datagen.builder.CompressingRecipeBuilder;
import com.mrbysco.thismatters.datagen.builder.MatterRecipeBuilder;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThisDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(new Loots(generator));
			generator.addProvider(new Recipes(generator));
			BlockTagsProvider provider;
			generator.addProvider(provider = new ThisBlockTags(generator, helper));
			generator.addProvider(new ThisItemTags(generator, provider, helper));
		}
		if (event.includeClient()) {
			generator.addProvider(new Language(generator));
			generator.addProvider(new BlockModels(generator, helper));
			generator.addProvider(new ItemModels(generator, helper));
			generator.addProvider(new BlockStates(generator, helper));
		}
	}
	private static class Loots extends LootTableProvider {
		public Loots(DataGenerator gen) {
			super(gen);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
			return ImmutableList.of(
					Pair.of(GeOreBlockTables::new, LootContextParamSets.BLOCK)
			);
		}

		public static class GeOreBlockTables extends BlockLoot {

			@Override
			protected void addTables() {
				this.add(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get(), BlockLoot::createNameableBlockEntityTable);
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) ThisRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> LootTables.validate(validationContext, name, table));
		}
	}

	public static class Recipes extends RecipeProvider {

		public Recipes(DataGenerator generator) {
			super(generator);
		}

		@Override
		protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "1_matter"), 1)
					.requires(Tags.Items.RODS_WOODEN).requires(ItemTags.LEAVES).save(recipeConsumer);

			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "2_matter"), 2)
					.requires(Items.WOODEN_SWORD).requires(Items.WOODEN_HOE).requires(Items.WOODEN_AXE)
					.requires(Items.WOODEN_PICKAXE).requires(Items.WOODEN_SHOVEL).requires(Tags.Items.LEATHER)
					.requires(Items.LEATHER_HELMET).requires(Items.LEATHER_CHESTPLATE).requires(Items.LEATHER_LEGGINGS)
					.requires(Items.LEATHER_BOOTS).requires(ItemTags.SIGNS).requires(Items.SADDLE)
					.requires(ItemTags.LECTERN_BOOKS).requires(Items.BOOK).requires(Items.ENCHANTED_BOOK)
					.requires(Items.FISHING_ROD).requires(Items.ITEM_FRAME).requires(Items.GLOW_ITEM_FRAME).requires(ItemTags.BOATS)
					.requires(Tags.Items.BONES).requires(ItemTags.BEDS).requires(Items.FILLED_MAP).requires(Items.MAP)
					.requires(Items.CARROT_ON_A_STICK).requires(Items.WARPED_FUNGUS_ON_A_STICK).requires(Items.NAME_TAG)
					.requires(Items.SUGAR).requires(Items.CAKE).requires(Tags.Items.SLIMEBALLS).requires(Items.PAPER)
					.requires(ItemTags.WOODEN_DOORS).requires(ItemTags.WOODEN_TRAPDOORS).requires(Items.LEAD)
					.requires(Tags.Items.CROPS).requires(Tags.Items.DYES).requires(Items.SUGAR_CANE)
					.requires(Tags.Items.FEATHERS).requires(Tags.Items.EGGS).requires(ItemTags.WOODEN_SLABS)
					.requires(ItemTags.WOODEN_STAIRS).requires(ItemTags.WOODEN_FENCES).requires(ItemTags.WOODEN_BUTTONS)
					.requires(ItemTags.WOODEN_PRESSURE_PLATES).requires(ItemTags.SAPLINGS).save(recipeConsumer);

			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "4_matter"), 4)
					.requires(ItemTags.PLANKS).requires(Items.MUSIC_DISC_13).requires(Items.MUSIC_DISC_CAT)
					.requires(Items.MUSIC_DISC_BLOCKS).requires(Items.MUSIC_DISC_CHIRP).requires(Items.MUSIC_DISC_FAR)
					.requires(Items.MUSIC_DISC_MALL).requires(Items.MUSIC_DISC_MELLOHI).requires(Items.MUSIC_DISC_STAL)
					.requires(Items.MUSIC_DISC_STRAD).requires(Items.MUSIC_DISC_WARD).requires(Items.MUSIC_DISC_11)
					.requires(Items.MUSIC_DISC_WAIT).requires(Items.MUSIC_DISC_OTHERSIDE).requires(Items.MUSIC_DISC_PIGSTEP).save(recipeConsumer);

			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "8_matter"), 8)
					.requires(Tags.Items.HEADS).save(recipeConsumer);

			ShapedRecipeBuilder.shaped(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get())
					.define('E', Tags.Items.GEMS_EMERALD)
					.define('O', Tags.Items.OBSIDIAN)
					.define('C', Items.CAULDRON)
					.define('I', Tags.Items.STORAGE_BLOCKS_IRON)
					.pattern("EOE").pattern("OCO").pattern("OIO").unlockedBy("has_obsidian", has(Blocks.OBSIDIAN)).save(recipeConsumer);

			CompressingRecipeBuilder.compressing(Ingredient.of(Items.COAL), Items.COAL_BLOCK, 900)
					.unlockedBy("has_coal", has(Items.COAL))
					.save(recipeConsumer, new ResourceLocation(ThisMatters.MOD_ID, "coal_block_from_compressing_coal"));
		}

		@Override
		protected void saveAdvancement(HashCache p_126014_, JsonObject p_126015_, Path p_126016_) {
			//NOOP
		}
	}

	private static class Language extends LanguageProvider {
		public Language(DataGenerator gen) {
			super(gen, ThisMatters.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			add("itemGroup.thismatters", "This Matters");

			add("thismatters.container.organic_matter_compressor", "Organic Matter Compressor");
			addBlock(ThisRegistry.ORGANIC_MATTER_COMPRESSOR, "Organic Matter Compressor");

			add("thismatters.organic_matter_compressor.not_low_enough", "Pressure too low, try placing the block deeper");

			add("thismatters.gui.jei.category.organic_matter_compressing", "Organic Matter Compressor");
			add("thismatters.gui.jei.compressing.matter", "<Matter>");
		}
	}

	private static class BlockStates extends BlockStateProvider {
		public BlockStates(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, ThisMatters.MOD_ID, helper);
		}

		@Override
		protected void registerStatesAndModels() {
			compressorState(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get());
		}

		private void compressorState(Block block) {
			ModelFile model = models().getExistingFile(modLoc("block/organic_matter_compressor"));
			getVariantBuilder(block)
					.partialState().modelForState().modelFile(model).addModel();
		}
	}

	private static class BlockModels extends BlockModelProvider {
		public BlockModels(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, ThisMatters.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {

		}
	}

	private static class ItemModels extends ItemModelProvider {
		public ItemModels(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, ThisMatters.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			withExistingParent(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get().getRegistryName().getPath(), modLoc("block/organic_matter_compressor"));
		}
	}

	public static class ThisBlockTags extends BlockTagsProvider {
		public ThisBlockTags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
			super(generator, ThisMatters.MOD_ID, existingFileHelper);
		}

		@Override
		protected void addTags() {
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get());
		}
	}

	public static class ThisItemTags extends ItemTagsProvider {
		public ThisItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
			super(dataGenerator, blockTagsProvider, ThisMatters.MOD_ID, existingFileHelper);
		}

		@Override
		protected void addTags() {

		}
	}
}
