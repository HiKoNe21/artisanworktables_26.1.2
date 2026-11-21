package hikone.artwork.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class AWExtraResultSlot extends Slot
{
    private final Supplier<ItemStack> ghostSupplier;

    public AWExtraResultSlot(Container container, int slot, int x, int y, Supplier<ItemStack> ghostSupplier)
    {
        super(container, slot, x, y);
        this.ghostSupplier = ghostSupplier;
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack stack)
    {
        return false;
    }

    public ItemStack getGhostStack()
    {
        return this.ghostSupplier.get();
    }
}
