package hikone.artwork.blockentity;

import hikone.artwork.api.reference.AWEnumTier;
import hikone.artwork.api.reference.AWEnumType;
import hikone.artwork.block.AWBlock;
import hikone.artwork.config.AWServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AWBlockEntity extends BaseContainerBlockEntity
{
    // FIELDS ##########################################################################################################

    protected NonNullList<ItemStack> items;
    protected FluidStacksResourceHandler fluidHandler;

    protected final AWEnumType type;
    protected final AWEnumTier tier;

    // CONSTRUCTORS ####################################################################################################

    protected AWBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState)
    {
        super(type, worldPosition, blockState);
        var block = (AWBlock) this.getBlockState().getBlock();
        this.type = block.getTableType();
        this.tier = block.getTableTier();
        this.items = NonNullList.withSize(9 + getGridSize() * getGridSize(), ItemStack.EMPTY);
        int capacity = AWServerConfig.getFluidCapacity(this.tier);
        this.fluidHandler = new FluidStacksResourceHandler(1, capacity)
        {
            @Override
            protected void onContentsChanged(int index, @NonNull FluidStack previousContents)
            {
                AWBlockEntity.this.setChanged();
                if (level != null && !level.isClientSide())
                {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };
    }

    // GETTERS #########################################################################################################

    public FluidStacksResourceHandler getFluidHandler()
    {
        return this.fluidHandler;
    }

    public AWEnumType getTableType()
    {
        return type;
    }

    public AWEnumTier getTableTier()
    {
        return tier;
    }

    // ABSTRACT ########################################################################################################

    public abstract int getGridSize();

    // OVERRIDES #######################################################################################################


    @Override
    protected @NonNull Component getDefaultName()
    {
        return this.getBlockState().getBlock().getName();
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input)
    {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        input.child("Fluid").ifPresent(this.fluidHandler::deserialize);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output)
    {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        this.fluidHandler.serialize(output.child("Fluid"));
    }

    @Override
    protected @NonNull NonNullList<ItemStack> getItems()
    {
        return this.items;
    }

    @Override
    protected void setItems(@NonNull NonNullList<ItemStack> items)
    {
        this.items = items;
    }

    @Override
    public int getContainerSize()
    {
        return this.items.size();
    }

    @Override
    public void setItem(int slot, @NonNull ItemStack itemStack, boolean insideTransaction)
    {
        // skip
        super.setItem(slot, itemStack, insideTransaction);
    }

    @Override
    public boolean canPlaceItem(int slot, @NonNull ItemStack itemStack)
    {
        // skip
        return true;
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries)
    {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
