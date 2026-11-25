package hikone.artwork.init;

import hikone.artwork.api.reference.AWEnumTier;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class AWCapabilityProviders
{
    public static void register(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, AWBlockEntities.get(AWEnumTier.WORKTABLE).get(), (blockEntity, side) -> blockEntity.getFluidHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, AWBlockEntities.get(AWEnumTier.WORKSTATION).get(), (blockEntity, side) -> blockEntity.getFluidHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, AWBlockEntities.get(AWEnumTier.WORKSHOP).get(), (blockEntity, side) -> blockEntity.getFluidHandler());
    }
}
