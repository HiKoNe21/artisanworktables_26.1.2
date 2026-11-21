package hikone.artwork.blockentity;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.init.AWBlockEntities;
import hikone.artwork.menu.WorkshopMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class WorkshopBlockEntity extends AWBlockEntity
{
    // CONSTRUCTORS ####################################################################################################

    public WorkshopBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(AWBlockEntities.get(AWEnumTier.WORKSHOP).get(), worldPosition, blockState);
    }

    // OVERRIDES #######################################################################################################

    @Override
    public int getGridSize()
    {
        return 5;
    }

    @Override
    protected @NonNull AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory)
    {
        return new WorkshopMenu(containerId, inventory, this);
    }
}
