package net.mehvahdjukaar.supplementaries.items;


import net.mehvahdjukaar.supplementaries.block.util.CapturedMobs;
import net.mehvahdjukaar.supplementaries.block.util.MobHolder;
import net.mehvahdjukaar.supplementaries.common.CommonUtil;
import net.mehvahdjukaar.supplementaries.common.ModTags;
import net.mehvahdjukaar.supplementaries.configs.ServerConfigs;
import net.mehvahdjukaar.supplementaries.setup.Registry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.function.Supplier;

public class EmptyCageItem extends BlockItem {
    public final Supplier<Item> full;
    public final CageWhitelist cageType;
    public EmptyCageItem(Block blockIn, Properties properties, Supplier<Item> full, CageWhitelist whitelist) {
        super(blockIn, properties);
        this.full = full;
        this.cageType = whitelist;
    }

    private static List<MobEntity> getEntitiesInRange(MobEntity e) {
        double d0 = e.getAttributeValue(Attributes.FOLLOW_RANGE);
        AxisAlignedBB axisalignedbb = AxisAlignedBB.unitCubeFromLowerCorner(e.position()).inflate(d0, 10.0D, d0);
        return e.level.getLoadedEntitiesOfClass(e.getClass(), axisalignedbb);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if(player.getUsedItemHand()==null)return false;

        return this.doInteract(stack,player, entity,player.getUsedItemHand()).consumesAction();

    }


    public ActionResultType doInteract(ItemStack stack, PlayerEntity player, Entity entity, Hand hand) {
        ResourceLocation n = entity.getType().getRegistryName();
        if(n==null)return ActionResultType.PASS;
        String name = n.toString();

        boolean isFirefly = false;
        boolean canBeCaught = false;
        if(ServerConfigs.cached.CAGE_ALL_MOBS) {
            canBeCaught = true;
        }
        else{
            switch (this.cageType) {
                case CAGE:
                    boolean dababy = entity instanceof LivingEntity && ((LivingEntity) entity).isBaby();
                    canBeCaught = ((ServerConfigs.cached.CAGE_ALL_BABIES && dababy) ||
                            ModTags.isTagged(ModTags.CAGE_CATCHABLE, entity.getType()) ||
                            (ModTags.isTagged(ModTags.CAGE_BABY_CATCHABLE, entity.getType()) && dababy));
                    break;
                case JAR:
                    isFirefly = entity.getType().getRegistryName().getPath().toLowerCase().contains("firefl");
                    canBeCaught = isFirefly || ModTags.isTagged(ModTags.JAR_CATCHABLE, entity.getType()) ||
                            CapturedMobs.CATCHABLE_FISHES.contains(entity.getType().getRegistryName().toString());
                    break;
                case TINTED_JAR:
                    canBeCaught = ModTags.isTagged(ModTags.TINTED_JAR_CATCHABLE, entity.getType())|| CapturedMobs.CATCHABLE_FISHES.contains(entity.getType().getRegistryName().toString());
                    break;
            }
        }
        if(!canBeCaught)return ActionResultType.PASS;

        if(!entity.isAlive() || (entity instanceof LivingEntity && ((LivingEntity) entity).isDeadOrDying()))return ActionResultType.PASS;
        if(entity instanceof SlimeEntity && ((SlimeEntity)entity).getSize()>1) return ActionResultType.PASS;

        if(player.level.isClientSide)return ActionResultType.SUCCESS;

        ItemStack returnStack = new ItemStack(isFirefly?  Registry.FIREFLY_JAR_ITEM.get() : this.full.get());

        if(!isFirefly) {

            if (stack.hasCustomHoverName()) returnStack.setHoverName(stack.getHoverName());

            CompoundNBT cmp = MobHolder.createMobHolderItemNBT(entity, this.cageType.height, this.cageType.width);
            if(cmp!=null) returnStack.addTagElement("BlockEntityTag", cmp);
        }

        CommonUtil.swapItemNBT(player,hand,stack,returnStack);
        //TODO: cage sound here
        if(this.cageType==CageWhitelist.CAGE)
            player.level.playSound(null, player.blockPosition(),  SoundEvents.CHAIN_FALL, SoundCategory.BLOCKS,1,0.7f);
        else
            player.level.playSound(null, player.blockPosition(),  SoundEvents.ARMOR_EQUIP_GENERIC, SoundCategory.BLOCKS,1,1);

        //anger entities
        if(entity instanceof IAngerable && entity instanceof MobEntity){
            getEntitiesInRange((MobEntity) entity).stream()
                    .filter((mob) -> mob != entity).map(
                    (mob) -> (IAngerable)mob).forEach((mob)->{
                mob.forgetCurrentTargetAndRefreshUniversalAnger();
                mob.setPersistentAngerTarget(player.getUUID());
                mob.setLastHurtByMob(player);
            });
        }
        //piglin workaround. don't know why they are IAngerable
        if(entity instanceof PiglinEntity){
            entity.hurt(DamageSource.playerAttack(player), 0);
        }

        entity.remove();
        return ActionResultType.CONSUME;
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        return this.doInteract(stack, player, entity, hand);
    }

    //TODO: add whitelist reference in here or in item constructor. maybe ad return item here too
    public enum CageWhitelist{
        CAGE( 1f, 0.875f),
        JAR( 0.875f, 0.625f),
        TINTED_JAR( 0.875f, 0.625f);

        public final float width;
        public final float height;

        CageWhitelist(float blockH, float blockW){
            this.width = blockW;
            this.height = blockH;
        }

        public boolean isJar(){
            return this!=CAGE;
        }
    }

}
