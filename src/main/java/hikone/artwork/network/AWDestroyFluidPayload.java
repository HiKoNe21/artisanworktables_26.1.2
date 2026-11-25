package hikone.artwork.network;

import hikone.artwork.ArtWork;
import hikone.artwork.menu.AWMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public record AWDestroyFluidPayload() implements CustomPacketPayload
{
    public static final Type<AWDestroyFluidPayload> TYPE = new Type<>(ArtWork.makeId("destroy_fluid"));

    public static final StreamCodec<ByteBuf, AWDestroyFluidPayload> STREAM_CODEC = StreamCodec.unit(new AWDestroyFluidPayload());

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handleOnServer(AWDestroyFluidPayload payload, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof AWMenu menu)
            {
                menu.destroyFluid();
            }
        });
    }
}
