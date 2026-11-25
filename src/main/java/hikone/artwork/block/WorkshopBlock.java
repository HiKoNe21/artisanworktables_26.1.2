package hikone.artwork.block;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.blockentity.WorkshopBlockEntity;
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

public class WorkshopBlock extends AWBlock
{
    // STATICS #########################################################################################################

    public static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0, 0, 0, 1, 0.4375, 1),
            Shapes.box(0.0625, 0.4375, 0.0625, 0.9375, 0.5625, 0.9375),
            Shapes.box(0, 0.5, 0, 1, 1, 1));

    // CONSTRUCTORS ####################################################################################################

    public WorkshopBlock(
            BlockBehaviour.Properties properties,
            AWEnumType type,
            SoundType soundType,
            float destroyTime,
            float explosionResistance)
    {
        super(
                properties
                        .sound(soundType)
                        .strength(destroyTime, explosionResistance), type, AWEnumTier.WORKSHOP);
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
        return new WorkshopBlockEntity(worldPosition, blockState);
    }
}
