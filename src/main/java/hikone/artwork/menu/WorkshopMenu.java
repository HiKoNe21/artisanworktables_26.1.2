package hikone.artwork.menu;

import hikone.artwork.blockentity.AWBlockEntity;
import hikone.artwork.init.AWMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public class WorkshopMenu extends AWMenu
{
    public WorkshopMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(AWMenuTypes.WORKSHOP.get(), containerId, playerInventory, extraData);
    }

    public WorkshopMenu(int containerId, Inventory playerInventory, @Nullable AWBlockEntity blockEntity)
    {
        super(AWMenuTypes.WORKSHOP.get(), containerId, playerInventory, blockEntity);
    }
}
