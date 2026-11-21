package hikone.artwork.menu.slot;

import hikone.artwork.menu.inventory.AWInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class AWResultSlot extends Slot
{
    private final AWInventory inventory;
    private int removeCount;

    public AWResultSlot(
            AWInventory inventory,
            Container container,
            int slot,
            int x,
            int y)
    {
        super(container, slot, x, y);
        this.inventory = inventory;
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack itemStack)
    {
        return false;
    }

    @Override
    public boolean mayPickup(@NonNull Player player)
    {
        if (!this.hasItem())
        {
            return false;
        }

        return this.inventory.getMenu().getCurrentRecipe().map(awRecipe -> awRecipe.getExperience().matches(player)).orElse(true);

    }

    @Override
    public void onTake(@NonNull Player player, @NonNull ItemStack carried)
    {
        this.checkTakeAchievements(carried);

        this.inventory.getMenu().getCurrentRecipe().ifPresent(r -> r.getExperience().consumeFrom(player));
        this.inventory.consumeInputs(player);
        this.inventory.getMenu().slotsChanged(this.inventory);
    }

    @Override
    protected void onQuickCraft(@NonNull ItemStack picked, int count)
    {
        this.removeCount += count;
        this.checkTakeAchievements(picked);
    }

    @Override
    protected void onSwapCraft(int count)
    {
        this.removeCount += count;
    }

    @Override
    protected void checkTakeAchievements(ItemStack carried)
    {
        carried.onCraftedBy(this.inventory.getMenu().getPlayer(), this.removeCount);
        this.removeCount = 0;
    }
}
