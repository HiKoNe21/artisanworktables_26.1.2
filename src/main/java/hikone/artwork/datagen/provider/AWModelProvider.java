package hikone.artwork.datagen.provider;

import hikone.artwork.ArtWork;
import hikone.artwork.init.AWBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AWModelProvider extends BlockStateProvider
{
    public AWModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
    {
        super(output, ArtWork.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        for (var tierMap : AWBlocks.BLOCKS.values())
        {
            for (var deferredBlock : tierMap.values())
            {
                var block = deferredBlock.get();
                var blockName = deferredBlock.getId().getPath();
                var split = blockName.split("_");
                var modelLocation = ArtWork.makeId("block/" + split[1] + "/" + split[0]);

                var model = models().getExistingFile(modelLocation);
                simpleBlockWithItem(block, model);
            }
        }
    }
}
