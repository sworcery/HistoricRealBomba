package com.historicbombs.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.historicbombs.data.BombData;
import com.historicbombs.entity.HistoricPrimedTNTEntity;
import com.historicbombs.init.ModBlocks;

@OnlyIn(Dist.CLIENT)
public class HistoricPrimedTNTRenderer extends EntityRenderer<HistoricPrimedTNTEntity, HistoricPrimedTNTRenderer.HistoricTNTRenderState> {
    private final BlockRenderDispatcher blockRenderer;

    public HistoricPrimedTNTRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public HistoricTNTRenderState createRenderState() {
        return new HistoricTNTRenderState();
    }

    @Override
    public void extractRenderState(HistoricPrimedTNTEntity entity, HistoricTNTRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.fuseRemainingInTicks = entity.getFuse() > 0 ? (float) entity.getFuse() - partialTick + 1.0F : -1.0F;
        state.bombData = entity.getBombData();
        state.blockState = ModBlocks.getBlock(entity.getBombData()).get().defaultBlockState();
    }

    @Override
    public void render(HistoricTNTRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);

        float fuseRemaining = state.fuseRemainingInTicks;
        if (fuseRemaining > -1.0F && fuseRemaining < 10.0F) {
            float scale = 1.0F - fuseRemaining / 10.0F;
            scale = Mth.clamp(scale, 0.0F, 1.0F);
            scale *= scale;
            scale *= scale;
            float grow = 1.0F + scale * 0.3F;
            poseStack.scale(grow, grow, grow);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, state.blockState, poseStack,
            bufferSource, packedLight, fuseRemaining > -1.0F && (int) fuseRemaining / 5 % 2 == 0);

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static class HistoricTNTRenderState extends EntityRenderState {
        public float fuseRemainingInTicks = -1.0F;
        public BombData bombData;
        public BlockState blockState;
    }
}
