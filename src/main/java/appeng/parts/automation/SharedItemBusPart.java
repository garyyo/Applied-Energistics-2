/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.parts.automation;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import alexiil.mc.lib.attributes.item.FixedItemInv;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Upgrades;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.me.GridAccessException;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.util.InventoryAdaptor;

public abstract class SharedItemBusPart extends UpgradeablePart implements IGridTickable {

    private final AppEngInternalAEInventory config = new AppEngInternalAEInventory(this, 9);
    private boolean lastRedstone = false;

    public SharedItemBusPart(final ItemStack is) {
        super(is);
    }

    @Override
    public void upgradesChanged() {
        this.updateState();
    }

    @Override
    public void readFromNBT(final net.minecraft.nbt.CompoundTag extra) {
        super.readFromNBT(extra);
        this.getConfig().readFromNBT(extra, "config");
    }

    @Override
    public void writeToNBT(final net.minecraft.nbt.CompoundTag extra) {
        super.writeToNBT(extra);
        this.getConfig().writeToNBT(extra, "config");
    }

    @Override
    public FixedItemInv getInventoryByName(final String name) {
        if (name.equals("config")) {
            return this.getConfig();
        }

        return super.getInventoryByName(name);
    }

    @Override
    public void onneighborUpdate(BlockView w, BlockPos pos, BlockPos neighbor) {
        this.updateState();
        if (this.lastRedstone != this.getHost().hasRedstone(this.getSide())) {
            this.lastRedstone = !this.lastRedstone;
            if (this.lastRedstone && this.getRSMode() == RedstoneMode.SIGNAL_PULSE) {
                this.doBusWork();
            }
        }
    }

    protected InventoryAdaptor getHandler() {
        final BlockEntity self = this.getHost().getTile();
        final BlockEntity target = this.getBlockEntity(self, self.getPos().offset(this.getSide().getFacing()));

        return InventoryAdaptor.getAdaptor(target, this.getSide().getFacing().getOpposite());
    }

    private BlockEntity getBlockEntity(final BlockEntity self, final BlockPos pos) {
        final World w = self.getWorld();

        if (w.getChunkManager().isChunkLoaded(new ChunkPos(pos))) {
            return w.getBlockEntity(pos);
        }

        return null;
    }

    protected int availableSlots() {
        return Math.min(1 + this.getInstalledUpgrades(Upgrades.CAPACITY) * 4, this.getConfig().getSlotCount());
    }

    protected int calculateItemsToSend() {
        switch (this.getInstalledUpgrades(Upgrades.SPEED)) {
            default:
            case 0:
                return 1;
            case 1:
                return 8;
            case 2:
                return 32;
            case 3:
                return 64;
            case 4:
                return 96;
        }
    }

    /**
     * Checks if the bus can actually do something.
     *
     * Currently this tests if the chunk for the target is actually loaded.
     *
     * @return true, if the the bus should do its work.
     */
    protected boolean canDoBusWork() {
        final BlockEntity self = this.getHost().getTile();
        final BlockPos selfPos = self.getPos().offset(this.getSide().getFacing());
        final World world = self.getWorld();

        return world != null && world.getChunkManager().isChunkLoaded(new ChunkPos(selfPos));
    }

    private void updateState() {
        try {
            if (!this.isSleeping()) {
                this.getProxy().getTick().wakeDevice(this.getProxy().getNode());
            } else {
                this.getProxy().getTick().sleepDevice(this.getProxy().getNode());
            }
        } catch (final GridAccessException e) {
            // :P
        }
    }

    protected abstract TickRateModulation doBusWork();

    AppEngInternalAEInventory getConfig() {
        return this.config;
    }
}
