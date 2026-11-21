package hikone.artwork.recipe;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.network.AWPlayCraftSoundPayload;
import hikone.artwork.recipe.format.AWExperienceIngredient;
import hikone.artwork.recipe.format.AWItemStackTemplate;
import hikone.artwork.recipe.format.AWSizedIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AWRecipe implements Recipe<AWCraftingInput>
{
    // FIELDS ##########################################################################################################

    protected final AWEnumType tableType;
    protected final ItemStackTemplate result;
    protected final List<AWSizedIngredient> secondary;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<SizedFluidIngredient> fluid;
    protected final List<AWItemStackTemplate> extra;
    protected final List<Integer> availableTiers;
    protected final AWExperienceIngredient experience;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<Holder<SoundEvent>> sound;

    // CONSTRUCTORS ####################################################################################################

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected AWRecipe(
            AWEnumType tableType, ItemStackTemplate result, List<AWSizedIngredient> secondary,
            Optional<SizedFluidIngredient> fluid, List<AWItemStackTemplate> extra, List<Integer> availableTiers,
            AWExperienceIngredient experience, Optional<Holder<SoundEvent>> sound)
    {
        this.tableType = tableType;
        this.result = result;
        this.secondary = secondary;
        this.fluid = fluid;
        this.extra = extra;
        this.availableTiers = availableTiers;
        this.experience = experience;
        this.sound = sound;
    }

    // GETTERS #########################################################################################################

    public AWEnumType getTableType()
    {
        return this.tableType;
    }

    public ItemStackTemplate getResult()
    {
        return this.result;
    }

    public List<AWSizedIngredient> getSecondary()
    {
        return this.secondary;
    }

    public Optional<SizedFluidIngredient> getFluid()
    {
        return this.fluid;
    }

    public List<AWItemStackTemplate> getExtra()
    {
        return this.extra;
    }

    public List<Integer> getAvailableTiers()
    {
        return this.availableTiers;
    }

    public AWExperienceIngredient getExperience()
    {
        return this.experience;
    }

    public Optional<Holder<SoundEvent>> getSound()
    {
        return this.sound;
    }

    // OTHERS ##########################################################################################################

    public NonNullList<ItemStack> getRemainingItems(AWCraftingInput inventory)
    {
        var remaining = NonNullList.withSize(9 + inventory.size(), ItemStack.EMPTY);

        var secondary = inventory.getSecondary();
        for (int i = 0; i < 9; i++)
        {
            var item = secondary.get(i);
            var remainder = item.getCraftingRemainder();
            if (remainder != null)
            {
                remaining.set(i, remainder.create());
            }
        }

        for (int i = 0; i < inventory.size(); ++i)
        {
            var item = inventory.getItem(i);
            var remainder = item.getCraftingRemainder();
            if (remainder != null)
            {
                remaining.set(9 + i, remainder.create());
            }
        }

        return remaining;
    }

    public void playCraftSound(ServerLevel level, BlockPos pos)
    {
        Holder<SoundEvent> soundHolder = this.sound.orElseGet(
                () -> BuiltInRegistries.SOUND_EVENT.wrapAsHolder(getDefaultSoundForType(this.tableType)));

        PacketDistributor.sendToPlayersNear(
                level, null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16.0,
                new AWPlayCraftSoundPayload(pos, soundHolder));
    }

    public boolean matchTier(AWEnumTier tier)
    {
        return this.availableTiers.isEmpty() || this.availableTiers.contains(tier.getId());
    }

    protected boolean matchSecondary(List<ItemStack> secondary)
    {
        if (this.secondary.isEmpty())
        {
            return true;
        }

        List<ItemStack> availableItems = new ArrayList<>();
        for (ItemStack stack : secondary)
        {
            if (!stack.isEmpty())
            {
                availableItems.add(stack.copy());
            }
        }

        for (AWSizedIngredient requiredIngredient : this.secondary)
        {
            int neededCount = requiredIngredient.sizedIngredient().count();
            var neededIngredient = requiredIngredient.sizedIngredient().ingredient();
            int neededDamage = requiredIngredient.damage();

            for (ItemStack availableStack : availableItems)
            {
                if (neededCount <= 0)
                {
                    break;
                }

                if (neededIngredient.test(availableStack))
                {
                    if (availableStack.isDamageableItem() && neededDamage > 0)
                    {
                        int remainingDurability = availableStack.getMaxDamage() - availableStack.getDamageValue();
                        if (remainingDurability >= neededDamage)
                        {
                            availableStack.shrink(1);
                            neededCount = 0;
                        }
                    }
                    else
                    {
                        int countInSlot = availableStack.getCount();
                        if (countInSlot >= neededCount)
                        {
                            availableStack.shrink(neededCount);
                            neededCount = 0;
                        }
                        else
                        {
                            neededCount -= countInSlot;
                            availableStack.setCount(0);
                        }
                    }
                }
            }

            if (neededCount > 0)
            {
                return false;
            }
        }

        return true;
    }

    protected boolean matchFluid(FluidStack fluid)
    {
        return this.fluid.map(sizedFluidIngredient -> sizedFluidIngredient.test(fluid)).orElse(true);

    }

    private static SoundEvent getDefaultSoundForType(AWEnumType type)
    {
        return switch (type)
        {
            case BLACKSMITH -> SoundEvents.ANVIL_USE;
            case TAILOR -> SoundEvents.SHEEP_SHEAR;
            case CARPENTER -> SoundEvents.AXE_STRIP;
            case MASON -> SoundEvents.UI_STONECUTTER_TAKE_RESULT;
            case CHEMIST -> SoundEvents.BREWING_STAND_BREW;
            case JEWELER -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case BASIC -> SoundEvents.WOOD_HIT;
            case ENGINEER -> SoundEvents.PISTON_EXTEND;
            case MAGE -> SoundEvents.ENCHANTMENT_TABLE_USE;
            case SCRIBE -> SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT;
            case FARMER -> SoundEvents.COMPOSTER_READY;
            case CHEF -> SoundEvents.SMOKER_SMOKE;
            case DESIGNER -> SoundEvents.UI_LOOM_TAKE_RESULT;
            case TANNER -> SoundEvents.ARMOR_EQUIP_LEATHER.value();
            case POTTER -> SoundEvents.DECORATED_POT_STEP;
        };
    }
}
