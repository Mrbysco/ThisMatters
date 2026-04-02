package com.mrbysco.thismatters.blockentity;

import com.mojang.serialization.Codec;
import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.menu.OrganicMatterCompressorMenu;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.registry.ThisRecipes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import com.mrbysco.thismatters.util.MatterUtil;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

public class OrganicMatterCompressorBlockEntity extends BaseContainerBlockEntity implements RecipeCraftingHolder {
	public static final TreeMap<Integer, Integer> cachedValues = new TreeMap<>();

	public final ItemStacksResourceHandler matterHandler = new ItemStacksResourceHandler(9) {
		@Override
		public boolean isValid(int index, @NotNull ItemResource resource) {
			if (!resource.isEmpty() && level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
				int matterValue = getMatterValue(serverLevel, resource.toStack());
				return matterValue > 0;
			}
			return false;
		}
	};
	public final ItemStacksResourceHandler inputHandler = new ItemStacksResourceHandler(1) {
		@Override
		public boolean isValid(int index, @NotNull ItemResource resource) {
			return super.isValid(index, resource);
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
				compressingTotalTime = getTotalCompressingTime(serverLevel, OrganicMatterCompressorBlockEntity.this, new SingleRecipeInput(getResource(0).toStack()));
				compressingProgress = 0;
			}
			setChanged();
		}
	};
	public final ItemStacksResourceHandler resultHandler = new ItemStacksResourceHandler(1) {
		@Override
		public boolean isValid(int index, @NotNull ItemResource resource) {
			return false;
		}
	};

	protected final ContainerData dataAccess = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> OrganicMatterCompressorBlockEntity.this.matterAmount;
				case 1 -> OrganicMatterCompressorBlockEntity.this.maxMatter;
				case 2 -> OrganicMatterCompressorBlockEntity.this.compressingProgress;
				case 3 -> OrganicMatterCompressorBlockEntity.this.compressingTotalTime;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> OrganicMatterCompressorBlockEntity.this.matterAmount = value;
				case 1 -> OrganicMatterCompressorBlockEntity.this.maxMatter = value;
				case 2 -> OrganicMatterCompressorBlockEntity.this.compressingProgress = value;
				case 3 -> OrganicMatterCompressorBlockEntity.this.compressingTotalTime = value;
			}
		}

		public int getCount() {
			return 4;
		}
	};

	protected static final int SLOT_INPUT = 9;
	protected static final int SLOT_RESULT = 10;

	private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
	private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
	private final RecipeManager.CachedCheck<RecipeInput, CompressingRecipe> quickCheck;
	private int compressingProgress;
	private int compressingTotalTime;
	private int matterAmount;
	private int maxMatter;

	public OrganicMatterCompressorBlockEntity(BlockPos pos, BlockState state) {
		super(ThisRegistry.ORGANIC_MATTER_COMPRESSOR_BE.get(), pos, state);
		this.maxMatter = ThisConfig.COMMON.maxMatter.get();
		this.quickCheck = RecipeManager.createCheck(ThisRecipes.ORGANIC_MATTER_COMPRESSION_RECIPE_TYPE.get());
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		ValueInput matterStackInput = input.childOrEmpty("MatterStackHandler");
		this.matterHandler.deserialize(matterStackInput);
		ValueInput inputStackInput = input.childOrEmpty("InputStackHandler");
		this.inputHandler.deserialize(inputStackInput);
		ValueInput resultStackInput = input.childOrEmpty("ResultStackHandler");
		this.resultHandler.deserialize(resultStackInput);

		this.matterAmount = input.getIntOr("MatterAmount", 0);
		this.maxMatter = input.getIntOr("MaxMatter", 0);
		this.compressingProgress = input.getIntOr("CompressingTime", 0);
		this.compressingTotalTime = input.getIntOr("CompressingTotalTime", 0);

		this.recipesUsed.clear();
		this.recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putFloat("MatterAmount", this.matterAmount);
		output.putFloat("MaxMatter", this.maxMatter);
		output.putInt("CompressingTime", this.compressingProgress);
		output.putInt("CompressingTotalTime", this.compressingTotalTime);

		ValueOutput matterStackOutput = output.child("MatterStackHandler");
		matterHandler.serialize(matterStackOutput);
		ValueOutput inputStackOutput = output.child("InputStackHandler");
		inputHandler.serialize(inputStackOutput);
		ValueOutput resultStackOutput = output.child("ResultStackHandler");
		resultHandler.serialize(resultStackOutput);

		output.store("RecipesUsed", RECIPES_USED_CODEC, this.recipesUsed);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, OrganicMatterCompressorBlockEntity compressorBlockEntity) {
		if (level instanceof ServerLevel serverLevel) {
			try (Transaction tx = Transaction.openRoot()) {
				boolean canCommit = true;
				for (int i = 0; i < compressorBlockEntity.matterHandler.size(); ++i) {
					ItemResource resource = compressorBlockEntity.matterHandler.getResource(i);
					if (!resource.isEmpty()) {
						int matterValue = getMatterValue(serverLevel, resource.toStack());
						if (matterValue > 0) {
							for (int j = 0; j < compressorBlockEntity.matterHandler.getAmountAsInt(i); ++j) {
								if (compressorBlockEntity.increaseMatter(matterValue)) {
									compressorBlockEntity.refreshClient();
									if (compressorBlockEntity.matterHandler.extract(i, resource, 1, tx) != 1) {
										canCommit = false;
										break;
									}
								} else {
									break;
								}
							}
						}
					}
				}
				if (canCommit) {
					tx.commit();

					ItemResource inputResource = compressorBlockEntity.inputHandler.getResource(0);
					if (compressorBlockEntity.hasMatter() && !inputResource.isEmpty()) {
						RecipeHolder<CompressingRecipe> recipeHolder = compressorBlockEntity.quickCheck.getRecipeFor(new SingleRecipeInput(inputResource.toStack()), serverLevel).orElse(null);
						if (recipeHolder == null) return;
						CompressingRecipe recipe = recipeHolder.value();
						int i = compressorBlockEntity.getMaxStackSize();
						if (compressorBlockEntity.hasMatter() && compressorBlockEntity.canCompress(recipe, i)) {
							++compressorBlockEntity.compressingProgress;
							if (compressorBlockEntity.compressingProgress == compressorBlockEntity.compressingTotalTime) {
								compressorBlockEntity.compressingProgress = 0;
								compressorBlockEntity.compressingTotalTime = getTotalCompressingTime(serverLevel, compressorBlockEntity, new SingleRecipeInput(inputResource.toStack()));
								if (compressorBlockEntity.compress(recipe, i)) {
									compressorBlockEntity.setRecipeUsed(recipeHolder);
								}

							}
						} else {
							compressorBlockEntity.compressingProgress = 0;
						}
						compressorBlockEntity.refreshClient();
					} else if (!compressorBlockEntity.hasMatter() && compressorBlockEntity.compressingProgress > 0) {
						compressorBlockEntity.compressingProgress = Mth.clamp(compressorBlockEntity.compressingProgress - 2, 0, compressorBlockEntity.compressingTotalTime);
						compressorBlockEntity.refreshClient();
					}
				}
			}
		}
	}

	public void refreshClient() {
		setChanged();
		if (level == null) return;
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	private boolean canCompress(@Nullable Recipe<?> recipe, int count) {
		if (this.level != null && !inputHandler.getResource(0).isEmpty() && recipe != null) {
			ItemStack assembledStack = ((CompressingRecipe) recipe).assemble(new SingleRecipeInput(inputHandler.getResource(0).toStack()));
			if (assembledStack.isEmpty()) {
				return false;
			} else {
				ItemResource resultResource = resultHandler.getResource(0);
				int resultCount = resultHandler.getAmountAsInt(0);
				if (resultResource.isEmpty()) {
					return true;
				} else if (!assembledStack.is(resultResource.getItem())) {
					return false;
				} else if (resultCount + assembledStack.getCount() <= count && resultCount + assembledStack.getCount() <= resultResource.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
					return true;
				} else {
					return resultCount + assembledStack.getCount() <= assembledStack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
				}
			}
		} else {
			return false;
		}
	}

	private boolean compress(@Nullable Recipe<?> recipe, int count) {
		if (this.level != null && recipe != null && this.canCompress(recipe, count)) {
			try (Transaction tx = Transaction.openRoot()) {
				ItemResource inputResource = inputHandler.getResource(0);
				ItemStack assembledStack = ((CompressingRecipe) recipe).assemble(new SingleRecipeInput(inputResource.toStack()));
				ItemResource resultResource = resultHandler.getResource(0);
				if (resultResource.isEmpty()) {
					resultHandler.set(0, ItemResource.of(assembledStack), assembledStack.getCount());
				} else if (resultResource.is(assembledStack.getItem())) {
					if (resultHandler.insert(resultResource, assembledStack.getCount(), tx) != assembledStack.getCount()) {
						return false;
					}
				}

				if (inputHandler.extract(0, inputResource, 1, tx) != 1) {
					return false;
				}

				tx.commit();
			}
			this.matterAmount = 0;
			return true;
		} else {
			return false;
		}
	}

	protected boolean hasMatter() {
		return matterAmount >= maxMatter;
	}

	protected boolean increaseMatter(int matter) {
		int newValue = matterAmount + matter;
		if (newValue <= maxMatter) {
			this.matterAmount = newValue;
			return true;
		} else {
			matterAmount = maxMatter;
			return false;
		}
	}

	@NotNull
	@Override
	protected Component getDefaultName() {
		return Component.translatable(ThisMatters.MOD_ID + ".container.organic_matter_compressor");
	}

	@NotNull
	@Override
	protected AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
		return new OrganicMatterCompressorMenu(id, inventory, this);
	}

	@Override
	public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
		if (recipeHolder != null) {
			ResourceKey<Recipe<?>> resourcekey = recipeHolder.id();
			this.recipesUsed.addTo(resourcekey, 1);
		}
	}

	@Nullable
	@Override
	public RecipeHolder<Recipe<?>> getRecipeUsed() {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 11;
	}

	@NotNull
	@Override
	protected NonNullList<ItemStack> getItems() {
		return NonNullList.create();
	}

	@Override
	protected void setItems(@NotNull NonNullList<ItemStack> pItems) {

	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < matterHandler.size(); i++) {
			if (!matterHandler.getResource(i).isEmpty()) {
				return false;
			}
		}
		for (int i = 0; i < inputHandler.size(); i++) {
			if (!inputHandler.getResource(i).isEmpty()) {
				return false;
			}
		}
		for (int i = 0; i < resultHandler.size(); i++) {
			if (!resultHandler.getResource(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@NotNull
	@Override
	public ItemStack getItem(int slot) {
		if (slot == SLOT_INPUT) {
			return inputHandler.getResource(0).toStack();
		} else if (slot == SLOT_RESULT) {
			return resultHandler.getResource(0).toStack();
		} else {
			return matterHandler.getResource(slot).toStack();
		}
	}

	@NotNull
	@Override
	public ItemStack removeItem(int slot, int count) {
		try (Transaction tx = Transaction.openRoot()) {
			ItemStack stack = ItemStack.EMPTY;
			if (slot == SLOT_INPUT) {
				ItemResource resource = inputHandler.getResource(0);
				if (inputHandler.extract(0, resource, count, tx) != count) return stack;
				stack = resource.toStack(count);
			} else if (slot == SLOT_RESULT) {
				ItemResource resource = resultHandler.getResource(0);
				if (resultHandler.extract(0, resource, count, tx) != count) return stack;
				stack = resource.toStack(count);
			} else {
				ItemResource resource = matterHandler.getResource(slot);
				if (resultHandler.extract(slot, resource, count, tx) != count) return stack;
				stack = resource.toStack(count);
			}
			tx.commit();
			return stack;
		}
	}

	@NotNull
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		try (Transaction tx = Transaction.openRoot()) {
			ItemStack stack = ItemStack.EMPTY;
			if (slot == SLOT_INPUT) {
				ItemResource resource = inputHandler.getResource(0);
				if (resource.isEmpty()) return stack;
				if (inputHandler.extract(0, resource, 1, tx) != 1) return stack;
				stack = resource.toStack();
			} else if (slot == SLOT_RESULT) {
				ItemResource resource = resultHandler.getResource(0);
				if (resource.isEmpty()) return stack;
				if (resultHandler.extract(0, resource, 1, tx) != 1) return stack;
				stack = resource.toStack();
			} else {
				ItemResource resource = matterHandler.getResource(slot);
				if (resource.isEmpty()) return stack;
				if (matterHandler.extract(slot, resource, 1, tx) != 1) return stack;
				stack = resource.toStack();
			}

			tx.commit();
			return stack;
		}
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
		try (Transaction tx = Transaction.openRoot()) {
			ItemResource resource;
			if (slot == SLOT_INPUT) {
				resource = inputHandler.getResource(0);
			} else if (slot == SLOT_RESULT) {
				resource = resultHandler.getResource(0);
			} else {
				resource = matterHandler.getResource(slot);
			}

			boolean flag = !stack.isEmpty() && resource.matches(stack);
			if (slot == SLOT_INPUT) {
				inputHandler.set(0, ItemResource.of(stack), stack.getCount());
			} else if (slot == SLOT_RESULT) {
				resultHandler.set(0, ItemResource.of(stack), stack.getCount());
			} else {
				matterHandler.set(slot, ItemResource.of(stack), stack.getCount());
			}
			tx.commit();

			if (stack.getCount() > this.getMaxStackSize()) {
				stack.setCount(this.getMaxStackSize());
			}

			if (slot == SLOT_INPUT && !flag) {
				assert level instanceof ServerLevel;
				this.compressingTotalTime = getTotalCompressingTime((ServerLevel) level, this, new SingleRecipeInput(this.inputHandler.getResource(0).toStack()));
				this.compressingProgress = 0;
				this.setChanged();
			}
		}
	}

	public static int getMatterValue(ServerLevel level, ItemStack stack) {
		int itemID = Item.getId(stack.getItem());
		if (cachedValues.containsKey(itemID)) {
			return cachedValues.get(itemID);
		}
		int value = level.recipeAccess().getRecipeFor(ThisRecipes.MATTER_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level)
				.map(holder -> holder.value().getMatterAmount()).orElse(getDefaultMatterValue(stack));
		cachedValues.put(itemID, value);
		return value;
	}

	public static int getDefaultMatterValue(ItemStack stack) {
		int defaultValue = 0;
		if (stack.getItem() instanceof BlockItem blockItem) {
			defaultValue = MatterUtil.getDefaultValue(blockItem);
		}
		return defaultValue;
	}

	private static int getTotalCompressingTime(ServerLevel level, OrganicMatterCompressorBlockEntity blockEntity, RecipeInput input) {
		return blockEntity.quickCheck.getRecipeFor(input, level)
				.map(holder -> holder.value().getCompressingTime()).orElse(900);
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		if (this.level == null) return false;
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			int minY = Mth.clamp(ThisConfig.COMMON.minY.get(), level.getMinY(), level.getMaxY());
			return this.worldPosition.getY() <= minY &&
					player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;

		}
	}

	@Override
	public void clearContent() {
		try (Transaction tx = Transaction.openRoot()) {
			for (int i = 0; i < matterHandler.size(); i++) {
				matterHandler.set(i, ItemResource.EMPTY, 0);
			}
			for (int i = 0; i < inputHandler.size(); i++) {
				inputHandler.set(i, ItemResource.EMPTY, 0);
			}
			for (int i = 0; i < resultHandler.size(); i++) {
				resultHandler.set(i, ItemResource.EMPTY, 0);
			}
			tx.commit();
		}
	}

	public ItemStacksResourceHandler getMatterInventory() {
		return matterHandler;
	}

	public ItemStacksResourceHandler getInputInventory() {
		return inputHandler;
	}

	public ItemStacksResourceHandler getResultInventory() {
		return resultHandler;
	}

	public ContainerData getDataAccess() {
		return dataAccess;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@NotNull
	@Override
	public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
		return saveCustomOnly(registries);
	}

	@Override
	public void handleUpdateTag(ValueInput input) {
		super.handleUpdateTag(input);
	}

	@NotNull
	@Override
	public CompoundTag getPersistentData() {
		CompoundTag tag = super.getPersistentData();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(ThisMatters.LOGGER)) {
			TagValueOutput tagvalueoutput = TagValueOutput.createWithContext(problemreporter$scopedcollector, level != null ? level.registryAccess() : VanillaRegistries.createLookup());
			this.saveAdditional(tagvalueoutput);
			tag.merge(tagvalueoutput.buildResult());
		}
		return tag;
	}

	public ResourceHandler<ItemResource> getHandler(@Nullable Direction side) {
		if (side == Direction.UP) {
			return inputHandler;
		} else if (side == Direction.DOWN) {
			return resultHandler;
		} else {
			return matterHandler;
		}
	}
}
