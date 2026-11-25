package hikone.artwork.network;

import hikone.artwork.ArtWork;
import hikone.artwork.config.AWClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public record AWPlayCraftSoundPayload(BlockPos pos, Holder<SoundEvent> sound) implements CustomPacketPayload
{
    public static final Type<AWPlayCraftSoundPayload> TYPE = new Type<>(ArtWork.makeId("play_craft_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AWPlayCraftSoundPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            AWPlayCraftSoundPayload::pos,
            SoundEvent.STREAM_CODEC,
            AWPlayCraftSoundPayload::sound,
            AWPlayCraftSoundPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handleOnClient(AWPlayCraftSoundPayload payload, IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            if (!AWClientConfig.ENABLE_CRAFT_SOUNDS.get())
            {
                return;
            }

            var level = Minecraft.getInstance().level;
            if (level != null)
            {
                BlockPos pos = payload.pos();
                level.playLocalSound(
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        payload.sound().value(),
                        SoundSource.BLOCKS,
                        1.0F,
                        0.9F + level.getRandom().nextFloat() * 0.2F,
                        false
                );
            }
        });
    }
}