package net.mehvahdjukaar.supplementaries.items;

import net.mehvahdjukaar.supplementaries.entities.BombEntity;
import net.mehvahdjukaar.supplementaries.setup.Registry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class BombItem extends Item {
    public BombItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        playerIn.getCooldownTracker().setCooldown(this, 30);
        if (!worldIn.isRemote) {
            BombEntity bombEntity = new BombEntity(worldIn, playerIn);
            float pitch = -10;//playerIn.isSneaking()?0:-20;
            bombEntity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, pitch, 1.25F, 0.9F);
            worldIn.addEntity(bombEntity);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);

        }

        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }
}

