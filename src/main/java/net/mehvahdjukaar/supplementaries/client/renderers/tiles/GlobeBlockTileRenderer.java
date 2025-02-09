package net.mehvahdjukaar.supplementaries.client.renderers.tiles;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.mehvahdjukaar.supplementaries.block.tiles.GlobeBlockTile;
import net.mehvahdjukaar.supplementaries.client.renderers.Const;
import net.mehvahdjukaar.supplementaries.client.renderers.GlobeTextureManager;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;


public class GlobeBlockTileRenderer extends TileEntityRenderer<GlobeBlockTile> {

    public static final ModelRenderer globe = new ModelRenderer(32, 16, 0, 0);
    public static final ModelRenderer flat = new ModelRenderer(32, 32, 0, 0);
    public static final ModelRenderer sheared = new ModelRenderer(32, 32, 0, 0);
    static {
        globe.addBox(-4.0F, -28.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        globe.setPos(0.0F, 24.0F, 0.0F);

        flat.setPos(0.0F, 24.0F, 0.0F);
        flat.texOffs(0, 0).addBox(-4.0F, -28.0F, -4.0F, 8.0F, 4.0F, 8.0F, 0.0F, false);
        flat.texOffs(0, 13).addBox(-4.0F, -24.0F, -4.0F, 8.0F, 2.0F, 8.0F, 0.0F, false);
        flat.texOffs(4, 23).addBox(-3.0F, -22.0F, -3.0F, 6.0F, 1.0F, 6.0F, 0.0F, false);
        flat.texOffs(8, 24).addBox(-2.0F, -21.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);

        sheared.setPos(0.0F, 24.0F, 0.0F);
        sheared.texOffs(0, 0).addBox(-4.0F, -28.0F, -4.0F, 8.0F, 8.0F, 4.0F, 0.0F, false);
        sheared.texOffs(0, 12).addBox(0.0F, -28.0F, 0.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
    }

    public GlobeBlockTileRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }


    @Override
    public void render(GlobeBlockTile tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
                       int combinedOverlayIn) {

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5,0.5,0.5);
        matrixStackIn.mulPose(tile.getDirection().getRotation());
        matrixStackIn.mulPose(Const.XN90);
        matrixStackIn.translate(0,+0.0625,0);
        matrixStackIn.mulPose(Const.XN22);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, tile.prevYaw+tile.face, tile.yaw+tile.face)));
        matrixStackIn.mulPose(Const.X180);

        IVertexBuilder builder;
        GlobeBlockTile.GlobeType type = ClientConfigs.cached.GLOBE_RANDOM ? tile.type : GlobeBlockTile.GlobeType.EARTH;
        ModelRenderer selected;
        switch(type){
            case FLAT:
                builder = bufferIn.getBuffer(RenderType.entityCutout(tile.type.texture));
                selected = flat;
                break;
            default:
            case EARTH:
                builder = bufferIn.getBuffer(RenderType.entityCutout(tile.type.texture));
                selected = globe;
                break;
            case SHEARED:
                builder = bufferIn.getBuffer(RenderType.entityCutout(tile.type.texture));
                selected = sheared;
                break;
            case DEFAULT:
                builder = bufferIn.getBuffer(GlobeTextureManager.INSTANCE.getRenderType(tile.getLevel()));
                selected = globe;
                break;
        }

        selected.render(matrixStackIn, builder, combinedLightIn,combinedOverlayIn,1,1,1,1);

        matrixStackIn.popPose();
    }

}