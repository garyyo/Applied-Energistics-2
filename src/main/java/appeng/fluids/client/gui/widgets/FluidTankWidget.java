/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2018, AlgorithmX2, All rights reserved.
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

package appeng.fluids.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.fabricmc.api.Environment;
import net.minecraftforge.fluids.FluidAttributes;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.AEColor;
import appeng.client.gui.widgets.ITooltip;
import appeng.fluids.util.IAEFluidTank;

@Environment(EnvType.CLIENT)
public class FluidTankWidget extends Widget implements ITooltip {
    private final IAEFluidTank tank;
    private final int slot;

    public FluidTankWidget(IAEFluidTank tank, int slot, int x, int y, int w, int h) {
        super(x, y, w, h, "");
        this.tank = tank;
        this.slot = slot;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.disableBlend();

            fill(this.x, this.y, this.x + this.width, this.y + this.height, AEColor.GRAY.blackVariant | 0xFF000000);

            final IAEFluidStack fluidStack = this.tank.getFluidInSlot(this.slot);
            if (fluidStack != null && fluidStack.getStackSize() > 0) {
                Fluid fluid = fluidStack.getFluid();
                FluidAttributes attributes = fluid.getAttributes();

                float red = (attributes.getColor() >> 16 & 255) / 255.0F;
                float green = (attributes.getColor() >> 8 & 255) / 255.0F;
                float blue = (attributes.getColor() & 255) / 255.0F;
                RenderSystem.color3f(red, green, blue);

                MinecraftClient mc = MinecraftClient.getInstance();
                mc.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
                final Sprite sprite = mc.getAtlasSpriteGetter(SpriteAtlasTexture.BLOCK_ATLAS_TEX)
                        .apply(attributes.getStillTexture(fluidStack.getFluidStack()));

                final int scaledHeight = (int) (this.height
                        * ((float) fluidStack.getStackSize() / this.tank.getTankCapacity(this.slot)));

                int iconHeightRemainder = scaledHeight % 16;
                if (iconHeightRemainder > 0) {
                    blit(this.x, this.y + this.height - iconHeightRemainder, getBlitOffset(), 16, iconHeightRemainder,
                            sprite);
                }
                for (int i = 0; i < scaledHeight / 16; i++) {
                    blit(this.x, this.y + this.height - iconHeightRemainder - (i + 1) * 16, getBlitOffset(), 16, 16,
                            sprite);
                }
            }

        }
    }

    @Override
    public String getMessage() {
        final IAEFluidStack fluid = this.tank.getFluidInSlot(this.slot);
        if (fluid != null && fluid.getStackSize() > 0) {
            String desc = fluid.getFluid().getAttributes().getName(fluid.getFluidStack()).getFormattedText();
            String amountToText = fluid.getStackSize() + "mB";

            return desc + "\n" + amountToText;
        }
        return null;
    }

    @Override
    public int xPos() {
        return this.x - 2;
    }

    @Override
    public int yPos() {
        return this.y - 2;
    }

    @Override
    public int getWidth() {
        return this.width + 4;
    }

    @Override
    public int getHeight() {
        return this.height + 4;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

}
