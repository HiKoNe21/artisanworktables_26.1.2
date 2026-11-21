package hikone.artwork.recipe.format;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public record AWExperienceIngredient(int required, int consume)
{
    public static final AWExperienceIngredient DEFAULT = new AWExperienceIngredient(0, 0);

    public static final Codec<AWExperienceIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("required", 0).forGetter(AWExperienceIngredient::required),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("consume", 0).forGetter(AWExperienceIngredient::consume)
    ).apply(instance, AWExperienceIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AWExperienceIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AWExperienceIngredient::required,
            ByteBufCodecs.VAR_INT, AWExperienceIngredient::consume,
            AWExperienceIngredient::new
    );

    public boolean isEmpty()
    {
        return this.required <= 0 && this.consume <= 0;
    }

    public int getMinLevelNeeded()
    {
        return Math.max(this.required, this.consume);
    }

    public boolean matches(Player player)
    {
        if (this.isEmpty() || player.hasInfiniteMaterials())
        {
            return true;
        }
        return player.experienceLevel >= this.getMinLevelNeeded();
    }

    public void consumeFrom(Player player)
    {
        if (this.consume <= 0 || player.hasInfiniteMaterials())
        {
            return;
        }
        player.giveExperienceLevels(-this.consume);
    }

    public Component getDisplayText()
    {
        if (this.required > 0 && this.consume > 0)
        {
            return Component.translatable("gui.artwork.tooltip.experience.required_consume", this.required, this.consume);
        }
        if (this.required > 0)
        {
            return Component.translatable("gui.artwork.tooltip.experience.required", this.required);
        }
        if (this.consume > 0)
        {
            return Component.translatable("gui.artwork.tooltip.experience.consume", this.consume);
        }
        return Component.empty();
    }

    public int getTextColor(@Nullable Player player)
    {
        if (player != null && !this.matches(player))
        {
            return 0xFFFF6060;
        }
        return 0xFF80FF20;
    }
}
