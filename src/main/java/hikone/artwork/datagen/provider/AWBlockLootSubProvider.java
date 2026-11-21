package hikone.artwork.datagen.provider;

import hikone.artwork.init.AWBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Set;

public class AWBlockLootSubProvider extends BlockLootSubProvider
{

    public AWBlockLootSubProvider(HolderLookup.Provider registries)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate()
    {
        for (var block : getKnownBlocks())
        {
            this.dropSelf(block);
        }
    }

    @Override
    protected @NonNull Iterable<Block> getKnownBlocks()
    {
        var blocks = new ArrayList<Block>();
        for (var tierMap : AWBlocks.BLOCKS.values())
        {
            for (var deferredBlock : tierMap.values())
            {
                blocks.add(deferredBlock.get());
            }
        }
        return blocks;
    }
}
