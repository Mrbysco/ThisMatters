package com.mrbysco.thismatters.blockentity;

import com.mrbysco.thismatters.ThisMatters;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.menu.OrganicMatterCompressorMenu;
import com.mrbysco.thismatters.recipe.CompressingRecipe;
import com.mrbysco.thismatters.registry.ThisRecipes;
import com.mrbysco.thismatters.registry.ThisRegistry;
import com.mrbysco.thismatters.util.CapabilityHelper;
import com.mrbysco.thismatters.util.MatterUtil;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;

public class OrganicMatterCompressorBlockEntity extends BaseContainerBlockEntity implements RecipeCraftingHolder {
	public static final TreeMap<Integer, Integer> cachedValues = new TreeMap<>();

	public final ItemStackHandler matterHandler = new ItemStackHandler(9) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			if (level != null && !level.isClientSide && level instanceof ServerLevel serverLevel)
				return getMatterValue(serverLevel, stack) > 0;
			return false;
		}
	};
	public final ItemStackHandler inputHandler = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return super.isItemValid(slot, stack);
		}

		@Override
		protected void onContentsChanged(int slot) {
			if (level != null && !level.isClientSide && level instanceof ServerLevel serverLevel) {
				compressingTotalTime = getTotalCompressingTime(serverLevel, OrganicMatterCompressorBlockEntity.this, new SingleRecipeInput(getStackInSlot(0)));
				compressingProgress = 0;
			}
			setChanged();
		}
	};
	public final ItemStackHandler resultHandler = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
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

	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
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
	public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.matterHandler.deserializeNBT(registries, tag.getCompound("MatterStackHandler"));
		this.inputHandler.deserializeNBT(registries, tag.getCompound("InputStackHandler"));
		this.resultHandler.deserializeNBT(registries, tag.getCompound("ResultStackHandler"));

		this.matterAmount = tag.getInt("MatterAmount");
		this.maxMatter = tag.getInt("MaxMatter");
		this.compressingProgress = tag.getInt("CompressingTime");
		this.compressingTotalTime = tag.getInt("CompressingTotalTime");
		CompoundTag compoundtag = tag.getCompound("RecipesUsed");

		for (String s : compoundtag.getAllKeys()) {
			this.recipesUsed.put(ResourceLocation.tryParse(s), compoundtag.getInt(s));
		}
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putFloat("MatterAmount", this.matterAmount);
		tag.putFloat("MaxMatter", this.maxMatter);
		tag.putInt("CompressingTime", this.compressingProgress);
		tag.putInt("CompressingTotalTime", this.compressingTotalTime);

		tag.put("MatterStackHandler", matterHandler.serializeNBT(registries));
		tag.put("InputStackHandler", inputHandler.serializeNBT(registries));
		tag.put("ResultStackHandler", resultHandler.serializeNBT(registries));

		CompoundTag compoundtag = new CompoundTag();
		this.recipesUsed.forEach((location, index) -> compoundtag.putInt(location.toString(), index));
		tag.put("RecipesUsed", compoundtag);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, OrganicMatterCompressorBlockEntity compressorBlockEntity) {
		if (level instanceof ServerLevel serverLevel) {
			for (int i = 0; i < compressorBlockEntity.matterHandler.getSlots(); ++i) {
				ItemStack itemstack = compressorBlockEntity.matterHandler.getStackInSlot(i);
				if (!itemstack.isEmpty()) {
					int matterValue = getMatterValue(serverLevel, itemstack);
					if (matterValue > 0) {
						for (int j = 0; j < itemstack.getCount(); ++j) {
							if (compressorBlockEntity.increaseMatter(matterValue)) {
								compressorBlockEntity.refreshClient();
								itemstack.shrink(1);
							} else {
								break;
							}
						}
					}
				}
			}

			ItemStack inputStack = compressorBlockEntity.inputHandler.getStackInSlot(0);
			if (compressorBlockEntity.hasMatter() && !inputStack.isEmpty()) {
				RecipeHolder<CompressingRecipe> recipeHolder = compressorBlockEntity.quickCheck.getRecipeFor(new SingleRecipeInput(inputStack), serverLevel).orElse(null);
				if (recipeHolder == null) return;
				CompressingRecipe recipe = recipeHolder.value();
				int i = compressorBlockEntity.getMaxStackSize();
				if (compressorBlockEntity.hasMatter() && compressorBlockEntity.canCompress(recipe, i)) {
					++compressorBlockEntity.compressingProgress;
					if (compressorBlockEntity.compressingProgress == compressorBlockEntity.compressingTotalTime) {
						compressorBlockEntity.compressingProgress = 0;
						compressorBlockEntity.compressingTotalTime = getTotalCompressingTime(serverLevel, compressorBlockEntity, new SingleRecipeInput(inputStack));
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

	public void refreshClient() {
		setChanged();
		if (level == null) return;
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	private boolean canCompress(@Nullable Recipe<?> recipe, int count) {
		if (this.level != null && !inputHandler.getStackInSlot(0).isEmpty() && recipe != null) {
			ItemStack assembledStack = ((CompressingRecipe) recipe).assemble(new SingleRecipeInput(inputHandler.getStackInSlot(0)), this.level.registryAccess());
			if (assembledStack.isEmpty()) {
				return false;
			} else {
				ItemStack resultStack = resultHandler.getStackInSlot(0);
				if (resultStack.isEmpty()) {
					return true;
				} else if (!ItemStack.isSameItem(resultStack, assembledStack)) {
					return false;
				} else if (resultStack.getCount() + assembledStack.getCount() <= count && resultStack.getCount() + assembledStack.getCount() <= resultStack.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
					return true;
				} else {
					return resultStack.getCount() + assembledStack.getCount() <= assembledStack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
				}
			}
		} else {
			return false;
		}
	}

	private boolean compress(@Nullable Recipe<?> recipe, int count) {
		if (this.level != null && recipe != null && this.canCompress(recipe, count)) {
			ItemStack inputStack = inputHandler.getStackInSlot(0);
			ItemStack assembledStack = ((CompressingRecipe) recipe).assemble(new SingleRecipeInput(inputHandler.getStackInSlot(0)), this.level.registryAccess());
			ItemStack resultStack = resultHandler.getStackInSlot(0);
			if (resultStack.isEmpty()) {
				resultHandler.setStackInSlot(0, assembledStack.copy());
			} else if (resultStack.is(assembledStack.getItem())) {
				resultStack.grow(assembledStack.getCount());
			}

			inputStack.shrink(1);
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
			ResourceLocation resourcelocation = recipeHolder.id().location();
			this.recipesUsed.addTo(resourcelocation, 1);
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
		for (int i = 0; i < matterHandler.getSlots(); i++) {
			if (!matterHandler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		for (int i = 0; i < inputHandler.getSlots(); i++) {
			if (!inputHandler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		for (int i = 0; i < resultHandler.getSlots(); i++) {
			if (!resultHandler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@NotNull
	@Override
	public ItemStack getItem(int slot) {
		if (slot == SLOT_INPUT) {
			return inputHandler.getStackInSlot(0);
		} else if (slot == SLOT_RESULT) {
			return resultHandler.getStackInSlot(0);
		} else {
			return matterHandler.getStackInSlot(slot);
		}
	}

	@NotNull
	@Override
	public ItemStack removeItem(int slot, int count) {
		if (slot == SLOT_INPUT) {
			return CapabilityHelper.removeItem(inputHandler, 0, count);
		} else if (slot == SLOT_RESULT) {
			return CapabilityHelper.removeItem(resultHandler, 0, count);
		} else {
			return CapabilityHelper.removeItem(matterHandler, slot, count);
		}
	}

	@NotNull
	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot == SLOT_INPUT) {
			return CapabilityHelper.takeItem(inputHandler, 0);
		} else if (slot == SLOT_RESULT) {
			return CapabilityHelper.takeItem(resultHandler, 0);
		} else {
			return CapabilityHelper.takeItem(matterHandler, slot);
		}
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
		ItemStack itemstack;
		if (slot == SLOT_INPUT) {
			itemstack = inputHandler.getStackInSlot(0);
		} else if (slot == SLOT_RESULT) {
			itemstack = resultHandler.getStackInSlot(0);
		} else {
			itemstack = matterHandler.getStackInSlot(slot);
		}

		boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack);
		if (slot == SLOT_INPUT) {
			inputHandler.setStackInSlot(0, stack);
		} else if (slot == SLOT_RESULT) {
			resultHandler.setStackInSlot(0, stack);
		} else {
			matterHandler.setStackInSlot(slot, stack);
		}
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}

		if (slot == SLOT_INPUT && !flag) {
			assert level instanceof ServerLevel;
			this.compressingTotalTime = getTotalCompressingTime((ServerLevel) level, this, new SingleRecipeInput(this.inputHandler.getStackInSlot(0)));
			this.compressingProgress = 0;
			this.setChanged();
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
		for (int i = 0; i < matterHandler.getSlots(); i++) {
			matterHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
		for (int i = 0; i < inputHandler.getSlots(); i++) {
			inputHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
		for (int i = 0; i < resultHandler.getSlots(); i++) {
			resultHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public IItemHandler getMatterInventory() {
		return matterHandler;
	}

	public IItemHandler getInputInventory() {
		return inputHandler;
	}

	public IItemHandler getResultInventory() {
		return resultHandler;
	}

	public ContainerData getDataAccess() {
		return dataAccess;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket packet, @NotNull HolderLookup.Provider registries) {
		this.loadAdditional(packet.getTag(), registries);
	}

	@NotNull
	@Override
	public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
		return saveCustomOnly(registries);
	}

	@Override
	public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
		this.loadAdditional(tag, registries);
	}

	@NotNull
	@Override
	public CompoundTag getPersistentData() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt, level != null ? level.registryAccess() : VanillaRegistries.createLookup());
		return nbt;
	}

	public IItemHandler getHandler(@Nullable Direction side) {
		if (side == Direction.UP) {
			return inputHandler;
		} else if (side == Direction.DOWN) {
			return resultHandler;
		} else {
			return matterHandler;
		}
	}
}
