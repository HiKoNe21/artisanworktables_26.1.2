package hikone.artwork.recipe.format;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jspecify.annotations.NonNull;

public record AWSizedIngredient(SizedIngredient sizedIngredient, int damage, boolean catalyst)
{
    public static final Codec<AWSizedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.sizedIngredient.ingredient()),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "count", 1)
                            .forGetter(r -> r.sizedIngredient.count()),
                    NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.NON_NEGATIVE_INT, "damage", 0)
                            .forGetter(AWSizedIngredient::damage),
                    Codec.BOOL.optionalFieldOf("catalyst", false).forGetter(AWSizedIngredient::catalyst))
            .apply(instance, AWSizedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AWSizedIngredient> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC, AWSizedIngredient::sizedIngredient, ByteBufCodecs.VAR_INT,
            AWSizedIngredient::damage, ByteBufCodecs.BOOL, AWSizedIngredient::catalyst, AWSizedIngredient::new);

    public AWSizedIngredient(Ingredient ingredient, int count, int damage, boolean catalyst)
    {
        this(new SizedIngredient(ingredient, count), damage, catalyst);
    }

    public AWSizedIngredient
    {
        if (damage < 0)
        {
            throw new IllegalArgumentException("Damage must be non negative");
        }
    }

    // GETTERS #########################################################################################################

    // OTHERS ##########################################################################################################

    public boolean test(ItemStack stack)
    {
        if (!this.sizedIngredient.test(stack))
        {
            return false;
        }

        if (this.damage > 0 && stack.isDamageableItem())
        {
            return (stack.getDamageValue() + this.damage) < stack.getMaxDamage();
        }

        return true;
    }

    // OVERRIDES #######################################################################################################

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        return obj instanceof AWSizedIngredient(SizedIngredient ingredient1, int damage1, boolean catalyst1) &&
                this.sizedIngredient.equals(ingredient1) && this.damage == damage1 && this.catalyst == catalyst1;
    }

    @Override
    public @NonNull String toString()
    {
        return "AWSizedIngredient{" + "sizedIngredient=" + this.sizedIngredient + ", damage=" + this.damage + ", catalyst=" + this.catalyst + '}';
    }

}
