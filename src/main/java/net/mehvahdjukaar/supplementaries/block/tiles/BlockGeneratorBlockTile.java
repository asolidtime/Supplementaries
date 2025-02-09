package net.mehvahdjukaar.supplementaries.block.tiles;


import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.block.blocks.EnhancedLanternBlock;
import net.mehvahdjukaar.supplementaries.block.blocks.NoticeBoardBlock;
import net.mehvahdjukaar.supplementaries.configs.ServerConfigs;
import net.mehvahdjukaar.supplementaries.setup.Registry;
import net.mehvahdjukaar.supplementaries.world.structures.RoadSignFeature;
import net.mehvahdjukaar.supplementaries.world.structures.StructureLocator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockGeneratorBlockTile extends TileEntity implements ITickableTileEntity {
    private boolean firstTick = true;
    public BlockGeneratorBlockTile() {
        super(Registry.BLOCK_GENERATOR_TILE.get());
    }

    private static final BlockState trapdoor = Blocks.SPRUCE_TRAPDOOR.defaultBlockState();
    private static final BlockState lantern = Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING,true);
    private static final BlockState lanternDown = Blocks.LANTERN.defaultBlockState();
    private static final BlockState fence = Blocks.SPRUCE_FENCE.defaultBlockState();
    private static final BlockState jar = Registry.FIREFLY_JAR.get().defaultBlockState();
    private static final BlockState slab = Blocks.SPRUCE_SLAB.defaultBlockState();
    private static final BlockState log = Blocks.STRIPPED_SPRUCE_LOG.defaultBlockState();
    private static final BlockState stoneSlab = Blocks.STONE_SLAB.defaultBlockState();
    private static final BlockState stone = Blocks.STONE.defaultBlockState();
    private static final BlockState stair = Blocks.STONE_STAIRS.defaultBlockState();
    private static final BlockState air = Blocks.AIR.defaultBlockState();


    private double averageAngles(float a, float b){
        a= (float) (a*Math.PI/180);
        b= (float) (b*Math.PI/180);

        return (180/Math.PI)*MathHelper.atan2(MathHelper.sin(a)+MathHelper.sin(b),MathHelper.cos(a)+MathHelper.cos(b));
    }

    //TODO: this has to be the worst code I've written here
    @Override
    public void tick() {
        //if you are reading this I'm sorry...



        if(!(this.level instanceof ServerWorld))return;

        try {
            if (this.firstTick) {
                this.firstTick = false;
                ServerWorld world = (ServerWorld) this.level;
                BlockPos pos = this.worldPosition.below(2);

                BlockState topState = trapdoor;

                List<Pair<Integer,BlockPos>> villages = StructureLocator.find(world, pos, 25, 2);


                if (villages.size() >= 1) {


                    Random rand = this.level.random;
                    //if two signs will spawn
                    boolean twoSigns = true;
                    BlockPos village1;
                    BlockPos village2;
                    int dist1;
                    int dist2;


                    //only 1 sing found/ 1 sign post. always to closest village. posts that are relatively close to a village will always have two.
                    //posts in a village will point away
                    if (villages.size() == 1 || (0.3 > rand.nextFloat() && villages.get(0).getLeft() > 192)) {
                        dist1 = villages.get(0).getLeft();
                        village1 = villages.get(0).getRight();
                        dist2 = dist1;
                        village2 = village1;
                        twoSigns = false;
                    } else {
                        boolean inv = rand.nextBoolean();
                        dist1 = villages.get(inv ? 0 : 1).getLeft();
                        village1 = villages.get(inv ? 0 : 1).getRight();
                        dist2 = villages.get(inv ? 1 : 0).getLeft();
                        village2 = villages.get(inv ? 1 : 0).getRight();
                    }


                    this.level.setBlock(pos, Registry.SIGN_POST.get().defaultBlockState(), 3);
                    TileEntity te = this.level.getBlockEntity(pos);
                    if (te instanceof SignPostBlockTile) {
                        SignPostBlockTile sign = ((SignPostBlockTile) te);
                        sign.setHeldBlock(Blocks.SPRUCE_FENCE.defaultBlockState());


                        boolean left = rand.nextBoolean();

                        sign.up = true;
                        sign.leftUp = left;
                        sign.pointToward(village1, true);


                        sign.down = twoSigns;
                        sign.leftDown = left;
                        sign.pointToward(village2, false);
                        if (Math.abs(sign.yawUp - sign.yawDown) > 90) {
                            sign.leftDown = !sign.leftDown;
                            sign.pointToward(village2, false);
                        }


                        if (ServerConfigs.cached.DISTANCE_TEXT) {
                            sign.textHolder.setText(0, getSignText(dist1));
                            if (twoSigns)
                                sign.textHolder.setText(1, getSignText(dist2));
                        }


                        float yaw = MathHelper.wrapDegrees(90 + (float) this.averageAngles(-sign.yawUp + 180, -sign.yawDown + 180));
                        Direction backDir = Direction.fromYRot(yaw);

                        float diff = MathHelper.degreesDifference(yaw, backDir.toYRot());

                        Direction sideDir = (diff < 0 ? backDir.getClockWise() : backDir.getCounterClockWise());

                        ArrayList<Direction> lampDir = new ArrayList<>();
                        //lampDir.remove(sideDir);
                        lampDir.add(backDir.getOpposite());
                        lampDir.add(backDir.getOpposite());
                        lampDir.add(backDir.getOpposite());
                        if (Math.abs(diff) > 30) {
                            lampDir.add(sideDir.getOpposite());
                        }

                        boolean hasGroundLantern = false;

                        RegistryKey<Biome> biome = RegistryKey.create(ForgeRegistries.Keys.BIOMES, world.getBiome(pos).getRegistryName());
                        boolean hasFirefly = (BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL) ||
                                BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP) ||
                                BiomeDictionary.hasType(biome, BiomeDictionary.Type.SPOOKY) ? 0.2f : 0.01f) > rand.nextFloat();


                        //stone
                        if (0.3 > rand.nextFloat() && MathHelper.degreesDifferenceAbs(sign.getPointingYaw(true) + 180, yaw) > 70) {
                            BlockPos stonePos = pos.below().offset(backDir.getNormal());
                            if (rand.nextBoolean()) {
                                world.setBlock(stonePos, stoneSlab, 2);
                            } else {
                                world.setBlock(stonePos, stair.setValue(StairsBlock.FACING, sideDir), 2);
                            }
                            stonePos = stonePos.offset(sideDir.getNormal());
                            world.setBlock(stonePos, stone, 2);
                            if (0.35 > rand.nextFloat()) {
                                world.setBlock(stonePos.above(), hasFirefly ? jar : lanternDown, 3);
                                hasGroundLantern = true;
                            }
                            stonePos = stonePos.offset(sideDir.getNormal());
                            if (!RoadSignFeature.isNotSolid(world, stonePos.below())) {
                                if (rand.nextBoolean()) {
                                    world.setBlock(stonePos, stoneSlab, 2);
                                } else {
                                    world.setBlock(stonePos, stair.setValue(StairsBlock.FACING, sideDir.getOpposite()), 2);
                                }
                            }

                        }


                        if (!hasGroundLantern) {

                            //lanterns
                            pos = pos.above(2);

                            BlockState light = hasFirefly ? jar : lantern;

                            Direction dir = lampDir.get(rand.nextInt(lampDir.size()));

                            boolean doubleSided = 0.25 > rand.nextFloat();
                            if (doubleSided) {
                                dir = dir.getClockWise();
                            }

                            //wall lanterns
                            if(0.28>rand.nextFloat()){
                                topState = 0.32>rand.nextFloat()?trapdoor:air;

                                EnhancedLanternBlock wl = ((EnhancedLanternBlock) Registry.WALL_LANTERN.get());
                                wl.placeOn(lanternDown,pos.below(),dir,world);

                                //double
                                if (doubleSided) {
                                    wl.placeOn(lanternDown,pos.below(),dir.getOpposite(),world);
                                }

                            }
                            else {
                                boolean isTrapdoor = 0.4 > rand.nextFloat();

                                if (!isTrapdoor) topState = fence;

                                //double
                                if (doubleSided) {
                                    BlockPos backPos = pos.relative(dir.getOpposite());

                                    world.setBlock(backPos, isTrapdoor ? trapdoor : fence, 2);

                                    if (0.25 > rand.nextFloat()) {
                                        topState = isTrapdoor ? slab : log;
                                    }

                                    world.setBlock(backPos.below(), light, 3);
                                }

                                pos = pos.relative(dir);
                                BlockState frontState = isTrapdoor ? trapdoor : fence;
                                world.setBlock(pos, frontState, 2);

                                world.setBlock(pos.below(), light, 3);
                            }


                        }


                    }
                } else {

                    ItemStack book = new ItemStack(Items.WRITABLE_BOOK);
                    CompoundNBT com = new CompoundNBT();
                    ListNBT listnbt =  new ListNBT();
                    listnbt.add(StringNBT.valueOf("nothing here but monsters\n\n\n"));
                    com.put("pages",listnbt);
                    book.setTag(com);
                    this.level.setBlock(this.worldPosition.below(2), Registry.NOTICE_BOARD.get().defaultBlockState().setValue(NoticeBoardBlock.HAS_BOOK,true)
                            .setValue(NoticeBoardBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(this.getLevel().random)), 3);
                    TileEntity te = world.getBlockEntity(this.worldPosition.below(2));
                    if(te instanceof NoticeBoardBlockTile){
                        ((ItemDisplayTile) te).setDisplayedItem(book);
                        //te.setChanged();
                    }
                }


                world.setBlock(this.worldPosition, topState, 3);

                return;
            }

        }
        catch (Exception exception){
            Supplementaries.LOGGER.warn("failed to generate road sign at "+this.worldPosition.toString()+": "+exception);
        }

        this.level.removeBlock(this.worldPosition, false);

    }


    private static ITextComponent getSignText(int d){
        int s;
        if(d<100)s=10;
        else if(d<2000)s=100;
        else s = 1000;
        return new TranslationTextComponent("message.supplementaries.road_sign",(((d + (s/2)) / s) * s));
    }
}
