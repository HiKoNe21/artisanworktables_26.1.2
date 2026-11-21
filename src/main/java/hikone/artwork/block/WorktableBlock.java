package hikone.artwork.block;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.blockentity.WorktableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public class WorktableBlock extends AWBlock
{
    // STATICS #########################################################################################################

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0, 0.4375, 0, 1, 0.9375, 1),
            Shapes.box(0.125, 0.125, 0.75, 0.25, 0.4375, 0.875),
            Shapes.box(0.0625, 0, 0.6875, 0.3125, 0.125, 0.9375),
            Shapes.box(0.75, 0.125, 0.75, 0.875, 0.4375, 0.875),
            Shapes.box(0.6875, 0, 0.6875, 0.9375, 0.125, 0.9375),
            Shapes.box(0.75, 0.125, 0.125, 0.875, 0.4375, 0.25),
            Shapes.box(0.6875, 0, 0.0625, 0.9375, 0.125, 0.3125),
            Shapes.box(0.125, 0.125, 0.125, 0.25, 0.4375, 0.25),
            Shapes.box(0.0625, 0, 0.0625, 0.3125, 0.125, 0.3125),
            Shapes.box(0.1875, 0.1875, 0.25, 0.25, 0.25, 0.75),
            Shapes.box(0.75, 0.1875, 0.25, 0.8125, 0.25, 0.75),
            Shapes.box(0.25, 0.1875, 0.1875, 0.75, 0.25, 0.25),
            Shapes.box(0.25, 0.1875, 0.75, 0.75, 0.25, 0.8125),
            Shapes.box(0.0625, 0.375, 0.0625, 0.9375, 0.4375, 0.9375));

    // CONSTRUCTORS ####################################################################################################

    public WorktableBlock(
            BlockBehaviour.Properties properties,
            AWEnumType type,
            SoundType soundType,
            float destroyTime,
            float explosionResistance)
    {
        super(
                properties
                        .sound(soundType)
                        .strength(destroyTime, explosionResistance), type, AWEnumTier.WORKTABLE);
    }

    // OVERRIDE ########################################################################################################

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public @NonNull BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        return new WorktableBlockEntity(worldPosition, blockState);
    }
}
