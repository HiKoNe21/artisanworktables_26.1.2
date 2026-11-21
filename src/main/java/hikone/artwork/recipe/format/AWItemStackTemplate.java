package hikone.artwork.recipe.format;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record AWItemStackTemplate(ItemStackTemplate result, float chance)
{
    public static final MapCodec<AWItemStackTemplate> MAP_CODEC = Codec.mapPair(
            ItemStackTemplate.MAP_CODEC,
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 1.0F)).xmap(
            pair -> new AWItemStackTemplate(pair.getFirst(), pair.getSecond()),
            chanceResult -> Pair.of(chanceResult.result(), chanceResult.chance()));

    public static final Codec<AWItemStackTemplate> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, AWItemStackTemplate> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, AWItemStackTemplate::result, ByteBufCodecs.FLOAT,
            AWItemStackTemplate::chance, AWItemStackTemplate::new);

    public ItemStack roll(RandomSource random)
    {
        if (this.chance >= 1.0F || random.nextFloat() < this.chance)
        {
            return this.result.create();
        }
        return ItemStack.EMPTY;
    }
}
