package net.mehvahdjukaar.supplementaries.client.renderers.items;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.mehvahdjukaar.supplementaries.client.renderers.Const;
import net.mehvahdjukaar.supplementaries.client.renderers.RendererUtil;
import net.mehvahdjukaar.supplementaries.fluids.SoftFluid;
import net.mehvahdjukaar.supplementaries.fluids.SoftFluidList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Random;

import static net.mehvahdjukaar.supplementaries.client.renderers.tiles.JarBlockTileRenderer.renderFluid;


public class JarItemRenderer extends CageItemRenderer {

    private static final Random RAND = new Random(420);

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        CompoundNBT compound = stack.getTagElement("BlockEntityTag");
        if(compound == null || compound.isEmpty())return;

        //JarBlockTile.SpecialJarContent specialType = JarBlockTile.SpecialJarContent.values()[compound.getInt("SpecialType")];




        if(compound.contains("MobHolder")||compound.contains("BucketHolder")){
            CompoundNBT com = compound.getCompound("BucketHolder");
            if(com.isEmpty())com = compound.getCompound("MobHolder");
            if(com.contains("FishTexture")) {
                int fishTexture = com.getInt("FishTexture");
                if (fishTexture >= 0) {
                    matrixStackIn.pushPose();
                    IVertexBuilder builder1 = bufferIn.getBuffer(RenderType.cutout());
                    matrixStackIn.translate(0.5, 0.3125, 0.5);
                    matrixStackIn.mulPose(Const.YN45);
                    matrixStackIn.scale(1.5f, 1.5f, 1.5f);
                    RendererUtil.renderFish(builder1, matrixStackIn, 0, 0, fishTexture, combinedLightIn, combinedOverlayIn);
                    matrixStackIn.popPose();
                }
                SoftFluid s = SoftFluidList.WATER;
                renderFluid(0.5625f, s.getTintColor(), 0, s.getStillTexture(),
                        matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, false);
            }
        }
        if(compound.contains("FluidHolder")){
            CompoundNBT com = compound.getCompound("FluidHolder");
            int height = com.getInt("Count");
            if(height!=0) {
                int color = com.getInt("CachedColor");
                SoftFluid fluid = SoftFluidList.fromID(com.getString("Fluid"));
                if (!fluid.isEmpty() && height > 0)
                    renderFluid(height / 16f, color, 0, fluid.getStillTexture(),
                            matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, false);
            }
        }
        if(compound.contains("Items")) {
            ItemStack cookieStack = ItemStack.of((compound.getList("Items", 10)).getCompound(0));
            int height = cookieStack.getCount();
            if(height!=0) {
                RAND.setSeed(420);
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5, 0.5, 0.5);
                matrixStackIn.mulPose(Const.XN90);
                matrixStackIn.translate(0, 0, -0.5);
                float scale = 8f / 14f;
                matrixStackIn.scale(scale, scale, scale);
                for (float i = 0; i < height; i++) {
                    matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(RAND.nextInt(360)));
                    // matrixStackIn.translate(0, 0, 0.0625);
                    matrixStackIn.translate(0, 0, 1 / (16f * scale));
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    IBakedModel ibakedmodel = itemRenderer.getModel(cookieStack, null, null);
                    itemRenderer.render(stack, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn,
                            combinedOverlayIn, ibakedmodel);
                }
                matrixStackIn.popPose();
            }
        }
        //render block & mob using cage renderer
        super.renderByItem(stack,transformType,matrixStackIn,bufferIn,combinedLightIn,combinedOverlayIn);

    }
}

