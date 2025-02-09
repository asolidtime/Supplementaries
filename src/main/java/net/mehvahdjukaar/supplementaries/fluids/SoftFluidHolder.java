package net.mehvahdjukaar.supplementaries.fluids;

import net.mehvahdjukaar.supplementaries.client.renderers.FluidParticleColors;
import net.mehvahdjukaar.supplementaries.common.CommonUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoftFluidHolder {
    public static final int BOTTLE_COUNT = 1;
    public static final int BOWL_COUNT = 2;
    public static final int BUCKET_COUNT = 4;

    @Nullable
    private World world = null;
    @Nullable
    private BlockPos pos = null;

    //count in bottles
    private int count = 0;
    private final int capacity;
    private CompoundNBT nbt = new CompoundNBT();
    private SoftFluid fluid = SoftFluidList.EMPTY;
    private int specialColor = 0;

    public SoftFluidHolder(int capacity) {
        this.capacity = capacity;
    }

    //again if you're readng this I'm sorry :/

    public void setWorldAndPos(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    //handles special nbt items such as potions or soups
    private void applyNBT(ItemStack stack) {
        if (this.nbt != null && !this.nbt.isEmpty())
            stack.setTag(this.nbt.copy());
    }

    private boolean addSpecial(SoftFluid s, int count, CompoundNBT com) {
        if (this.isEmpty()) {
            this.setFluid(s, com.copy());
            this.grow(count);
            return true;
        } else if (this.fluid == s && this.canAdd(count) && this.nbt.equals(com)) {
            this.grow(count);
            return true;
        }
        return false;
    }

    //returns null to handle the item with previous behavior (water bottles)
    @Nullable
    private ItemStack handleNBTItems(ItemStack stack) {
        Item item = stack.getItem();
        CompoundNBT com = stack.getTag();
        if (com != null) {
            if (item instanceof PotionItem) {
                Potion potion = PotionUtils.getPotion(stack);
                if(potion == Potions.WATER){
                    if (addSpecial(SoftFluidList.WATER, BOTTLE_COUNT, new CompoundNBT())) {
                        return new ItemStack(Items.GLASS_BOTTLE);
                    }
                }
                if (potion != Potions.EMPTY && com.contains("Potion")) {
                    CompoundNBT newCom = new CompoundNBT();
                    newCom.putString("Potion", com.getString("Potion"));
                    if (addSpecial(SoftFluidList.POTION, BOTTLE_COUNT, newCom)) {
                        return new ItemStack(Items.GLASS_BOTTLE);
                    }
                }
                //every other potion is water bottle. handles by normal code
                return ItemStack.EMPTY;
            } else if (item instanceof SuspiciousStewItem) {
                if (com.contains("Effects", 9)) {
                    ListNBT listnbt = com.getList("Effects", 10);
                    CompoundNBT newCom = new CompoundNBT();
                    newCom.put("Effects", listnbt);
                    if (addSpecial(SoftFluidList.SUS_STEW, BOWL_COUNT, newCom)) {
                        return new ItemStack(Items.BOWL);
                    }
                }
            }
        }
        return null;
    }

    private void applyFluidNBT(FluidStack fluidStack) {
        if (this.nbt != null && !this.nbt.isEmpty())
            fluidStack.setTag(this.nbt.copy());
    }

    public boolean interactWithPlayer(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        ItemStack returnStack = this.interactWithItem(handStack);
        //for items that have no bottle
        if(returnStack==null){
            CommonUtil.swapItem(player, hand, ItemStack.EMPTY);
            return true;
        }
        else if (!returnStack.isEmpty()) {
            CommonUtil.swapItem(player, hand, returnStack);
            //TODO: replace all those with these
            //player.setHeldItem(hand, DrinkHelper.fill(handStack.copy(), player, returnStack, false));
            player.awardStat(Stats.ITEM_USED.get(handStack.getItem()));
            return true;
        }
        return false;
    }

    @Nullable
    public ItemStack interactWithItem(ItemStack stack) {
        //special nbt items like potions and stews
        ItemStack specialCase = this.handleNBTItems(stack);
        if (specialCase != null) {
            if (this.world != null && !this.world.isClientSide && this.pos != null && !specialCase.isEmpty())
                this.world.playSound(null, this.pos, SoundEvents.BOTTLE_EMPTY, SoundCategory.BLOCKS, 1, 1);
            return specialCase;
        }
        //used for items that don't have a bottle like slimeballs

        Item i = stack.getItem();
        ItemStack returnStack;
        SoundEvent sound;
        if (i == fluid.getEmptyBottle()) {
            sound = SoundEvents.BOTTLE_FILL;
            returnStack = this.fillBottle();
        } else if (i == Items.BOWL) {
            sound = SoundEvents.BOTTLE_FILL;
            returnStack = this.fillBowl();
        } else if (i == Items.BUCKET) {
            sound = this.fluid.getFillSound();
            returnStack = this.fillBucket();
        } else {
            returnStack = this.drainItem(stack);
            //eh. might return null here for success on empty hand
            sound = (returnStack==null||returnStack.getItem() == Items.BUCKET) ? this.fluid.getEmptySound() : SoundEvents.BOTTLE_EMPTY;
        }
        if (this.world != null && !this.world.isClientSide && (returnStack==null||!returnStack.isEmpty()) && i!=Items.AIR) {
            this.world.playSound(null, this.pos, sound, SoundCategory.BLOCKS, 1, 1);
        }
        return returnStack;
    }

    @Nonnull
    //fills a bottle and removes equivalent liquid
    public ItemStack fillBottle() {
        if (this.canRemove(BOTTLE_COUNT) && (fluid.hasBottle() || fluid == SoftFluidList.WATER)) {
            ItemStack stack = new ItemStack(fluid.getBottle());
            if (fluid == SoftFluidList.WATER)
                stack = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
            this.applyNBT(stack);
            this.shrink(BOTTLE_COUNT);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack fillBucket() {
        if (this.canRemove(BUCKET_COUNT) && fluid.hasBucket()) {
            ItemStack stack = new ItemStack(fluid.getBucket());
            this.applyNBT(stack);
            this.shrink(BUCKET_COUNT);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack fillBowl() {
        if (this.canRemove(BOWL_COUNT) && fluid.hasBowl()) {
            ItemStack stack = new ItemStack(fluid.getBowl());
            this.applyNBT(stack);
            this.shrink(BOWL_COUNT);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    //empties a fluid contained item and returns its empty counterpart. null for empty hand return
    @Nullable
    public ItemStack drainItem(ItemStack stack) {
        Item i = stack.getItem();
        //set new fluid
        if (this.isEmpty()) {
            SoftFluid s = SoftFluidList.fromItem(i);
            if (PotionUtils.getPotion(stack) == Potions.WATER) s = SoftFluidList.WATER;
            if (s.isEmpty()) return ItemStack.EMPTY;
            this.setFluid(s);
        }
        if (this.fluid.hasBottle(i) && this.canAdd(BOTTLE_COUNT)) {
            this.grow(BOTTLE_COUNT);

            ItemStack returnStack = new ItemStack(this.fluid.getEmptyBottle());
            if(returnStack.isEmpty())return null;
            return returnStack;
        } else if (this.fluid.hasBowl(i) && this.canAdd(BOWL_COUNT)) {
            this.grow(BOWL_COUNT);
            return new ItemStack(Items.BOWL);
        } else if (this.fluid.hasBucket(i) && this.canAdd(BUCKET_COUNT)) {
            this.grow(BUCKET_COUNT);
            return new ItemStack(Items.BUCKET);
        } else if (this.canAdd(BOTTLE_COUNT) && PotionUtils.getPotion(stack) == Potions.WATER) {
            this.grow(BOTTLE_COUNT);
            return new ItemStack(Items.GLASS_BOTTLE);
        }

        return ItemStack.EMPTY;
    }

    public boolean isSameFluidAs(FluidStack fluidStack, CompoundNBT com) {
        return this.fluid.isEquivalent(fluidStack.getFluid()) && com.equals(this.nbt);
    }

    public boolean isSameFluidAs(FluidStack fluidStack) {
        return this.fluid.isEquivalent(fluidStack.getFluid()) && fluidStack.getOrCreateTag().equals(this.nbt);
    }

    public boolean isSameAs(SoftFluid other) {
        return this.fluid.equals(other) && this.nbt.isEmpty();
    }

    public boolean isSameAs(SoftFluidHolder other) {
        return this.fluid.equals(other.getFluid()) && this.nbt.equals(other.getNbt());
    }

    public boolean tryAddingFluid(SoftFluid s) {
        if (this.canAdd(1)) {
            if (this.isEmpty()) {
                this.setFluid(s);
                this.grow(1);
                return true;
            } else if (this.isSameAs(s)) {
                this.grow(1);
                return true;
            }
        }
        return false;
    }

    public boolean tryTransferFluid(SoftFluidHolder other) {
        return this.tryTransferFluid(other, BOTTLE_COUNT);
    }

    //transfers between two fluid holders
    public boolean tryTransferFluid(SoftFluidHolder other, int amount) {
        if (other.canAdd(amount) && this.canRemove(amount)) {
            if (other.isEmpty()) {
                other.setFluid(this.getFluid(), this.getNbt());
                this.shrink(amount);
                other.grow(amount);
                return true;
            } else if (this.isSameAs(other)) {
                this.shrink(amount);
                other.grow(amount);
                return true;
            }
        }
        return false;
    }

    //empties 1 bottle of content into said fluid tank
    public boolean fillFluidTank(IFluidHandler fluidDestination, int bottles) {
        if (!this.canRemove(bottles)) return false;
        int milliBuckets = bottles * 250;
        FluidStack stack = this.toFluid(milliBuckets);
        if (!stack.isEmpty()) {
            int fillableAmount = fluidDestination.fill(stack, IFluidHandler.FluidAction.SIMULATE);
            if (fillableAmount == milliBuckets) {
                fluidDestination.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                this.shrink(bottles);
                return true;
            }
        }
        return false;
    }

    public boolean fillFluidTank(IFluidHandler fluidDestination) {
        return this.fillFluidTank(fluidDestination, BOTTLE_COUNT);
    }

    //drains said fluid tank of 250mb (1 bottle) of fluid
    public boolean drainFluidTank(IFluidHandler fluidSource, int bottles) {
        if (!this.canAdd(bottles)) return false;
        int milliBuckets = bottles * 250;
        FluidStack drainable = fluidSource.drain(milliBuckets, IFluidHandler.FluidAction.SIMULATE);
        if (!drainable.isEmpty() && drainable.getAmount() == milliBuckets) {
            boolean transfer = false;
            CompoundNBT fsTag = drainable.getOrCreateTag();
            if (this.fluid.isEmpty()) {
                this.setFluid(SoftFluidList.fromFluid(drainable.getFluid()), drainable.getOrCreateTag());
                transfer = true;
            } else if (this.isSameFluidAs(drainable, fsTag)) {
                transfer = true;
            }
            if (transfer) {
                fluidSource.drain(milliBuckets, IFluidHandler.FluidAction.EXECUTE);
                this.grow(bottles);
                return true;
            }
        }
        return false;
    }

    public boolean drainFluidTank(IFluidHandler fluidSource) {
        return this.drainFluidTank(fluidSource, BOTTLE_COUNT);
    }

    //returns n mb of contained fluid without draining. Do empty check
    public FluidStack toFluid(int mb) {
        FluidStack stack = new FluidStack(this.fluid.getFluid(), mb);

        this.applyFluidNBT(stack);
        return stack;
    }

    public boolean canRemove(int i) {
        return this.count >= i && !this.isEmpty();
    }

    public boolean canAdd(int i) {
        return this.count + i <= this.capacity;
    }

    public boolean isFull() {
        return this.count == this.capacity;
    }

    public boolean isEmpty() {
        //count 0 should always = to fluid.empty
        return this.fluid.isEmpty()||this.count<=0;
    }

    public void lossyAdd(int inc) {
        this.count = Math.min(this.capacity, this.count + inc);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int inc) {
        this.count = inc;
    }

    public void fillCount() {
        this.count = capacity;
    }

    public void grow(int inc) {
        this.setCount(this.count + inc);
    }

    public void shrink(int inc) {
        this.grow(-inc);
        if(this.count==0)this.fluid = SoftFluidList.EMPTY;
    }

    public float getHeight() {
        return 0.75f * (float) this.count / (float) this.capacity;
    }

    public int getComparator() {
        float f = this.count / (float) this.capacity;
        return MathHelper.floor(f * 14.0F) + 1;
    }

    @Nonnull
    public SoftFluid getFluid() {
        return fluid;
    }

    public CompoundNBT getNbt() {
        return nbt;
    }

    public void setNbt(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    //clears the tank
    public void clear() {
        this.fluid = SoftFluidList.EMPTY;
        this.setCount(0);
        this.nbt = new CompoundNBT();
        this.specialColor = 0;
    }


    public void copy(SoftFluidHolder other) {
        this.setFluid(other.getFluid(), other.getNbt());
        this.setCount(Math.min(this.capacity, other.getCount()));
    }

    public void fill(FluidStack fluidStack) {
        this.setFluid(fluidStack);
        this.setCount(capacity);
    }

    //fills to max capacity with said fluid
    public void fill(SoftFluid fluid) {
        this.fill(fluid, new CompoundNBT());
    }

    public void fill(SoftFluid fluid, CompoundNBT nbt) {
        this.setFluid(fluid, nbt);
        this.setCount(capacity);
    }

    public void setFluid(FluidStack fluidStack) {
        SoftFluid s = SoftFluidList.fromFluid(fluidStack.getFluid());
        this.setFluid(s, fluidStack.getOrCreateTag());
    }

    public void setFluid(SoftFluid fluid) {
        this.setFluid(fluid, new CompoundNBT());
    }

    //called when it goes from empty to full
    public void setFluid(SoftFluid fluid, CompoundNBT nbt) {
        this.fluid = fluid;
        this.nbt = nbt;
        this.specialColor = 0;
        //only possible and needed client side
        if (fluid == SoftFluidList.WATER) {
            if (this.world != null && this.world.isClientSide && this.pos != null) {
                this.specialColor = BiomeColors.getAverageWaterColor(this.world, this.pos);
            }
        } else if (fluid == SoftFluidList.POTION) {
            Potion potion = PotionUtils.getPotion(this.nbt);
            this.specialColor = PotionUtils.getColor(potion);
        }
    }

    public boolean isFood() {
        return this.fluid.isFood();
    }

    public ItemStack getFood() {
        ItemStack stack = new ItemStack(this.fluid.getFoodItem());
        this.applyNBT(stack);
        return stack;
    }

    public int getTintColor() {
        if (this.specialColor != 0) return this.specialColor;
        return this.fluid.getTintColor();
    }

    //only client
    public int getParticleColor() {
        if (this.isEmpty()) return -1;
        int tintColor = this.getTintColor();
        if (tintColor == -1) return FluidParticleColors.get(this.fluid.getID());
        return tintColor;
    }

    public void read(CompoundNBT compound) {
        if (compound.contains("FluidHolder")) {
            CompoundNBT cmp = compound.getCompound("FluidHolder");
            this.count = cmp.getInt("Count");
            this.nbt = cmp.getCompound("NBT");
            this.setFluid(SoftFluidList.fromID(cmp.getString("Fluid")), this.nbt);
        }
    }

    public CompoundNBT write(CompoundNBT compound) {

        CompoundNBT cmp = new CompoundNBT();
        cmp.putInt("Count", this.count);
        cmp.putString("Fluid", this.fluid.getID());
        //for item render. needed for potion colors
        cmp.putInt("CachedColor", this.getTintColor());
        if (!this.nbt.isEmpty()) cmp.put("NBT", this.nbt);
        compound.put("FluidHolder", cmp);

        return compound;
    }

    //makes player drink 1 bottle and consumes it
    public boolean drinkUpFluid(PlayerEntity player, World world, Hand hand) {
        if (this.isEmpty()) return false;
        ItemStack stack = this.getFood();
        Item item = stack.getItem();

        boolean success = false;
        Food food = item.getFoodProperties();


        int div = this.fluid.getFoodDivider();
        if(div == 1){
            stack.getItem().finishUsingItem(stack.copy(),world,player);
            if (world.isClientSide) return true;
            this.shrink(1);
            if(food==null||stack.getItem().isEdible()){
                player.playNotifySound(item.getDrinkingSound(), SoundCategory.PLAYERS, 1, 1);
            }
            //player already plays sound
            return true;
        }

        if (food!=null && player.canEat(false)) {

            if (item instanceof SuspiciousStewItem) susStewBehavior(player, stack, div);

            if (world.isClientSide) return true;
            player.getFoodData().eat(food.getNutrition() / div, food.getSaturationModifier() / (float) div);

            success = true;
        }
        //unused
        else if (item instanceof PotionItem) {
            if (world.isClientSide) return true;
            //potion code
            for (EffectInstance effectinstance : PotionUtils.getMobEffects(stack)) {
                if (effectinstance.getEffect().isInstantenous()) {
                    effectinstance.getEffect().applyInstantenousEffect(player, player, player, effectinstance.getAmplifier(), 1.0D);
                } else {
                    player.addEffect(new EffectInstance(effectinstance));
                }
            }
            success = true;
        }
        else if (item instanceof MilkBucketItem || item.getRegistryName().toString().equals("farmersdelight:hot_cocoa")) {
            if (world.isClientSide) return true;
            milkBottleBehavior(player, stack);
            success = true;
        }
        if (success) {
            this.shrink(1);
            player.playNotifySound(item.getDrinkingSound(), SoundCategory.PLAYERS, 1, 1);
            return true;
        }
        return false;
    }

    //stew code
    public static void susStewBehavior(PlayerEntity player, ItemStack stack, int div) {
        CompoundNBT compoundnbt = stack.getTag();
        if (compoundnbt != null && compoundnbt.contains("Effects", 9)) {
            ListNBT listnbt = compoundnbt.getList("Effects", 10);
            for (int i = 0; i < listnbt.size(); ++i) {
                int j = 160;
                CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                if (compoundnbt1.contains("EffectDuration", 3))
                    j = compoundnbt1.getInt("EffectDuration") / div;
                Effect effect = Effect.byId(compoundnbt1.getByte("EffectId"));
                if (effect != null) {
                    player.addEffect(new EffectInstance(effect, j));
                }
            }
        }
    }

    //removes just 1 effect
    public static boolean milkBottleBehavior(PlayerEntity player, ItemStack stack) {
        for (EffectInstance effect : player.getActiveEffectsMap().values()) {
            if (effect.isCurativeItem(stack)) {
                player.removeEffect(effect.getEffect());
                return true;
            }
        }
        return false;
    }

    public static void honeyBehavior(PlayerEntity player) {
        player.removeEffect(Effects.POISON);
    }

    //util functions
    public static int getLiquidCountFromItem(Item i) {
        if (i == Items.GLASS_BOTTLE) {
            return BOTTLE_COUNT;
        } else if (i == Items.BOWL) {
            return BOWL_COUNT;
        } else if (i == Items.BUCKET) {
            return BUCKET_COUNT;
        }
        return 0;
    }

}
