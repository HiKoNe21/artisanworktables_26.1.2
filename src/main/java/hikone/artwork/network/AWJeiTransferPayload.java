package hikone.artwork.network;

import hikone.artwork.ArtWork;
import hikone.artwork.menu.AWMenu;
import hikone.artwork.recipe.AWRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record AWJeiTransferPayload(Identifier recipeId, boolean maxTransfer) implements CustomPacketPayload
{
    public static final Type<AWJeiTransferPayload> TYPE = new Type<>(ArtWork.makeId("jei_transfer"));

    public static final StreamCodec<ByteBuf, AWJeiTransferPayload> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AWJeiTransferPayload::recipeId, ByteBufCodecs.BOOL,
            AWJeiTransferPayload::maxTransfer, AWJeiTransferPayload::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handleOnServer(AWJeiTransferPayload payload, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            if (!(context.player() instanceof ServerPlayer player))
            {
                return;
            }

            if (!(player.containerMenu instanceof AWMenu menu))
            {
                return;
            }

            var level = player.level();
            var recipeKey = ResourceKey.create(Registries.RECIPE, payload.recipeId());

            level.recipeAccess().byKey(recipeKey).ifPresent(holder ->
            {
                if (holder.value() instanceof AWRecipe awRecipe)
                {
                    menu.transferRecipeFromJei(awRecipe, player, payload.maxTransfer());
                }
            });
        });
    }
}
