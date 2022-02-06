package com.mrbysco.thismatters.block;

import com.mrbysco.thismatters.blockentity.OrganicMatterCompressorBlockEntity;
import com.mrbysco.thismatters.config.ThisConfig;
import com.mrbysco.thismatters.registry.ThisRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class OrganicMatterCompressorBlock extends BaseEntityBlock {
	public OrganicMatterCompressorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			if (level.getBlockEntity(pos) instanceof OrganicMatterCompressorBlockEntity compressorBE) {
				int minY = Mth.clamp(ThisConfig.COMMON.minY.get(), level.getMinBuildHeight(), level.getMaxBuildHeight());
				boolean flag = pos.getY() <= minY &&
						player.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;

				if(!flag) {
					player.displayClientMessage(new TranslatableComponent("thismatters.organic_matter_compressor.not_low_enough").withStyle(ChatFormatting.RED), true);
				} else {
					NetworkHooks.openGui((ServerPlayer) player, compressorBE, pos);
				}
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof OrganicMatterCompressorBlockEntity) {
				((OrganicMatterCompressorBlockEntity)blockentity).setCustomName(stack.getHoverName());
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacementState, boolean isMoving) {
		if (!state.is(replacementState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof OrganicMatterCompressorBlockEntity) {
				if (level instanceof ServerLevel) {
					Containers.dropContents(level, pos, (OrganicMatterCompressorBlockEntity)blockentity);
				}

				level.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, level, pos, replacementState, isMoving);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}

	@Override
	public RenderShape getRenderShape(BlockState p_48727_) {
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
		return level.isClientSide ? null : createTickerHelper(type, compressorType, OrganicMatterCompressorBlockEntity::serverTick);
	}
}
