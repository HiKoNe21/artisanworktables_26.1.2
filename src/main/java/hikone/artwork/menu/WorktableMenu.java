package hikone.artwork.menu;

import hikone.artwork.blockentity.AWBlockEntity;
import hikone.artwork.init.AWMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class WorktableMenu extends AWMenu
{
    public WorktableMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(AWMenuTypes.WORKTABLE.get(), containerId, playerInventory, extraData);
    }

    public WorktableMenu(int containerId, Inventory playerInventory, @Nullable AWBlockEntity blockEntity)
    {
        super(AWMenuTypes.WORKTABLE.get(), containerId, playerInventory, blockEntity);
    }
}
