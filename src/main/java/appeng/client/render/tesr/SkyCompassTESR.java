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

package appeng.client.render.tesr;

import net.fabricmc.api.EnvType;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.Environment;
import net.minecraftforge.client.model.data.ModelDataMap;

import appeng.client.render.FacingToRotation;
import appeng.client.render.model.SkyCompassBakedModel;
import appeng.tile.misc.SkyCompassBlockEntity;

@Environment(EnvType.CLIENT)
public class SkyCompassTESR extends BlockEntityRenderer<SkyCompassBlockEntity> {

    private static BlockRenderManager blockRenderer;

    public SkyCompassTESR(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(SkyCompassBlockEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffers,
                       int combinedLightIn, int combinedOverlayIn) {

        if (blockRenderer == null) {
            blockRenderer = MinecraftClient.getInstance().getBlockRenderManager();
        }

        VertexConsumer buffer = buffers.getBuffer(Atlases.getTranslucentBlockType());

        BlockState blockState = te.getCachedState();
        BakedModel model = blockRenderer.getBlockModelShapes().getModel(blockState);

        // FIXME: Rotation was previously handled by an auto rotating model I think, but
        // FIXME: Should be handled using matrices instead
        Direction forward = te.getForward();
        Direction up = te.getUp();
        // This ensures the needle isn't flipped by the model rotator. Since the model
        // is symmetrical, this should
        // not affect the appearance
        if (forward == Direction.UP || forward == Direction.DOWN) {
            up = Direction.NORTH;
        }
        // Flip forward/up for rendering, the base model is facing up without any
        // rotation
        ms.push();
        ms.translate(0.5D, 0.5D, 0.5D);
        FacingToRotation.get(up, forward).push(ms);
        ms.translate(-0.5D, -0.5D, -0.5D);

        ModelDataMap modelData = new ModelDataMap.Builder().withInitial(SkyCompassBakedModel.ROTATION, getRotation(te))
                .build();

        blockRenderer.getBlockModelRenderer().renderModel(ms.getLast(), buffer, null, model, 1, 1, 1, combinedLightIn,
                combinedOverlayIn, modelData);
        ms.pop();

    }

    private static float getRotation(SkyCompassBlockEntity skyCompass) {
        float rotation;

        if (skyCompass.getForward() == Direction.UP || skyCompass.getForward() == Direction.DOWN) {
            rotation = SkyCompassBakedModel.getAnimatedRotation(skyCompass.getPos(), false);
        } else {
            rotation = SkyCompassBakedModel.getAnimatedRotation(null, false);
        }

        if (skyCompass.getForward() == Direction.DOWN) {
            rotation = flipidiy(rotation);
        }

        return rotation;
    }

    private static float flipidiy(float rad) {
        float x = (float) Math.cos(rad);
        float y = (float) Math.sin(rad);
        return (float) Math.atan2(-y, x);
    }
}
