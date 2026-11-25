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
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AWBlockEntity extends BaseContainerBlockEntity
{
    // FIELDS ##########################################################################################################

    protected NonNullList<ItemStack> items;
    protected FluidTank fluidHandler;

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
        this.fluidHandler = new FluidTank(capacity)
        {
            @Override
            protected void onContentsChanged()
            {
                AWBlockEntity.this.setChanged();
                if (level != null && !level.isClientSide)
                {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };
    }

    // GETTERS #########################################################################################################

    public FluidTank getFluidHandler()
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
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
    {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        if (tag.contains("Fluid"))
        {
            this.fluidHandler.readFromNBT(registries, tag.getCompound("Fluid"));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
    {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.put("Fluid", this.fluidHandler.writeToNBT(registries, new CompoundTag()));
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
