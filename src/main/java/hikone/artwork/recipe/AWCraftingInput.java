package hikone.artwork.recipe;

import hikone.artwork.api.reference.AWEnumTier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

public class AWCraftingInput extends CraftingInput
{
    // STATICS #########################################################################################################

    public static final AWCraftingInput EMPTY = new AWCraftingInput(0, 0, AWEnumTier.WORKTABLE, List.of(), 0, 0, List.of(), FluidStack.EMPTY);

    public static AWCraftingInput of(
            int width, int height, AWEnumTier tier, List<ItemStack> items, List<ItemStack> secondary, FluidStack fluid)
    {
        var positioned = CraftingInput.ofPositioned(width, height, items);
        var input = positioned.input();
        return new AWCraftingInput(
                input.width(), input.height(), tier, input.items(), positioned.top(), positioned.left(), secondary, fluid);
    }

    // FIELDS ##########################################################################################################

    protected final int top;
    protected final int left;
    protected final AWEnumTier tier;
    protected final List<ItemStack> secondary;
    protected final FluidStack fluid;

    // CONSTRUCTORS ####################################################################################################

    protected AWCraftingInput(
            int width, int height, AWEnumTier tier, List<ItemStack> grid, int top, int left, List<ItemStack> secondary,
            FluidStack fluid)
    {
        super(width, height, grid);
        this.top = top;
        this.left = left;
        this.tier = tier;
        this.secondary = secondary;
        this.fluid = fluid;
    }

    // GETTERS #########################################################################################################

    public int getLeft()
    {
        return this.left;
    }

    public int getTop()
    {
        return this.top;
    }

    public AWEnumTier getTier()
    {
        return this.tier;
    }

    public List<ItemStack> getSecondary()
    {
        return this.secondary;
    }

    public FluidStack getFluid()
    {
        return this.fluid;
    }

    // OVERRIDES #######################################################################################################

    /// Effect only on grid
    @Override
    public boolean isEmpty()
    {
        return super.isEmpty();
    }

    /// Effect only on grid
    @Override
    public @NonNull List<ItemStack> items()
    {
        return super.items();
    }

    /// Effect only on grid
    @Override
    public int ingredientCount()
    {
        return super.ingredientCount();
    }

    /// Effect only on grid
    @Override
    public @NonNull ItemStack getItem(int index)
    {
        return super.getItem(index);
    }

    /// Effect only on grid
    @Override
    public int size()
    {
        return super.size();
    }

    @Override
    public boolean equals(@NonNull Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof AWCraftingInput other))
        {
            return false;
        }
        return super.equals(obj) && ItemStack.listMatches(this.secondary, other.secondary) && FluidStack.matches(
                this.fluid, other.fluid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(
                super.hashCode(), ItemStack.hashStackList(this.secondary),
                FluidStack.hashFluidAndComponents(this.fluid));
    }
}
