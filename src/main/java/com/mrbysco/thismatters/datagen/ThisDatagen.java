package com.mrbysco.thismatters.datagen;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.datagen.builder.CompressingRecipeBuilder;
import com.mrbysco.thismatters.datagen.builder.MatterRecipeBuilder;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class ThisDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new Loots(packOutput, lookupProvider));
		generator.addProvider(true, new Recipes.Runner(packOutput, lookupProvider));
		generator.addProvider(true, new ThisBlockTags(packOutput, lookupProvider));
		generator.addProvider(true, new ThisItemTags(packOutput, lookupProvider));

		generator.addProvider(true, new Language(packOutput));
		generator.addProvider(true, new Models(packOutput));
	}

	private static class Loots extends LootTableProvider {
		public Loots(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(ThisBlockLoot::new, LootContextParamSets.BLOCK)
			), lookupProvider);
		}

		public static class ThisBlockLoot extends BlockLootSubProvider {

			protected ThisBlockLoot(HolderLookup.Provider provider) {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
			}

			@Override
			protected void generate() {
				this.add(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get(), createNameableBlockEntityTable(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get()));
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return ThisRegistry.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
			}
		}

		@Override
		protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {
			super.validate(writableregistry, validationcontext, problemreporter$collector);
		}
	}

	public static class Recipes extends RecipeProvider {

		private final HolderGetter<Item> items;

		public Recipes(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			super(provider, recipeOutput);
			this.items = registries.lookupOrThrow(Registries.ITEM);
		}

		@Override
		protected void buildRecipes() {
			matter(ResourceLocation.fromNamespaceAndPath(ThisMatters.MOD_ID, "1_matter"), 1)
					.requires(Tags.Items.RODS_WOODEN).requires(Items.BAMBOO).requires(ItemTags.LEAVES)
					.requires(Items.DEAD_BRAIN_CORAL).requires(Items.DEAD_BUBBLE_CORAL).requires(Items.DEAD_FIRE_CORAL)
					.requires(Items.DEAD_HORN_CORAL).requires(Items.DEAD_TUBE_CORAL).requires(Items.DEAD_TUBE_CORAL_FAN)
					.requires(Items.DEAD_BRAIN_CORAL_FAN).requires(Items.DEAD_BUBBLE_CORAL_FAN).requires(Items.DEAD_FIRE_CORAL_FAN)
					.requires(Items.DEAD_HORN_CORAL_FAN).requires(Items.LEAF_LITTER).requires(Items.WILDFLOWERS)
					.requires(Items.BUSH).requires(Items.FIREFLY_BUSH).requires(Items.CACTUS_FLOWER)
					.save(output);

			matter(ResourceLocation.fromNamespaceAndPath(ThisMatters.MOD_ID, "2_matter"), 2)
					.requires(Items.WOODEN_SWORD).requires(Items.WOODEN_HOE).requires(Items.WOODEN_AXE)
					.requires(Items.WOODEN_PICKAXE).requires(Items.WOODEN_SHOVEL).requires(Tags.Items.LEATHERS)
					.requires(Items.LEATHER_HELMET).requires(Items.LEATHER_CHESTPLATE).requires(Items.LEATHER_LEGGINGS)
					.requires(Items.LEATHER_BOOTS).requires(ItemTags.SIGNS).requires(Items.SADDLE)
					.requires(ItemTags.LECTERN_BOOKS).requires(Items.BOOK).requires(Items.ENCHANTED_BOOK)
					.requires(Items.FISHING_ROD).requires(Items.ITEM_FRAME).requires(Items.GLOW_ITEM_FRAME).requires(ItemTags.BOATS)
					.requires(Tags.Items.BONES).requires(ItemTags.BEDS).requires(Items.FILLED_MAP).requires(Items.MAP)
					.requires(Items.CARROT_ON_A_STICK).requires(Items.WARPED_FUNGUS_ON_A_STICK).requires(Items.NAME_TAG)
					.requires(Items.SUGAR).requires(Items.CAKE).requires(Tags.Items.SLIME_BALLS).requires(Items.PAPER)
					.requires(ItemTags.WOODEN_DOORS).requires(ItemTags.WOODEN_TRAPDOORS).requires(Items.LEAD)
					.requires(Tags.Items.CROPS).requires(Tags.Items.DYES).requires(Items.SUGAR_CANE)
					.requires(Tags.Items.FEATHERS).requires(Tags.Items.EGGS).requires(ItemTags.WOODEN_SLABS)
					.requires(ItemTags.WOODEN_STAIRS).requires(ItemTags.WOODEN_FENCES).requires(ItemTags.WOODEN_BUTTONS)
					.requires(Items.TUBE_CORAL).requires(Items.BRAIN_CORAL).requires(Items.BUBBLE_CORAL)
					.requires(Items.FIRE_CORAL).requires(Items.HORN_CORAL).requires(Items.TUBE_CORAL_FAN)
					.requires(Items.BRAIN_CORAL_FAN).requires(Items.BUBBLE_CORAL_FAN).requires(Items.FIRE_CORAL_FAN)
					.requires(Items.HORN_CORAL_FAN).requires(Items.DEAD_TUBE_CORAL_BLOCK).requires(Items.DEAD_BRAIN_CORAL_BLOCK)
					.requires(Items.DEAD_BUBBLE_CORAL_BLOCK).requires(Items.DEAD_FIRE_CORAL_BLOCK).requires(Items.DEAD_HORN_CORAL_BLOCK)
					.requires(ItemTags.WOODEN_PRESSURE_PLATES).requires(ItemTags.SAPLINGS).requires(Items.PAINTING)
					.save(output);

			matter(ResourceLocation.fromNamespaceAndPath(ThisMatters.MOD_ID, "4_matter"), 4)
					.requires(ItemTags.PLANKS).requires(Tags.Items.MUSIC_DISCS).requires(Items.TUBE_CORAL_BLOCK)
					.requires(Items.BRAIN_CORAL_BLOCK).requires(Items.BUBBLE_CORAL_BLOCK)
					.requires(Items.FIRE_CORAL_BLOCK).requires(Items.HORN_CORAL_BLOCK)
					.requires(ItemTags.HARNESSES)
					.save(output);

			matter(ResourceLocation.fromNamespaceAndPath(ThisMatters.MOD_ID, "5_matter"), 5)
					.requires(ItemTags.WARPED_STEMS)
					.save(output);

			matter(ResourceLocation.fromNamespaceAndPath(ThisMatters.MOD_ID, "8_matter"), 8)
					.requires(ItemTags.SKULLS)
					.save(output);

			shaped(RecipeCategory.REDSTONE, ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get())
					.define('E', Tags.Items.GEMS_EMERALD)
					.define('O', Tags.Items.OBSIDIANS)
					.define('C', Items.CAULDRON)
					.define('I', Tags.Items.STORAGE_BLOCKS_IRON)
					.pattern("EOE").pattern("OCO").pattern("OIO").unlockedBy("has_obsidian", has(Blocks.OBSIDIAN))
					.save(output);

			CompressingRecipeBuilder.compressing(Ingredient.of(Items.COAL), Items.COAL_BLOCK, 900)
					.unlockedBy("has_coal", has(Items.COAL))
					.save(output, ResourceLocation.fromNamespaceAndPath(ThisMatters.MOD_ID, "coal_block_from_compressing_coal").toString());
		}

		private MatterRecipeBuilder matter(ResourceLocation location, int matterAmount) {
			return MatterRecipeBuilder.matter(this.items, location, matterAmount);
		}

		public static class Runner extends RecipeProvider.Runner {
			public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
				super(output, completableFuture);
			}

			@Override
			protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
				return new Recipes(provider, recipeOutput);
			}

			@Override
			public String getName() {
				return "ThisMatters Recipes";
			}
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

			addConfig("General", "General", "General Settings");
			addConfig("minY", "Min Y", "Defines the minimum y level at which the Matter Compressor can work [default: 15]");
			addConfig("maxMatter", "Max Matter", "Defines the maximum amount of matter until the Matter Compressor displays 100% [default: 150]");
			addConfig("useDefaults", "Use Defaults", "Use built-in matter checks when no item match is found using recipes [default: true]");
		}

		/**
		 * Add the translation for a config entry
		 *
		 * @param path        The path of the config entry
		 * @param name        The name of the config entry
		 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
		 */
		private void addConfig(String path, String name, @Nullable String description) {
			this.add("thismatters.configuration." + path, name);
			if (description != null && !description.isEmpty())
				this.add("thismatters.configuration." + path + ".tooltip", description);
		}
	}

	private static class Models extends ModelProvider {
		public Models(PackOutput packOutput) {
			super(packOutput, ThisMatters.MOD_ID);
		}

		@Override
		protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
			ResourceLocation resourcelocation = TexturedModel.CUBE.create(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get(), blockModels.modelOutput);
			blockModels.blockStateOutput.accept(
					BlockModelGenerators.createSimpleBlock(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get(), BlockModelGenerators.plainVariant(resourcelocation))
			);
		}
	}

	public static class ThisBlockTags extends BlockTagsProvider {
		public ThisBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, ThisMatters.MOD_ID);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ThisRegistry.ORGANIC_MATTER_COMPRESSOR.get());
		}
	}

	public static class ThisItemTags extends ItemTagsProvider {
		public ThisItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, ThisMatters.MOD_ID);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {

		}
	}
}
