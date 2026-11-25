package hikone.artwork.network;

import hikone.artwork.ArtWork;
import hikone.artwork.menu.AWMenu;
import hikone.artwork.recipe.AWRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AWJeiTransferPayload(ResourceLocation recipeId, boolean maxTransfer) implements CustomPacketPayload
{
    public static final Type<AWJeiTransferPayload> TYPE = new Type<>(ArtWork.makeId("jei_transfer"));

    public static final StreamCodec<ByteBuf, AWJeiTransferPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AWJeiTransferPayload::recipeId,
            ByteBufCodecs.BOOL, AWJeiTransferPayload::maxTransfer,
            AWJeiTransferPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type()
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

            level.getRecipeManager().byKey(payload.recipeId()).ifPresent(holder ->
            {
                if (holder.value() instanceof AWRecipe awRecipe)
                {
                    menu.transferRecipeFromJei(awRecipe, player, payload.maxTransfer());
                }
            });
        });
    }
}
