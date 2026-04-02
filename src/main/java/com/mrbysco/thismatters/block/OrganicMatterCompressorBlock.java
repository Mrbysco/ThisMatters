package com.mrbysco.thismatters.block;

import com.mojang.serialization.MapCodec;
import com.mrbysco.thismatters.blockentity.OrganicMatterCompressorBlockEntity;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class OrganicMatterCompressorBlock extends BaseEntityBlock {
	public static final MapCodec<OrganicMatterCompressorBlock> CODEC = simpleCodec(OrganicMatterCompressorBlock::new);

	public OrganicMatterCompressorBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			if (level.getBlockEntity(pos) instanceof OrganicMatterCompressorBlockEntity compressorBE) {
				boolean flag = player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
				if (flag) {
					int minY = Mth.clamp(ThisConfig.COMMON.minY.get(), level.getMinY(), level.getMaxY());
					boolean flag2 = pos.getY() <= minY;
					if (flag2) {
						player.openMenu(compressorBE, pos);
					} else {
						player.sendOverlayMessage(Component.translatable("thismatters.organic_matter_compressor.not_low_enough").withStyle(ChatFormatting.RED));
					}
				}
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		if (blockEntity instanceof OrganicMatterCompressorBlockEntity compressorBlockEntity) {
			if (level instanceof ServerLevel) {
				Containers.dropContents(level, pos, compressorBlockEntity);
			}

			level.updateNeighbourForOutputSignal(pos, this);
		}
		super.playerDestroy(level, player, pos, state, blockEntity, tool);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OrganicMatterCompressorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return createCompressorTicker(level, blockEntityType, ThisRegistry.ORGANIC_MATTER_COMPRESSOR_BE.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createCompressorTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends OrganicMatterCompressorBlockEntity> compressorType) {
		return level.isClientSide() ? null : createTickerHelper(type, compressorType, OrganicMatterCompressorBlockEntity::serverTick);
	}
}
