package com.mrbysco.thismatters.datagen;

import com.google.gson.JsonObject;
import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.datagen.builder.CompressingRecipeBuilder;
import com.mrbysco.thismatters.datagen.builder.MatterRecipeBuilder;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThisDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new Loots(packOutput));
			generator.addProvider(event.includeServer(), new Recipes(packOutput));
			BlockTagsProvider provider;
			generator.addProvider(event.includeServer(), provider = new ThisBlockTags(packOutput, lookupProvider, helper));
			generator.addProvider(event.includeServer(), new ThisItemTags(packOutput, lookupProvider, provider, helper));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeServer(), new Language(packOutput));
			generator.addProvider(event.includeServer(), new BlockModels(packOutput, helper));
			generator.addProvider(event.includeServer(), new ItemModels(packOutput, helper));
			generator.addProvider(event.includeServer(), new BlockStates(packOutput, helper));
		}
	}

	private static class Loots extends LootTableProvider {
		public Loots(PackOutput packOutput) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(ThisBlockLoot::new, LootContextParamSets.BLOCK)
			));
		}

		public static class ThisBlockLoot extends BlockLootSubProvider {

			protected ThisBlockLoot() {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags());
			}

			@Override
			protected void generate() {
				this.add(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get(), createNameableBlockEntityTable(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()));
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return ThisRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> table.validate(validationContext));
		}
	}

	public static class Recipes extends RecipeProvider {

		public Recipes(PackOutput packOutput) {
			super(packOutput);
		}

		@Override
		protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "1_matter"), 1)
					.requires(Tags.Items.RODS_WOODEN).requires(Items.BAMBOO).requires(ItemTags.LEAVES)
					.requires(Items.DEAD_BRAIN_CORAL).requires(Items.DEAD_BUBBLE_CORAL).requires(Items.DEAD_FIRE_CORAL)
					.requires(Items.DEAD_HORN_CORAL).requires(Items.DEAD_TUBE_CORAL).requires(Items.DEAD_TUBE_CORAL_FAN)
					.requires(Items.DEAD_BRAIN_CORAL_FAN).requires(Items.DEAD_BUBBLE_CORAL_FAN).requires(Items.DEAD_FIRE_CORAL_FAN)
					.requires(Items.DEAD_HORN_CORAL_FAN)
					.save(recipeConsumer);

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
					.requires(Items.TUBE_CORAL).requires(Items.BRAIN_CORAL).requires(Items.BUBBLE_CORAL)
					.requires(Items.FIRE_CORAL).requires(Items.HORN_CORAL).requires(Items.TUBE_CORAL_FAN)
					.requires(Items.BRAIN_CORAL_FAN).requires(Items.BUBBLE_CORAL_FAN).requires(Items.FIRE_CORAL_FAN)
					.requires(Items.HORN_CORAL_FAN).requires(Items.DEAD_TUBE_CORAL_BLOCK).requires(Items.DEAD_BRAIN_CORAL_BLOCK)
					.requires(Items.DEAD_BUBBLE_CORAL_BLOCK).requires(Items.DEAD_FIRE_CORAL_BLOCK).requires(Items.DEAD_HORN_CORAL_BLOCK)
					.requires(ItemTags.WOODEN_PRESSURE_PLATES).requires(ItemTags.SAPLINGS).save(recipeConsumer);

			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "4_matter"), 4)
					.requires(ItemTags.PLANKS).requires(Items.MUSIC_DISC_13).requires(Items.MUSIC_DISC_CAT)
					.requires(Items.MUSIC_DISC_BLOCKS).requires(Items.MUSIC_DISC_CHIRP).requires(Items.MUSIC_DISC_FAR)
					.requires(Items.MUSIC_DISC_MALL).requires(Items.MUSIC_DISC_MELLOHI).requires(Items.MUSIC_DISC_STAL)
					.requires(Items.MUSIC_DISC_STRAD).requires(Items.MUSIC_DISC_WARD).requires(Items.MUSIC_DISC_11)
					.requires(Items.MUSIC_DISC_WAIT).requires(Items.MUSIC_DISC_OTHERSIDE).requires(Items.MUSIC_DISC_PIGSTEP)
					.requires(Items.TUBE_CORAL_BLOCK).requires(Items.BRAIN_CORAL_BLOCK).requires(Items.BUBBLE_CORAL_BLOCK)
					.requires(Items.FIRE_CORAL_BLOCK).requires(Items.HORN_CORAL_BLOCK).save(recipeConsumer);

			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "5_matter"), 5)
					.requires(ItemTags.WARPED_STEMS).save(recipeConsumer);

			MatterRecipeBuilder.matter(new ResourceLocation(ThisMatters.MOD_ID, "8_matter"), 8)
					.requires(Tags.Items.HEADS).save(recipeConsumer);

			ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get())
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
		protected @Nullable CompletableFuture<?> saveAdvancement(CachedOutput output, FinishedRecipe finishedRecipe, JsonObject advancementJson) {
			return null;
		}
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, ThisMatters.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			add("itemGroup.thismatters", "This Matters");

			add("thismatters.container.organic_matter_compressor", "Organic Matter Compressor");
			addBlock(ThisRegistry.ORGANIC_MATTER_COMPRESSOR, "Organic Matter Compressor");

			add("thismatters.organic_matter_compressor.not_low_enough", "Pressure too low, try placing the block deeper");

			add("thismatters.gui.jei.category.organic_matter_compressing", "Organic Matter Compressor");
			add("thismatters.gui.jei.compressing.matter", "<Matter>");
			add("thismatters.gui.jei.compressing.matter_amount", "Produces %s Matter");
		}
	}

	private static class BlockStates extends BlockStateProvider {
		public BlockStates(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, ThisMatters.MOD_ID, helper);
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
		public BlockModels(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, ThisMatters.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {

		}
	}

	private static class ItemModels extends ItemModelProvider {
		public ItemModels(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, ThisMatters.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			withExistingParent(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.getId().getPath(), modLoc("block/organic_matter_compressor"));
		}
	}

	public static class ThisBlockTags extends BlockTagsProvider {
		public ThisBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
			super(output, lookupProvider, ThisMatters.MOD_ID, existingFileHelper);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get());
		}
	}

	public static class ThisItemTags extends ItemTagsProvider {
		public ThisItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
			super(output, lookupProvider, blockTagsProvider.contentsGetter(), ThisMatters.MOD_ID, existingFileHelper);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {

		}
	}
}
