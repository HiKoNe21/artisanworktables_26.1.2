package hikone.artwork.init;

import hikone.artwork.ArtWork;
import hikone.artwork.api.ids.AWBlockIds;
import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.block.AWBlock;
import hikone.artwork.block.WorkshopBlock;
import hikone.artwork.block.WorkstationBlock;
import hikone.artwork.block.WorktableBlock;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Function;

public final class AWBlocks
{
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(ArtWork.MODID);

    public static final Map<AWEnumType, Map<AWEnumTier, DeferredBlock<? extends AWBlock>>> BLOCKS = new EnumMap<>(AWEnumType.class);

    static
    {
        register(AWEnumType.TAILOR, SoundType.WOOL);
        register(AWEnumType.CARPENTER, SoundType.CHERRY_WOOD);
        register(AWEnumType.MASON, SoundType.STONE);
        register(AWEnumType.BLACKSMITH, SoundType.NETHERITE_BLOCK);
        register(AWEnumType.JEWELER, SoundType.AMETHYST);
        register(AWEnumType.BASIC, SoundType.WOOD);
        register(AWEnumType.ENGINEER, SoundType.IRON);
        register(AWEnumType.MAGE, SoundType.ANCIENT_DEBRIS);
        register(AWEnumType.SCRIBE, SoundType.SHELF);
        register(AWEnumType.CHEMIST, SoundType.BONE_BLOCK);
        register(AWEnumType.FARMER, SoundType.GRAVEL);
        register(AWEnumType.CHEF, SoundType.CALCITE);
        register(AWEnumType.DESIGNER, SoundType.SCAFFOLDING);
        register(AWEnumType.TANNER, SoundType.PACKED_MUD);
        register(AWEnumType.POTTER, SoundType.DECORATED_POT);
    }

    public static DeferredBlock<? extends AWBlock> get(AWEnumType type, AWEnumTier tier)
    {
        return BLOCKS.get(type).get(tier);
    }

    private static void register(AWEnumType type, SoundType soundType)
    {
        register(type, AWEnumTier.WORKTABLE, properties -> new WorktableBlock(properties, type, soundType, 2.0F, 3.0F));
        register(type, AWEnumTier.WORKSTATION, properties -> new WorkstationBlock(properties, type, soundType, 3.0F, 6.0F));
        register(type, AWEnumTier.WORKSHOP, properties -> new WorkshopBlock(properties, type, soundType, 4.0F, 9.0F));
    }

    private static <B extends AWBlock> void register(AWEnumType type, AWEnumTier tier, Function<BlockBehaviour.Properties, B> blockFactory)
    {
        var id = AWBlockIds.get(type, tier);
        var deferredBlock = DR.registerBlock(id.getPath(), blockFactory);
        AWItems.DR.registerSimpleBlockItem(id.getPath(), deferredBlock);
        AWCreativeTabs.add(deferredBlock);
        AWCreativeTabs.addExternal(CreativeModeTabs.FUNCTIONAL_BLOCKS, deferredBlock);
        BLOCKS.computeIfAbsent(type, k -> new EnumMap<>(AWEnumTier.class)).put(tier, deferredBlock);
    }
}
