package net.mehvahdjukaar.supplementaries.common;

import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.datagen.types.IWoodType;
import net.mehvahdjukaar.supplementaries.datagen.types.WoodTypes;
import net.mehvahdjukaar.supplementaries.setup.Variants;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class Textures {

    private static final String MOD_ID = Supplementaries.MOD_ID;

    //minecraft
    public static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:block/dirt");
    public static final ResourceLocation WHITE_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/white_concrete_powder");
    public static final ResourceLocation ORANGE_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/orange_concrete_powder");
    public static final ResourceLocation LIGHT_BLUE_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/light_blue_concrete_powder");
    public static final ResourceLocation YELLOW_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/yellow_concrete_powder");
    public static final ResourceLocation LIME_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/lime_concrete_powder");
    public static final ResourceLocation PINK_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/pink_concrete_powder");
    public static final ResourceLocation GRAY_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/gray_concrete_powder");
    public static final ResourceLocation LIGHT_GRAY_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/light_gray_concrete_powder");
    public static final ResourceLocation CYAN_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/cyan_concrete_powder");
    public static final ResourceLocation PURPLE_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/purple_concrete_powder");
    public static final ResourceLocation BLUE_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/blue_concrete_powder");
    public static final ResourceLocation BROWN_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/brown_concrete_powder");
    public static final ResourceLocation GREEN_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/green_concrete_powder");
    public static final ResourceLocation RED_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/red_concrete_powder");
    public static final ResourceLocation BLACK_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/black_concrete_powder");
    public static final ResourceLocation MAGENTA_CONCRETE_TEXTURE = new ResourceLocation("minecraft:block/magenta_concrete_powder");
    public static final ResourceLocation RED_SAND_TEXTURE = new ResourceLocation("minecraft:block/red_sand");
    public static final ResourceLocation SAND_TEXTURE = new ResourceLocation("minecraft:block/sand");
    public static final ResourceLocation WATER_TEXTURE = new ResourceLocation("minecraft:block/water_still");
    public static final ResourceLocation LAVA_TEXTURE = new ResourceLocation("minecraft:block/lava_still");
    public static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation("minecraft:block/chain");
    public static final ResourceLocation FLOWING_WATER_TEXTURE = new ResourceLocation("minecraft:block/water_flow");
    public static final ResourceLocation SLIME_TEXTURE = new ResourceLocation("minecraft:block/slime_block");
    public static final ResourceLocation BLACKBOARD_TEXTURE = new ResourceLocation(MOD_ID+":blocks/blackboard");

    //blocks (to stitch)
    public static final ResourceLocation MILK_TEXTURE = new ResourceLocation(MOD_ID, "blocks/milk_liquid");
    public static final ResourceLocation POTION_TEXTURE = new ResourceLocation(MOD_ID, "blocks/potion_still");
    public static final ResourceLocation POTION_TEXTURE_FLOW = new ResourceLocation(MOD_ID, "blocks/potion_flow");
    public static final ResourceLocation HONEY_TEXTURE = new ResourceLocation(MOD_ID, "blocks/honey_liquid");
    public static final ResourceLocation DRAGON_BREATH_TEXTURE = new ResourceLocation(MOD_ID, "blocks/dragon_breath_liquid");
    public static final ResourceLocation XP_TEXTURE = new ResourceLocation(MOD_ID, "blocks/xp_still");
    public static final ResourceLocation XP_TEXTURE_FLOW = new ResourceLocation(MOD_ID, "blocks/xp_flow");
    public static final ResourceLocation FAUCET_TEXTURE = new ResourceLocation(MOD_ID, "blocks/faucet_water");
    public static final ResourceLocation FISHIES_TEXTURE = new ResourceLocation(MOD_ID, "blocks/fishies");
    public static final ResourceLocation BELLOWS_TEXTURE = new ResourceLocation(MOD_ID, "entity/bellows");
    public static final ResourceLocation LASER_BEAM_TEXTURE = new ResourceLocation(MOD_ID, "blocks/laser_beam");
    public static final ResourceLocation LASER_OVERLAY_TEXTURE = new ResourceLocation(MOD_ID, "blocks/laser_overlay");
    public static final ResourceLocation LASER_BEAM_END_TEXTURE = new ResourceLocation(MOD_ID, "blocks/laser_beam_end");
    public static final ResourceLocation SOUP_TEXTURE = new ResourceLocation(MOD_ID, "blocks/soup_liquid");
    public static final ResourceLocation SOUL_TEXTURE = new ResourceLocation(MOD_ID, "blocks/soul");
    public static final ResourceLocation CLOCK_HAND_TEXTURE = new ResourceLocation(MOD_ID, "blocks/clock_hand");
    public static final ResourceLocation CRIMSON_LANTERN_TEXTURE = new ResourceLocation(MOD_ID, "blocks/crimson_lantern_front");
    public static final ResourceLocation HOURGLASS_REDSTONE = new ResourceLocation(MOD_ID, "blocks/hourglass_redstone");
    public static final ResourceLocation HOURGLASS_GLOWSTONE = new ResourceLocation(MOD_ID, "blocks/hourglass_glowstone");
    public static final ResourceLocation HOURGLASS_SUGAR = new ResourceLocation(MOD_ID, "blocks/hourglass_sugar");
    public static final ResourceLocation HOURGLASS_BLAZE = new ResourceLocation(MOD_ID, "blocks/hourglass_blaze");
    public static final ResourceLocation HOURGLASS_GUNPOWDER = new ResourceLocation(MOD_ID, "blocks/hourglass_gunpowder");
    public static final ResourceLocation MAGMA_TEXTURE = new ResourceLocation(MOD_ID, "blocks/magma_still");
    public static final ResourceLocation MAGMA_TEXTURE_FLOW = new ResourceLocation(MOD_ID, "blocks/magma_flow");
    public static final ResourceLocation TIMBER_FRAME_TEXTURE = new ResourceLocation(MOD_ID, "blocks/timber_frame");
    public static final ResourceLocation TIMBER_BRACE_TEXTURE = new ResourceLocation(MOD_ID, "blocks/timber_brace");
    public static final ResourceLocation TIMBER_BRACE_F_TEXTURE = new ResourceLocation(MOD_ID, "blocks/timber_brace_f");
    public static final ResourceLocation TIMBER_CROSS_BRACE_TEXTURE = new ResourceLocation(MOD_ID, "blocks/timber_cross_brace");
    //entities
    public static final ResourceLocation GLOBE_TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_the_world.png");
    public static final ResourceLocation GLOBE_FLAT_TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_flat.png");
    public static final ResourceLocation GLOBE_MOON_TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_moon.png");
    public static final ResourceLocation GLOBE_SUN_TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_sun.png");
    public static final ResourceLocation GLOBE_SHEARED_TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_sheared.png");
    public static final ResourceLocation GLOBE_CUSTOM_1 = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_plantkillable.png");
    public static final ResourceLocation GLOBE_CUSTOM_2 = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_toffanelly.png");
    public static final ResourceLocation GLOBE_CUSTOM_3 = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_sylvetichearts.png");
    public static final ResourceLocation GLOBE_CUSTOM_4 = new ResourceLocation(MOD_ID,"textures/entity/globes/globe_agrona.png");
    public static final ResourceLocation ROPE_ARROW = new ResourceLocation(MOD_ID,"textures/entity/rope_arrow.png");
    public static final ResourceLocation ORANGE_TRADER = new ResourceLocation(MOD_ID,"textures/entity/misc/orange_trader.png");

    public static final ResourceLocation FIREFLY_TEXTURE =  new ResourceLocation(MOD_ID,"textures/entity/firefly.png");
    public static final ResourceLocation BELL_ROPE_TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/bell_rope.png");
    public static final ResourceLocation BELL_CHAIN_TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/bell_chain.png");
    public static final ResourceLocation THICK_GOLEM = new ResourceLocation(MOD_ID,"textures/entity/misc/iron_golem.png");
    public static final ResourceLocation SEA_PICKLE_RICK = new ResourceLocation(MOD_ID,"textures/entity/misc/sea_pickle.png");
    public static final ResourceLocation JAR_MAN = new ResourceLocation(MOD_ID,"textures/entity/misc/jar_man.png");


    //gui
    public static final ResourceLocation BLACKBOARD_GUI_TEXTURE = new ResourceLocation(MOD_ID,"textures/gui/blackboard.png");
    public static final ResourceLocation EMPTY_TEXTURE = new ResourceLocation(MOD_ID,"textures/gui/config_background.png");
    public static final ResourceLocation NOTICE_BOARD_GUI_TEXTURE = new ResourceLocation(MOD_ID,"textures/gui/notice_board_gui.png");
    public static final ResourceLocation SACK_GUI_TEXTURE = new ResourceLocation(MOD_ID,"textures/gui/sack_gui.png");
    public static final ResourceLocation SACK_GUI_TEXTURE_7 = new ResourceLocation(MOD_ID,"textures/gui/sack_gui_7.png");
    public static final ResourceLocation SACK_GUI_TEXTURE_9 = new ResourceLocation(MOD_ID,"textures/gui/sack_gui_9.png");
    public static final ResourceLocation PULLEY_BLOCK_GUI_TEXTURE = new ResourceLocation(MOD_ID,"textures/gui/pulley_block_gui.png");
    public static final ResourceLocation ORANGE_MERCHANT_GUI_TEXTURE = new ResourceLocation(MOD_ID,"textures/gui/orange_trader.png");
    //map markers
    public static final ResourceLocation SIGN_POST_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/sign_post.png");
    public static final ResourceLocation FLAG_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/flag.png");
    public static final ResourceLocation BED_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/bed.png");
    public static final ResourceLocation RESPAWN_ANCHOR_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/respawn_anchor.png");
    public static final ResourceLocation LODESTONE_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/lodestone.png");
    public static final ResourceLocation BEACON_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/beacon.png");
    public static final ResourceLocation CONDUIT_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/conduit.png");
    public static final ResourceLocation NETHER_PORTAL_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/nether_portal.png");
    public static final ResourceLocation END_PORTAL_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/end_portal.png");
    public static final ResourceLocation END_GATEWAY_MARKER_TEXTURE = new ResourceLocation(MOD_ID,"textures/map/end_gateway.png");



    public static final Map<IWoodType,ResourceLocation> HANGING_SIGNS_TEXTURES = new HashMap<>();
    public static final Map<IWoodType,ResourceLocation> SIGN_POSTS_TEXTURES = new HashMap<>();
    static {
        for(IWoodType type : WoodTypes.TYPES.values()){
            HANGING_SIGNS_TEXTURES.put(type, new ResourceLocation(MOD_ID, "entity/hanging_signs/"+type.getLocation()+Variants.getHangingSignName(type)));
            SIGN_POSTS_TEXTURES.put(type, new ResourceLocation(MOD_ID, "entity/sign_posts/"+type.getLocation()+Variants.getSignPostName(type)));
        }
    }


    //TODO: rethink this
    public static List<ResourceLocation> getBlockTextures() {
        return new ArrayList<>(Arrays.asList(
                MILK_TEXTURE, POTION_TEXTURE, POTION_TEXTURE_FLOW, HONEY_TEXTURE, DRAGON_BREATH_TEXTURE, SOUP_TEXTURE,MAGMA_TEXTURE,MAGMA_TEXTURE_FLOW,
                XP_TEXTURE, XP_TEXTURE_FLOW, FISHIES_TEXTURE, BELLOWS_TEXTURE, LASER_BEAM_TEXTURE, LASER_BEAM_END_TEXTURE,LASER_OVERLAY_TEXTURE,
                CLOCK_HAND_TEXTURE,HOURGLASS_REDSTONE, HOURGLASS_GLOWSTONE, HOURGLASS_SUGAR, HOURGLASS_BLAZE, HOURGLASS_GUNPOWDER));
    }



}