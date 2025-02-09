package net.mehvahdjukaar.supplementaries.datagen;

import net.mehvahdjukaar.supplementaries.datagen.types.IWoodType;
import net.mehvahdjukaar.supplementaries.setup.Registry;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }
    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumerIn) {

        for(DyeColor color : DyeColor.values()){
            makeFlagRecipe(color,consumerIn);
        }
        /*
        for (IWoodType wood : WoodTypes.TYPES.values()) {
            makeSignPostRecipe(wood, consumerIn);
            makeHangingSignRecipe(wood,consumerIn);
        }
        */

    }


    public static void makeConditionalRec(IFinishedRecipe r, Consumer<IFinishedRecipe> consumer, String name){


        ConditionalRecipe.builder()
                .addCondition(new RecipeCondition(name, RecipeCondition.MY_FLAG))
                .addRecipe(r)
                .generateAdvancement()
                .build(consumer,"supplementaries",name);
    }

    public static void makeConditionalWoodRec(IFinishedRecipe r, IWoodType wood, Consumer<IFinishedRecipe> consumer, String name){


        ConditionalRecipe.builder().addCondition(new RecipeCondition(name, RecipeCondition.MY_FLAG))
                .addCondition(new ModLoadedCondition(wood.getNamespace()))
                .addRecipe(r)
                .generateAdvancement()
                .build(consumer,"supplementaries",name+"_"+wood.getRegName());
    }

    private static void makeSignPostRecipe(IWoodType wood, Consumer<IFinishedRecipe> consumer) {
        try{
            Item plank = ForgeRegistries.ITEMS.getValue(new ResourceLocation(wood.getPlankRegName()));
            Item sign = ForgeRegistries.ITEMS.getValue(new ResourceLocation(wood.getSignRegName()));
            if (plank == null || plank == Items.AIR) return;
            if(sign!=null && sign != Items.AIR) {
                ShapelessRecipeBuilder.shapeless(Registry.SIGN_POST_ITEMS.get(wood).get(), 2)
                        .requires(sign)
                        .group(Registry.SIGN_POST_NAME)
                        .unlockedBy("has_plank", InventoryChangeTrigger.Instance.hasItems(plank))
                        //.build(consumer);
                        .save((s) -> makeConditionalWoodRec(s, wood, consumer, Registry.SIGN_POST_NAME)); //
            }
            else{
                ShapedRecipeBuilder.shaped(Registry.SIGN_POST_ITEMS.get(wood).get(), 3)
                        .pattern("   ")
                        .pattern("222")
                        .pattern(" 1 ")
                        .define('1', Items.STICK)
                        .define('2', plank)
                        .group(Registry.SIGN_POST_NAME)
                        .unlockedBy("has_plank", InventoryChangeTrigger.Instance.hasItems(plank))
                        //.build(consumer);
                        .save((s) -> makeConditionalWoodRec(s, wood, consumer,Registry.SIGN_POST_NAME)); //
            }
        }
        catch (Exception ignored){}
    }


    private static void makeHangingSignRecipe(IWoodType wood, Consumer<IFinishedRecipe> consumer) {

            Item plank = ForgeRegistries.ITEMS.getValue(new ResourceLocation(wood.getPlankRegName()));
            if (plank == null || plank == Items.AIR){
                return;
            }
            ShapedRecipeBuilder.shaped(Registry.HANGING_SIGNS.get(wood).get(), 2)
                    .pattern("010")
                    .pattern("222")
                    .pattern("222")
                    .define('0', Items.IRON_NUGGET)
                    .define('1', Items.STICK)
                    .define('2', plank)
                    .group(Registry.HANGING_SIGN_NAME)
                    .unlockedBy("has_plank", InventoryChangeTrigger.Instance.hasItems(plank))
                    //.build(consumer);
                    .save((s) -> makeConditionalWoodRec(s, wood, consumer,Registry.HANGING_SIGN_NAME)); //


    }

    private static void makeFlagRecipe(DyeColor color, Consumer<IFinishedRecipe> consumer) {

        Item wool = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", color.name()+"_wool"));
        if (wool == null || wool == Items.AIR){
            return;
        }
        ShapedRecipeBuilder.shaped(Registry.FLAGS.get(color).get(), 1)
                .pattern("222")
                .pattern("222")
                .pattern("1  ")
                .define('1', Items.STICK)
                .define('2', wool)
                .group(Registry.FLAG_NAME)
                .unlockedBy("has_wool", InventoryChangeTrigger.Instance.hasItems(wool))
                //.build(consumer);
                .save((s) -> makeConditionalRec(s, consumer,Registry.HANGING_SIGN_NAME+"_"+color.getName())); //


    }


}
