package hikone.artwork.config;

import hikone.artwork.api.reference.AWEnumTier;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class AWServerConfig
{
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue WORKTABLE_FLUID_CAPACITY;
    public static final ModConfigSpec.IntValue WORKSTATION_FLUID_CAPACITY;
    public static final ModConfigSpec.IntValue WORKSHOP_FLUID_CAPACITY;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("fluid_capacity");

        WORKTABLE_FLUID_CAPACITY = builder
                .comment("Fluid tank capacity in millibuckets (mB) for Worktable. Default: 4000 (4 buckets).")
                .translation("artwork.configuration.server.worktable_fluid_capacity")
                .defineInRange("worktableFluidCapacity", 4000, 1000, 1000000);

        WORKSTATION_FLUID_CAPACITY = builder
                .comment("Fluid tank capacity in millibuckets (mB) for Workstation. Default: 8000 (8 buckets).")
                .translation("artwork.configuration.server.workstation_fluid_capacity")
                .defineInRange("workstationFluidCapacity", 8000, 1000, 1000000);

        WORKSHOP_FLUID_CAPACITY = builder
                .comment("Fluid tank capacity in millibuckets (mB) for Workshop. Default: 16000 (16 buckets).")
                .translation("artwork.configuration.server.workshop_fluid_capacity")
                .defineInRange("workshopFluidCapacity", 16000, 1000, 1000000);

        builder.pop();
        SPEC = builder.build();
    }

    public static int getFluidCapacity(AWEnumTier tier)
    {
        return switch (tier)
        {
            case WORKTABLE -> WORKTABLE_FLUID_CAPACITY.get();
            case WORKSTATION -> WORKSTATION_FLUID_CAPACITY.get();
            case WORKSHOP -> WORKSHOP_FLUID_CAPACITY.get();
        };
    }

    private AWServerConfig()
    {
    }
}