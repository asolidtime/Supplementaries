package net.mehvahdjukaar.supplementaries.fluids;

import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.client.renderers.FluidParticleColors;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SoftFluid {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final int tintColor;
    private final List<Fluid> equivalentFluids;
    private final int luminosity;
    private final List<Item> filledBottles;
    private final List<Item> filledBuckets;
    private final List<Item> filledBowls;
    private final Item bottleReturnItem;
    private final Item foodItem;
    private final int foodDivider;
    private final SoundEvent fillSound;
    private final SoundEvent emptySound;
    private final String id;
    private final String translationKey;
    private final boolean isEmptyHand;

    public SoftFluid(Builder builder){
        this.stillTexture = builder.stillTexture;
        this.flowingTexture = builder.flowingTexture;
        this.tintColor = builder.tintColor;
        this.equivalentFluids = builder.equivalentFluids;
        this.luminosity = builder.luminosity;
        this.filledBottles = builder.filledBottles;
        this.filledBuckets = builder.filledBuckets;
        this.filledBowls = builder.filledBowls;
        this.foodItem = builder.getOrCreateFood();
        this.foodDivider = Math.max(0,builder.foodDivider);
        this.fillSound = builder.fillSound;
        this.emptySound = builder.emptySound;
        this.id = builder.id;
        this.translationKey = builder.translationKey;
        this.bottleReturnItem = builder.bottleReturnItem;
        //empty bottles = empty hand
        this.isEmptyHand = (this.bottleReturnItem == Items.AIR);
    }

    public int getFoodDivider() {
        return foodDivider;
    }

    public Item getFoodItem() {
        return foodItem;
    }

    public TranslationTextComponent getTranslatedName(){
        return new TranslationTextComponent(this.translationKey);
    }

    public String getName(){return this.getID();}
    public String toString(){return this.getID();}
    public String getString(){return this.getID();}

    //gets equivalent forge fluid if present
    public Fluid getFluid(){
        for(Fluid fluid : this.equivalentFluids){
            return fluid;
        }
        return Fluids.EMPTY;
    }

    public boolean isEquivalent(Fluid fluid){
        return this.equivalentFluids.contains(fluid);
    }

    public String getID() {
        return id;
    }

    public boolean isEmpty(){
        return this==SoftFluidList.EMPTY;
    }

    public boolean hasBucket(Item item){
        return this.filledBuckets.contains(item);
    }

    public boolean hasBowl(Item item){
        return this.filledBowls.contains(item);
    }

    public boolean hasBottle(Item item){
        return this.filledBottles.contains(item);
    }

    public boolean hasBucket(){
        return !this.filledBuckets.isEmpty();
    }

    public boolean hasBowl(){
        return !this.filledBowls.isEmpty();
    }

    public boolean hasBottle(){
        return !this.filledBottles.isEmpty();
    }

    public int getLuminosity() {
        return luminosity;
    }

    public int getTintColor() {
        return tintColor;
    }

    public boolean isColored(){
        return this.tintColor!=-1;
    }

    //only client
    public int getParticleColor() {
        if(!this.isColored()) return FluidParticleColors.get(this.id);
        return this.tintColor;
    }

    public Item getEmptyBottle(){
        return this.bottleReturnItem;
    }

    public Collection<Item> getBowls(){
        return this.filledBowls;
    }
    public Collection<Item> getBuckets(){
        return this.filledBuckets;
    }
    public Collection<Item> getBottles(){
        return this.filledBottles;
    }

    @Nullable
    public Item getBottle() {
        for(Item item : this.filledBottles){
            return item;
        }
        return null;
    }

    @Nullable
    public Item getBowl() {
        for(Item item : this.filledBowls){
            return item;
        }
        return null;
    }
    @Nullable
    public Item getBucket() {
        for(Item item : this.filledBuckets){
            return item;
        }
        return null;
    }

    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    public SoundEvent getEmptySound() {
        return emptySound;
    }

    public SoundEvent getFillSound() {
        return fillSound;
    }

    public boolean isFood(){
        return this.foodItem!=Items.AIR;
    }

    public static class Builder{
        private ResourceLocation stillTexture;
        private ResourceLocation flowingTexture;
        private String translationKey = "fluid.supplementaries.jar_fluid";
        private int tintColor = -1;
        private int luminosity = 0;
        private final List<Item> filledBottles = new ArrayList<>();
        private final List<Item> filledBuckets = new ArrayList<>();
        private final List<Item> filledBowls = new ArrayList<>();
        private Item bottleReturnItem = Items.GLASS_BOTTLE;
        private Item foodItem = Items.AIR;
        private int foodDivider = 1;
        //only used for buckets. rest only depends on return item
        private SoundEvent fillSound = SoundEvents.BUCKET_FILL;
        private SoundEvent emptySound = SoundEvents.BUCKET_EMPTY;
        private String id;
        public boolean isDisabled = false;

        private final List<Fluid> equivalentFluids = new ArrayList<>();

        public Builder(ResourceLocation stillTexture, ResourceLocation flowingTexture, String id) {
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
            this.id = Supplementaries.MOD_ID+":"+id;
        }
        public Builder(Fluid fluid) {
            FluidAttributes att = fluid.getAttributes();
            this.stillTexture = att.getStillTexture();
            this.flowingTexture = att.getFlowingTexture();
            this.color(att.getColor());
            this.bucket(fluid.getBucket());
            this.luminosity = att.getLuminosity();
            this.translationKey = att.getTranslationKey();
            this.addEqFluid(fluid);
            this.id = fluid.getRegistryName().toString();
            //TODO: figure out particleColor
        }
        public Builder(String fluidRes) {
            if(ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(fluidRes))) {
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidRes));
                if (fluid != null && fluid != Fluids.EMPTY) {
                    FluidAttributes att = fluid.getAttributes();
                    this.stillTexture = att.getStillTexture();
                    this.flowingTexture = att.getFlowingTexture();
                    this.color(att.getColor());
                    this.bucket(fluid.getBucket());
                    this.luminosity = att.getLuminosity();
                    this.translationKey = att.getTranslationKey();
                    this.addEqFluid(fluid);
                    this.id = fluid.getRegistryName().toString();
                }
                else this.isDisabled=true;
            }
            else this.isDisabled=true;
        }
        public final Builder textures(ResourceLocation still, ResourceLocation flow) {
            this.stillTexture = still;
            this.flowingTexture = flow;
            return this;
        }
        public final Builder translationKey(String translationKey) {
            if(translationKey == null) this.translationKey = "fluid.supplementaries.jar_fluid";
            else{this.translationKey = translationKey;}
            return this;
        }
        public final Builder color(int tintColor) {
            this.tintColor = tintColor;
            return this;
        }
        public final Builder luminosity(int luminosity) {
            this.luminosity = luminosity;
            return this;
        }
        public final Builder addEqFluid(ResourceLocation fluidRes) {
            if(ForgeRegistries.FLUIDS.containsKey(fluidRes)) {
                Fluid f = ForgeRegistries.FLUIDS.getValue(fluidRes);
                if (f != null && f != Fluids.EMPTY) {
                    this.equivalentFluids.add(f);
                    Item i = f.getBucket();
                    if (i != null && i != Items.AIR) this.bucket(i);
                }
            }
            return this;
        }
        public final Builder addEqFluid(String res) {
            this.addEqFluid(new ResourceLocation(res));
            return this;
        }
        public final Builder addEqFluid(Fluid fluid) {
            this.addEqFluid(fluid.getRegistryName());
            return this;
        }
        public final Builder textureOverrideF(String fluidRes){
            if(ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(fluidRes))) {
                Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidRes));
                if (f != null && f != Fluids.EMPTY) {
                    this.flowingTexture = f.getAttributes().getFlowingTexture();
                    //this.stillTexture = f.getAttributes().getStillTexture();
                }
            }
            return this;
        }
        public final Builder condition(String modId){
            this.isDisabled = !ModList.get().isLoaded(modId);
            if(this.id==null)this.id = modId;
            else if(this.id.contains(Supplementaries.MOD_ID)) this.id = this.id.replace(Supplementaries.MOD_ID,modId);
            return this;
        }
        public final Builder textureOverride(String fluidRes, int newColor){
            if(ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(fluidRes))) {
                Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidRes));
                if (f != null && f != Fluids.EMPTY) {
                    this.flowingTexture = f.getAttributes().getFlowingTexture();
                    this.stillTexture = f.getAttributes().getStillTexture();
                    this.color(newColor);
                }
            }
            return this;
        }
        public final Builder textureOverride(String fluidRes){
            if(ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(fluidRes))) {
                Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidRes));
                if (f != null && f != Fluids.EMPTY) {
                    this.flowingTexture = f.getAttributes().getFlowingTexture();
                    this.stillTexture = f.getAttributes().getStillTexture();
                }
            }
            return this;
        }
        //bottle
        public final Builder bottle(Item item) {
            if(item!=null&&item!=Items.AIR) {
                this.filledBottles.add(item);
            }
            return this;
        }
        public final Builder bottle(ResourceLocation itemRes) {
            if(ForgeRegistries.ITEMS.containsKey(itemRes)) {
                Item i = ForgeRegistries.ITEMS.getValue(itemRes);
                if (i != null) this.bottle(i);
            }
            return this;
        }
        public final Builder bottle(String res) {
            return this.bottle(new ResourceLocation(res));
        }
        public final Builder drink(String res){
            return this.bottle(res).food(res);
        }
        //bucket
        public final Builder bucket(Item item) {
            if(item!=null&&item!=Items.AIR)
                this.filledBuckets.add(item);
            return this;
        }
        public final Builder bucket(ResourceLocation itemRes) {
            if(ForgeRegistries.ITEMS.containsKey(itemRes)) {
                Item i = ForgeRegistries.ITEMS.getValue(itemRes);
                if (i != null) this.bucket(i);
            }
            return this;
        }
        public final Builder bucket(String res) {
            return this.bucket(new ResourceLocation(res));
        }
        //bowl
        public final Builder bowl(Item item) {
            if(item!=null&&item!=Items.AIR) {
                this.filledBowls.add(item);
                //only works with bowls
                this.translationKey(item.getDescriptionId());
            }
            return this;
        }
        public final Builder bowl(ResourceLocation itemRes) {
            if(ForgeRegistries.ITEMS.containsKey(itemRes)) {
                Item i = ForgeRegistries.ITEMS.getValue(itemRes);
                if (i != null) this.bowl(i);
            }
            return this;
        }
        public final Builder bowl(String res) {
            return this.bowl(new ResourceLocation(res));
        }
        public final Builder fillSound(SoundEvent sound) {
            this.fillSound = sound;
            return this;
        }
        public final Builder emptySound(SoundEvent sound) {
            this.emptySound = sound;
            return this;
        }
        public final Builder sound(SoundEvent fill, SoundEvent empty) {
            this.emptySound = empty;
            this.fillSound = fill;
            return this;
        }
        public final Builder specialEmptyBottle(Item item){
            this.bottleReturnItem = item;
            return this;
        }
        public final Builder specialItem(String item){
            this.bottle(item);
            return this.specialEmptyBottle(Items.AIR);
        }
        //food
        public final Builder food(Item item) {
            if(item!=null&&item!=Items.AIR)
                this.foodItem = item;
            return this;
        }
        public final Builder food(ResourceLocation itemRes) {
            if(ForgeRegistries.ITEMS.containsKey(itemRes)){
                Item i = ForgeRegistries.ITEMS.getValue(itemRes);
                if (i != null) this.food(i);
            }
            return this;
        }
        public final Builder food(String res) {
            return this.food(new ResourceLocation(res));
        }
        public final Builder food(Item item, int divider) {
            this.foodDivider = divider;
            return this.food(item);
        }
        public final Builder food(String res, int divider) {
            this.foodDivider = divider;
            return this.food(res);
        }

        //I can only have 1 food item
        protected Item getOrCreateFood(){
            for (Item i : this.filledBuckets){
                if(i.isEdible()){
                    this.foodItem=i;
                    this.foodDivider=SoftFluidHolder.BUCKET_COUNT;
                    break;
                }
            }
            for (Item i : this.filledBowls){
                if(i.isEdible()){
                    this.foodItem=i;
                    this.foodDivider=SoftFluidHolder.BOWL_COUNT;
                    break;
                }
            }
            for (Item i : this.filledBottles){
                if(i.isEdible()){
                    this.foodItem=i;
                    this.foodDivider=SoftFluidHolder.BOTTLE_COUNT;
                    break;
                }
            }
            return this.foodItem;
        }
    }
}
