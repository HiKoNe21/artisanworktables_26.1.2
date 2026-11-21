package hikone.artwork.menu.inventory;

import com.google.common.collect.Lists;
import hikone.artwork.init.AWRecipeTypes;
import hikone.artwork.menu.AWMenu;
import hikone.artwork.recipe.AWCraftingInput;
import hikone.artwork.recipe.format.AWSizedIngredient;
import hikone.artwork.recipe.AWRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class AWInventory extends TransientCraftingContainer
{
    // FIELDS ##########################################################################################################

    private final AWMenu menu;
    private final Container container;

    private long lastSoundTime = 0L;

    // CONSTRUCTORS ####################################################################################################

    public AWInventory(AWMenu menu, Container container, int size)
    {
        super(menu, size, size);
        this.menu = menu;
        this.container = container;
    }

    // GETTERS #########################################################################################################

    public AWMenu getMenu()
    {
        return menu;
    }

    // OVERRIDES #######################################################################################################

    @Override
    public int getContainerSize()
    {
        return this.container.getContainerSize();
    }

    @Override
    public boolean isEmpty()
    {
        return this.container.isEmpty();
    }

    @Override
    public @NonNull ItemStack getItem(int slot)
    {
        return this.container.getItem(slot);
    }

    @Override
    public @NonNull ItemStack removeItem(int slot, int count)
    {
        var r = this.container.removeItem(slot, count);
        this.menu.slotsChanged(this.container);
        return r;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int slot)
    {
        return this.container.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NonNull ItemStack itemStack)
    {
        this.container.setItem(slot, itemStack);
        this.menu.slotsChanged(this.container);
    }

    @Override
    public void clearContent()
    {
        this.container.clearContent();
    }

    @Override
    public @NonNull List<ItemStack> getItems()
    {
        return Lists.newArrayList(this.container.iterator());
    }

    // OTHERS ##########################################################################################################

    public List<ItemStack> getSecondary()
    {
        return getItems().subList(0, 9);
    }

    public List<ItemStack> getGrid()
    {
        int gridSize = this.getWidth() * this.getHeight();
        return getItems().subList(9, 9 + gridSize);
    }

    public FluidStack getFluid()
    {
        return menu.getFluid();
    }

    public AWCraftingInput asCraftingInput()
    {
        var width = this.getWidth();
        var height = this.getHeight();
        return AWCraftingInput.of(width, height, this.menu.getTableTier(), getGrid(), getSecondary(), getFluid());
    }

    public void consumeInputs(Player player)
    {
        if (player.level().isClientSide())
        {
            return;
        }

        var sPlayer = (ServerPlayer) player;
        var level = sPlayer.level();

        var input = asCraftingInput();
        var maybeRecipe = level.recipeAccess().getRecipeFor(AWRecipeTypes.get(this.menu.getTableType()), input, level);
        if (maybeRecipe.isEmpty())
        {
            return;
        }

        var recipe = maybeRecipe.get().value();

        consumeGrid(input, recipe, sPlayer);
        consumeSecondary(input, recipe, sPlayer);
        consumeFluid(input, recipe);
        produceExtraResults(recipe, sPlayer);

        long gameTime = sPlayer.level().getGameTime();
        if (this.lastSoundTime != gameTime)
        {
            recipe.playCraftSound(sPlayer.level(), this.menu.getBlockEntity() != null ? this.menu.getBlockEntity().getBlockPos() : sPlayer.blockPosition());
            this.lastSoundTime = gameTime;
        }
    }

    public void consumeGrid(AWCraftingInput input, AWRecipe recipe, ServerPlayer player)
    {
        var remaining = recipe.getRemainingItems(input);

        for (int y = 0; y < input.height(); y++)
        {
            for (int x = 0; x < input.width(); x++)
            {
                var index = 9 + x + input.getLeft() + (y + input.getTop()) * this.getWidth();
                var slotStack = this.getItem(index);

                if (!slotStack.isEmpty())
                {
                    this.removeItem(index, 1);
                    slotStack = this.getItem(index);
                }

                var remainingStack = remaining.get(9 + x + y * input.width());
                if (!remainingStack.isEmpty())
                {
                    if (slotStack.isEmpty())
                    {
                        this.setItem(index, remainingStack);
                    }
                    else if (ItemStack.isSameItemSameComponents(slotStack, remainingStack))
                    {
                        remainingStack.grow(slotStack.getCount());
                        this.setItem(index, remainingStack);
                    }
                    else if (!player.getInventory().add(remainingStack))
                    {
                        player.drop(remainingStack, false);
                    }
                }
            }
        }
    }

    public void consumeSecondary(AWCraftingInput input, AWRecipe recipe, ServerPlayer player)
    {
        var secondaryIngredients = recipe.getSecondary();
        if (secondaryIngredients == null || secondaryIngredients.isEmpty())
        {
            return;
        }

        var remaining = recipe.getRemainingItems(input);
        boolean[] usedSlots = new boolean[9];

        for (AWSizedIngredient requiredIngredient : secondaryIngredients)
        {
            if (requiredIngredient.catalyst())
            {
                continue;
            }

            int neededCount = requiredIngredient.sizedIngredient().count();
            var neededIngredient = requiredIngredient.sizedIngredient().ingredient();
            int neededDamage = requiredIngredient.damage();

            for (int secIdx = 0; secIdx < 9; secIdx++)
            {
                if (neededCount <= 0)
                {
                    break;
                }
                if (usedSlots[secIdx])
                {
                    continue;
                }

                var slotStack = this.getItem(secIdx);

                if (!slotStack.isEmpty() && neededIngredient.test(slotStack))
                {
                    if (slotStack.isDamageableItem() && neededDamage > 0)
                    {
                        int remainingDurability = slotStack.getMaxDamage() - slotStack.getDamageValue();
                        if (remainingDurability < neededDamage)
                        {
                            continue;
                        }

                        slotStack.hurtAndBreak(
                                neededDamage, player.level(), player, (_) -> player.level().playSound(
                                        null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK,
                                        SoundSource.PLAYERS, 1.0F, 1.0F));
                        this.setItem(secIdx, slotStack);
                        usedSlots[secIdx] = true;
                        neededCount = 0;
                    }
                    else
                    {
                        int countInSlot = slotStack.getCount();
                        if (countInSlot >= neededCount)
                        {
                            this.removeItem(secIdx, neededCount);
                            neededCount = 0;
                        }
                        else
                        {
                            neededCount -= countInSlot;
                            this.setItem(secIdx, ItemStack.EMPTY);
                        }
                        usedSlots[secIdx] = true;

                        var remainingStack = remaining.get(secIdx);
                        if (!remainingStack.isEmpty())
                        {
                            var currentStack = this.getItem(secIdx);
                            if (currentStack.isEmpty())
                            {
                                this.setItem(secIdx, remainingStack);
                            }
                            else if (ItemStack.isSameItemSameComponents(currentStack, remainingStack))
                            {
                                currentStack.grow(remainingStack.getCount());
                                this.setItem(secIdx, currentStack);
                            }
                            else if (!player.getInventory().add(remainingStack))
                            {
                                player.drop(remainingStack, false);
                            }
                        }
                    }
                }
            }
        }
    }

    public void consumeFluid(AWCraftingInput input, AWRecipe recipe)
    {
        var fluidIngredientOpt = recipe.getFluid();
        if (fluidIngredientOpt.isEmpty())
        {
            return;
        }

        var fluidIngredient = fluidIngredientOpt.get();
        var handler = menu.getFluidHandler();

        var resourceInTank = handler.getResource(0);
        int amountToExtract = fluidIngredient.amount();

        if (!resourceInTank.isEmpty() && fluidIngredient.test(input.getFluid()))
        {
            try (Transaction tx = Transaction.openRoot())
            {
                int extracted = handler.extract(0, resourceInTank, amountToExtract, tx);

                if (extracted == amountToExtract)
                {
                    tx.commit();
                }
            }
        }
    }

    public void produceExtraResults(AWRecipe recipe, ServerPlayer player)
    {
        var extraList = recipe.getExtra();
        if (extraList == null || extraList.isEmpty())
        {
            return;
        }

        for (var chancedExtra : extraList)
        {
            ItemStack rolled = chancedExtra.roll(player.getRandom());
            if (rolled.isEmpty())
            {
                continue;
            }

            ItemStack remainder = this.menu.getExtra().addItem(rolled);

            if (!remainder.isEmpty() && !player.getInventory().add(remainder))
            {
                player.drop(remainder, false);
            }
        }
    }
}
