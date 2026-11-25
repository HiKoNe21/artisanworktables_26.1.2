package hikone.artwork.blockentity;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.init.AWBlockEntities;
import hikone.artwork.menu.WorktableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WorktableBlockEntity extends AWBlockEntity
{
    // CONSTRUCTORS ####################################################################################################

    public WorktableBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(AWBlockEntities.get(AWEnumTier.WORKTABLE).get(), worldPosition, blockState);
    }

    // OVERRIDES #######################################################################################################

    @Override
    public int getGridSize()
    {
        return 3;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory)
    {
        return new WorktableMenu(containerId, inventory, this);
    }
}
