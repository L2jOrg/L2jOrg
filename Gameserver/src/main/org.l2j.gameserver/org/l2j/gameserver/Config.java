/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver;

import org.l2j.commons.util.PropertiesParser;
import org.l2j.commons.util.StringUtil;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.settings.RateSettings;
import org.l2j.gameserver.util.FloodProtectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * This class loads all the game server related configurations from files.<br>
 * The files are usually located in config folder in server root folder.<br>
 * Each configuration has a default value (that should reflect retail behavior).
 */
public final class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    // --------------------------------------------------
    // Config File Definitions
    // --------------------------------------------------

    public static final String SIEGE_CONFIG_FILE = "./config/Siege.ini";
    private static final String FEATURE_CONFIG_FILE = "config/feature.properties";
    private static final String FLOOD_PROTECTOR_CONFIG_FILE = "./config/FloodProtector.ini";

    private static final String GRANDBOSS_CONFIG_FILE = "./config/GrandBoss.ini";

    private static final String NPC_CONFIG_FILE = "./config/npc.properties";
    private static final String PVP_CONFIG_FILE = "./config/PVP.ini";
    private static final String RATES_CONFIG_FILE = "config/rates.properties";
    private static final String ALTHARS_CONFIG_FILE = "config/althars.ini";

    // --------------------------------------------------
    // Custom Config File Definitions
    // --------------------------------------------------
    private static final String CUSTOM_BANKING_CONFIG_FILE = "config/custom/Banking.ini";
    private static final String CUSTOM_COMMUNITY_BOARD_CONFIG_FILE = "config/custom/CommunityBoard.ini";
    private static final String CUSTOM_DUALBOX_CHECK_CONFIG_FILE = "config/custom/DualboxCheck.ini";
    private static final String CUSTOM_MULTILANGUAL_SUPPORT_CONFIG_FILE = "config/custom/MultilingualSupport.ini";
    private static final String CUSTOM_NPC_STAT_MULTIPIERS_CONFIG_FILE = "config/custom/NpcStatMultipliers.ini";
    private static final String CUSTOM_PC_CAFE_CONFIG_FILE = "config/custom/PcCafe.ini";
    private static final String CUSTOM_AUTO_POTIONS_CONFIG_FILE = "config/custom/AutoPotions.ini";
    private static final String CUSTOM_DONATION_CONFIG_FILE = "config/custom/Donate.ini";

    private static final String CUSTOM_PVP_ANNOUNCE_CONFIG_FILE = "config/custom/PvpAnnounce.ini";
    private static final String CUSTOM_PVP_REWARD_ITEM_CONFIG_FILE = "config/custom/PvpRewardItem.ini";
    private static final String CUSTOM_PVP_TITLE_CONFIG_FILE = "config/custom/PvpTitleColor.ini";
    private static final String CUSTOM_RANDOM_SPAWNS_CONFIG_FILE = "config/custom/RandomSpawns.ini";
    private static final String CUSTOM_SCREEN_WELCOME_MESSAGE_CONFIG_FILE = "config/custom/ScreenWelcomeMessage.ini";
    private static final String CUSTOM_SELL_BUFFS_CONFIG_FILE = "config/custom/SellBuffs.ini";
    private static final String CUSTOM_SCHEME_BUFFER_CONFIG_FILE = "config/custom/ShemeBuffer.ini";
    private static final String CUSTOM_STARTING_LOCATION_CONFIG_FILE = "config/custom/StartingLocation.ini";
    private static final String CUSTOM_VOTE_REWARD_CONFIG_FILE = "config/custom/VoteReward.ini";
    private static final String MAGIC_LAMP_CONFIG_FILE = "./config/magic-lamp.properties";

    // --------------------------------------------------
    // Castle Settings
    // --------------------------------------------------
    public static long CS_TELE_FEE_RATIO;
    public static int CS_TELE1_FEE;
    public static int CS_TELE2_FEE;
    public static long CS_MPREG_FEE_RATIO;
    public static int CS_MPREG1_FEE;
    public static int CS_MPREG2_FEE;
    public static long CS_HPREG_FEE_RATIO;
    public static int CS_HPREG1_FEE;
    public static int CS_HPREG2_FEE;
    public static long CS_EXPREG_FEE_RATIO;
    public static int CS_EXPREG1_FEE;
    public static int CS_EXPREG2_FEE;
    public static long CS_SUPPORT_FEE_RATIO;
    public static int CS_SUPPORT1_FEE;
    public static int CS_SUPPORT2_FEE;

    public static int OUTER_DOOR_UPGRADE_PRICE2;
    public static int OUTER_DOOR_UPGRADE_PRICE3;
    public static int OUTER_DOOR_UPGRADE_PRICE5;
    public static int INNER_DOOR_UPGRADE_PRICE2;
    public static int INNER_DOOR_UPGRADE_PRICE3;
    public static int INNER_DOOR_UPGRADE_PRICE5;
    public static int WALL_UPGRADE_PRICE2;
    public static int WALL_UPGRADE_PRICE3;
    public static int WALL_UPGRADE_PRICE5;
    public static int TRAP_UPGRADE_PRICE1;
    public static int TRAP_UPGRADE_PRICE2;
    public static int TRAP_UPGRADE_PRICE3;
    public static int TRAP_UPGRADE_PRICE4;
    public static int TAKE_CASTLE_POINTS;
    public static int LOOSE_CASTLE_POINTS;
    public static int CASTLE_DEFENDED_POINTS;

    // --------------------------------------------------
    // FloodProtector Settings
    // --------------------------------------------------
    public static FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON;
    public static FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT;
    public static FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL;
    public static FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MANUFACTURE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SENDMAIL;
    public static FloodProtectorConfig FLOOD_PROTECTOR_CHARACTER_SELECT;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_AUCTION;

    // --------------------------------------------------
    // NPC Settings
    // --------------------------------------------------
    public static boolean SHOW_NPC_LVL;
    public static boolean GUARD_ATTACK_AGGRO_MOB;
    public static double RAID_HP_REGEN_MULTIPLIER;
    public static double RAID_MP_REGEN_MULTIPLIER;
    public static double RAID_PDEFENCE_MULTIPLIER;
    public static double RAID_MDEFENCE_MULTIPLIER;
    public static double RAID_PATTACK_MULTIPLIER;
    public static double RAID_MATTACK_MULTIPLIER;
    public static double RAID_MINION_RESPAWN_TIMER;
    public static Map<Integer, Integer> MINIONS_RESPAWN_TIME;
    public static float RAID_MIN_RESPAWN_MULTIPLIER;
    public static float RAID_MAX_RESPAWN_MULTIPLIER;
    public static boolean RAID_DISABLE_CURSE;
    public static boolean FORCE_DELETE_MINIONS;
    public static long DESPAWN_MINION_DELAY;
    public static int RAID_CHAOS_TIME;
    public static int GRAND_CHAOS_TIME;
    public static int MINION_CHAOS_TIME;
    public static int INVENTORY_MAXIMUM_PET;
    public static double PET_HP_REGEN_MULTIPLIER;
    public static double PET_MP_REGEN_MULTIPLIER;
    public static int VITALITY_CONSUME_BY_MOB;
    public static int VITALITY_CONSUME_BY_BOSS;

    // --------------------------------------------------
    // PvP Settings
    // --------------------------------------------------
    public static boolean KARMA_DROP_GM;
    public static int KARMA_PK_LIMIT;
    public static String KARMA_NONDROPPABLE_PET_ITEMS;
    public static String KARMA_NONDROPPABLE_ITEMS;
    public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
    public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
    public static boolean ANTIFEED_ENABLE;
    public static boolean ANTIFEED_DUALBOX;
    public static boolean ANTIFEED_DISCONNECTED_AS_DUALBOX;
    public static int ANTIFEED_INTERVAL;
    public static boolean ACTIVATE_PVP_BOSS_FLAG;

    // --------------------------------------------------
    // Rate Settings
    // --------------------------------------------------
    public static float RATE_SP;
    public static float RATE_PARTY_XP;
    public static float RATE_PARTY_SP;
    public static float RATE_INSTANCE_XP;
    public static float RATE_INSTANCE_SP;
    public static float RATE_INSTANCE_PARTY_XP;
    public static float RATE_INSTANCE_PARTY_SP;
    public static float RATE_RAIDBOSS_POINTS;
    public static float RATE_EXTRACTABLE;
    public static int RATE_DROP_MANOR;
    public static float RATE_QUEST_DROP;
    public static float RATE_QUEST_REWARD;
    public static float RATE_QUEST_REWARD_XP;
    public static float RATE_QUEST_REWARD_SP;
    public static float RATE_QUEST_REWARD_ADENA;
    public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
    public static float RATE_QUEST_REWARD_POTION;
    public static float RATE_QUEST_REWARD_SCROLL;
    public static float RATE_QUEST_REWARD_RECIPE;
    public static float RATE_QUEST_REWARD_MATERIAL;
    public static float RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_HERB_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_RAID_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_DEATH_DROP_CHANCE_MULTIPLIER;
    public static float RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
    public static float RATE_HERB_DROP_CHANCE_MULTIPLIER;
    public static float RATE_RAID_DROP_CHANCE_MULTIPLIER;
    public static Map<Integer, Float> RATE_DROP_AMOUNT_BY_ID;
    public static Map<Integer, Float> RATE_DROP_CHANCE_BY_ID;
    public static int DROP_MAX_OCCURRENCES_NORMAL;
    public static int DROP_MAX_OCCURRENCES_RAIDBOSS;
    public static int DROP_ADENA_MIN_LEVEL_DIFFERENCE;
    public static int DROP_ADENA_MAX_LEVEL_DIFFERENCE;
    public static double DROP_ADENA_MIN_LEVEL_GAP_CHANCE;
    public static int DROP_ITEM_MIN_LEVEL_DIFFERENCE;
    public static int DROP_ITEM_MAX_LEVEL_DIFFERENCE;
    public static double DROP_ITEM_MIN_LEVEL_GAP_CHANCE;
    public static float RATE_KARMA_LOST;
    public static float RATE_KARMA_EXP_LOST;
    public static float RATE_SIEGE_GUARDS_PRICE;
    public static int PLAYER_DROP_LIMIT;
    public static int PLAYER_RATE_DROP;
    public static int PLAYER_RATE_DROP_ITEM;
    public static int PLAYER_RATE_DROP_EQUIP;
    public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
    public static float PET_XP_RATE;
    public static int PET_FOOD_RATE;
    public static float SINEATER_XP_RATE;
    public static int KARMA_DROP_LIMIT;
    public static int KARMA_RATE_DROP;
    public static int KARMA_RATE_DROP_ITEM;
    public static int KARMA_RATE_DROP_EQUIP;
    public static int KARMA_RATE_DROP_EQUIP_WEAPON;

    // --------------------------------------------------
    // Magic Lamp
    // --------------------------------------------------

    public static boolean ENABLE_MAGIC_LAMP;
    public static int MAGIC_LAMP_MAX_GAME_COUNT;
    public static int MAGIC_LAMP_REWARD_COUNT;
    public static int MAGIC_LAMP_GREATER_REWARD_COUNT;
    public static int MAGIC_LAMP_MAX_LEVEL_EXP;
    public static double MAGIC_LAMP_CHARGE_RATE;

    // --------------------------------------------------
    // No classification assigned to the following yet
    // --------------------------------------------------
    public static int PVP_NORMAL_TIME;
    public static int PVP_PVP_TIME;
    public static int MAX_REPUTATION;
    public static int REPUTATION_INCREASE;

    // GrandBoss Settings
    public static int ANTHARAS_SPAWN_INTERVAL;
    public static int ANTHARAS_SPAWN_RANDOM;
    // Baium
    public static int BAIUM_SPAWN_INTERVAL;
    // Core
    public static int CORE_SPAWN_INTERVAL;
    public static int CORE_SPAWN_RANDOM;
    // Offen
    public static int ORFEN_SPAWN_INTERVAL;
    public static int ORFEN_SPAWN_RANDOM;
    // Queen Ant
    public static int QUEEN_ANT_SPAWN_INTERVAL;
    public static int QUEEN_ANT_SPAWN_RANDOM;
    // Zaken
    public static int ZAKEN_SPAWN_INTERVAL;
    public static int ZAKEN_SPAWN_RANDOM;

    // --------------------------------------------------
    // Custom Settings
    // --------------------------------------------------

    public static boolean CHAMPION_ENABLE_IN_INSTANCES;
    public static int BANKING_SYSTEM_GOLDBARS;
    public static int BANKING_SYSTEM_ADENA;
    public static boolean ENABLE_NPC_STAT_MULTIPIERS;
    public static double MONSTER_HP_MULTIPLIER;
    public static double MONSTER_MP_MULTIPLIER;
    public static double MONSTER_PATK_MULTIPLIER;
    public static double MONSTER_MATK_MULTIPLIER;
    public static double MONSTER_PDEF_MULTIPLIER;
    public static double MONSTER_MDEF_MULTIPLIER;
    public static double MONSTER_AGRRO_RANGE_MULTIPLIER;
    public static float MONSTER_CLAN_HELP_RANGE_MULTIPLIER;
    public static double RAIDBOSS_HP_MULTIPLIER;
    public static double RAIDBOSS_MP_MULTIPLIER;
    public static double RAIDBOSS_PATK_MULTIPLIER;
    public static double RAIDBOSS_MATK_MULTIPLIER;
    public static double RAIDBOSS_PDEF_MULTIPLIER;
    public static double RAIDBOSS_MDEF_MULTIPLIER;
    public static double RAIDBOSS_AGRRO_RANGE_MULTIPLIER;
    public static float RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER;
    public static int RAIDBOSS_LIMIT_BARRIER;
    public static double GUARD_HP_MULTIPLIER;
    public static double GUARD_MP_MULTIPLIER;
    public static double GUARD_PATK_MULTIPLIER;
    public static double GUARD_MATK_MULTIPLIER;
    public static double GUARD_PDEF_MULTIPLIER;
    public static double GUARD_MDEF_MULTIPLIER;
    public static double GUARD_AGRRO_RANGE_MULTIPLIER;
    public static float GUARD_CLAN_HELP_RANGE_MULTIPLIER;
    public static double DEFENDER_HP_MULTIPLIER;
    public static double DEFENDER_MP_MULTIPLIER;
    public static double DEFENDER_PATK_MULTIPLIER;
    public static double DEFENDER_MATK_MULTIPLIER;
    public static double DEFENDER_PDEF_MULTIPLIER;
    public static double DEFENDER_MDEF_MULTIPLIER;
    public static double DEFENDER_AGRRO_RANGE_MULTIPLIER;
    public static float DEFENDER_CLAN_HELP_RANGE_MULTIPLIER;

    public static int BUFFER_MAX_SCHEMES;
    public static int BUFFER_STATIC_BUFF_COST;
    public static boolean WELCOME_MESSAGE_ENABLED;
    public static String WELCOME_MESSAGE_TEXT;
    public static int WELCOME_MESSAGE_TIME;
    public static boolean ANNOUNCE_PK_PVP;
    public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
    public static String ANNOUNCE_PK_MSG;
    public static String ANNOUNCE_PVP_MSG;
    public static boolean REWARD_PVP_ITEM;
    public static int REWARD_PVP_ITEM_ID;
    public static int REWARD_PVP_ITEM_AMOUNT;
    public static boolean REWARD_PVP_ITEM_MESSAGE;
    public static boolean REWARD_PK_ITEM;
    public static int REWARD_PK_ITEM_ID;
    public static int REWARD_PK_ITEM_AMOUNT;
    public static boolean REWARD_PK_ITEM_MESSAGE;
    public static boolean DISABLE_REWARDS_IN_INSTANCES;
    public static boolean DISABLE_REWARDS_IN_PVP_ZONES;
    public static boolean PVP_COLOR_SYSTEM_ENABLED;
    public static int PVP_AMOUNT1;
    public static int PVP_AMOUNT2;
    public static int PVP_AMOUNT3;
    public static int PVP_AMOUNT4;
    public static int PVP_AMOUNT5;
    public static int NAME_COLOR_FOR_PVP_AMOUNT1;
    public static int NAME_COLOR_FOR_PVP_AMOUNT2;
    public static int NAME_COLOR_FOR_PVP_AMOUNT3;
    public static int NAME_COLOR_FOR_PVP_AMOUNT4;
    public static int NAME_COLOR_FOR_PVP_AMOUNT5;
    public static String TITLE_FOR_PVP_AMOUNT1;
    public static String TITLE_FOR_PVP_AMOUNT2;
    public static String TITLE_FOR_PVP_AMOUNT3;
    public static String TITLE_FOR_PVP_AMOUNT4;
    public static String TITLE_FOR_PVP_AMOUNT5;
    public static boolean MULTILANG_ENABLE;
    public static List<String> MULTILANG_ALLOWED = new ArrayList<>();
    public static String MULTILANG_DEFAULT;
    public static boolean MULTILANG_VOICED_ALLOW;

    public static int DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
    public static int DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
    public static boolean DUALBOX_COUNT_OFFLINE_TRADERS;
    public static Map<Integer, Integer> DUALBOX_CHECK_WHITELIST;

    public static boolean AUTO_POTIONS_ENABLED;
    public static boolean AUTO_POTIONS_IN_OLYMPIAD;
    public static int AUTO_POTION_MIN_LVL;
    public static boolean AUTO_CP_ENABLED;
    public static boolean AUTO_HP_ENABLED;
    public static boolean AUTO_MP_ENABLED;
    public static int AUTO_CP_PERCENTAGE;
    public static int AUTO_HP_PERCENTAGE;
    public static int AUTO_MP_PERCENTAGE;
    public static List<Integer> AUTO_CP_ITEM_IDS;
    public static List<Integer> AUTO_HP_ITEM_IDS;
    public static List<Integer> AUTO_MP_ITEM_IDS;

    public static boolean ENABLE_DONATION;
    public static boolean CUSTOM_STARTING_LOC;
    public static int CUSTOM_STARTING_LOC_X;
    public static int CUSTOM_STARTING_LOC_Y;
    public static int CUSTOM_STARTING_LOC_Z;
    public static boolean ENABLE_RANDOM_MONSTER_SPAWNS;
    public static int MOB_MIN_SPAWN_RANGE;
    public static int MOB_MAX_SPAWN_RANGE;
    public static List<Integer> MOBS_LIST_NOT_RANDOM;
    public static boolean CUSTOM_CB_ENABLED;
    public static int COMMUNITYBOARD_CURRENCY;
    public static boolean COMMUNITYBOARD_ENABLE_MULTISELLS;
    public static boolean COMMUNITYBOARD_ENABLE_TELEPORTS;
    public static boolean COMMUNITYBOARD_ENABLE_BUFFS;
    public static boolean COMMUNITYBOARD_ENABLE_HEAL;
    public static boolean COMMUNITYBOARD_ENABLE_CLEANUP;
    public static boolean COMMUNITYBOARD_ENABLE_PREMIUM;
    public static boolean COMMUNITYBOARD_ENABLE_AUTO_HP_MP_CP;
    public static boolean COMMUNITYBOARD_ENABLE_BETA;
    public static int COMMUNITYBOARD_TELEPORT_PRICE;
    public static int COMMUNITYBOARD_BUFF_PRICE;
    public static int COMMUNITYBOARD_HEAL_PRICE;

    public static boolean COMMUNITYBOARD_KARMA_DISABLED;
    public static boolean COMMUNITYBOARD_CAST_ANIMATIONS;

    public static List<Integer> COMMUNITY_AVAILABLE_BUFFS;
    public static Map<String, Location> COMMUNITY_AVAILABLE_TELEPORTS;

    public static boolean PC_CAFE_ENABLED;
    public static boolean PC_CAFE_ONLY_VIP;
    public static int PC_CAFE_MAX_POINTS;
    public static boolean PC_CAFE_ENABLE_DOUBLE_POINTS;
    public static int PC_CAFE_DOUBLE_POINTS_CHANCE;
    public static double PC_CAFE_POINT_RATE;
    public static boolean PC_CAFE_RANDOM_POINT;
    public static boolean PC_CAFE_REWARD_LOW_EXP_KILLS;
    public static int PC_CAFE_LOW_EXP_KILLS_CHANCE;
    public static boolean SELLBUFF_ENABLED;
    public static int SELLBUFF_MP_MULTIPLER;
    public static int SELLBUFF_PAYMENT_ID;
    public static long SELLBUFF_MIN_PRICE;
    public static long SELLBUFF_MAX_PRICE;
    public static int SELLBUFF_MAX_BUFFS;
    public static boolean ALLOW_NETWORK_VOTE_REWARD;
    public static String NETWORK_SERVER_LINK;
    public static int NETWORK_VOTES_DIFFERENCE;
    public static int NETWORK_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> NETWORK_REWARD = new HashMap<>();
    public static int NETWORK_DUALBOXES_ALLOWED;
    public static boolean ALLOW_NETWORK_GAME_SERVER_REPORT;
    public static boolean ALLOW_TOPZONE_VOTE_REWARD;
    public static String TOPZONE_SERVER_LINK;
    public static int TOPZONE_VOTES_DIFFERENCE;
    public static int TOPZONE_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> TOPZONE_REWARD = new HashMap<>();
    public static int TOPZONE_DUALBOXES_ALLOWED;
    public static boolean ALLOW_TOPZONE_GAME_SERVER_REPORT;
    public static boolean ALLOW_HOPZONE_VOTE_REWARD;
    public static String HOPZONE_SERVER_LINK;
    public static int HOPZONE_VOTES_DIFFERENCE;
    public static int HOPZONE_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> HOPZONE_REWARD = new HashMap<>();
    public static int HOPZONE_DUALBOXES_ALLOWED;
    public static boolean ALLOW_HOPZONE_GAME_SERVER_REPORT;

    // --------------------------------------------------
    // HUNTING ZONE
    // --------------------------------------------------
    public static float L2_COIN_DROP_RATE;

    // --------------------------------------------------
    // ALTHARS ZONE
    // --------------------------------------------------
    public static int ALTHARS_ACTIVATE_CHANCE_RATE;
    public static int ALTHARS_MAX_ACTIVE;
    public static int ALTHARS_MIN_DURATION_CYCLE;
    public static int ALTHARS_MAX_DURATION_CYCLE;

    /**
     * This class initializes all global variables for configuration.<br>
     * If the key doesn't appear in properties file, a default value is set by this class. (properties file) for configuring your server.
     */
    public static void load() {
        FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
        FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
        FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
        FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
        FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
        FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
        FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
        FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
        FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
        FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
        FLOOD_PROTECTOR_MANUFACTURE = new FloodProtectorConfig("ManufactureFloodProtector");
        FLOOD_PROTECTOR_SENDMAIL = new FloodProtectorConfig("SendMailFloodProtector");
        FLOOD_PROTECTOR_CHARACTER_SELECT = new FloodProtectorConfig("CharacterSelectFloodProtector");
        FLOOD_PROTECTOR_ITEM_AUCTION = new FloodProtectorConfig("ItemAuctionFloodProtector");

        // Load Feature config file (if exists)
        final PropertiesParser Feature = new PropertiesParser(FEATURE_CONFIG_FILE);

        CS_TELE_FEE_RATIO = Feature.getLong("CastleTeleportFunctionFeeRatio", 604800000);
        CS_TELE1_FEE = Feature.getInt("CastleTeleportFunctionFeeLvl1", 1000);
        CS_TELE2_FEE = Feature.getInt("CastleTeleportFunctionFeeLvl2", 10000);
        CS_SUPPORT_FEE_RATIO = Feature.getLong("CastleSupportFunctionFeeRatio", 604800000);
        CS_SUPPORT1_FEE = Feature.getInt("CastleSupportFeeLvl1", 49000);
        CS_SUPPORT2_FEE = Feature.getInt("CastleSupportFeeLvl2", 120000);
        CS_MPREG_FEE_RATIO = Feature.getLong("CastleMpRegenerationFunctionFeeRatio", 604800000);
        CS_MPREG1_FEE = Feature.getInt("CastleMpRegenerationFeeLvl1", 45000);
        CS_MPREG2_FEE = Feature.getInt("CastleMpRegenerationFeeLvl2", 65000);
        CS_HPREG_FEE_RATIO = Feature.getLong("CastleHpRegenerationFunctionFeeRatio", 604800000);
        CS_HPREG1_FEE = Feature.getInt("CastleHpRegenerationFeeLvl1", 12000);
        CS_HPREG2_FEE = Feature.getInt("CastleHpRegenerationFeeLvl2", 20000);
        CS_EXPREG_FEE_RATIO = Feature.getLong("CastleExpRegenerationFunctionFeeRatio", 604800000);
        CS_EXPREG1_FEE = Feature.getInt("CastleExpRegenerationFeeLvl1", 63000);
        CS_EXPREG2_FEE = Feature.getInt("CastleExpRegenerationFeeLvl2", 70000);

        OUTER_DOOR_UPGRADE_PRICE2 = Feature.getInt("OuterDoorUpgradePriceLvl2", 3000000);
        OUTER_DOOR_UPGRADE_PRICE3 = Feature.getInt("OuterDoorUpgradePriceLvl3", 4000000);
        OUTER_DOOR_UPGRADE_PRICE5 = Feature.getInt("OuterDoorUpgradePriceLvl5", 5000000);
        INNER_DOOR_UPGRADE_PRICE2 = Feature.getInt("InnerDoorUpgradePriceLvl2", 750000);
        INNER_DOOR_UPGRADE_PRICE3 = Feature.getInt("InnerDoorUpgradePriceLvl3", 900000);
        INNER_DOOR_UPGRADE_PRICE5 = Feature.getInt("InnerDoorUpgradePriceLvl5", 1000000);
        WALL_UPGRADE_PRICE2 = Feature.getInt("WallUpgradePriceLvl2", 1600000);
        WALL_UPGRADE_PRICE3 = Feature.getInt("WallUpgradePriceLvl3", 1800000);
        WALL_UPGRADE_PRICE5 = Feature.getInt("WallUpgradePriceLvl5", 2000000);
        TRAP_UPGRADE_PRICE1 = Feature.getInt("TrapUpgradePriceLvl1", 3000000);
        TRAP_UPGRADE_PRICE2 = Feature.getInt("TrapUpgradePriceLvl2", 4000000);
        TRAP_UPGRADE_PRICE3 = Feature.getInt("TrapUpgradePriceLvl3", 5000000);
        TRAP_UPGRADE_PRICE4 = Feature.getInt("TrapUpgradePriceLvl4", 6000000);

        TAKE_CASTLE_POINTS = Feature.getInt("TakeCastlePoints", 1500);
        LOOSE_CASTLE_POINTS = Feature.getInt("LooseCastlePoints", 3000);
        CASTLE_DEFENDED_POINTS = Feature.getInt("CastleDefendedPoints", 750);

        // Load FloodProtector config file
        final PropertiesParser FloodProtectors = new PropertiesParser(FLOOD_PROTECTOR_CONFIG_FILE);

        loadFloodProtectorConfigs(FloodProtectors);

        // Load NPC config file (if exists)
        final PropertiesParser NPC = new PropertiesParser(NPC_CONFIG_FILE);

        GUARD_ATTACK_AGGRO_MOB = NPC.getBoolean("GuardAttackAggroMob", false);
        RAID_HP_REGEN_MULTIPLIER = NPC.getDouble("RaidHpRegenMultiplier", 100) / 100;
        RAID_MP_REGEN_MULTIPLIER = NPC.getDouble("RaidMpRegenMultiplier", 100) / 100;
        RAID_PDEFENCE_MULTIPLIER = NPC.getDouble("RaidPDefenceMultiplier", 100) / 100;
        RAID_MDEFENCE_MULTIPLIER = NPC.getDouble("RaidMDefenceMultiplier", 100) / 100;
        RAID_PATTACK_MULTIPLIER = NPC.getDouble("RaidPAttackMultiplier", 100) / 100;
        RAID_MATTACK_MULTIPLIER = NPC.getDouble("RaidMAttackMultiplier", 100) / 100;
        RAID_MIN_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMinRespawnMultiplier", 1.0f);
        RAID_MAX_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMaxRespawnMultiplier", 1.0f);
        RAID_MINION_RESPAWN_TIMER = NPC.getInt("RaidMinionRespawnTime", 300000);
        RAIDBOSS_LIMIT_BARRIER = NPC.getInt("LimitBarrier", 500);
        final String[] propertySplit = NPC.getString("CustomMinionsRespawnTime", "").split(";");
        MINIONS_RESPAWN_TIME = new HashMap<>(propertySplit.length);
        for (String prop : propertySplit) {
            final String[] propSplit = prop.split(",");
            if (propSplit.length != 2) {
                LOGGER.warn(StringUtil.concat("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", prop, "\""));
            }

            try {
                MINIONS_RESPAWN_TIME.put(Integer.valueOf(propSplit[0]), Integer.valueOf(propSplit[1]));
            } catch (NumberFormatException nfe) {
                if (!prop.isEmpty()) {
                    LOGGER.warn(StringUtil.concat("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", propSplit[0], "\"", propSplit[1]));
                }
            }
        }
        FORCE_DELETE_MINIONS = NPC.getBoolean("ForceDeleteMinions", false);
        DESPAWN_MINION_DELAY = NPC.getLong("DespawnDelayMinions", 20000);

        RAID_DISABLE_CURSE = NPC.getBoolean("DisableRaidCurse", false);
        RAID_CHAOS_TIME = NPC.getInt("RaidChaosTime", 10);
        GRAND_CHAOS_TIME = NPC.getInt("GrandChaosTime", 10);
        MINION_CHAOS_TIME = NPC.getInt("MinionChaosTime", 10);
        INVENTORY_MAXIMUM_PET = NPC.getInt("MaximumSlotsForPet", 12);
        PET_HP_REGEN_MULTIPLIER = NPC.getDouble("PetHpRegenMultiplier", 100) / 100;
        PET_MP_REGEN_MULTIPLIER = NPC.getDouble("PetMpRegenMultiplier", 100) / 100;

        VITALITY_CONSUME_BY_MOB = NPC.getInt("VitalityConsumeByMob", 2250);
        VITALITY_CONSUME_BY_BOSS = NPC.getInt("VitalityConsumeByBoss", 1125);

        // Load Rates config file (if exists)
        final PropertiesParser RatesSettings = new PropertiesParser(RATES_CONFIG_FILE);

        RATE_SP = RatesSettings.getFloat("RateSp", 1);
        RATE_PARTY_XP = RatesSettings.getFloat("RatePartyXp", 1);
        RATE_PARTY_SP = RatesSettings.getFloat("RatePartySp", 1);
        L2_COIN_DROP_RATE = RatesSettings.getFloat("L2CoinDropRate", 0.1f);

        RATE_INSTANCE_XP = RatesSettings.getFloat("RateInstanceXp", -1);
        if (RATE_INSTANCE_XP < 0) {
            RATE_INSTANCE_XP = RateSettings.xp();
        }
        RATE_INSTANCE_SP = RatesSettings.getFloat("RateInstanceSp", -1);
        if (RATE_INSTANCE_SP < 0) {
            RATE_INSTANCE_SP = RATE_SP;
        }
        RATE_INSTANCE_PARTY_XP = RatesSettings.getFloat("RateInstancePartyXp", -1);
        if (RATE_INSTANCE_PARTY_XP < 0) {
            RATE_INSTANCE_PARTY_XP = RATE_PARTY_XP;
        }
        RATE_INSTANCE_PARTY_SP = RatesSettings.getFloat("RateInstancePartyXp", -1);
        if (RATE_INSTANCE_PARTY_SP < 0) {
            RATE_INSTANCE_PARTY_SP = RATE_PARTY_SP;
        }

        RATE_EXTRACTABLE = RatesSettings.getFloat("RateExtractable", 1);
        RATE_DROP_MANOR = RatesSettings.getInt("RateDropManor", 1);
        RATE_QUEST_DROP = RatesSettings.getFloat("RateQuestDrop", 1);
        RATE_QUEST_REWARD = RatesSettings.getFloat("RateQuestReward", 1);
        RATE_QUEST_REWARD_XP = RatesSettings.getFloat("RateQuestRewardXP", 1);
        RATE_QUEST_REWARD_SP = RatesSettings.getFloat("RateQuestRewardSP", 1);
        RATE_QUEST_REWARD_ADENA = RatesSettings.getFloat("RateQuestRewardAdena", 1);
        RATE_QUEST_REWARD_USE_MULTIPLIERS = RatesSettings.getBoolean("UseQuestRewardMultipliers", false);
        RATE_QUEST_REWARD_POTION = RatesSettings.getFloat("RateQuestRewardPotion", 1);
        RATE_QUEST_REWARD_SCROLL = RatesSettings.getFloat("RateQuestRewardScroll", 1);
        RATE_QUEST_REWARD_RECIPE = RatesSettings.getFloat("RateQuestRewardRecipe", 1);
        RATE_QUEST_REWARD_MATERIAL = RatesSettings.getFloat("RateQuestRewardMaterial", 1);
        RATE_RAIDBOSS_POINTS = RatesSettings.getFloat("RateRaidbossPointsReward", 1);

        RATE_KARMA_LOST = RatesSettings.getFloat("RateKarmaLost", -1);
        if (RATE_KARMA_LOST == -1) {
            RATE_KARMA_LOST = RateSettings.xp();
        }
        RATE_KARMA_EXP_LOST = RatesSettings.getFloat("RateKarmaExpLost", 1);
        RATE_SIEGE_GUARDS_PRICE = RatesSettings.getFloat("RateSiegeGuardsPrice", 1);
        PLAYER_DROP_LIMIT = RatesSettings.getInt("PlayerDropLimit", 3);
        PLAYER_RATE_DROP = RatesSettings.getInt("PlayerRateDrop", 5);
        PLAYER_RATE_DROP_ITEM = RatesSettings.getInt("PlayerRateDropItem", 70);
        PLAYER_RATE_DROP_EQUIP = RatesSettings.getInt("PlayerRateDropEquip", 25);
        PLAYER_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("PlayerRateDropEquipWeapon", 5);
        PET_XP_RATE = RatesSettings.getFloat("PetXpRate", 1);
        PET_FOOD_RATE = RatesSettings.getInt("PetFoodRate", 1);
        SINEATER_XP_RATE = RatesSettings.getFloat("SinEaterXpRate", 1);
        KARMA_DROP_LIMIT = RatesSettings.getInt("KarmaDropLimit", 10);
        KARMA_RATE_DROP = RatesSettings.getInt("KarmaRateDrop", 70);
        KARMA_RATE_DROP_ITEM = RatesSettings.getInt("KarmaRateDropItem", 50);
        KARMA_RATE_DROP_EQUIP = RatesSettings.getInt("KarmaRateDropEquip", 40);
        KARMA_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("KarmaRateDropEquipWeapon", 10);

        RATE_DEATH_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("DeathDropAmountMultiplier", 1);
        RATE_SPOIL_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("SpoilDropAmountMultiplier", 1);
        RATE_HERB_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("HerbDropAmountMultiplier", 1);
        RATE_RAID_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("RaidDropAmountMultiplier", 1);
        RATE_DEATH_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("DeathDropChanceMultiplier", 1);
        RATE_SPOIL_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("SpoilDropChanceMultiplier", 1);
        RATE_HERB_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("HerbDropChanceMultiplier", 1);
        RATE_RAID_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("RaidDropChanceMultiplier", 1);

        final String[] dropAmountMultiplier = RatesSettings.getString("DropAmountMultiplierByItemId", "").split(";");
        RATE_DROP_AMOUNT_BY_ID = new HashMap<>(dropAmountMultiplier.length);
        if (!dropAmountMultiplier[0].isEmpty()) {
            for (String item : dropAmountMultiplier) {
                final String[] itemSplit = item.split(",");
                if (itemSplit.length != 2) {
                    LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
                } else {
                    try {
                        RATE_DROP_AMOUNT_BY_ID.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!item.isEmpty()) {
                            LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
                        }
                    }
                }
            }
        }

        final String[] dropChanceMultiplier = RatesSettings.getString("DropChanceMultiplierByItemId", "").split(";");
        RATE_DROP_CHANCE_BY_ID = new HashMap<>(dropChanceMultiplier.length);
        if (!dropChanceMultiplier[0].isEmpty()) {
            for (String item : dropChanceMultiplier) {
                final String[] itemSplit = item.split(",");
                if (itemSplit.length != 2) {
                    LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
                } else {
                    try {
                        RATE_DROP_CHANCE_BY_ID.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!item.isEmpty()) {
                            LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
                        }
                    }
                }
            }
        }

        DROP_MAX_OCCURRENCES_NORMAL = RatesSettings.getInt("DropMaxOccurrencesNormal", 2);
        DROP_MAX_OCCURRENCES_RAIDBOSS = RatesSettings.getInt("DropMaxOccurrencesRaidboss", 7);

        DROP_ADENA_MIN_LEVEL_DIFFERENCE = RatesSettings.getInt("DropAdenaMinLevelDifference", 8);
        DROP_ADENA_MAX_LEVEL_DIFFERENCE = RatesSettings.getInt("DropAdenaMaxLevelDifference", 15);
        DROP_ADENA_MIN_LEVEL_GAP_CHANCE = RatesSettings.getDouble("DropAdenaMinLevelGapChance", 10);
        DROP_ITEM_MIN_LEVEL_DIFFERENCE = RatesSettings.getInt("DropItemMinLevelDifference", 5);
        DROP_ITEM_MAX_LEVEL_DIFFERENCE = RatesSettings.getInt("DropItemMaxLevelDifference", 10);
        DROP_ITEM_MIN_LEVEL_GAP_CHANCE = RatesSettings.getDouble("DropItemMinLevelGapChance", 10);

        // Load PvP config file (if exists)
        final PropertiesParser PVPSettings = new PropertiesParser(PVP_CONFIG_FILE);

        KARMA_DROP_GM = PVPSettings.getBoolean("CanGMDropEquipment", false);
        KARMA_PK_LIMIT = PVPSettings.getInt("MinimumPKRequiredToDrop", 4);
        KARMA_NONDROPPABLE_PET_ITEMS = PVPSettings.getString("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650,9882");
        KARMA_NONDROPPABLE_ITEMS = PVPSettings.getString("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,7694,8181,5575,7694,9388,9389,9390");

        String[] karma = KARMA_NONDROPPABLE_PET_ITEMS.split(",");
        KARMA_LIST_NONDROPPABLE_PET_ITEMS = new int[karma.length];

        for (int i = 0; i < karma.length; i++) {
            KARMA_LIST_NONDROPPABLE_PET_ITEMS[i] = Integer.parseInt(karma[i]);
        }

        karma = KARMA_NONDROPPABLE_ITEMS.split(",");
        KARMA_LIST_NONDROPPABLE_ITEMS = new int[karma.length];

        for (int i = 0; i < karma.length; i++) {
            KARMA_LIST_NONDROPPABLE_ITEMS[i] = Integer.parseInt(karma[i]);
        }

        ANTIFEED_ENABLE = PVPSettings.getBoolean("AntiFeedEnable", false);
        ANTIFEED_DUALBOX = PVPSettings.getBoolean("AntiFeedDualbox", true);
        ANTIFEED_DISCONNECTED_AS_DUALBOX = PVPSettings.getBoolean("AntiFeedDisconnectedAsDualbox", true);
        ANTIFEED_INTERVAL = PVPSettings.getInt("AntiFeedInterval", 120) * 1000;

        // sorting so binarySearch can be used later
        Arrays.sort(KARMA_LIST_NONDROPPABLE_PET_ITEMS);
        Arrays.sort(KARMA_LIST_NONDROPPABLE_ITEMS);

        PVP_NORMAL_TIME = PVPSettings.getInt("PvPVsNormalTime", 120000);
        PVP_PVP_TIME = PVPSettings.getInt("PvPVsPvPTime", 60000);
        MAX_REPUTATION = PVPSettings.getInt("MaxReputation", 500);
        REPUTATION_INCREASE = PVPSettings.getInt("ReputationIncrease", 100);
        ACTIVATE_PVP_BOSS_FLAG = PVPSettings.getBoolean("ActivatePvPBossFlag", false);

        // Grand bosses
        final PropertiesParser GrandBossSettings = new PropertiesParser(GRANDBOSS_CONFIG_FILE);

        ANTHARAS_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfAntharasSpawn", 264);
        ANTHARAS_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfAntharasSpawn", 72);

        BAIUM_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfBaiumSpawn", 168);

        CORE_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfCoreSpawn", 60);
        CORE_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfCoreSpawn", 24);

        ORFEN_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfOrfenSpawn", 48);
        ORFEN_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfOrfenSpawn", 20);

        QUEEN_ANT_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfQueenAntSpawn", 36);
        QUEEN_ANT_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfQueenAntSpawn", 17);

        ZAKEN_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfZakenSpawn", 168);
        ZAKEN_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfZakenSpawn", 48);

        // Load Banking config file (if exists)
        final PropertiesParser Banking = new PropertiesParser(CUSTOM_BANKING_CONFIG_FILE);

        BANKING_SYSTEM_GOLDBARS = Banking.getInt("BankingGoldbarCount", 1);
        BANKING_SYSTEM_ADENA = Banking.getInt("BankingAdenaCount", 500000000);

        // Load BoostNpcStats config file (if exists)
        final PropertiesParser BoostNpcStats = new PropertiesParser(CUSTOM_NPC_STAT_MULTIPIERS_CONFIG_FILE);

        ENABLE_NPC_STAT_MULTIPIERS = BoostNpcStats.getBoolean("EnableNpcStatMultipliers", false);
        MONSTER_HP_MULTIPLIER = BoostNpcStats.getDouble("MonsterHP", 1.0);
        MONSTER_MP_MULTIPLIER = BoostNpcStats.getDouble("MonsterMP", 1.0);
        MONSTER_PATK_MULTIPLIER = BoostNpcStats.getDouble("MonsterPAtk", 1.0);
        MONSTER_MATK_MULTIPLIER = BoostNpcStats.getDouble("MonsterMAtk", 1.0);
        MONSTER_PDEF_MULTIPLIER = BoostNpcStats.getDouble("MonsterPDef", 1.0);
        MONSTER_MDEF_MULTIPLIER = BoostNpcStats.getDouble("MonsterMDef", 1.0);
        MONSTER_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("MonsterAggroRange", 1.0);
        MONSTER_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getFloat("MonsterClanHelpRange", 1.0f);
        RAIDBOSS_HP_MULTIPLIER = BoostNpcStats.getDouble("RaidbossHP", 1.0);
        RAIDBOSS_MP_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMP", 1.0);
        RAIDBOSS_PATK_MULTIPLIER = BoostNpcStats.getDouble("RaidbossPAtk", 1.0);
        RAIDBOSS_MATK_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMAtk", 1.0);
        RAIDBOSS_PDEF_MULTIPLIER = BoostNpcStats.getDouble("RaidbossPDef", 1.0);
        RAIDBOSS_MDEF_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMDef", 1.0);
        RAIDBOSS_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("RaidbossAggroRange", 1.0);
        RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getFloat("RaidbossClanHelpRange", 1.0f);
        GUARD_HP_MULTIPLIER = BoostNpcStats.getDouble("GuardHP", 1.0);
        GUARD_MP_MULTIPLIER = BoostNpcStats.getDouble("GuardMP", 1.0);
        GUARD_PATK_MULTIPLIER = BoostNpcStats.getDouble("GuardPAtk", 1.0);
        GUARD_MATK_MULTIPLIER = BoostNpcStats.getDouble("GuardMAtk", 1.0);
        GUARD_PDEF_MULTIPLIER = BoostNpcStats.getDouble("GuardPDef", 1.0);
        GUARD_MDEF_MULTIPLIER = BoostNpcStats.getDouble("GuardMDef", 1.0);
        GUARD_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("GuardAggroRange", 1.0);
        GUARD_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getFloat("GuardClanHelpRange", 1.0f);
        DEFENDER_HP_MULTIPLIER = BoostNpcStats.getDouble("DefenderHP", 1.0);
        DEFENDER_MP_MULTIPLIER = BoostNpcStats.getDouble("DefenderMP", 1.0);
        DEFENDER_PATK_MULTIPLIER = BoostNpcStats.getDouble("DefenderPAtk", 1.0);
        DEFENDER_MATK_MULTIPLIER = BoostNpcStats.getDouble("DefenderMAtk", 1.0);
        DEFENDER_PDEF_MULTIPLIER = BoostNpcStats.getDouble("DefenderPDef", 1.0);
        DEFENDER_MDEF_MULTIPLIER = BoostNpcStats.getDouble("DefenderMDef", 1.0);
        DEFENDER_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("DefenderAggroRange", 1.0);
        DEFENDER_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getFloat("DefenderClanHelpRange", 1.0f);


        // Load CommunityBoard config file (if exists)
        final PropertiesParser CommunityBoard = new PropertiesParser(CUSTOM_COMMUNITY_BOARD_CONFIG_FILE);

        CUSTOM_CB_ENABLED = CommunityBoard.getBoolean("CustomCommunityBoard", false);
        COMMUNITYBOARD_CURRENCY = CommunityBoard.getInt("CommunityCurrencyId", 57);
        COMMUNITYBOARD_ENABLE_MULTISELLS = CommunityBoard.getBoolean("CommunityEnableMultisells", true);
        COMMUNITYBOARD_ENABLE_TELEPORTS = CommunityBoard.getBoolean("CommunityEnableTeleports", false);
        COMMUNITYBOARD_ENABLE_BUFFS = CommunityBoard.getBoolean("CommunityEnableBuffs", false);
        COMMUNITYBOARD_ENABLE_HEAL = CommunityBoard.getBoolean("CommunityEnableHeal", false);
        COMMUNITYBOARD_ENABLE_CLEANUP = CommunityBoard.getBoolean("CommunityEnableCleanUp", false);
        COMMUNITYBOARD_ENABLE_PREMIUM = CommunityBoard.getBoolean("CommunityEnablePremium", false);
        COMMUNITYBOARD_ENABLE_AUTO_HP_MP_CP = CommunityBoard.getBoolean("CommunityEnableAutoHpMpCp", false);
        COMMUNITYBOARD_ENABLE_BETA = CommunityBoard.getBoolean("CommunityEnableBeta", false);
        COMMUNITYBOARD_TELEPORT_PRICE = CommunityBoard.getInt("CommunityTeleportPrice", 0);
        COMMUNITYBOARD_BUFF_PRICE = CommunityBoard.getInt("CommunityBuffPrice", 0);
        COMMUNITYBOARD_HEAL_PRICE = CommunityBoard.getInt("CommunityHealPrice", 0);
        COMMUNITYBOARD_KARMA_DISABLED = CommunityBoard.getBoolean("CommunityKarmaDisabled", true);
        COMMUNITYBOARD_CAST_ANIMATIONS = CommunityBoard.getBoolean("CommunityCastAnimations", false);

        final String[] allowedBuffs = CommunityBoard.getString("CommunityAvailableBuffs", "").split(",");
        COMMUNITY_AVAILABLE_BUFFS = new ArrayList<>(allowedBuffs.length);
        for (String s : allowedBuffs) {
            COMMUNITY_AVAILABLE_BUFFS.add(Integer.parseInt(s));
        }
        final String[] availableTeleports = CommunityBoard.getString("CommunityTeleportList", "").split(";");
        COMMUNITY_AVAILABLE_TELEPORTS = new HashMap<>(availableTeleports.length);
        for (String s : availableTeleports) {
            final String[] splitInfo = s.split(",");
            COMMUNITY_AVAILABLE_TELEPORTS.put(splitInfo[0], new Location(Integer.parseInt(splitInfo[1]), Integer.parseInt(splitInfo[2]), Integer.parseInt(splitInfo[3])));
        }

        // Load DualboxCheck config file (if exists)
        final PropertiesParser DualboxCheck = new PropertiesParser(CUSTOM_DUALBOX_CHECK_CONFIG_FILE);

        DUALBOX_CHECK_MAX_PLAYERS_PER_IP = DualboxCheck.getInt("DualboxCheckMaxPlayersPerIP", 0);
        DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP = DualboxCheck.getInt("DualboxCheckMaxL2EventParticipantsPerIP", 0);
        DUALBOX_COUNT_OFFLINE_TRADERS = DualboxCheck.getBoolean("DualboxCountOfflineTraders", false);
        final String[] dualboxCheckWhiteList = DualboxCheck.getString("DualboxCheckWhitelist", "127.0.0.1,0").split(";");
        DUALBOX_CHECK_WHITELIST = new HashMap<>(dualboxCheckWhiteList.length);
        for (String entry : dualboxCheckWhiteList) {
            final String[] entrySplit = entry.split(",");
            if (entrySplit.length != 2) {
                LOGGER.warn(StringUtil.concat("DualboxCheck[Config.load()]: invalid config property -> DualboxCheckWhitelist \"", entry, "\""));
            } else {
                try {
                    int num = Integer.parseInt(entrySplit[1]);
                    num = num == 0 ? -1 : num;
                    DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
                } catch (UnknownHostException e) {
                    LOGGER.warn(StringUtil.concat("DualboxCheck[Config.load()]: invalid address -> DualboxCheckWhitelist \"", entrySplit[0], "\""));
                } catch (NumberFormatException e) {
                    LOGGER.warn(StringUtil.concat("DualboxCheck[Config.load()]: invalid number -> DualboxCheckWhitelist \"", entrySplit[1], "\""));
                }
            }
        }

        // Load MultilingualSupport config file (if exists)
        final PropertiesParser MultilingualSupport = new PropertiesParser(CUSTOM_MULTILANGUAL_SUPPORT_CONFIG_FILE);

        MULTILANG_DEFAULT = MultilingualSupport.getString("MultiLangDefault", "en");
        MULTILANG_ENABLE = MultilingualSupport.getBoolean("MultiLangEnable", false);
        final String[] allowed = MultilingualSupport.getString("MultiLangAllowed", MULTILANG_DEFAULT).split(";");
        MULTILANG_ALLOWED = new ArrayList<>(allowed.length);
        Collections.addAll(MULTILANG_ALLOWED, allowed);
        if (!MULTILANG_ALLOWED.contains(MULTILANG_DEFAULT)) {
            LOGGER.warn("MultiLang[Config.load()]: default language: " + MULTILANG_DEFAULT + " is not in allowed list !");
        }
        MULTILANG_VOICED_ALLOW = MultilingualSupport.getBoolean("MultiLangVoiceCommand", true);

        // Load PcCafe config file (if exists)
        final PropertiesParser PcCafe = new PropertiesParser(CUSTOM_PC_CAFE_CONFIG_FILE);

        PC_CAFE_ENABLED = PcCafe.getBoolean("PcCafeEnabled", false);
        PC_CAFE_ONLY_VIP = PcCafe.getBoolean("PcCafeOnlyVip", false);
        PC_CAFE_MAX_POINTS = PcCafe.getInt("MaxPcCafePoints", 200000);
        if (PC_CAFE_MAX_POINTS < 0) {
            PC_CAFE_MAX_POINTS = 0;
        }
        PC_CAFE_ENABLE_DOUBLE_POINTS = PcCafe.getBoolean("DoublingAcquisitionPoints", false);
        PC_CAFE_DOUBLE_POINTS_CHANCE = PcCafe.getInt("DoublingAcquisitionPointsChance", 1);
        if ((PC_CAFE_DOUBLE_POINTS_CHANCE < 0) || (PC_CAFE_DOUBLE_POINTS_CHANCE > 100)) {
            PC_CAFE_DOUBLE_POINTS_CHANCE = 1;
        }
        PC_CAFE_POINT_RATE = PcCafe.getDouble("AcquisitionPointsRate", 1.0);
        PC_CAFE_RANDOM_POINT = PcCafe.getBoolean("AcquisitionPointsRandom", false);
        if (PC_CAFE_POINT_RATE < 0) {
            PC_CAFE_POINT_RATE = 1;
        }
        PC_CAFE_REWARD_LOW_EXP_KILLS = PcCafe.getBoolean("RewardLowExpKills", true);
        PC_CAFE_LOW_EXP_KILLS_CHANCE = PcCafe.getInt("RewardLowExpKillsChance", 50);
        if (PC_CAFE_LOW_EXP_KILLS_CHANCE < 0) {
            PC_CAFE_LOW_EXP_KILLS_CHANCE = 0;
        }
        if (PC_CAFE_LOW_EXP_KILLS_CHANCE > 100) {
            PC_CAFE_LOW_EXP_KILLS_CHANCE = 100;
        }

        // Load AutoPotions config file (if exists)
        final PropertiesParser AutoPotions = new PropertiesParser(CUSTOM_AUTO_POTIONS_CONFIG_FILE);
        AUTO_POTIONS_ENABLED = AutoPotions.getBoolean("AutoPotionsEnabled", false);
        AUTO_POTIONS_IN_OLYMPIAD = AutoPotions.getBoolean("AutoPotionsInOlympiad", false);
        AUTO_POTION_MIN_LVL = AutoPotions.getInt("AutoPotionMinimumLevel", 1);
        AUTO_CP_ENABLED = AutoPotions.getBoolean("AutoCpEnabled", true);
        AUTO_HP_ENABLED = AutoPotions.getBoolean("AutoHpEnabled", true);
        AUTO_MP_ENABLED = AutoPotions.getBoolean("AutoMpEnabled", true);
        AUTO_CP_PERCENTAGE = AutoPotions.getInt("AutoCpPercentage", 70);
        AUTO_HP_PERCENTAGE = AutoPotions.getInt("AutoHpPercentage", 70);
        AUTO_MP_PERCENTAGE = AutoPotions.getInt("AutoMpPercentage", 70);
        AUTO_CP_ITEM_IDS = new ArrayList<>();
        for (String s : AutoPotions.getString("AutoCpItemIds", "0").split(","))
        {
            AUTO_CP_ITEM_IDS.add(Integer.parseInt(s));
        }
        AUTO_HP_ITEM_IDS = new ArrayList<>();
        for (String s : AutoPotions.getString("AutoHpItemIds", "0").split(","))
        {
            AUTO_HP_ITEM_IDS.add(Integer.parseInt(s));
        }
        AUTO_MP_ITEM_IDS = new ArrayList<>();
        for (String s : AutoPotions.getString("AutoMpItemIds", "0").split(","))
        {
            AUTO_MP_ITEM_IDS.add(Integer.parseInt(s));
        }

        final PropertiesParser Donations = new PropertiesParser(CUSTOM_DONATION_CONFIG_FILE);
        ENABLE_DONATION = Donations.getBoolean("EnableDonate", false);

        // Load PvpAnnounce config file (if exists)
        final PropertiesParser PvpAnnounce = new PropertiesParser(CUSTOM_PVP_ANNOUNCE_CONFIG_FILE);

        ANNOUNCE_PK_PVP = PvpAnnounce.getBoolean("AnnouncePkPvP", false);
        ANNOUNCE_PK_PVP_NORMAL_MESSAGE = PvpAnnounce.getBoolean("AnnouncePkPvPNormalMessage", true);
        ANNOUNCE_PK_MSG = PvpAnnounce.getString("AnnouncePkMsg", "$killer has slaughtered $target");
        ANNOUNCE_PVP_MSG = PvpAnnounce.getString("AnnouncePvpMsg", "$killer has defeated $target");

        // Load PvpRewardItem config file (if exists)
        final PropertiesParser PvpRewardItem = new PropertiesParser(CUSTOM_PVP_REWARD_ITEM_CONFIG_FILE);

        REWARD_PVP_ITEM = PvpRewardItem.getBoolean("RewardPvpItem", false);
        REWARD_PVP_ITEM_ID = PvpRewardItem.getInt("RewardPvpItemId", 57);
        REWARD_PVP_ITEM_AMOUNT = PvpRewardItem.getInt("RewardPvpItemAmount", 1000);
        REWARD_PVP_ITEM_MESSAGE = PvpRewardItem.getBoolean("RewardPvpItemMessage", true);
        REWARD_PK_ITEM = PvpRewardItem.getBoolean("RewardPkItem", false);
        REWARD_PK_ITEM_ID = PvpRewardItem.getInt("RewardPkItemId", 57);
        REWARD_PK_ITEM_AMOUNT = PvpRewardItem.getInt("RewardPkItemAmount", 500);
        REWARD_PK_ITEM_MESSAGE = PvpRewardItem.getBoolean("RewardPkItemMessage", true);
        DISABLE_REWARDS_IN_INSTANCES = PvpRewardItem.getBoolean("DisableRewardsInInstances", true);
        DISABLE_REWARDS_IN_PVP_ZONES = PvpRewardItem.getBoolean("DisableRewardsInPvpZones", true);

        // Load PvpRewardItem config file (if exists)
        final PropertiesParser PvpTitleColor = new PropertiesParser(CUSTOM_PVP_TITLE_CONFIG_FILE);

        PVP_COLOR_SYSTEM_ENABLED = PvpTitleColor.getBoolean("EnablePvPColorSystem", false);
        PVP_AMOUNT1 = PvpTitleColor.getInt("PvpAmount1", 500);
        PVP_AMOUNT2 = PvpTitleColor.getInt("PvpAmount2", 1000);
        PVP_AMOUNT3 = PvpTitleColor.getInt("PvpAmount3", 1500);
        PVP_AMOUNT4 = PvpTitleColor.getInt("PvpAmount4", 2500);
        PVP_AMOUNT5 = PvpTitleColor.getInt("PvpAmount5", 5000);
        NAME_COLOR_FOR_PVP_AMOUNT1 = Integer.decode("0x" + PvpTitleColor.getString("ColorForAmount1", "00FF00"));
        NAME_COLOR_FOR_PVP_AMOUNT2 = Integer.decode("0x" + PvpTitleColor.getString("ColorForAmount2", "00FF00"));
        NAME_COLOR_FOR_PVP_AMOUNT3 = Integer.decode("0x" + PvpTitleColor.getString("ColorForAmount3", "00FF00"));
        NAME_COLOR_FOR_PVP_AMOUNT4 = Integer.decode("0x" + PvpTitleColor.getString("ColorForAmount4", "00FF00"));
        NAME_COLOR_FOR_PVP_AMOUNT5 = Integer.decode("0x" + PvpTitleColor.getString("ColorForAmount5", "00FF00"));
        TITLE_FOR_PVP_AMOUNT1 = PvpTitleColor.getString("PvPTitleForAmount1", "Title");
        TITLE_FOR_PVP_AMOUNT2 = PvpTitleColor.getString("PvPTitleForAmount2", "Title");
        TITLE_FOR_PVP_AMOUNT3 = PvpTitleColor.getString("PvPTitleForAmount3", "Title");
        TITLE_FOR_PVP_AMOUNT4 = PvpTitleColor.getString("PvPTitleForAmount4", "Title");
        TITLE_FOR_PVP_AMOUNT5 = PvpTitleColor.getString("PvPTitleForAmount5", "Title");

        // Load RandomSpawns config file (if exists)
        final PropertiesParser RandomSpawns = new PropertiesParser(CUSTOM_RANDOM_SPAWNS_CONFIG_FILE);

        ENABLE_RANDOM_MONSTER_SPAWNS = RandomSpawns.getBoolean("EnableRandomMonsterSpawns", false);
        MOB_MAX_SPAWN_RANGE = RandomSpawns.getInt("MaxSpawnMobRange", 150);
        MOB_MIN_SPAWN_RANGE = MOB_MAX_SPAWN_RANGE * -1;
        if (ENABLE_RANDOM_MONSTER_SPAWNS) {
            final String[] mobsIds = RandomSpawns.getString("MobsSpawnNotRandom", "18812,18813,18814,22138").split(",");
            MOBS_LIST_NOT_RANDOM = new ArrayList<>(mobsIds.length);
            for (String id : mobsIds) {
                MOBS_LIST_NOT_RANDOM.add(Integer.valueOf(id));
            }
        }

        // Load ScreenWelcomeMessage config file (if exists)
        final PropertiesParser ScreenWelcomeMessage = new PropertiesParser(CUSTOM_SCREEN_WELCOME_MESSAGE_CONFIG_FILE);

        WELCOME_MESSAGE_ENABLED = ScreenWelcomeMessage.getBoolean("ScreenWelcomeMessageEnable", false);
        WELCOME_MESSAGE_TEXT = ScreenWelcomeMessage.getString("ScreenWelcomeMessageText", "Welcome to our server!");
        WELCOME_MESSAGE_TIME = ScreenWelcomeMessage.getInt("ScreenWelcomeMessageTime", 10) * 1000;

        // Load SellBuffs config file (if exists)
        final PropertiesParser SellBuffs = new PropertiesParser(CUSTOM_SELL_BUFFS_CONFIG_FILE);

        SELLBUFF_ENABLED = SellBuffs.getBoolean("SellBuffEnable", false);
        SELLBUFF_MP_MULTIPLER = SellBuffs.getInt("MpCostMultipler", 1);
        SELLBUFF_PAYMENT_ID = SellBuffs.getInt("PaymentID", 57);
        SELLBUFF_MIN_PRICE = SellBuffs.getLong("MinimalPrice", 100000);
        SELLBUFF_MAX_PRICE = SellBuffs.getLong("MaximalPrice", 100000000);
        SELLBUFF_MAX_BUFFS = SellBuffs.getInt("MaxBuffs", 15);

        // Load SchemeBuffer config file (if exists)
        final PropertiesParser SchemeBuffer = new PropertiesParser(CUSTOM_SCHEME_BUFFER_CONFIG_FILE);

        BUFFER_MAX_SCHEMES = SchemeBuffer.getInt("BufferMaxSchemesPerChar", 4);
        BUFFER_STATIC_BUFF_COST = SchemeBuffer.getInt("BufferStaticCostPerBuff", -1);

        // Load StartingLocation config file (if exists)
        final PropertiesParser StartingLocation = new PropertiesParser(CUSTOM_STARTING_LOCATION_CONFIG_FILE);

        CUSTOM_STARTING_LOC = StartingLocation.getBoolean("CustomStartingLocation", false);
        CUSTOM_STARTING_LOC_X = StartingLocation.getInt("CustomStartingLocX", 50821);
        CUSTOM_STARTING_LOC_Y = StartingLocation.getInt("CustomStartingLocY", 186527);
        CUSTOM_STARTING_LOC_Z = StartingLocation.getInt("CustomStartingLocZ", -3625);

        // Load VoteReward config file (if exists)
        final PropertiesParser VoteReward = new PropertiesParser(CUSTOM_VOTE_REWARD_CONFIG_FILE);

        ALLOW_NETWORK_VOTE_REWARD = VoteReward.getBoolean("AllowNetworkVoteReward", false);
        NETWORK_SERVER_LINK = VoteReward.getString("NetworkServerLink", "");
        NETWORK_VOTES_DIFFERENCE = VoteReward.getInt("NetworkVotesDifference", 5);
        NETWORK_REWARD_CHECK_TIME = VoteReward.getInt("NetworkRewardCheckTime", 5);
        String NETWORK_SMALL_REWARD_VALUE = VoteReward.getString("NetworkReward", "57,100000000;");
        String[] NETWORK_small_reward_splitted_1 = NETWORK_SMALL_REWARD_VALUE.split(";");
        for (String i : NETWORK_small_reward_splitted_1) {
            String[] NETWORK_small_reward_splitted_2 = i.split(",");
            NETWORK_REWARD.put(Integer.parseInt(NETWORK_small_reward_splitted_2[0]), Integer.parseInt(NETWORK_small_reward_splitted_2[1]));
        }
        NETWORK_DUALBOXES_ALLOWED = VoteReward.getInt("NetworkDualboxesAllowed", 1);
        ALLOW_NETWORK_GAME_SERVER_REPORT = VoteReward.getBoolean("AllowNetworkGameServerReport", false);
        ALLOW_TOPZONE_VOTE_REWARD = VoteReward.getBoolean("AllowTopzoneVoteReward", false);
        TOPZONE_SERVER_LINK = VoteReward.getString("TopzoneServerLink", "");
        TOPZONE_VOTES_DIFFERENCE = VoteReward.getInt("TopzoneVotesDifference", 5);
        TOPZONE_REWARD_CHECK_TIME = VoteReward.getInt("TopzoneRewardCheckTime", 5);
        String TOPZONE_SMALL_REWARD_VALUE = VoteReward.getString("TopzoneReward", "57,100000000;");
        String[] topzone_small_reward_splitted_1 = TOPZONE_SMALL_REWARD_VALUE.split(";");
        for (String i : topzone_small_reward_splitted_1) {
            String[] topzone_small_reward_splitted_2 = i.split(",");
            TOPZONE_REWARD.put(Integer.parseInt(topzone_small_reward_splitted_2[0]), Integer.parseInt(topzone_small_reward_splitted_2[1]));
        }
        TOPZONE_DUALBOXES_ALLOWED = VoteReward.getInt("TopzoneDualboxesAllowed", 1);
        ALLOW_TOPZONE_GAME_SERVER_REPORT = VoteReward.getBoolean("AllowTopzoneGameServerReport", false);
        ALLOW_HOPZONE_VOTE_REWARD = VoteReward.getBoolean("AllowHopzoneVoteReward", false);
        HOPZONE_SERVER_LINK = VoteReward.getString("HopzoneServerLink", "");
        HOPZONE_VOTES_DIFFERENCE = VoteReward.getInt("HopzoneVotesDifference", 5);
        HOPZONE_REWARD_CHECK_TIME = VoteReward.getInt("HopzoneRewardCheckTime", 5);
        String HOPZONE_SMALL_REWARD_VALUE = VoteReward.getString("HopzoneReward", "57,100000000;");
        String[] hopzone_small_reward_splitted_1 = HOPZONE_SMALL_REWARD_VALUE.split(";");
        for (String i : hopzone_small_reward_splitted_1) {
            String[] hopzone_small_reward_splitted_2 = i.split(",");
            HOPZONE_REWARD.put(Integer.parseInt(hopzone_small_reward_splitted_2[0]), Integer.parseInt(hopzone_small_reward_splitted_2[1]));
        }
        HOPZONE_DUALBOXES_ALLOWED = VoteReward.getInt("HopzoneDualboxesAllowed", 1);
        ALLOW_HOPZONE_GAME_SERVER_REPORT = VoteReward.getBoolean("AllowHopzoneGameServerReport", false);

        final PropertiesParser magicLampSettings = new PropertiesParser(MAGIC_LAMP_CONFIG_FILE);
        ENABLE_MAGIC_LAMP = magicLampSettings.getBoolean("MagicLampEnabled", false);
        MAGIC_LAMP_MAX_GAME_COUNT = magicLampSettings.getInt("MagicLampMaxGames", 127);
        MAGIC_LAMP_REWARD_COUNT = magicLampSettings.getInt("MagicLampRewardCount", 1);
        MAGIC_LAMP_GREATER_REWARD_COUNT = magicLampSettings.getInt("MagicLampGreaterRewardCount", 10);
        MAGIC_LAMP_MAX_LEVEL_EXP = magicLampSettings.getInt("MagicLampMaxLevelExp", 10000000);
        MAGIC_LAMP_CHARGE_RATE = magicLampSettings.getDouble("MagicLampChargeRate", 0.1);

        // Load althars config
        final PropertiesParser althars = new PropertiesParser(ALTHARS_CONFIG_FILE);

        ALTHARS_ACTIVATE_CHANCE_RATE = althars.getInt("althars_activation_chance_rate", 70);
        ALTHARS_MAX_ACTIVE = althars.getInt("althars_max_active", 3);
        ALTHARS_MIN_DURATION_CYCLE = althars.getInt("althars_min_duration_cycle", 240000);
        ALTHARS_MAX_DURATION_CYCLE = althars.getInt("althars_max_duration_cycle", 480000);
    }

    /**
     * Loads flood protector configurations.
     *
     * @param properties the properties object containing the actual values of the flood protector configs
     */
    private static void loadFloodProtectorConfigs(PropertiesParser properties) {
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_USE_ITEM, "UseItem", 4);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ROLL_DICE, "RollDice", 42);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_FIREWORK, "Firework", 42);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_PET_SUMMON, "ItemPetSummon", 16);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_HERO_VOICE, "HeroVoice", 100);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_GLOBAL_CHAT, "GlobalChat", 5);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_DROP_ITEM, "DropItem", 10);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", 5);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MULTISELL, "MultiSell", 1);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_TRANSACTION, "Transaction", 10);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANUFACTURE, "Manufacture", 3);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SENDMAIL, "SendMail", 100);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_CHARACTER_SELECT, "CharacterSelect", 30);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_AUCTION, "ItemAuction", 9);
    }

    /**
     * Loads single flood protector configuration.
     *
     * @param properties      properties file reader
     * @param config          flood protector configuration instance
     * @param configString    flood protector configuration string that determines for which flood protector configuration should be read
     * @param defaultInterval default flood protector interval
     */
    private static void loadFloodProtectorConfig(PropertiesParser properties, FloodProtectorConfig config, String configString, int defaultInterval) {
        config.FLOOD_PROTECTION_INTERVAL = properties.getInt("FloodProtector" + configString + "Interval", defaultInterval);
        config.LOG_FLOODING = properties.getBoolean("FloodProtector" + configString + "LogFlooding", false);
        config.PUNISHMENT_LIMIT = properties.getInt("FloodProtector" + configString + "PunishmentLimit", 0);
        config.PUNISHMENT_TYPE = properties.getString("FloodProtector" + configString + "PunishmentType", "none");
        config.PUNISHMENT_TIME = properties.getInt("FloodProtector" + configString + "PunishmentTime", 0) * 60000L;
    }
}
