package hikone.artwork.block;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AWBlock extends Block implements EntityBlock, SimpleWaterloggedBlock
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // FIELDS ##########################################################################################################

    protected final AWEnumType type;
    protected final AWEnumTier tier;

    // CONSTRUCTORS ####################################################################################################

    protected AWBlock(BlockBehaviour.Properties properties, AWEnumType type, AWEnumTier tier)
    {
        super(properties.mapColor(MapColor.WOOD).noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
        this.type = type;
        this.tier = tier;
    }

    // GETTERS #########################################################################################################

    public AWEnumType getTableType()
    {
        return this.type;
    }

    public AWEnumTier getTableTier()
    {
        return this.tier;
    }

    // OVERRIDE ########################################################################################################


    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context)
    {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        var state = super.getStateForPlacement(context);
        if (state != null)
        {
            return state.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
        return null;
    }

    @Override
    protected @NotNull FluidState getFluidState(@NotNull BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected @NotNull BlockState updateShape(
            @NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
            @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos)
    {
        if (state.getValue(WATERLOGGED))
        {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(
            @NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player,
            @NonNull BlockHitResult hitResult)
    {
        if (!level.isClientSide())
        {
            var be = level.getBlockEntity(pos);
            if (be instanceof MenuProvider menuProvider)
            {
                player.openMenu(menuProvider, pos);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NonNull ItemInteractionResult useItemOn(
            ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
            @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult)
    {
        if (itemStack.getCapability(Capabilities.FluidHandler.ITEM, null) != null)
        {
            if (!level.isClientSide() && FluidUtil.interactWithFluidHandler(
                    player, hand, level, pos,
                    hitResult.getDirection()))
            {
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }
}
