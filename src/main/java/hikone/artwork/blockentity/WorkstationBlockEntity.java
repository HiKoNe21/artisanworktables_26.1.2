package hikone.artwork.blockentity;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.init.AWBlockEntities;
import hikone.artwork.menu.WorkstationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WorkstationBlockEntity extends AWBlockEntity
{
    // CONSTRUCTORS ####################################################################################################

    public WorkstationBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(AWBlockEntities.get(AWEnumTier.WORKSTATION).get(), worldPosition, blockState);
    }

    // OVERRIDES #######################################################################################################

    @Override
    public int getGridSize()
    {
        return 4;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory)
    {
        return new WorkstationMenu(containerId, inventory, this);
    }
}
