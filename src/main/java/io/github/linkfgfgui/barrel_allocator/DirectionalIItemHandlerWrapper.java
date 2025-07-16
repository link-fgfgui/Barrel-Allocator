package io.github.linkfgfgui.barrel_allocator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class DirectionalIItemHandlerWrapper implements IItemHandler {
    private final IItemHandler handler;
    private final boolean isLeft;
    private final Level level;
    private final BlockPos targetBlockPos;
    private final Direction targetBlockSide;

    public DirectionalIItemHandlerWrapper(IItemHandler handler, boolean isLeft, Level level, BlockPos pos, Direction side) {
        this.handler = handler;
        this.isLeft = isLeft;
        this.level = level;
        targetBlockPos = pos;
        targetBlockSide = side;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (isLeft) {
            if (stack.is(Barrel_allocator.ALLOCATOR_BLOCK_ITEM)) return stack;
            return handler.insertItem(slot, stack, simulate);
        } else {
            IItemHandler targetBlockHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, targetBlockPos, targetBlockSide);
            if (targetBlockHandler == null) return stack;
            return targetBlockHandler.insertItem(slot, stack, simulate);
        }
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }
}
