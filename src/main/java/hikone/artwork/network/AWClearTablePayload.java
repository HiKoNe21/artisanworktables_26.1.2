package hikone.artwork.network;

import hikone.artwork.ArtWork;
import hikone.artwork.menu.AWMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public record AWClearTablePayload(boolean includeExtra) implements CustomPacketPayload
{
    public static final AWClearTablePayload INSTANCE = new AWClearTablePayload(true);

    public static final Type<AWClearTablePayload> TYPE = new Type<>(ArtWork.makeId("clear_table"));

    public static final StreamCodec<ByteBuf, AWClearTablePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AWClearTablePayload::includeExtra, AWClearTablePayload::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handleOnServer(AWClearTablePayload payload, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof AWMenu menu)
            {
                menu.clearTable(player);
            }
        });
    }
}
