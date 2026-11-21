package hikone.artwork.block;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import org.jspecify.annotations.NonNull;

public abstract class AWBlock extends Block implements EntityBlock
{
    // FIELDS ##########################################################################################################

    protected final AWEnumType type;
    protected final AWEnumTier tier;

    // CONSTRUCTORS ####################################################################################################

    protected AWBlock(BlockBehaviour.Properties properties, AWEnumType type, AWEnumTier tier)
    {
        super(properties.mapColor(MapColor.WOOD).noOcclusion());
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
    protected @NonNull InteractionResult useWithoutItem(
            @NonNull BlockState state,
            Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
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
    protected @NonNull InteractionResult useItemOn(
            ItemStack itemStack,
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull InteractionHand hand,
            @NonNull BlockHitResult hitResult)
    {
        if (itemStack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forPlayerInteraction(player, hand)) != null)
        {
            if (!level.isClientSide() && FluidUtil.interactWithFluidHandler(
                    player,
                    hand,
                    level,
                    pos,
                    hitResult.getDirection(),
                    null))
            {
                return InteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }
}
