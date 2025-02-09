package net.mehvahdjukaar.supplementaries.block.tiles;

import net.mehvahdjukaar.supplementaries.compat.CompatHandler;
import net.mehvahdjukaar.supplementaries.compat.curios.SupplementariesCuriosPlugin;
import net.mehvahdjukaar.supplementaries.items.KeyItem;
import net.mehvahdjukaar.supplementaries.setup.Registry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.concurrent.atomic.AtomicReference;

public class KeyLockableTile extends TileEntity {

    public String password = null;

    public KeyLockableTile() {
        super(Registry.KEY_LOCKABLE_TILE.get());
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPassword(ItemStack stack){
        this.setPassword(stack.getHoverName().getString());
    }

    public void clearOwner(){
        this.password=null;
    }

    public static boolean isCorrectKey(ItemStack key, String password){
        return key.getHoverName().getString().equals(password);
    }
    public boolean isCorrectKey(ItemStack key){
        return isCorrectKey(key,this.password);
    }

    public static boolean isKeyInInventory(PlayerEntity player, String key, String translName){

        if(CompatHandler.curios && SupplementariesCuriosPlugin.isKeyInCurio(player, key, translName))return true;

        AtomicReference<IItemHandler> itemHandler = new AtomicReference<>();
        player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(itemHandler::set);
        if (itemHandler.get() != null) {
            boolean hasKey = false;
            for (int _idx = 0; _idx < itemHandler.get().getSlots(); _idx++) {
                ItemStack stack = itemHandler.get().getStackInSlot(_idx);
                if(stack.getItem() instanceof KeyItem){
                    hasKey = true;
                    if(isCorrectKey(stack,key))return true;
                }
            }
            if(hasKey){
                player.displayClientMessage(new TranslationTextComponent("message.supplementaries.safe.incorrect_key"), true);
                return false;
            }
        }
        player.displayClientMessage(new TranslationTextComponent("message.supplementaries."+translName+".locked"), true);
        return false;
    }

    //returns true if door has to open
    public boolean handleAction(PlayerEntity player, Hand handIn, String translName) {
        if (player.isSpectator()) return false;

        ItemStack stack = player.getItemInHand(handIn);
        Item item = stack.getItem();

        boolean isKey = item instanceof KeyItem;
        //clear ownership with tripwire
        if(player.isShiftKeyDown() && isKey && (player.isCreative() || this.isCorrectKey(stack))){
            this.clearOwner();
            player.displayClientMessage(new TranslationTextComponent("message.supplementaries.safe.cleared"),true);
            this.level.playSound(null, worldPosition.getX()+0.5, worldPosition.getY()+0.5, worldPosition.getZ()+0.5,
                    SoundEvents.IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.5F, 1.5F);
            return false;
        }
        //set key
        else if(this.password==null){
            if(isKey) {
                this.setPassword(stack);
                player.displayClientMessage(new TranslationTextComponent("message.supplementaries.safe.assigned_key", this.password), true);
                this.level.playSound(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                        SoundEvents.IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 0.5F, 1.5F);
                return false;
            }
            return true;
        }
        //open
        else return isKeyInInventory(player, this.password,translName) || player.isCreative();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        if(compound.contains("Password"))
            this.password=compound.getString("Password");;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        if(this.password!=null)
            compound.putString("Password",this.password);
        return compound;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }
}
