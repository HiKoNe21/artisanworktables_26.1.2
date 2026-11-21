package hikone.artwork.menu;

import hikone.artwork.ArtWork;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.blockentity.AWBlockEntity;
import hikone.artwork.init.AWRecipeTypes;
import hikone.artwork.menu.inventory.AWInventory;
import hikone.artwork.menu.slot.AWExtraResultSlot;
import hikone.artwork.menu.slot.AWResultSlot;
import hikone.artwork.recipe.AWRecipe;
import hikone.artwork.recipe.AWShapedRecipe;
import hikone.artwork.recipe.AWShapelessRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public abstract class AWMenu extends AbstractContainerMenu
{
    // STATICS #########################################################################################################\

    public static final int RESULT_SLOT_START = 0;
    public static final int RESULT_SLOT_COUNT = 1;
    public static final int RESULT_SLOT_END = RESULT_SLOT_START + RESULT_SLOT_COUNT;
    public static final int EXTRA_RESULT_SLOT_START = RESULT_SLOT_END;
    public static final int EXTRA_RESULT_SLOT_COUNT = 3;
    public static final int EXTRA_RESULT_SLOT_END = EXTRA_RESULT_SLOT_START + EXTRA_RESULT_SLOT_COUNT;
    public static final int SECONDARY_SLOT_START = EXTRA_RESULT_SLOT_END;
    public static final int SECONDARY_SLOT_COUNT = 9;
    public static final int SECONDARY_SLOT_END = SECONDARY_SLOT_START + SECONDARY_SLOT_COUNT;
    public static final int GRID_SLOT_START = SECONDARY_SLOT_END;

    protected int GRID_SLOT_COUNT;
    protected int GRID_SLOT_END;

    protected int INV_SLOT_START;
    protected static final int INV_SLOT_COUNT = 27;
    protected int INV_SLOT_END;

    protected int USE_ROW_SLOT_START;
    protected static final int USE_ROW_SLOT_COUNT = 9;
    protected int USE_ROW_SLOT_END;

    protected static final FluidStacksResourceHandler EMPTY_FLUID_HANDLER = new FluidStacksResourceHandler(1, 0);

    // FIELDS ##########################################################################################################

    protected final Player player;
    protected final Level level;
    protected final SimpleContainer extra;
    protected final Container result;
    protected final AWInventory inventory;
    @Nullable
    protected final AWBlockEntity blockEntity;
    protected final int gridSize;

    // CONSTRUCTORS ####################################################################################################

    public AWMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        this(
                menuType, containerId, playerInventory, playerInventory.player.level()
                        .getBlockEntity(extraData.readBlockPos()) instanceof AWBlockEntity be ? be : null);
    }

    public AWMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable AWBlockEntity blockEntity)
    {
        super(menuType, containerId);

        this.player = playerInventory.player;
        this.level = playerInventory.player.level();
        this.blockEntity = blockEntity;
        this.result = new ResultContainer();
        this.extra = new SimpleContainer(3);

        switch (getTableTier())
        {
            case WORKTABLE ->
            {
                var container = blockEntity != null ? blockEntity : new SimpleContainer(9 + 9);
                this.inventory = new AWInventory(this, container, 3);
                gridSize = 3;

                // result
                this.addSlot(new AWResultSlot(this.inventory, this.result, 0, 124, 35));
                // extra
                this.addExtraOutputSlots(this.extra, 1, 3, 152, 17);
                // secondary
                this.addGridSlots(this.inventory, 0, 9, 1, 8, 75);
                // grid
                this.addGridSlots(this.inventory, 9, 3, 3, 30, 17);
                // player
                this.addStandardInventorySlots(playerInventory, 8, 106);
            }
            case WORKSTATION ->
            {
                var container = blockEntity != null ? blockEntity : new SimpleContainer(9 + 16);
                this.inventory = new AWInventory(this, container, 4);
                gridSize = 4;

                // result
                this.addSlot(new AWResultSlot(this.inventory, this.result, 0, 133, 53));
                // extra
                this.addExtraOutputSlots(this.extra, 3, 1, 116, 17);
                // secondary
                this.addGridSlots(this.inventory, 0, 9, 1, 8, 93);
                // grid
                this.addGridSlots(this.inventory, 9, 4, 4, 21, 17);
                // player
                this.addStandardInventorySlots(playerInventory, 8, 124);
            }
            case WORKSHOP ->
            {
                var container = blockEntity != null ? blockEntity : new SimpleContainer(9 + 25);
                this.inventory = new AWInventory(this, container, 5);
                gridSize = 5;

                // result
                this.addSlot(new AWResultSlot(this.inventory, this.result, 0, 144, 71));
                // extra
                this.addExtraOutputSlots(this.extra, 3, 1, 116, 35);
                // secondary
                this.addGridSlots(this.inventory, 0, 9, 1, 8, 111);
                // grid
                this.addGridSlots(this.inventory, 9, 5, 5, 21, 17);
                // player
                this.addStandardInventorySlots(playerInventory, 8, 142);
            }
            case null, default ->
            {
                var container = blockEntity != null ? blockEntity : new SimpleContainer(9 + 9);
                this.inventory = new AWInventory(this, container, 3);
                gridSize = 3;
            }
        }

        GRID_SLOT_COUNT = gridSize * gridSize;
        GRID_SLOT_END = GRID_SLOT_START + GRID_SLOT_COUNT;

        INV_SLOT_START = GRID_SLOT_END;
        INV_SLOT_END = INV_SLOT_START + INV_SLOT_COUNT;

        USE_ROW_SLOT_START = INV_SLOT_END;
        USE_ROW_SLOT_END = USE_ROW_SLOT_START + USE_ROW_SLOT_COUNT;

        this.slotsChanged(this.inventory);
    }

    // GETTERS #########################################################################################################

    public Player getPlayer()
    {
        return player;
    }

    public @Nullable AWBlockEntity getBlockEntity()
    {
        return blockEntity;
    }

    public FluidStacksResourceHandler getFluidHandler()
    {
        if (this.blockEntity == null)
        {
            return EMPTY_FLUID_HANDLER;
        }
        return this.blockEntity.getFluidHandler();
    }

    public AWEnumType getTableType()
    {
        return this.blockEntity != null ? this.blockEntity.getTableType() : AWEnumType.BASIC;
    }

    public AWEnumTier getTableTier()
    {
        return this.blockEntity != null ? this.blockEntity.getTableTier() : AWEnumTier.WORKTABLE;
    }

    public int getGridSize()
    {
        return this.gridSize;
    }

    public FluidStack getFluid()
    {
        var handler = this.getFluidHandler();
        int amount = handler.getAmountAsInt(0);
        if (amount <= 0)
        {
            return FluidStack.EMPTY;
        }
        return handler.getResource(0).toStack(amount);
    }

    public int getFluidCapacity()
    {
        var handler = this.getFluidHandler();
        return handler.getCapacityAsInt(0, handler.getResource(0));
    }

    public SimpleContainer getExtra()
    {
        return this.extra;
    }

    public Optional<AWRecipe> getCurrentRecipe()
    {
        var input = this.inventory.asCraftingInput();
        if (this.level.isClientSide())
        {
            var recipeManager = ArtWork.instance().getRecipeManager();
            if (recipeManager == null)
            {
                return Optional.empty();
            }
            else
            {
                return recipeManager.getRecipeFor(AWRecipeTypes.get(getTableType()), input, this.level).map(
                        RecipeHolder::value);
            }
        }
        else
        {
            return ((ServerLevel) this.level).recipeAccess().getRecipeFor(
                    AWRecipeTypes.get(getTableType()), input, this.level).map(RecipeHolder::value);
        }
    }

    // OVERRIDES #######################################################################################################

    @Override
    public void slotsChanged(@NonNull Container container)
    {
        if (this.level.isClientSide())
        {
            return;
        }

        var input = inventory.asCraftingInput();
        var maybeRecipe = ((ServerLevel) this.level).recipeAccess().getRecipeFor(
                AWRecipeTypes.get(getTableType()), input, this.level);

        var result = ItemStack.EMPTY;
        if (maybeRecipe.isPresent())
        {
            var recipe = maybeRecipe.get().value();
            var recipeResult = recipe.assemble(input);
            if (recipeResult.isItemEnabled(this.level.enabledFeatures()))
            {
                result = recipeResult;
            }
        }

        this.result.setItem(0, result);
        this.broadcastChanges();
    }

    @Override
    public void removed(@NonNull Player player)
    {
        super.removed(player);
        if (!this.level.isClientSide())
        {
            this.clearContainer(player, this.extra);
        }
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex)
    {
        var clicked = ItemStack.EMPTY;
        var slot = this.slots.get(slotIndex);

        if (slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex == RESULT_SLOT_START)
            {
                stack.getItem().onCraftedBy(stack, player);
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, clicked);
            }
            else if (slotIndex >= INV_SLOT_START && slotIndex < USE_ROW_SLOT_END)
            {
                if (!this.moveItemStackTo(stack, GRID_SLOT_START, GRID_SLOT_END, false) && !this.moveItemStackTo(
                        stack,
                        SECONDARY_SLOT_START, SECONDARY_SLOT_END, false))
                {
                    if (slotIndex < USE_ROW_SLOT_START)
                    {
                        if (!this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false))
                        {
                            return ItemStack.EMPTY;
                        }
                    }
                    else if (!this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false))
            {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty())
            {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
            if (slotIndex == RESULT_SLOT_START)
            {
                player.drop(stack, false);
            }
        }

        return clicked;
    }

    @Override
    public boolean canTakeItemForPickAll(@NonNull ItemStack carried, Slot target)
    {
        return target.container != this.result && super.canTakeItemForPickAll(carried, target);
    }

    @Override
    public boolean stillValid(@NonNull Player player)
    {
        return this.inventory.stillValid(player);
    }

    // OTHERS ##########################################################################################################

    protected void addGridSlots(Container container, int indexStart, int width, int height, int left, int top)
    {
        int index = indexStart;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                this.addSlot(new Slot(container, index++, left + x * 18, top + y * 18));
            }
        }
    }

    protected void addExtraOutputSlots(Container outputContainer, int width, int height, int left, int top)
    {
        int index = 0;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int slotIdx = index;
                this.addSlot(new AWExtraResultSlot(
                        outputContainer, index++, left + x * 18, top + y * 18, () -> this.getCurrentRecipe().map(
                        AWRecipe::getExtra).filter(list -> slotIdx < list.size()).map(list -> list.get(slotIdx).result()
                        .create()).orElse(ItemStack.EMPTY)));
            }
        }
    }

    public void destroyFluid()
    {
        if (this.blockEntity != null)
        {
            var handler = this.blockEntity.getFluidHandler();
            int amount = handler.getAmountAsInt(0);
            if (amount > 0)
            {
                try (var tx = Transaction.openRoot())
                {
                    handler.extract(0, handler.getResource(0), amount, tx);
                    tx.commit();
                }
                this.slotsChanged(this.inventory);
            }
        }
    }

    public void clearTable(ServerPlayer player)
    {
        var playerInv = player.getInventory();

        for (int i = SECONDARY_SLOT_START; i < SECONDARY_SLOT_END; i++)
        {
            returnSlotToPlayer(this.slots.get(i), playerInv);
        }

        for (int i = GRID_SLOT_START; i < GRID_SLOT_END; i++)
        {
            returnSlotToPlayer(this.slots.get(i), playerInv);
        }

        for (int i = EXTRA_RESULT_SLOT_START; i < EXTRA_RESULT_SLOT_COUNT; i++)
        {
            returnSlotToPlayer(this.slots.get(i), playerInv);
        }

        this.slotsChanged(this.inventory);
        this.broadcastFullState();
    }

    private void returnSlotToPlayer(Slot slot, Inventory playerInv)
    {
        if (slot.hasItem())
        {
            var stack = slot.getItem();
            slot.set(ItemStack.EMPTY);
            playerInv.placeItemBackInInventory(stack);
        }
    }

    public void transferRecipeFromJei(AWRecipe recipe, ServerPlayer player, boolean maxTransfer)
    {
        if (recipe.getTableType() != this.getTableType() || !recipe.matchTier(this.getTableTier()))
        {
            return;
        }

        this.clearTable(player);

        var playerInv = player.getInventory();
        var gridSlots = this.slots.subList(GRID_SLOT_START, GRID_SLOT_START + GRID_SLOT_COUNT);

        if (recipe instanceof AWShapedRecipe shaped)
        {
            this.placeShapedIngredients(shaped, gridSlots, playerInv, maxTransfer);
        }
        else if (recipe instanceof AWShapelessRecipe shapeless)
        {
            this.placeShapelessIngredients(shapeless, gridSlots, playerInv, maxTransfer);
        }

        this.placeSecondaryIngredients(recipe, playerInv, maxTransfer);
        this.slotsChanged(this.inventory);
        this.broadcastFullState();
    }

    private void placeShapedIngredients(AWShapedRecipe recipe, List<Slot> gridSlots, Inventory playerInv, boolean maxTransfer)
    {
        var pattern = recipe.getPattern();
        int pWidth = pattern.width();
        int pHeight = pattern.height();
        var ingredients = pattern.ingredients();

        int maxPasses = maxTransfer ? 64 : 1;

        for (int pass = 0; pass < maxPasses; pass++)
        {
            boolean placedAnyInPass = false;

            for (int y = 0; y < pHeight; y++)
            {
                for (int x = 0; x < pWidth; x++)
                {
                    if (x >= this.gridSize || y >= this.gridSize)
                    {
                        continue;
                    }

                    var optIngredient = ingredients.get(x + y * pWidth);
                    if (optIngredient.isEmpty())
                    {
                        continue;
                    }

                    Slot targetSlot = gridSlots.get(x + y * this.gridSize);
                    if (tryPlaceOneItem(optIngredient.get(), targetSlot, playerInv))
                    {
                        placedAnyInPass = true;
                    }
                }
            }

            if (!placedAnyInPass)
            {
                break;
            }
        }
    }

    private void placeShapelessIngredients(AWShapelessRecipe recipe, List<Slot> gridSlots, Inventory playerInv, boolean maxTransfer)
    {
        var ingredients = recipe.getIngredients();
        int limit = Math.min(ingredients.size(), gridSlots.size());
        int maxPasses = maxTransfer ? 64 : 1;

        for (int pass = 0; pass < maxPasses; pass++)
        {
            boolean placedAnyInPass = false;

            for (int i = 0; i < limit; i++)
            {
                if (tryPlaceOneItem(ingredients.get(i), gridSlots.get(i), playerInv))
                {
                    placedAnyInPass = true;
                }
            }

            if (!placedAnyInPass)
            {
                break;
            }
        }
    }

    private boolean tryPlaceOneItem(Ingredient ingredient, Slot targetSlot, Inventory playerInv)
    {
        ItemStack current = targetSlot.getItem();
        if (!current.isEmpty() && current.getCount() >= Math.min(current.getMaxStackSize(), targetSlot.getMaxStackSize()))
        {
            return false;
        }

        for (int invSlot = 0; invSlot < playerInv.getContainerSize(); invSlot++)
        {
            ItemStack invStack = playerInv.getItem(invSlot);
            if (invStack.isEmpty() || !ingredient.test(invStack))
            {
                continue;
            }

            if (current.isEmpty())
            {
                targetSlot.set(invStack.split(1));
                return true;
            }
            else if (ItemStack.isSameItemSameComponents(current, invStack))
            {
                current.grow(1);
                invStack.shrink(1);
                return true;
            }
        }

        return false;
    }

    private void placeSecondaryIngredients(AWRecipe recipe, Inventory playerInv, boolean maxTransfer)
    {
        var secondaryReqs = recipe.getSecondary();
        if (secondaryReqs == null || secondaryReqs.isEmpty())
        {
            return;
        }

        for (int reqIdx = 0; reqIdx < Math.min(secondaryReqs.size(), 9); reqIdx++)
        {
            var req = secondaryReqs.get(reqIdx);
            var ingredient = req.sizedIngredient().ingredient();
            int reqCount = req.sizedIngredient().count();
            int neededDamage = req.damage();

            int neededCount = (maxTransfer && !req.catalyst() && neededDamage <= 0) ? 64 : reqCount;

            Slot targetSlot = this.slots.get(SECONDARY_SLOT_START + reqIdx);

            for (int invSlot = 0; invSlot < playerInv.getContainerSize(); invSlot++)
            {
                if (neededCount <= 0)
                {
                    break;
                }

                ItemStack invStack = playerInv.getItem(invSlot);
                if (invStack.isEmpty() || !ingredient.test(invStack))
                {
                    continue;
                }

                if (invStack.isDamageableItem() && neededDamage > 0)
                {
                    int remainingDurability = invStack.getMaxDamage() - invStack.getDamageValue();
                    if (remainingDurability < neededDamage)
                    {
                        continue;
                    }
                }

                if (targetSlot.getItem().isEmpty())
                {
                    int toMove = Math.min(neededCount, Math.min(invStack.getCount(), targetSlot.getMaxStackSize()));
                    targetSlot.set(invStack.split(toMove));
                    neededCount -= toMove;
                }
                else if (ItemStack.isSameItemSameComponents(targetSlot.getItem(), invStack))
                {
                    int space = targetSlot.getMaxStackSize() - targetSlot.getItem().getCount();
                    int toMove = Math.min(space, Math.min(neededCount, invStack.getCount()));
                    if (toMove > 0)
                    {
                        targetSlot.getItem().grow(toMove);
                        invStack.shrink(toMove);
                        neededCount -= toMove;
                    }
                }
            }
        }
    }
}
