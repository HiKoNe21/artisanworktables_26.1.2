package hikone.artwork.menu;

import hikone.artwork.blockentity.AWBlockEntity;
import hikone.artwork.init.AWMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class WorkstationMenu extends AWMenu
{
    public WorkstationMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(AWMenuTypes.WORKSTATION.get(), containerId, playerInventory, extraData);
    }

    public WorkstationMenu(int containerId, Inventory playerInventory, @Nullable AWBlockEntity blockEntity)
    {
        super(AWMenuTypes.WORKSTATION.get(), containerId, playerInventory, blockEntity);
    }
}
