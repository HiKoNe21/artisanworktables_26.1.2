package hikone.artwork.block;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.blockentity.WorkstationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class WorkstationBlock extends AWBlock
{
    // STATICS #########################################################################################################

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.25, 0.9375),
            Shapes.box(0.1875, 0.25, 0.1875, 0.8125, 0.5, 0.8125),
            Shapes.box(0, 0.5, 0, 1, 1, 1),
            Shapes.box(0.0625, 0.4375, 0.0625, 0.9375, 0.5625, 0.9375));

    // CONSTRUCTORS ####################################################################################################

    public WorkstationBlock(
            BlockBehaviour.Properties properties,
            AWEnumType type,
            SoundType soundType,
            float destroyTime,
            float explosionResistance)
    {
        super(
                properties
                        .sound(soundType)
                        .strength(destroyTime, explosionResistance), type, AWEnumTier.WORKSTATION);
    }

    // OVERRIDE ########################################################################################################

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos worldPosition, @NotNull BlockState blockState)
    {
        return new WorkstationBlockEntity(worldPosition, blockState);
    }
}
