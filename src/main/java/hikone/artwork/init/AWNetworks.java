package hikone.artwork.init;

import hikone.artwork.network.AWClearTablePayload;
import hikone.artwork.network.AWDestroyFluidPayload;
import hikone.artwork.network.AWJeiTransferPayload;
import hikone.artwork.network.AWPlayCraftSoundPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class AWNetworks
{
    public static void register(RegisterPayloadHandlersEvent event)
    {
        var registrar = event.registrar("1");

        registrar.playToServer(AWDestroyFluidPayload.TYPE, AWDestroyFluidPayload.STREAM_CODEC, AWDestroyFluidPayload::handleOnServer);
        registrar.playToServer(AWClearTablePayload.TYPE, AWClearTablePayload.STREAM_CODEC, AWClearTablePayload::handleOnServer);
        registrar.playToServer(AWJeiTransferPayload.TYPE, AWJeiTransferPayload.STREAM_CODEC, AWJeiTransferPayload::handleOnServer);

        registrar.playToClient(AWPlayCraftSoundPayload.TYPE, AWPlayCraftSoundPayload.STREAM_CODEC, AWPlayCraftSoundPayload::handleOnClient);
    }
}
