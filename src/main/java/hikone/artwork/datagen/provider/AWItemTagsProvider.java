package hikone.artwork.datagen.provider;

import hikone.artwork.ArtWork;
import hikone.artwork.api.ids.AWTags;
import hikone.artwork.init.AWBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AWItemTagsProvider extends ItemTagsProvider
{
    public AWItemTagsProvider(
            PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, blockTags, ArtWork.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        var allTablesTag = this.tag(AWTags.Items.ALL_TABLES);

        for (var tierEntry : AWBlocks.BLOCKS.entrySet())
        {
            var type = tierEntry.getKey();
            var typeTag = this.tag(AWTags.Items.forType(type));

            for (var typeEntry : tierEntry.getValue().entrySet())
            {
                var tier = typeEntry.getKey();
                var item = typeEntry.getValue().get().asItem();
                var tierTag = this.tag(AWTags.Items.forTier(tier));

                allTablesTag.add(item);
                typeTag.add(item);
                tierTag.add(item);
            }
        }
    }
}
