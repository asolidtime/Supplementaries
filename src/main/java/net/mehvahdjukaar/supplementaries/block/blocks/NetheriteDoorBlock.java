package net.mehvahdjukaar.supplementaries.block.blocks;

import net.mehvahdjukaar.supplementaries.block.tiles.KeyLockableTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class NetheriteDoorBlock extends DoorBlock {

    public NetheriteDoorBlock(Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        BlockPos p = this.hasTileEntity(state) ? pos : pos.below();
        TileEntity te = worldIn.getBlockEntity(p);
        if (te instanceof KeyLockableTile) {
            if (((KeyLockableTile) te).handleAction(player, handIn, "door")) {

                //if(ModList.get().isLoaded("quark")) QuarkDoubleDoorPlugin.openDoorKey(worldIn,state,pos,player,handIn);

                state = state.cycle(OPEN);
                worldIn.setBlock(pos, state, 10);
                //TODO: replace with proper sound event
                worldIn.levelEvent(player, state.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
            }
        }

        return ActionResultType.sidedSuccess(worldIn.isClientSide);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) { }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return state;
        return state.setValue(OPEN, false).setValue(POWERED, false);
    }

    private int getCloseSound() {
        return 1011;
    }

    private int getOpenSound() {
        return 1005;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new KeyLockableTile();
    }

}
