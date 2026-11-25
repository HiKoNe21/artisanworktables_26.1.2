package hikone.artwork.datagen.provider;

import hikone.artwork.ArtWork;
import hikone.artwork.init.AWBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class AWBlockTagsProvider extends BlockTagsProvider
{
    public AWBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, ArtWork.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider registries)
    {
        var axeTag = this.tag(BlockTags.MINEABLE_WITH_AXE);
        var pickaxeTag = this.tag(BlockTags.MINEABLE_WITH_PICKAXE);

        for (var typeEntry : AWBlocks.BLOCKS.entrySet())
        {
            for (var tierEntry : typeEntry.getValue().entrySet())
            {
                var block = tierEntry.getValue().get();

                switch (tierEntry.getKey())
                {
                    case WORKTABLE -> axeTag.add(block);
                    case WORKSTATION -> pickaxeTag.add(block);
                    case WORKSHOP ->
                    {
                        axeTag.add(block);
                        pickaxeTag.add(block);
                    }
                }
            }
        }
    }
}
