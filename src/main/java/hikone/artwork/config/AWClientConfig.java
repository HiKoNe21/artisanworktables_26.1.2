package hikone.artwork.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class AWClientConfig
{
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_CRAFT_SOUNDS;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("audio");

        ENABLE_CRAFT_SOUNDS = builder
                .comment("Enable or disable crafting sound effects for all tables.")
                .translation("artwork.configuration.client.enable_craft_sounds")
                .define("enableCraftSounds", true);

        builder.pop();
        SPEC = builder.build();
    }

    private AWClientConfig()
    {
    }
}
