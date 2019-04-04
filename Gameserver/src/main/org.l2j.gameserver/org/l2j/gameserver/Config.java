package org.l2j.gameserver;

import org.l2j.commons.util.PropertiesParser;
import org.l2j.commons.util.StringUtil;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.util.FloodProtectorConfig;
import org.l2j.gameserver.util.IGameXmlReader;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class loads all the game server related configurations from files.<br>
 * The files are usually located in config folder in server root folder.<br>
 * Each configuration has a default value (that should reflect retail behavior).
 */
public final class Config {
    // --------------------------------------------------
    // Constants
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class.getName());
    // --------------------------------------------------
    public static final String EOL = System.lineSeparator();
    // --------------------------------------------------
    // Config File Definitions
    // --------------------------------------------------
    public static final String GEOENGINE_CONFIG_FILE = "./config/GeoEngine.ini";
    public static final String OLYMPIAD_CONFIG_FILE = "./config/Olympiad.ini";
    public static final String SIEGE_CONFIG_FILE = "./config/Siege.ini";
    public static final String FORTSIEGE_CONFIG_FILE = "./config/FortSiege.ini";
    private static final String ATTENDANCE_CONFIG_FILE = "./config/AttendanceRewards.ini";
    private static final String CHARACTER_CONFIG_FILE = "./config/Character.ini";
    private static final String FEATURE_CONFIG_FILE = "./config/Feature.ini";
    private static final String FLOOD_PROTECTOR_CONFIG_FILE = "./config/FloodProtector.ini";
    private static final String GENERAL_CONFIG_FILE = "./config/General.ini";
    private static final String GRACIASEEDS_CONFIG_FILE = "./config/GraciaSeeds.ini";
    private static final String GRANDBOSS_CONFIG_FILE = "./config/GrandBoss.ini";
    private static final String IDFACTORY_CONFIG_FILE = "./config/IdFactory.ini";
    private static final String LOGIN_CONFIG_FILE = "./config/LoginServer.ini";
    private static final String NPC_CONFIG_FILE = "./config/NPC.ini";
    private static final String PVP_CONFIG_FILE = "./config/PVP.ini";
    private static final String RATES_CONFIG_FILE = "./config/Rates.ini";
    private static final String SERVER_CONFIG_FILE = "./config/Server.ini";
    private static final String TRAINING_CAMP_CONFIG_FILE = "./config/TrainingCamp.ini";
    private static final String CHAT_FILTER_FILE = "./config/chatfilter.txt";
    private static final String IPCONFIG_FILE = "./config/ipconfig.xml";

    public static final int MAX_ACTIVE_ACCOUNTS_ON_ONE_IP = 0;
    public static final String[] MAX_ACTIVE_ACCOUNTS_IGNORED_IP = { "127.0.0.1"};
    public static final int MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID = 0;
    public static final int CHARACTER_DELETE_AFTER_HOURS = 168;

    // --------------------------------------------------
    // Custom Config File Definitions
    // --------------------------------------------------
    private static final String CUSTOM_ALLOWED_PLAYER_RACES_CONFIG_FILE = "./config/Custom/AllowedPlayerRaces.ini";
    private static final String CUSTOM_AUTO_POTIONS_CONFIG_FILE = "./config/Custom/AutoPotions.ini";
    private static final String CUSTOM_BANKING_CONFIG_FILE = "./config/Custom/Banking.ini";
    private static final String CUSTOM_CHAMPION_MONSTERS_CONFIG_FILE = "./config/Custom/ChampionMonsters.ini";
    private static final String CUSTOM_CHAT_MODERATION_CONFIG_FILE = "./config/Custom/ChatModeration.ini";
    private static final String CUSTOM_COMMUNITY_BOARD_CONFIG_FILE = "./config/Custom/CommunityBoard.ini";
    private static final String CUSTOM_DUALBOX_CHECK_CONFIG_FILE = "./config/Custom/DualboxCheck.ini";
    private static final String CUSTOM_FIND_PVP_CONFIG_FILE = "./config/Custom/FindPvP.ini";
    private static final String CUSTOM_MULTILANGUAL_SUPPORT_CONFIG_FILE = "./config/Custom/MultilingualSupport.ini";
    private static final String CUSTOM_NPC_STAT_MULTIPIERS_CONFIG_FILE = "./config/Custom/NpcStatMultipliers.ini";
    private static final String CUSTOM_OFFLINE_TRADE_CONFIG_FILE = "./config/Custom/OfflineTrade.ini";
    private static final String CUSTOM_PASSWORD_CHANGE_CONFIG_FILE = "./config/Custom/PasswordChange.ini";
    private static final String CUSTOM_PC_CAFE_CONFIG_FILE = "./config/Custom/PcCafe.ini";
    private static final String CUSTOM_PREMIUM_SYSTEM_CONFIG_FILE = "./config/Custom/PremiumSystem.ini";
    private static final String CUSTOM_PRIVATE_STORE_RANGE_CONFIG_FILE = "./config/Custom/PrivateStoreRange.ini";
    private static final String CUSTOM_PVP_ANNOUNCE_CONFIG_FILE = "./config/Custom/PvpAnnounce.ini";
    private static final String CUSTOM_PVP_REWARD_ITEM_CONFIG_FILE = "./config/Custom/PvpRewardItem.ini";
    private static final String CUSTOM_PVP_TITLE_CONFIG_FILE = "./config/Custom/PvpTitleColor.ini";
    private static final String CUSTOM_RANDOM_SPAWNS_CONFIG_FILE = "./config/Custom/RandomSpawns.ini";
    private static final String CUSTOM_SCREEN_WELCOME_MESSAGE_CONFIG_FILE = "./config/Custom/ScreenWelcomeMessage.ini";
    private static final String CUSTOM_SELL_BUFFS_CONFIG_FILE = "./config/Custom/SellBuffs.ini";
    private static final String CUSTOM_SERVER_TIME_CONFIG_FILE = "./config/Custom/ServerTime.ini";
    private static final String CUSTOM_SCHEME_BUFFER_CONFIG_FILE = "./config/Custom/ShemeBuffer.ini";
    private static final String CUSTOM_STARTING_LOCATION_CONFIG_FILE = "./config/Custom/StartingLocation.ini";
    private static final String CUSTOM_VOTE_REWARD_CONFIG_FILE = "./config/Custom/VoteReward.ini";
    private static final String CUSTOM_WALKER_BOT_PROTECTION_CONFIG_FILE = "./config/Custom/WalkerBotProtection.ini";

    // --------------------------------------------------
    // Variable Definitions
    // --------------------------------------------------
    public static boolean ENABLE_ATTENDANCE_REWARDS;
    public static boolean PREMIUM_ONLY_ATTENDANCE_REWARDS;
    public static boolean ATTENDANCE_REWARDS_SHARE_ACCOUNT;
    public static int ATTENDANCE_REWARD_DELAY;
    public static boolean ATTENDANCE_POPUP_WINDOW;
    public static boolean PLAYER_DELEVEL;
    public static int DELEVEL_MINIMUM;
    public static boolean DECREASE_SKILL_LEVEL;
    public static double ALT_WEIGHT_LIMIT;
    public static int RUN_SPD_BOOST;
    public static double RESPAWN_RESTORE_CP;
    public static double RESPAWN_RESTORE_HP;
    public static double RESPAWN_RESTORE_MP;
    public static boolean ENABLE_MODIFY_SKILL_DURATION;
    public static Map<Integer, Integer> SKILL_DURATION_LIST;
    public static boolean ENABLE_MODIFY_SKILL_REUSE;
    public static Map<Integer, Integer> SKILL_REUSE_LIST;
    public static boolean AUTO_LEARN_SKILLS;
    public static boolean AUTO_LEARN_FS_SKILLS;
    public static boolean AUTO_LOOT_HERBS;
    public static byte BUFFS_MAX_AMOUNT;
    public static byte TRIGGERED_BUFFS_MAX_AMOUNT;
    public static byte DANCES_MAX_AMOUNT;
    public static boolean DANCE_CANCEL_BUFF;
    public static boolean DANCE_CONSUME_ADDITIONAL_MP;
    public static boolean ALT_STORE_DANCES;
    public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
    public static boolean ALT_GAME_CANCEL_BOW;
    public static boolean ALT_GAME_CANCEL_CAST;
    public static boolean ALT_GAME_MAGICFAILURES;
    public static boolean ALT_GAME_STUN_BREAK;
    public static int PLAYER_FAKEDEATH_UP_PROTECTION;
    public static boolean STORE_SKILL_COOLTIME;
    public static boolean SUBCLASS_STORE_SKILL_COOLTIME;
    public static boolean SUMMON_STORE_SKILL_COOLTIME;
    public static long EFFECT_TICK_RATIO;
    public static boolean LIFE_CRYSTAL_NEEDED;
    public static boolean DIVINE_SP_BOOK_NEEDED;
    public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
    public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
    public static boolean ALLOW_TRANSFORM_WITHOUT_QUEST;
    public static int FEE_DELETE_TRANSFER_SKILLS;
    public static int FEE_DELETE_SUBCLASS_SKILLS;
    public static int FEE_DELETE_DUALCLASS_SKILLS;
    public static boolean RESTORE_SERVITOR_ON_RECONNECT;
    public static boolean RESTORE_PET_ON_RECONNECT;
    public static double MAX_BONUS_EXP;
    public static double MAX_BONUS_SP;
    public static int MAX_RUN_SPEED;
    public static int MAX_PATK;
    public static int MAX_MATK;
    public static int MAX_PCRIT_RATE;
    public static int MAX_MCRIT_RATE;
    public static int MAX_PATK_SPEED;
    public static int MAX_MATK_SPEED;
    public static int MAX_EVASION;
    public static int MIN_ABNORMAL_STATE_SUCCESS_RATE;
    public static int MAX_ABNORMAL_STATE_SUCCESS_RATE;
    public static long MAX_SP;
    public static byte MAX_SUBCLASS;
    public static byte BASE_SUBCLASS_LEVEL;
    public static byte BASE_DUALCLASS_LEVEL;
    public static byte MAX_SUBCLASS_LEVEL;
    public static int MAX_PVTSTORESELL_SLOTS_DWARF;
    public static int MAX_PVTSTORESELL_SLOTS_OTHER;
    public static int MAX_PVTSTOREBUY_SLOTS_DWARF;
    public static int MAX_PVTSTOREBUY_SLOTS_OTHER;
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    public static int INVENTORY_MAXIMUM_DWARF;
    public static int INVENTORY_MAXIMUM_GM;
    public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
    public static int WAREHOUSE_SLOTS_DWARF;
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    public static int WAREHOUSE_SLOTS_CLAN;
    public static int ALT_FREIGHT_SLOTS;
    public static int ALT_FREIGHT_PRICE;
    public static long MENTOR_PENALTY_FOR_MENTEE_COMPLETE;
    public static long MENTOR_PENALTY_FOR_MENTEE_LEAVE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
    public static int MAX_PERSONAL_FAME_POINTS;
    public static int FORTRESS_ZONE_FAME_TASK_FREQUENCY;
    public static int FORTRESS_ZONE_FAME_AQUIRE_POINTS;
    public static int CASTLE_ZONE_FAME_TASK_FREQUENCY;
    public static int CASTLE_ZONE_FAME_AQUIRE_POINTS;
    public static boolean FAME_FOR_DEAD_PLAYERS;
    public static boolean IS_CRAFTING_ENABLED;
    public static boolean CRAFT_MASTERWORK;
    public static int DWARF_RECIPE_LIMIT;
    public static int COMMON_RECIPE_LIMIT;
    public static boolean ALT_GAME_CREATION;
    public static double ALT_GAME_CREATION_SPEED;
    public static double ALT_GAME_CREATION_XP_RATE;
    public static double ALT_GAME_CREATION_RARE_XPSP_RATE;
    public static double ALT_GAME_CREATION_SP_RATE;
    public static boolean ALT_CLAN_LEADER_INSTANT_ACTIVATION;
    public static int ALT_CLAN_JOIN_DAYS;
    public static int ALT_CLAN_CREATE_DAYS;
    public static int ALT_CLAN_DISSOLVE_DAYS;
    public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
    public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
    public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
    public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
    public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
    public static int ALT_CLAN_MEMBERS_FOR_WAR;
    public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
    public static long ALT_CLAN_MEMBERS_TIME_FOR_BONUS;
    public static boolean REMOVE_CASTLE_CIRCLETS;
    public static int ALT_PARTY_MAX_MEMBERS;
    public static int ALT_PARTY_RANGE;
    public static int ALT_PARTY_RANGE2;
    public static boolean ALT_LEAVE_PARTY_LEADER;
    public static boolean INITIAL_EQUIPMENT_EVENT;
    public static long STARTING_ADENA;
    public static byte STARTING_LEVEL;
    public static int STARTING_SP;
    public static long MAX_ADENA;
    public static boolean AUTO_LOOT;
    public static boolean AUTO_LOOT_RAIDS;
    public static boolean AUTO_LOOT_SLOT_LIMIT;
    public static int LOOT_RAIDS_PRIVILEGE_INTERVAL;
    public static int LOOT_RAIDS_PRIVILEGE_CC_SIZE;
    public static List<Integer> AUTO_LOOT_ITEM_IDS;
    public static boolean ENABLE_KEYBOARD_MOVEMENT;
    public static int UNSTUCK_INTERVAL;
    public static int TELEPORT_WATCHDOG_TIMEOUT;
    public static int PLAYER_SPAWN_PROTECTION;
    public static int PLAYER_TELEPORT_PROTECTION;
    public static boolean RANDOM_RESPAWN_IN_TOWN_ENABLED;
    public static boolean OFFSET_ON_TELEPORT_ENABLED;
    public static int MAX_OFFSET_ON_TELEPORT;
    public static boolean PETITIONING_ALLOWED;
    public static int MAX_PETITIONS_PER_PLAYER;
    public static int MAX_PETITIONS_PENDING;
    public static int MAX_FREE_TELEPORT_LEVEL;
    public static int MAX_NEWBIE_BUFF_LEVEL;
    public static int DELETE_DAYS;
    public static String PARTY_XP_CUTOFF_METHOD;
    public static double PARTY_XP_CUTOFF_PERCENT;
    public static int PARTY_XP_CUTOFF_LEVEL;
    public static int[][] PARTY_XP_CUTOFF_GAPS;
    public static int[] PARTY_XP_CUTOFF_GAP_PERCENTS;
    public static boolean DISABLE_TUTORIAL;
    public static boolean EXPERTISE_PENALTY;
    public static boolean STORE_RECIPE_SHOPLIST;
    public static boolean STORE_UI_SETTINGS;
    public static String[] FORBIDDEN_NAMES;
    public static boolean SILENCE_MODE_EXCLUDE;
    public static boolean SHOW_GOD_VIDEO_INTRO;

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
    public static List<Integer> SIEGE_HOUR_LIST;
    public static int CASTLE_BUY_TAX_NEUTRAL;
    public static int CASTLE_BUY_TAX_LIGHT;
    public static int CASTLE_BUY_TAX_DARK;
    public static int CASTLE_SELL_TAX_NEUTRAL;
    public static int CASTLE_SELL_TAX_LIGHT;
    public static int CASTLE_SELL_TAX_DARK;
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

    // --------------------------------------------------
    // Fortress Settings
    // --------------------------------------------------
    public static long FS_TELE_FEE_RATIO;
    public static int FS_TELE1_FEE;
    public static int FS_TELE2_FEE;
    public static long FS_MPREG_FEE_RATIO;
    public static int FS_MPREG1_FEE;
    public static int FS_MPREG2_FEE;
    public static long FS_HPREG_FEE_RATIO;
    public static int FS_HPREG1_FEE;
    public static int FS_HPREG2_FEE;
    public static long FS_EXPREG_FEE_RATIO;
    public static int FS_EXPREG1_FEE;
    public static int FS_EXPREG2_FEE;
    public static long FS_SUPPORT_FEE_RATIO;
    public static int FS_SUPPORT1_FEE;
    public static int FS_SUPPORT2_FEE;
    public static int FS_BLOOD_OATH_COUNT;
    public static int FS_UPDATE_FRQ;
    public static int FS_MAX_SUPPLY_LEVEL;
    public static int FS_FEE_FOR_CASTLE;
    public static int FS_MAX_OWN_TIME;

    // --------------------------------------------------
    // Feature Settings
    // --------------------------------------------------
    public static int TAKE_FORT_POINTS;
    public static int LOOSE_FORT_POINTS;
    public static int TAKE_CASTLE_POINTS;
    public static int LOOSE_CASTLE_POINTS;
    public static int CASTLE_DEFENDED_POINTS;
    public static int FESTIVAL_WIN_POINTS;
    public static int HERO_POINTS;
    public static int ROYAL_GUARD_COST;
    public static int KNIGHT_UNIT_COST;
    public static int KNIGHT_REINFORCE_COST;
    public static int BALLISTA_POINTS;
    public static int BLOODALLIANCE_POINTS;
    public static int BLOODOATH_POINTS;
    public static int KNIGHTSEPAULETTE_POINTS;
    public static int REPUTATION_SCORE_PER_KILL;
    public static int JOIN_ACADEMY_MIN_REP_SCORE;
    public static int JOIN_ACADEMY_MAX_REP_SCORE;
    public static int CLAN_LEVEL_6_COST;
    public static int CLAN_LEVEL_7_COST;
    public static int CLAN_LEVEL_8_COST;
    public static int CLAN_LEVEL_9_COST;
    public static int CLAN_LEVEL_10_COST;
    public static int CLAN_LEVEL_11_COST;
    public static int CLAN_LEVEL_6_REQUIREMENT;
    public static int CLAN_LEVEL_7_REQUIREMENT;
    public static int CLAN_LEVEL_8_REQUIREMENT;
    public static int CLAN_LEVEL_9_REQUIREMENT;
    public static int CLAN_LEVEL_10_REQUIREMENT;
    public static int CLAN_LEVEL_11_REQUIREMENT;
    public static boolean ALLOW_WYVERN_ALWAYS;
    public static boolean ALLOW_WYVERN_DURING_SIEGE;
    public static boolean ALLOW_MOUNTS_DURING_SIEGE;

    // --------------------------------------------------
    // General Settings
    // --------------------------------------------------
    public static int DEFAULT_ACCESS_LEVEL;
    public static boolean SERVER_GMONLY;
    public static boolean GM_HERO_AURA;
    public static boolean GM_STARTUP_BUILDER_HIDE;
    public static boolean GM_STARTUP_INVULNERABLE;
    public static boolean GM_STARTUP_INVISIBLE;
    public static boolean GM_STARTUP_SILENCE;
    public static boolean GM_STARTUP_AUTO_LIST;
    public static boolean GM_STARTUP_DIET_MODE;
    public static boolean GM_ITEM_RESTRICTION;
    public static boolean GM_SKILL_RESTRICTION;
    public static boolean GM_TRADE_RESTRICTED_ITEMS;
    public static boolean GM_RESTART_FIGHTING;
    public static boolean GM_ANNOUNCER_NAME;
    public static boolean GM_GIVE_SPECIAL_SKILLS;
    public static boolean GM_GIVE_SPECIAL_AURA_SKILLS;
    public static boolean GM_DEBUG_HTML_PATHS;
    public static boolean USE_SUPER_HASTE_AS_GM_SPEED;
    public static boolean LOG_CHAT;
    public static boolean LOG_AUTO_ANNOUNCEMENTS;
    public static boolean LOG_ITEMS;
    public static boolean LOG_ITEMS_SMALL_LOG;
    public static boolean LOG_ITEM_ENCHANTS;
    public static boolean LOG_SKILL_ENCHANTS;
    public static boolean GMAUDIT;
    public static boolean SKILL_CHECK_ENABLE;
    public static boolean SKILL_CHECK_REMOVE;
    public static boolean SKILL_CHECK_GM;
    public static boolean HTML_ACTION_CACHE_DEBUG;
    public static boolean DEVELOPER;
    public static boolean ALT_DEV_NO_QUESTS;
    public static boolean ALT_DEV_NO_SPAWNS;
    public static boolean ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS;
    public static boolean ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS;
    public static int SCHEDULED_THREAD_POOL_COUNT;
    public static int THREADS_PER_SCHEDULED_THREAD_POOL;
    public static int INSTANT_THREAD_POOL_COUNT;
    public static int THREADS_PER_INSTANT_THREAD_POOL;
    public static int IO_PACKET_THREAD_CORE_SIZE;
    public static boolean DEADLOCK_DETECTOR;
    public static int DEADLOCK_CHECK_INTERVAL;
    public static boolean RESTART_ON_DEADLOCK;
    public static boolean ALLOW_DISCARDITEM;
    public static int AUTODESTROY_ITEM_AFTER;
    public static int HERB_AUTO_DESTROY_TIME;
    public static List<Integer> LIST_PROTECTED_ITEMS;
    public static int CHAR_DATA_STORE_INTERVAL;
    public static int CLAN_VARIABLES_STORE_INTERVAL;
    public static boolean LAZY_ITEMS_UPDATE;
    public static boolean UPDATE_ITEMS_ON_CHAR_STORE;
    public static boolean DESTROY_DROPPED_PLAYER_ITEM;
    public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
    public static boolean DESTROY_ALL_ITEMS;
    public static boolean SAVE_DROPPED_ITEM;
    public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
    public static int SAVE_DROPPED_ITEM_INTERVAL;
    public static boolean CLEAR_DROPPED_ITEM_TABLE;
    public static boolean ORDER_QUEST_LIST_BY_QUESTID;
    public static boolean AUTODELETE_INVALID_QUEST_DATA;
    public static boolean ENABLE_STORY_QUEST_BUFF_REWARD;
    public static boolean MULTIPLE_ITEM_DROP;
    public static boolean FORCE_INVENTORY_UPDATE;
    public static boolean LAZY_CACHE;
    public static boolean CACHE_CHAR_NAMES;
    public static int MIN_NPC_ANIMATION;
    public static int MAX_NPC_ANIMATION;
    public static int MIN_MONSTER_ANIMATION;
    public static int MAX_MONSTER_ANIMATION;
    public static boolean ENABLE_FALLING_DAMAGE;
    public static boolean GRIDS_ALWAYS_ON;
    public static int GRID_NEIGHBOR_TURNON_TIME;
    public static int GRID_NEIGHBOR_TURNOFF_TIME;
    public static int PEACE_ZONE_MODE;
    public static String DEFAULT_GLOBAL_CHAT;
    public static String DEFAULT_TRADE_CHAT;
    public static boolean ENABLE_WORLD_CHAT;
    public static int MINIMUM_CHAT_LEVEL;
    public static boolean ALLOW_WAREHOUSE;
    public static boolean WAREHOUSE_CACHE;
    public static int WAREHOUSE_CACHE_TIME;
    public static boolean ALLOW_REFUND;
    public static boolean ALLOW_MAIL;
    public static boolean ALLOW_ATTACHMENTS;
    public static boolean ALLOW_WEAR;
    public static int WEAR_DELAY;
    public static int WEAR_PRICE;
    public static int INSTANCE_FINISH_TIME;
    public static boolean RESTORE_PLAYER_INSTANCE;
    public static int EJECT_DEAD_PLAYER_TIME;
    public static boolean ALLOW_WATER;
    public static boolean ALLOW_FISHING;
    public static boolean ALLOW_BOAT;
    public static int BOAT_BROADCAST_RADIUS;
    public static boolean ALLOW_CURSED_WEAPONS;
    public static boolean ALLOW_MANOR;
    public static boolean SERVER_NEWS;
    public static boolean ENABLE_COMMUNITY_BOARD;
    public static String BBS_DEFAULT;
    public static boolean USE_SAY_FILTER;
    public static String CHAT_FILTER_CHARS;
    public static Set<ChatType> BAN_CHAT_CHANNELS;
    public static int WORLD_CHAT_MIN_LEVEL;
    public static int WORLD_CHAT_POINTS_PER_DAY;
    public static Duration WORLD_CHAT_INTERVAL;
    public static int ALT_OLY_START_TIME;
    public static int ALT_OLY_MIN;
    public static long ALT_OLY_CPERIOD;
    public static long ALT_OLY_BATTLE;
    public static long ALT_OLY_WPERIOD;
    public static long ALT_OLY_VPERIOD;
    public static int ALT_OLY_START_POINTS;
    public static int ALT_OLY_WEEKLY_POINTS;
    public static int ALT_OLY_CLASSED;
    public static int ALT_OLY_NONCLASSED;
    public static int ALT_OLY_REG_DISPLAY;
    public static List<ItemHolder> ALT_OLY_CLASSED_REWARD;
    public static List<ItemHolder> ALT_OLY_NONCLASSED_REWARD;
    public static List<ItemHolder> ALT_OLY_TEAM_REWARD;
    public static int ALT_OLY_COMP_RITEM;
    public static int ALT_OLY_MIN_MATCHES;
    public static int ALT_OLY_MARK_PER_POINT;
    public static int ALT_OLY_HERO_POINTS;
    public static int ALT_OLY_RANK1_POINTS;
    public static int ALT_OLY_RANK2_POINTS;
    public static int ALT_OLY_RANK3_POINTS;
    public static int ALT_OLY_RANK4_POINTS;
    public static int ALT_OLY_RANK5_POINTS;
    public static int ALT_OLY_MAX_POINTS;
    public static int ALT_OLY_DIVIDER_CLASSED;
    public static int ALT_OLY_DIVIDER_NON_CLASSED;
    public static int ALT_OLY_MAX_WEEKLY_MATCHES;
    public static int ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED;
    public static int ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED;
    public static int ALT_OLY_MAX_WEEKLY_MATCHES_TEAM;
    public static boolean ALT_OLY_LOG_FIGHTS;
    public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
    public static boolean ALT_OLY_ANNOUNCE_GAMES;
    public static List<Integer> LIST_OLY_RESTRICTED_ITEMS;
    public static int ALT_OLY_ENCHANT_LIMIT;
    public static int ALT_OLY_WAIT_TIME;
    public static boolean ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS;
    public static String ALT_OLY_PERIOD;
    public static int ALT_OLY_PERIOD_MULTIPLIER;
    public static List<Integer> ALT_OLY_COMPETITION_DAYS;
    public static int ALT_MANOR_REFRESH_TIME;
    public static int ALT_MANOR_REFRESH_MIN;
    public static int ALT_MANOR_APPROVE_TIME;
    public static int ALT_MANOR_APPROVE_MIN;
    public static int ALT_MANOR_MAINTENANCE_MIN;
    public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
    public static int ALT_MANOR_SAVE_PERIOD_RATE;
    public static boolean ALT_ITEM_AUCTION_ENABLED;
    public static int ALT_ITEM_AUCTION_EXPIRED_AFTER;
    public static long ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
    public static IllegalActionPunishmentType DEFAULT_PUNISH;
    public static int DEFAULT_PUNISH_PARAM;
    public static boolean ONLY_GM_ITEMS_FREE;
    public static boolean JAIL_IS_PVP;
    public static boolean JAIL_DISABLE_CHAT;
    public static boolean JAIL_DISABLE_TRANSACTION;
    public static boolean CUSTOM_NPC_DATA;
    public static boolean CUSTOM_TELEPORT_TABLE;
    public static boolean CUSTOM_SKILLS_LOAD;
    public static boolean CUSTOM_ITEMS_LOAD;
    public static boolean CUSTOM_MULTISELL_LOAD;
    public static boolean CUSTOM_BUYLIST_LOAD;
    public static int ALT_BIRTHDAY_GIFT;
    public static String ALT_BIRTHDAY_MAIL_SUBJECT;
    public static String ALT_BIRTHDAY_MAIL_TEXT;
    public static boolean ENABLE_BLOCK_CHECKER_EVENT;
    public static int MIN_BLOCK_CHECKER_TEAM_MEMBERS;
    public static boolean HBCE_FAIR_PLAY;
    public static int PLAYER_MOVEMENT_BLOCK_TIME;
    public static int ABILITY_MAX_POINTS;
    public static long ABILITY_POINTS_RESET_ADENA;
    public static boolean BOTREPORT_ENABLE;
    public static String[] BOTREPORT_RESETPOINT_HOUR;
    public static long BOTREPORT_REPORT_DELAY;
    public static boolean BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS;

    // --------------------------------------------------
    // FloodProtector Settings
    // --------------------------------------------------
    public static FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON;
    public static FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SUBCLASS;
    public static FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL;
    public static FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MANUFACTURE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MANOR;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SENDMAIL;
    public static FloodProtectorConfig FLOOD_PROTECTOR_CHARACTER_SELECT;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_AUCTION;

    // --------------------------------------------------
    // NPC Settings
    // --------------------------------------------------
    public static boolean ANNOUNCE_MAMMON_SPAWN;
    public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
    public static boolean ALT_ATTACKABLE_NPCS;
    public static boolean ALT_GAME_VIEWNPC;
    public static int MAX_DRIFT_RANGE;
    public static boolean SHOW_NPC_LVL;
    public static boolean SHOW_CREST_WITHOUT_QUEST;
    public static boolean ENABLE_RANDOM_ENCHANT_EFFECT;
    public static int MIN_NPC_LVL_DMG_PENALTY;
    public static Map<Integer, Float> NPC_DMG_PENALTY;
    public static Map<Integer, Float> NPC_CRIT_DMG_PENALTY;
    public static Map<Integer, Float> NPC_SKILL_DMG_PENALTY;
    public static int MIN_NPC_LVL_MAGIC_PENALTY;
    public static Map<Integer, Float> NPC_SKILL_CHANCE_PENALTY;
    public static int DEFAULT_CORPSE_TIME;
    public static int SPOILED_CORPSE_EXTEND_TIME;
    public static int CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY;
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
    public static boolean ANNOUNCE_GAINAK_SIEGE;

    // --------------------------------------------------
    // Rate Settings
    // --------------------------------------------------
    public static float RATE_XP;
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
    // Server Settings
    // --------------------------------------------------
    public static int PORT_GAME;
    public static int PORT_LOGIN;
    public static String LOGIN_BIND_ADDRESS;
    public static int LOGIN_TRY_BEFORE_BAN;
    public static int LOGIN_BLOCK_AFTER_BAN;
    public static String GAMESERVER_HOSTNAME;
    public static String DATABASE_DRIVER;
    public static String DATABASE_URL;
    public static String DATABASE_LOGIN;
    public static String DATABASE_PASSWORD;
    public static int DATABASE_MAX_CONNECTIONS;
    public static int DATABASE_MAX_IDLE_TIME;
    public static boolean BACKUP_DATABASE;
    public static String MYSQL_BIN_PATH;
    public static String BACKUP_PATH;
    public static int BACKUP_DAYS;
    public static int MAXIMUM_ONLINE_USERS;
    public static boolean HARDWARE_INFO_ENABLED;
    public static int MAX_PLAYERS_PER_HWID;
    public static Pattern CHARNAME_TEMPLATE_PATTERN;
    public static String PET_NAME_TEMPLATE;
    public static String CLAN_NAME_TEMPLATE;
    public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
    public static File DATAPACK_ROOT;
    public static boolean ACCEPT_ALTERNATE_ID;
    public static int REQUEST_ID;
    public static boolean RESERVE_HOST_ON_LOGIN = false;
    public static List<Integer> PROTOCOL_LIST;
    public static int SERVER_LIST_TYPE;
    public static int SERVER_LIST_AGE;
    public static boolean SERVER_LIST_BRACKET;
    public static boolean LOGIN_SERVER_SCHEDULE_RESTART;
    public static long LOGIN_SERVER_SCHEDULE_RESTART_TIME;
    public static boolean SERVER_RESTART_SCHEDULE_ENABLED;
    public static boolean SERVER_RESTART_SCHEDULE_MESSAGE;
    public static int SERVER_RESTART_SCHEDULE_COUNTDOWN;
    public static String[] SERVER_RESTART_SCHEDULE;

    // --------------------------------------------------
    // Vitality Settings
    // --------------------------------------------------
    public static boolean ENABLE_VITALITY;
    public static int STARTING_VITALITY_POINTS;
    public static boolean RAIDBOSS_USE_VITALITY;
    public static float RATE_VITALITY_EXP_MULTIPLIER;
    public static int VITALITY_MAX_ITEMS_ALLOWED;
    public static float RATE_VITALITY_LOST;
    public static float RATE_VITALITY_GAIN;

    // --------------------------------------------------
    // No classification assigned to the following yet
    // --------------------------------------------------
    public static int MAX_ITEM_IN_PACKET;
    public static int GAME_SERVER_LOGIN_PORT;
    public static String GAME_SERVER_LOGIN_HOST;
    public static List<String> GAME_SERVER_SUBNETS;
    public static List<String> GAME_SERVER_HOSTS;
    public static int PVP_NORMAL_TIME;
    public static int PVP_PVP_TIME;
    public static int MAX_REPUTATION;
    public static int REPUTATION_INCREASE;
    public static IdFactoryType IDFACTORY_TYPE;
    public static boolean BAD_ID_CHECKING;
    public static double ENCHANT_CHANCE_ELEMENT_STONE;
    public static double ENCHANT_CHANCE_ELEMENT_CRYSTAL;
    public static double ENCHANT_CHANCE_ELEMENT_JEWEL;
    public static double ENCHANT_CHANCE_ELEMENT_ENERGY;
    public static int[] ENCHANT_BLACKLIST;
    public static boolean DISABLE_OVER_ENCHANTING;
    public static int[] AUGMENTATION_BLACKLIST;
    public static boolean ALT_ALLOW_AUGMENT_PVP_ITEMS;
    public static double HP_REGEN_MULTIPLIER;
    public static double MP_REGEN_MULTIPLIER;
    public static double CP_REGEN_MULTIPLIER;
    public static boolean TELNET_ENABLED;
    public static String TELNET_PASSWORD;
    public static String TELNET_HOSTNAME;
    public static List<String> TELNET_HOSTS;
    public static int TELNET_PORT;
    public static boolean TRAINING_CAMP_ENABLE;
    public static boolean TRAINING_CAMP_PREMIUM_ONLY;
    public static int TRAINING_CAMP_MAX_DURATION;
    public static int TRAINING_CAMP_MIN_LEVEL;
    public static int TRAINING_CAMP_MAX_LEVEL;
    public static double TRAINING_CAMP_EXP_MULTIPLIER;
    public static double TRAINING_CAMP_SP_MULTIPLIER;
    public static boolean SHOW_LICENCE;
    public static boolean SHOW_PI_AGREEMENT;
    public static boolean ACCEPT_NEW_GAMESERVER;
    public static byte[] HEX_ID;
    public static boolean AUTO_CREATE_ACCOUNTS;
    public static boolean FLOOD_PROTECTION;
    public static int FAST_CONNECTION_LIMIT;
    public static int NORMAL_CONNECTION_TIME;
    public static int FAST_CONNECTION_TIME;
    public static int MAX_CONNECTION_PER_IP;
    // Antharas
    public static int ANTHARAS_WAIT_TIME;

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
    // Gracia Seeds Settings
    public static int SOD_TIAT_KILL_COUNT;
    public static long SOD_STAGE_2_LENGTH;
    // chatfilter
    public static List<String> FILTER_LIST;
    /**
     * Geodata
     */
    public static String GEODATA_PATH;

    // --------------------------------------------------
    // GeoEngine
    // --------------------------------------------------
    public static int COORD_SYNCHRONIZE;
    /**
     * Path checking
     */
    public static int PART_OF_CHARACTER_HEIGHT;
    public static int MAX_OBSTACLE_HEIGHT;
    /**
     * Path finding
     */
    public static boolean PATHFINDING;
    public static String PATHFIND_BUFFERS;
    public static int BASE_WEIGHT;
    public static int DIAGONAL_WEIGHT;
    public static int HEURISTIC_WEIGHT;
    public static int OBSTACLE_MULTIPLIER;
    public static int MAX_ITERATIONS;
    // --------------------------------------------------
    // Custom Settings
    // --------------------------------------------------
    public static boolean CHAMPION_ENABLE;
    public static boolean CHAMPION_PASSIVE;
    public static int CHAMPION_FREQUENCY;
    public static String CHAMP_TITLE;
    public static boolean SHOW_CHAMPION_AURA;
    public static int CHAMP_MIN_LVL;
    public static int CHAMP_MAX_LVL;
    public static int CHAMPION_HP;
    public static float CHAMPION_REWARDS_EXP_SP;
    public static float CHAMPION_REWARDS_CHANCE;
    public static float CHAMPION_REWARDS_AMOUNT;
    public static float CHAMPION_ADENAS_REWARDS_CHANCE;
    public static float CHAMPION_ADENAS_REWARDS_AMOUNT;
    public static float CHAMPION_HP_REGEN;
    public static float CHAMPION_ATK;
    public static float CHAMPION_SPD_ATK;
    public static int CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE;
    public static int CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE;
    public static int CHAMPION_REWARD_ID;
    public static int CHAMPION_REWARD_QTY;
    public static boolean CHAMPION_ENABLE_VITALITY;
    public static boolean CHAMPION_ENABLE_IN_INSTANCES;
    public static boolean BANKING_SYSTEM_ENABLED;
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
    public static double MONSTER_CLAN_HELP_RANGE_MULTIPLIER;
    public static double RAIDBOSS_HP_MULTIPLIER;
    public static double RAIDBOSS_MP_MULTIPLIER;
    public static double RAIDBOSS_PATK_MULTIPLIER;
    public static double RAIDBOSS_MATK_MULTIPLIER;
    public static double RAIDBOSS_PDEF_MULTIPLIER;
    public static double RAIDBOSS_MDEF_MULTIPLIER;
    public static double RAIDBOSS_AGRRO_RANGE_MULTIPLIER;
    public static double RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER;
    public static double GUARD_HP_MULTIPLIER;
    public static double GUARD_MP_MULTIPLIER;
    public static double GUARD_PATK_MULTIPLIER;
    public static double GUARD_MATK_MULTIPLIER;
    public static double GUARD_PDEF_MULTIPLIER;
    public static double GUARD_MDEF_MULTIPLIER;
    public static double GUARD_AGRRO_RANGE_MULTIPLIER;
    public static double GUARD_CLAN_HELP_RANGE_MULTIPLIER;
    public static double DEFENDER_HP_MULTIPLIER;
    public static double DEFENDER_MP_MULTIPLIER;
    public static double DEFENDER_PATK_MULTIPLIER;
    public static double DEFENDER_MATK_MULTIPLIER;
    public static double DEFENDER_PDEF_MULTIPLIER;
    public static double DEFENDER_MDEF_MULTIPLIER;
    public static double DEFENDER_AGRRO_RANGE_MULTIPLIER;
    public static double DEFENDER_CLAN_HELP_RANGE_MULTIPLIER;
    public static boolean OFFLINE_TRADE_ENABLE;
    public static boolean OFFLINE_CRAFT_ENABLE;
    public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
    public static boolean OFFLINE_MODE_NO_DAMAGE;
    public static boolean RESTORE_OFFLINERS;
    public static int OFFLINE_MAX_DAYS;
    public static boolean OFFLINE_DISCONNECT_FINISHED;
    public static boolean OFFLINE_SET_NAME_COLOR;
    public static int OFFLINE_NAME_COLOR;
    public static boolean OFFLINE_FAME;
    public static boolean STORE_OFFLINE_TRADE_IN_REALTIME;
    public static boolean DISPLAY_SERVER_TIME;
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
    public static boolean CHAT_ADMIN;
    public static boolean MULTILANG_ENABLE;
    public static List<String> MULTILANG_ALLOWED = new ArrayList<>();
    public static String MULTILANG_DEFAULT;
    public static boolean MULTILANG_VOICED_ALLOW;
    public static boolean L2WALKER_PROTECTION;
    public static int DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
    public static int DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
    public static int DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
    public static boolean DUALBOX_COUNT_OFFLINE_TRADERS;
    public static Map<Integer, Integer> DUALBOX_CHECK_WHITELIST;
    public static boolean ALLOW_CHANGE_PASSWORD;
    public static boolean ALLOW_HUMAN;
    public static boolean ALLOW_ELF;
    public static boolean ALLOW_DARKELF;
    public static boolean ALLOW_ORC;
    public static boolean ALLOW_DWARF;
    public static boolean ALLOW_KAMAEL;
    public static boolean ALLOW_ERTHEIA;
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
    public static boolean CUSTOM_STARTING_LOC;
    public static int CUSTOM_STARTING_LOC_X;
    public static int CUSTOM_STARTING_LOC_Y;
    public static int CUSTOM_STARTING_LOC_Z;
    public static int SHOP_MIN_RANGE_FROM_NPC;
    public static int SHOP_MIN_RANGE_FROM_PLAYER;
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
    public static int COMMUNITYBOARD_TELEPORT_PRICE;
    public static int COMMUNITYBOARD_BUFF_PRICE;
    public static int COMMUNITYBOARD_HEAL_PRICE;
    public static boolean COMMUNITYBOARD_COMBAT_DISABLED;
    public static boolean COMMUNITYBOARD_KARMA_DISABLED;
    public static boolean COMMUNITYBOARD_CAST_ANIMATIONS;
    public static boolean COMMUNITY_PREMIUM_SYSTEM_ENABLED;
    public static int COMMUNITY_PREMIUM_COIN_ID;
    public static int COMMUNITY_PREMIUM_PRICE_PER_DAY;
    public static List<Integer> COMMUNITY_AVAILABLE_BUFFS;
    public static Map<String, Location> COMMUNITY_AVAILABLE_TELEPORTS;


    public static boolean ENABLE_FIND_PVP;
    public static boolean PREMIUM_SYSTEM_ENABLED;
    public static float PREMIUM_RATE_XP;
    public static float PREMIUM_RATE_SP;
    public static Map<Integer, Float> PREMIUM_RATE_DROP_ITEMS_ID;
    public static float PREMIUM_RATE_DROP_CHANCE;
    public static float PREMIUM_RATE_DROP_AMOUNT;
    public static float PREMIUM_RATE_SPOIL_CHANCE;
    public static float PREMIUM_RATE_SPOIL_AMOUNT;
    public static Map<Integer, Float> PREMIUM_RATE_DROP_CHANCE_BY_ID;
    public static Map<Integer, Float> PREMIUM_RATE_DROP_AMOUNT_BY_ID;
    public static boolean PC_CAFE_ENABLED;
    public static boolean PC_CAFE_ONLY_PREMIUM;
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
    ;

    /**
     * This class initializes all global variables for configuration.<br>
     * If the key doesn't appear in properties file, a default value is set by this class. {@link #SERVER_CONFIG_FILE} (properties file) for configuring your server.
     */
    public static void load() {
        FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
        FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
        FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
        FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
        FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
        FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
        FLOOD_PROTECTOR_SUBCLASS = new FloodProtectorConfig("SubclassFloodProtector");
        FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
        FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
        FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
        FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
        FLOOD_PROTECTOR_MANUFACTURE = new FloodProtectorConfig("ManufactureFloodProtector");
        FLOOD_PROTECTOR_MANOR = new FloodProtectorConfig("ManorFloodProtector");
        FLOOD_PROTECTOR_SENDMAIL = new FloodProtectorConfig("SendMailFloodProtector");
        FLOOD_PROTECTOR_CHARACTER_SELECT = new FloodProtectorConfig("CharacterSelectFloodProtector");
        FLOOD_PROTECTOR_ITEM_AUCTION = new FloodProtectorConfig("ItemAuctionFloodProtector");

        final PropertiesParser serverSettings = new PropertiesParser(SERVER_CONFIG_FILE);

        GAMESERVER_HOSTNAME = serverSettings.getString("GameserverHostname", "0.0.0.0");
        PORT_GAME = serverSettings.getInt("GameserverPort", 7777);

        GAME_SERVER_LOGIN_PORT = serverSettings.getInt("LoginPort", 9014);
        GAME_SERVER_LOGIN_HOST = serverSettings.getString("LoginHost", "127.0.0.1");

        REQUEST_ID = serverSettings.getInt("RequestServerID", 0);
        ACCEPT_ALTERNATE_ID = serverSettings.getBoolean("AcceptAlternateID", true);

        try {
            DATAPACK_ROOT = new File(serverSettings.getString("DatapackRoot", ".").replaceAll("\\\\", "/")).getCanonicalFile();
        } catch (IOException e) {
            LOGGER.warn("Error setting datapack root!", e);
            DATAPACK_ROOT = new File(".");
        }

        Pattern charNamePattern;

        try {
            charNamePattern = Pattern.compile(serverSettings.getString("CnameTemplate", ".*"));
        } catch (PatternSyntaxException e) {
            LOGGER.warn("Character name pattern is invalid!", e);
            charNamePattern = Pattern.compile(".*");
        }

        CHARNAME_TEMPLATE_PATTERN = charNamePattern;

        PET_NAME_TEMPLATE = serverSettings.getString("PetNameTemplate", ".*");
        CLAN_NAME_TEMPLATE = serverSettings.getString("ClanNameTemplate", ".*");

        MAX_CHARACTERS_NUMBER_PER_ACCOUNT = serverSettings.getInt("CharMaxNumber", 7);
        MAXIMUM_ONLINE_USERS = serverSettings.getInt("MaximumOnlineUsers", 100);

        HARDWARE_INFO_ENABLED = serverSettings.getBoolean("EnableHardwareInfo", false);
        MAX_PLAYERS_PER_HWID = serverSettings.getInt("MaxPlayersPerHWID", 0);

        final String[] protocols = serverSettings.getString("AllowedProtocolRevisions", "603;606;607").split(";");
        PROTOCOL_LIST = new ArrayList<>(protocols.length);
        for (String protocol : protocols) {
            try {
                PROTOCOL_LIST.add(Integer.parseInt(protocol.trim()));
            } catch (NumberFormatException e) {
                LOGGER.warn("Wrong config protocol version: " + protocol + ". Skipped.");
            }
        }
        SERVER_LIST_TYPE = getServerTypeId(serverSettings.getString("ServerListType", "Free").split(","));
        SERVER_LIST_AGE = serverSettings.getInt("ServerListAge", 0);
        SERVER_LIST_BRACKET = serverSettings.getBoolean("ServerListBrackets", false);

        SCHEDULED_THREAD_POOL_COUNT = serverSettings.getInt("ScheduledThreadPoolCount", -1);
        THREADS_PER_SCHEDULED_THREAD_POOL = serverSettings.getInt("ThreadsPerScheduledThreadPool", 4);
        INSTANT_THREAD_POOL_COUNT = serverSettings.getInt("InstantThreadPoolCount", -1);
        THREADS_PER_INSTANT_THREAD_POOL = serverSettings.getInt("ThreadsPerInstantThreadPool", 2);
        IO_PACKET_THREAD_CORE_SIZE = serverSettings.getInt("UrgentPacketThreadCoreSize", 2);

        DEADLOCK_DETECTOR = serverSettings.getBoolean("DeadLockDetector", true);
        DEADLOCK_CHECK_INTERVAL = serverSettings.getInt("DeadLockCheckInterval", 20);
        RESTART_ON_DEADLOCK = serverSettings.getBoolean("RestartOnDeadlock", false);

        SERVER_RESTART_SCHEDULE_ENABLED = serverSettings.getBoolean("ServerRestartScheduleEnabled", false);
        SERVER_RESTART_SCHEDULE_MESSAGE = serverSettings.getBoolean("ServerRestartScheduleMessage", false);
        SERVER_RESTART_SCHEDULE_COUNTDOWN = serverSettings.getInt("ServerRestartScheduleCountdown", 600);
        SERVER_RESTART_SCHEDULE = serverSettings.getString("ServerRestartSchedule", "08:00").split(",");

        // Hosts and Subnets
        final IPConfigData ipcd = new IPConfigData();
        GAME_SERVER_SUBNETS = ipcd.getSubnets();
        GAME_SERVER_HOSTS = ipcd.getHosts();

        // Load Feature config file (if exists)
        final PropertiesParser Feature = new PropertiesParser(FEATURE_CONFIG_FILE);
        SIEGE_HOUR_LIST = new ArrayList<>();
        for (String hour : Feature.getString("SiegeHourList", "").split(",")) {
            if (Util.isDigit(hour)) {
                SIEGE_HOUR_LIST.add(Integer.parseInt(hour));
            }
        }
        CASTLE_BUY_TAX_NEUTRAL = Feature.getInt("BuyTaxForNeutralSide", 15);
        CASTLE_BUY_TAX_LIGHT = Feature.getInt("BuyTaxForLightSide", 0);
        CASTLE_BUY_TAX_DARK = Feature.getInt("BuyTaxForDarkSide", 30);
        CASTLE_SELL_TAX_NEUTRAL = Feature.getInt("SellTaxForNeutralSide", 0);
        CASTLE_SELL_TAX_LIGHT = Feature.getInt("SellTaxForLightSide", 0);
        CASTLE_SELL_TAX_DARK = Feature.getInt("SellTaxForDarkSide", 20);

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

        FS_TELE_FEE_RATIO = Feature.getLong("FortressTeleportFunctionFeeRatio", 604800000);
        FS_TELE1_FEE = Feature.getInt("FortressTeleportFunctionFeeLvl1", 1000);
        FS_TELE2_FEE = Feature.getInt("FortressTeleportFunctionFeeLvl2", 10000);
        FS_SUPPORT_FEE_RATIO = Feature.getLong("FortressSupportFunctionFeeRatio", 86400000);
        FS_SUPPORT1_FEE = Feature.getInt("FortressSupportFeeLvl1", 7000);
        FS_SUPPORT2_FEE = Feature.getInt("FortressSupportFeeLvl2", 17000);
        FS_MPREG_FEE_RATIO = Feature.getLong("FortressMpRegenerationFunctionFeeRatio", 86400000);
        FS_MPREG1_FEE = Feature.getInt("FortressMpRegenerationFeeLvl1", 6500);
        FS_MPREG2_FEE = Feature.getInt("FortressMpRegenerationFeeLvl2", 9300);
        FS_HPREG_FEE_RATIO = Feature.getLong("FortressHpRegenerationFunctionFeeRatio", 86400000);
        FS_HPREG1_FEE = Feature.getInt("FortressHpRegenerationFeeLvl1", 2000);
        FS_HPREG2_FEE = Feature.getInt("FortressHpRegenerationFeeLvl2", 3500);
        FS_EXPREG_FEE_RATIO = Feature.getLong("FortressExpRegenerationFunctionFeeRatio", 86400000);
        FS_EXPREG1_FEE = Feature.getInt("FortressExpRegenerationFeeLvl1", 9000);
        FS_EXPREG2_FEE = Feature.getInt("FortressExpRegenerationFeeLvl2", 10000);
        FS_UPDATE_FRQ = Feature.getInt("FortressPeriodicUpdateFrequency", 360);
        FS_BLOOD_OATH_COUNT = Feature.getInt("FortressBloodOathCount", 1);
        FS_MAX_SUPPLY_LEVEL = Feature.getInt("FortressMaxSupplyLevel", 6);
        FS_FEE_FOR_CASTLE = Feature.getInt("FortressFeeForCastle", 25000);
        FS_MAX_OWN_TIME = Feature.getInt("FortressMaximumOwnTime", 168);

        TAKE_FORT_POINTS = Feature.getInt("TakeFortPoints", 200);
        LOOSE_FORT_POINTS = Feature.getInt("LooseFortPoints", 0);
        TAKE_CASTLE_POINTS = Feature.getInt("TakeCastlePoints", 1500);
        LOOSE_CASTLE_POINTS = Feature.getInt("LooseCastlePoints", 3000);
        CASTLE_DEFENDED_POINTS = Feature.getInt("CastleDefendedPoints", 750);
        FESTIVAL_WIN_POINTS = Feature.getInt("FestivalOfDarknessWin", 200);
        HERO_POINTS = Feature.getInt("HeroPoints", 1000);
        ROYAL_GUARD_COST = Feature.getInt("CreateRoyalGuardCost", 5000);
        KNIGHT_UNIT_COST = Feature.getInt("CreateKnightUnitCost", 10000);
        KNIGHT_REINFORCE_COST = Feature.getInt("ReinforceKnightUnitCost", 5000);
        BALLISTA_POINTS = Feature.getInt("KillBallistaPoints", 500);
        BLOODALLIANCE_POINTS = Feature.getInt("BloodAlliancePoints", 500);
        BLOODOATH_POINTS = Feature.getInt("BloodOathPoints", 200);
        KNIGHTSEPAULETTE_POINTS = Feature.getInt("KnightsEpaulettePoints", 20);
        REPUTATION_SCORE_PER_KILL = Feature.getInt("ReputationScorePerKill", 1);
        JOIN_ACADEMY_MIN_REP_SCORE = Feature.getInt("CompleteAcademyMinPoints", 190);
        JOIN_ACADEMY_MAX_REP_SCORE = Feature.getInt("CompleteAcademyMaxPoints", 650);
        CLAN_LEVEL_6_COST = Feature.getInt("ClanLevel6Cost", 5000);
        CLAN_LEVEL_7_COST = Feature.getInt("ClanLevel7Cost", 10000);
        CLAN_LEVEL_8_COST = Feature.getInt("ClanLevel8Cost", 20000);
        CLAN_LEVEL_9_COST = Feature.getInt("ClanLevel9Cost", 40000);
        CLAN_LEVEL_10_COST = Feature.getInt("ClanLevel10Cost", 40000);
        CLAN_LEVEL_11_COST = Feature.getInt("ClanLevel11Cost", 75000);
        CLAN_LEVEL_6_REQUIREMENT = Feature.getInt("ClanLevel6Requirement", 30);
        CLAN_LEVEL_7_REQUIREMENT = Feature.getInt("ClanLevel7Requirement", 50);
        CLAN_LEVEL_8_REQUIREMENT = Feature.getInt("ClanLevel8Requirement", 80);
        CLAN_LEVEL_9_REQUIREMENT = Feature.getInt("ClanLevel9Requirement", 120);
        CLAN_LEVEL_10_REQUIREMENT = Feature.getInt("ClanLevel10Requirement", 140);
        CLAN_LEVEL_11_REQUIREMENT = Feature.getInt("ClanLevel11Requirement", 170);
        ALLOW_WYVERN_ALWAYS = Feature.getBoolean("AllowRideWyvernAlways", false);
        ALLOW_WYVERN_DURING_SIEGE = Feature.getBoolean("AllowRideWyvernDuringSiege", true);
        ALLOW_MOUNTS_DURING_SIEGE = Feature.getBoolean("AllowRideMountsDuringSiege", false);

        // Load Attandance config file (if exists)
        final PropertiesParser Attandance = new PropertiesParser(ATTENDANCE_CONFIG_FILE);
        ENABLE_ATTENDANCE_REWARDS = Attandance.getBoolean("EnableAttendanceRewards", false);
        PREMIUM_ONLY_ATTENDANCE_REWARDS = Attandance.getBoolean("PremiumOnlyAttendanceRewards", false);
        ATTENDANCE_REWARDS_SHARE_ACCOUNT = Attandance.getBoolean("AttendanceRewardsShareAccount", false);
        ATTENDANCE_REWARD_DELAY = Attandance.getInt("AttendanceRewardDelay", 30);
        ATTENDANCE_POPUP_WINDOW = Attandance.getBoolean("AttendancePopupWindow", false);

        // Load Character config file (if exists)
        final PropertiesParser Character = new PropertiesParser(CHARACTER_CONFIG_FILE);

        PLAYER_DELEVEL = Character.getBoolean("Delevel", true);
        DELEVEL_MINIMUM = Character.getInt("DelevelMinimum", 85);
        DECREASE_SKILL_LEVEL = Character.getBoolean("DecreaseSkillOnDelevel", true);
        ALT_WEIGHT_LIMIT = Character.getDouble("AltWeightLimit", 1);
        RUN_SPD_BOOST = Character.getInt("RunSpeedBoost", 0);
        RESPAWN_RESTORE_CP = Character.getDouble("RespawnRestoreCP", 0) / 100;
        RESPAWN_RESTORE_HP = Character.getDouble("RespawnRestoreHP", 65) / 100;
        RESPAWN_RESTORE_MP = Character.getDouble("RespawnRestoreMP", 0) / 100;
        HP_REGEN_MULTIPLIER = Character.getDouble("HpRegenMultiplier", 100) / 100;
        MP_REGEN_MULTIPLIER = Character.getDouble("MpRegenMultiplier", 100) / 100;
        CP_REGEN_MULTIPLIER = Character.getDouble("CpRegenMultiplier", 100) / 100;
        ENABLE_MODIFY_SKILL_DURATION = Character.getBoolean("EnableModifySkillDuration", false);

        // Create Map only if enabled
        if (ENABLE_MODIFY_SKILL_DURATION) {
            final String[] propertySplit = Character.getString("SkillDurationList", "").split(";");
            SKILL_DURATION_LIST = new HashMap<>(propertySplit.length);
            for (String skill : propertySplit) {
                final String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2) {
                    LOGGER.warn("[SkillDurationList]: invalid config property -> SkillDurationList " + skill);
                } else {
                    try {
                        SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!skill.isEmpty()) {
                            LOGGER.warn(StringUtil.concat("[SkillDurationList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
                        }
                    }
                }
            }
        }
        ENABLE_MODIFY_SKILL_REUSE = Character.getBoolean("EnableModifySkillReuse", false);
        // Create Map only if enabled
        if (ENABLE_MODIFY_SKILL_REUSE) {
            final String[] propertySplit = Character.getString("SkillReuseList", "").split(";");
            SKILL_REUSE_LIST = new HashMap<>(propertySplit.length);
            for (String skill : propertySplit) {
                final String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2) {
                    LOGGER.warn(StringUtil.concat("[SkillReuseList]: invalid config property -> SkillReuseList \"", skill, "\""));
                } else {
                    try {
                        SKILL_REUSE_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!skill.isEmpty()) {
                            LOGGER.warn(StringUtil.concat("[SkillReuseList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
                        }
                    }
                }
            }
        }

        AUTO_LEARN_SKILLS = Character.getBoolean("AutoLearnSkills", false);
        AUTO_LEARN_FS_SKILLS = Character.getBoolean("AutoLearnForgottenScrollSkills", false);
        AUTO_LOOT_HERBS = Character.getBoolean("AutoLootHerbs", false);
        BUFFS_MAX_AMOUNT = Character.getByte("MaxBuffAmount", (byte) 20);
        TRIGGERED_BUFFS_MAX_AMOUNT = Character.getByte("MaxTriggeredBuffAmount", (byte) 12);
        DANCES_MAX_AMOUNT = Character.getByte("MaxDanceAmount", (byte) 12);
        DANCE_CANCEL_BUFF = Character.getBoolean("DanceCancelBuff", false);
        DANCE_CONSUME_ADDITIONAL_MP = Character.getBoolean("DanceConsumeAdditionalMP", true);
        ALT_STORE_DANCES = Character.getBoolean("AltStoreDances", false);
        AUTO_LEARN_DIVINE_INSPIRATION = Character.getBoolean("AutoLearnDivineInspiration", false);
        ALT_GAME_CANCEL_BOW = Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
        ALT_GAME_CANCEL_CAST = Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
        ALT_GAME_MAGICFAILURES = Character.getBoolean("MagicFailures", true);
        ALT_GAME_STUN_BREAK = Character.getBoolean("BreakStun", false);
        PLAYER_FAKEDEATH_UP_PROTECTION = Character.getInt("PlayerFakeDeathUpProtection", 0);
        STORE_SKILL_COOLTIME = Character.getBoolean("StoreSkillCooltime", true);
        SUBCLASS_STORE_SKILL_COOLTIME = Character.getBoolean("SubclassStoreSkillCooltime", false);
        SUMMON_STORE_SKILL_COOLTIME = Character.getBoolean("SummonStoreSkillCooltime", true);
        EFFECT_TICK_RATIO = Character.getLong("EffectTickRatio", 666);
        LIFE_CRYSTAL_NEEDED = Character.getBoolean("LifeCrystalNeeded", true);
        DIVINE_SP_BOOK_NEEDED = Character.getBoolean("DivineInspirationSpBookNeeded", true);
        ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Character.getBoolean("AltSubClassWithoutQuests", false);
        ALT_GAME_SUBCLASS_EVERYWHERE = Character.getBoolean("AltSubclassEverywhere", false);
        RESTORE_SERVITOR_ON_RECONNECT = Character.getBoolean("RestoreServitorOnReconnect", true);
        RESTORE_PET_ON_RECONNECT = Character.getBoolean("RestorePetOnReconnect", true);
        ALLOW_TRANSFORM_WITHOUT_QUEST = Character.getBoolean("AltTransformationWithoutQuest", false);
        FEE_DELETE_TRANSFER_SKILLS = Character.getInt("FeeDeleteTransferSkills", 10000000);
        FEE_DELETE_SUBCLASS_SKILLS = Character.getInt("FeeDeleteSubClassSkills", 10000000);
        FEE_DELETE_DUALCLASS_SKILLS = Character.getInt("FeeDeleteDualClassSkills", 20000000);
        ENABLE_VITALITY = Character.getBoolean("EnableVitality", true);
        STARTING_VITALITY_POINTS = Character.getInt("StartingVitalityPoints", 140000);
        RAIDBOSS_USE_VITALITY = Character.getBoolean("RaidbossUseVitality", true);
        MAX_BONUS_EXP = Character.getDouble("MaxExpBonus", 0);
        MAX_BONUS_SP = Character.getDouble("MaxSpBonus", 0);
        MAX_RUN_SPEED = Character.getInt("MaxRunSpeed", 300);
        MAX_PATK = Character.getInt("MaxPAtk", 999999);
        MAX_MATK = Character.getInt("MaxMAtk", 999999);
        MAX_PCRIT_RATE = Character.getInt("MaxPCritRate", 500);
        MAX_MCRIT_RATE = Character.getInt("MaxMCritRate", 200);
        MAX_PATK_SPEED = Character.getInt("MaxPAtkSpeed", 1500);
        MAX_MATK_SPEED = Character.getInt("MaxMAtkSpeed", 1999);
        MAX_EVASION = Character.getInt("MaxEvasion", 250);
        MIN_ABNORMAL_STATE_SUCCESS_RATE = Character.getInt("MinAbnormalStateSuccessRate", 10);
        MAX_ABNORMAL_STATE_SUCCESS_RATE = Character.getInt("MaxAbnormalStateSuccessRate", 90);
        MAX_SP = Character.getLong("MaxSp", 50000000000L) >= 0 ? Character.getLong("MaxSp", 50000000000L) : Long.MAX_VALUE;
        MAX_SUBCLASS = (byte) Math.min(3, Character.getByte("MaxSubclass", (byte) 3));
        BASE_SUBCLASS_LEVEL = Character.getByte("BaseSubclassLevel", (byte) 40);
        BASE_DUALCLASS_LEVEL = Character.getByte("BaseDualclassLevel", (byte) 85);
        MAX_SUBCLASS_LEVEL = Character.getByte("MaxSubclassLevel", (byte) 80);
        MAX_PVTSTORESELL_SLOTS_DWARF = Character.getInt("MaxPvtStoreSellSlotsDwarf", 4);
        MAX_PVTSTORESELL_SLOTS_OTHER = Character.getInt("MaxPvtStoreSellSlotsOther", 3);
        MAX_PVTSTOREBUY_SLOTS_DWARF = Character.getInt("MaxPvtStoreBuySlotsDwarf", 5);
        MAX_PVTSTOREBUY_SLOTS_OTHER = Character.getInt("MaxPvtStoreBuySlotsOther", 4);
        INVENTORY_MAXIMUM_NO_DWARF = Character.getInt("MaximumSlotsForNoDwarf", 80);
        INVENTORY_MAXIMUM_DWARF = Character.getInt("MaximumSlotsForDwarf", 100);
        INVENTORY_MAXIMUM_GM = Character.getInt("MaximumSlotsForGMPlayer", 250);
        INVENTORY_MAXIMUM_QUEST_ITEMS = Character.getInt("MaximumSlotsForQuestItems", 100);
        MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
        WAREHOUSE_SLOTS_DWARF = Character.getInt("MaximumWarehouseSlotsForDwarf", 120);
        WAREHOUSE_SLOTS_NO_DWARF = Character.getInt("MaximumWarehouseSlotsForNoDwarf", 100);
        WAREHOUSE_SLOTS_CLAN = Character.getInt("MaximumWarehouseSlotsForClan", 150);
        ALT_FREIGHT_SLOTS = Character.getInt("MaximumFreightSlots", 200);
        ALT_FREIGHT_PRICE = Character.getInt("FreightPrice", 1000);
        MENTOR_PENALTY_FOR_MENTEE_COMPLETE = Character.getInt("MentorPenaltyForMenteeComplete", 1) * 24 * 60 * 60 * 1000;
        MENTOR_PENALTY_FOR_MENTEE_COMPLETE = Character.getInt("MentorPenaltyForMenteeLeave", 2) * 24 * 60 * 60 * 1000;
        ENCHANT_CHANCE_ELEMENT_STONE = Character.getDouble("EnchantChanceElementStone", 50);
        ENCHANT_CHANCE_ELEMENT_CRYSTAL = Character.getDouble("EnchantChanceElementCrystal", 30);
        ENCHANT_CHANCE_ELEMENT_JEWEL = Character.getDouble("EnchantChanceElementJewel", 20);
        ENCHANT_CHANCE_ELEMENT_ENERGY = Character.getDouble("EnchantChanceElementEnergy", 10);
        final String[] notenchantable = Character.getString("EnchantBlackList", "7816,7817,7818,7819,7820,7821,7822,7823,7824,7825,7826,7827,7828,7829,7830,7831,13293,13294,13296").split(",");
        ENCHANT_BLACKLIST = new int[notenchantable.length];
        for (int i = 0; i < notenchantable.length; i++) {
            ENCHANT_BLACKLIST[i] = Integer.parseInt(notenchantable[i]);
        }
        Arrays.sort(ENCHANT_BLACKLIST);
        DISABLE_OVER_ENCHANTING = Character.getBoolean("DisableOverEnchanting", true);
        String[] array = Character.getString("AugmentationBlackList", "6656,6657,6658,6659,6660,6661,6662,8191,10170,10314,13740,13741,13742,13743,13744,13745,13746,13747,13748,14592,14593,14594,14595,14596,14597,14598,14599,14600,14664,14665,14666,14667,14668,14669,14670,14671,14672,14801,14802,14803,14804,14805,14806,14807,14808,14809,15282,15283,15284,15285,15286,15287,15288,15289,15290,15291,15292,15293,15294,15295,15296,15297,15298,15299,16025,16026,21712,22173,22174,22175").split(",");
        AUGMENTATION_BLACKLIST = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            AUGMENTATION_BLACKLIST[i] = Integer.parseInt(array[i]);
        }

        Arrays.sort(AUGMENTATION_BLACKLIST);
        ALT_ALLOW_AUGMENT_PVP_ITEMS = Character.getBoolean("AltAllowAugmentPvPItems", false);
        ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Character.getBoolean("AltKarmaPlayerCanBeKilledInPeaceZone", false);
        ALT_GAME_KARMA_PLAYER_CAN_SHOP = Character.getBoolean("AltKarmaPlayerCanShop", true);
        ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Character.getBoolean("AltKarmaPlayerCanTeleport", true);
        ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Character.getBoolean("AltKarmaPlayerCanUseGK", false);
        ALT_GAME_KARMA_PLAYER_CAN_TRADE = Character.getBoolean("AltKarmaPlayerCanTrade", true);
        ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Character.getBoolean("AltKarmaPlayerCanUseWareHouse", true);
        MAX_PERSONAL_FAME_POINTS = Character.getInt("MaxPersonalFamePoints", 100000);
        FORTRESS_ZONE_FAME_TASK_FREQUENCY = Character.getInt("FortressZoneFameTaskFrequency", 300);
        FORTRESS_ZONE_FAME_AQUIRE_POINTS = Character.getInt("FortressZoneFameAquirePoints", 31);
        CASTLE_ZONE_FAME_TASK_FREQUENCY = Character.getInt("CastleZoneFameTaskFrequency", 300);
        CASTLE_ZONE_FAME_AQUIRE_POINTS = Character.getInt("CastleZoneFameAquirePoints", 125);
        FAME_FOR_DEAD_PLAYERS = Character.getBoolean("FameForDeadPlayers", true);
        IS_CRAFTING_ENABLED = Character.getBoolean("CraftingEnabled", true);
        CRAFT_MASTERWORK = Character.getBoolean("CraftMasterwork", true);
        DWARF_RECIPE_LIMIT = Character.getInt("DwarfRecipeLimit", 50);
        COMMON_RECIPE_LIMIT = Character.getInt("CommonRecipeLimit", 50);
        ALT_GAME_CREATION = Character.getBoolean("AltGameCreation", false);
        ALT_GAME_CREATION_SPEED = Character.getDouble("AltGameCreationSpeed", 1);
        ALT_GAME_CREATION_XP_RATE = Character.getDouble("AltGameCreationXpRate", 1);
        ALT_GAME_CREATION_SP_RATE = Character.getDouble("AltGameCreationSpRate", 1);
        ALT_GAME_CREATION_RARE_XPSP_RATE = Character.getDouble("AltGameCreationRareXpSpRate", 2);
        ALT_CLAN_LEADER_INSTANT_ACTIVATION = Character.getBoolean("AltClanLeaderInstantActivation", false);
        ALT_CLAN_JOIN_DAYS = Character.getInt("DaysBeforeJoinAClan", 1);
        ALT_CLAN_CREATE_DAYS = Character.getInt("DaysBeforeCreateAClan", 10);
        ALT_CLAN_DISSOLVE_DAYS = Character.getInt("DaysToPassToDissolveAClan", 7);
        ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = Character.getInt("DaysBeforeJoinAllyWhenLeaved", 1);
        ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = Character.getInt("DaysBeforeJoinAllyWhenDismissed", 1);
        ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = Character.getInt("DaysBeforeAcceptNewClanWhenDismissed", 1);
        ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = Character.getInt("DaysBeforeCreateNewAllyWhenDissolved", 1);
        ALT_MAX_NUM_OF_CLANS_IN_ALLY = Character.getInt("AltMaxNumOfClansInAlly", 3);
        ALT_CLAN_MEMBERS_FOR_WAR = Character.getInt("AltClanMembersForWar", 15);
        ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Character.getBoolean("AltMembersCanWithdrawFromClanWH", false);
        ALT_CLAN_MEMBERS_TIME_FOR_BONUS = Character.getDuration("AltClanMembersTimeForBonus", "30mins").toMillis();
        REMOVE_CASTLE_CIRCLETS = Character.getBoolean("RemoveCastleCirclets", true);
        ALT_PARTY_MAX_MEMBERS = Character.getInt("AltPartyMaxMembers", 7);
        ALT_PARTY_RANGE = Character.getInt("AltPartyRange", 1600);
        ALT_PARTY_RANGE2 = Character.getInt("AltPartyRange2", 1400);
        ALT_LEAVE_PARTY_LEADER = Character.getBoolean("AltLeavePartyLeader", false);
        INITIAL_EQUIPMENT_EVENT = Character.getBoolean("InitialEquipmentEvent", false);
        STARTING_ADENA = Character.getLong("StartingAdena", 0);
        STARTING_LEVEL = Character.getByte("StartingLevel", (byte) 1);
        STARTING_SP = Character.getInt("StartingSP", 0);
        MAX_ADENA = Character.getLong("MaxAdena", 99900000000L);
        if (MAX_ADENA < 0) {
            MAX_ADENA = Long.MAX_VALUE;
        }
        AUTO_LOOT = Character.getBoolean("AutoLoot", false);
        AUTO_LOOT_RAIDS = Character.getBoolean("AutoLootRaids", false);
        AUTO_LOOT_SLOT_LIMIT = Character.getBoolean("AutoLootSlotLimit", false);
        LOOT_RAIDS_PRIVILEGE_INTERVAL = Character.getInt("RaidLootRightsInterval", 900) * 1000;
        LOOT_RAIDS_PRIVILEGE_CC_SIZE = Character.getInt("RaidLootRightsCCSize", 45);
        final String[] autoLootItemIds = Character.getString("AutoLootItemIds", "0").split(",");
        AUTO_LOOT_ITEM_IDS = new ArrayList<>(autoLootItemIds.length);
        for (String item : autoLootItemIds) {
            Integer itm = 0;
            try {
                itm = Integer.parseInt(item);
            } catch (NumberFormatException nfe) {
                LOGGER.warn("Auto loot item ids: Wrong ItemId passed: " + item);
                LOGGER.warn(nfe.getMessage());
            }
            if (itm != 0) {
                AUTO_LOOT_ITEM_IDS.add(itm);
            }
        }
        ENABLE_KEYBOARD_MOVEMENT = Character.getBoolean("KeyboardMovement", true);
        UNSTUCK_INTERVAL = Character.getInt("UnstuckInterval", 300);
        TELEPORT_WATCHDOG_TIMEOUT = Character.getInt("TeleportWatchdogTimeout", 0);
        PLAYER_SPAWN_PROTECTION = Character.getInt("PlayerSpawnProtection", 0);
        PLAYER_TELEPORT_PROTECTION = Character.getInt("PlayerTeleportProtection", 0);
        RANDOM_RESPAWN_IN_TOWN_ENABLED = Character.getBoolean("RandomRespawnInTownEnabled", true);
        OFFSET_ON_TELEPORT_ENABLED = Character.getBoolean("OffsetOnTeleportEnabled", true);
        MAX_OFFSET_ON_TELEPORT = Character.getInt("MaxOffsetOnTeleport", 50);
        PETITIONING_ALLOWED = Character.getBoolean("PetitioningAllowed", true);
        MAX_PETITIONS_PER_PLAYER = Character.getInt("MaxPetitionsPerPlayer", 5);
        MAX_PETITIONS_PENDING = Character.getInt("MaxPetitionsPending", 25);
        MAX_FREE_TELEPORT_LEVEL = Character.getInt("MaxFreeTeleportLevel", 0);
        MAX_NEWBIE_BUFF_LEVEL = Character.getInt("MaxNewbieBuffLevel", 0);
        DELETE_DAYS = Character.getInt("DeleteCharAfterDays", 1);
        PARTY_XP_CUTOFF_METHOD = Character.getString("PartyXpCutoffMethod", "level").toLowerCase();
        PARTY_XP_CUTOFF_PERCENT = Character.getDouble("PartyXpCutoffPercent", 3);
        PARTY_XP_CUTOFF_LEVEL = Character.getInt("PartyXpCutoffLevel", 20);
        final String[] gaps = Character.getString("PartyXpCutoffGaps", "0,9;10,14;15,99").split(";");
        PARTY_XP_CUTOFF_GAPS = new int[gaps.length][2];
        for (int i = 0; i < gaps.length; i++) {
            PARTY_XP_CUTOFF_GAPS[i] = new int[]
                    {
                            Integer.parseInt(gaps[i].split(",")[0]),
                            Integer.parseInt(gaps[i].split(",")[1])
                    };
        }
        final String[] percents = Character.getString("PartyXpCutoffGapPercent", "100;30;0").split(";");
        PARTY_XP_CUTOFF_GAP_PERCENTS = new int[percents.length];
        for (int i = 0; i < percents.length; i++) {
            PARTY_XP_CUTOFF_GAP_PERCENTS[i] = Integer.parseInt(percents[i]);
        }
        DISABLE_TUTORIAL = Character.getBoolean("DisableTutorial", false);
        EXPERTISE_PENALTY = Character.getBoolean("ExpertisePenalty", true);
        STORE_RECIPE_SHOPLIST = Character.getBoolean("StoreRecipeShopList", false);
        STORE_UI_SETTINGS = Character.getBoolean("StoreCharUiSettings", true);
        FORBIDDEN_NAMES = Character.getString("ForbiddenNames", "").split(",");
        SILENCE_MODE_EXCLUDE = Character.getBoolean("SilenceModeExclude", false);
        SHOW_GOD_VIDEO_INTRO = Character.getBoolean("GoDVideoIntro", true);
        PLAYER_MOVEMENT_BLOCK_TIME = Character.getInt("NpcTalkBlockingTime", 0) * 1000;
        ABILITY_MAX_POINTS = Character.getInt("AbilityMaxPoints", 16);
        ABILITY_POINTS_RESET_ADENA = Character.getLong("AbilityPointsResetAdena", 10_000_000);

        // Load Training Camp config file (if exists)
        final PropertiesParser trainingCampSettings = new PropertiesParser(TRAINING_CAMP_CONFIG_FILE);

        TRAINING_CAMP_ENABLE = trainingCampSettings.getBoolean("TrainingCampEnable", false);
        TRAINING_CAMP_PREMIUM_ONLY = trainingCampSettings.getBoolean("TrainingCampPremiumOnly", false);
        TRAINING_CAMP_MAX_DURATION = trainingCampSettings.getInt("TrainingCampDuration", 18000);
        TRAINING_CAMP_MIN_LEVEL = trainingCampSettings.getInt("TrainingCampMinLevel", 18);
        TRAINING_CAMP_MAX_LEVEL = trainingCampSettings.getInt("TrainingCampMaxLevel", 127);
        TRAINING_CAMP_EXP_MULTIPLIER = trainingCampSettings.getDouble("TrainingCampExpMultiplier", 1.0);
        TRAINING_CAMP_SP_MULTIPLIER = trainingCampSettings.getDouble("TrainingCampSpMultiplier", 1.0);

        // Load IdFactory config file (if exists)
        final PropertiesParser IdFactory = new PropertiesParser(IDFACTORY_CONFIG_FILE);

        BAD_ID_CHECKING = IdFactory.getBoolean("BadIdChecking", true);

        // Load General config file (if exists)
        final PropertiesParser General = new PropertiesParser(GENERAL_CONFIG_FILE);
        DEFAULT_ACCESS_LEVEL = General.getInt("DefaultAccessLevel", 0);
        SERVER_GMONLY = General.getBoolean("ServerGMOnly", false);
        GM_HERO_AURA = General.getBoolean("GMHeroAura", false);
        GM_STARTUP_BUILDER_HIDE = General.getBoolean("GMStartupBuilderHide", false);
        GM_STARTUP_INVULNERABLE = General.getBoolean("GMStartupInvulnerable", false);
        GM_STARTUP_INVISIBLE = General.getBoolean("GMStartupInvisible", false);
        GM_STARTUP_SILENCE = General.getBoolean("GMStartupSilence", false);
        GM_STARTUP_AUTO_LIST = General.getBoolean("GMStartupAutoList", false);
        GM_STARTUP_DIET_MODE = General.getBoolean("GMStartupDietMode", false);
        GM_ITEM_RESTRICTION = General.getBoolean("GMItemRestriction", true);
        GM_SKILL_RESTRICTION = General.getBoolean("GMSkillRestriction", true);
        GM_TRADE_RESTRICTED_ITEMS = General.getBoolean("GMTradeRestrictedItems", false);
        GM_RESTART_FIGHTING = General.getBoolean("GMRestartFighting", true);
        GM_ANNOUNCER_NAME = General.getBoolean("GMShowAnnouncerName", false);
        GM_GIVE_SPECIAL_SKILLS = General.getBoolean("GMGiveSpecialSkills", false);
        GM_GIVE_SPECIAL_AURA_SKILLS = General.getBoolean("GMGiveSpecialAuraSkills", false);
        GM_DEBUG_HTML_PATHS = General.getBoolean("GMDebugHtmlPaths", true);
        USE_SUPER_HASTE_AS_GM_SPEED = General.getBoolean("UseSuperHasteAsGMSpeed", false);
        LOG_CHAT = General.getBoolean("LogChat", false);
        LOG_AUTO_ANNOUNCEMENTS = General.getBoolean("LogAutoAnnouncements", false);
        LOG_ITEMS = General.getBoolean("LogItems", false);
        LOG_ITEMS_SMALL_LOG = General.getBoolean("LogItemsSmallLog", false);
        LOG_ITEM_ENCHANTS = General.getBoolean("LogItemEnchants", false);
        LOG_SKILL_ENCHANTS = General.getBoolean("LogSkillEnchants", false);
        GMAUDIT = General.getBoolean("GMAudit", false);
        SKILL_CHECK_ENABLE = General.getBoolean("SkillCheckEnable", false);
        SKILL_CHECK_REMOVE = General.getBoolean("SkillCheckRemove", false);
        SKILL_CHECK_GM = General.getBoolean("SkillCheckGM", true);
        HTML_ACTION_CACHE_DEBUG = General.getBoolean("HtmlActionCacheDebug", false);
        DEVELOPER = General.getBoolean("Developer", false);
        ALT_DEV_NO_QUESTS = General.getBoolean("AltDevNoQuests", false) || Boolean.getBoolean("noquests");
        ALT_DEV_NO_SPAWNS = General.getBoolean("AltDevNoSpawns", false) || Boolean.getBoolean("nospawns");
        ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowQuestsLoadInLogs", false);
        ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowScriptsLoadInLogs", false);
        ALLOW_DISCARDITEM = General.getBoolean("AllowDiscardItem", true);
        AUTODESTROY_ITEM_AFTER = General.getInt("AutoDestroyDroppedItemAfter", 600);
        HERB_AUTO_DESTROY_TIME = General.getInt("AutoDestroyHerbTime", 60) * 1000;
        final String[] split = General.getString("ListOfProtectedItems", "0").split(",");
        LIST_PROTECTED_ITEMS = new ArrayList<>(split.length);
        for (String id : split) {
            LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
        }
        CHAR_DATA_STORE_INTERVAL = General.getInt("CharacterDataStoreInterval", 15) * 60 * 1000;
        CLAN_VARIABLES_STORE_INTERVAL = General.getInt("ClanVariablesStoreInterval", 15) * 60 * 1000;
        LAZY_ITEMS_UPDATE = General.getBoolean("LazyItemsUpdate", false);
        UPDATE_ITEMS_ON_CHAR_STORE = General.getBoolean("UpdateItemsOnCharStore", false);
        DESTROY_DROPPED_PLAYER_ITEM = General.getBoolean("DestroyPlayerDroppedItem", false);
        DESTROY_EQUIPABLE_PLAYER_ITEM = General.getBoolean("DestroyEquipableItem", false);
        DESTROY_ALL_ITEMS = General.getBoolean("DestroyAllItems", false);
        SAVE_DROPPED_ITEM = General.getBoolean("SaveDroppedItem", false);
        EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = General.getBoolean("EmptyDroppedItemTableAfterLoad", false);
        SAVE_DROPPED_ITEM_INTERVAL = General.getInt("SaveDroppedItemInterval", 60) * 60000;
        CLEAR_DROPPED_ITEM_TABLE = General.getBoolean("ClearDroppedItemTable", false);
        ORDER_QUEST_LIST_BY_QUESTID = General.getBoolean("OrderQuestListByQuestId", true);
        AUTODELETE_INVALID_QUEST_DATA = General.getBoolean("AutoDeleteInvalidQuestData", false);
        ENABLE_STORY_QUEST_BUFF_REWARD = General.getBoolean("StoryQuestRewardBuff", true);
        MULTIPLE_ITEM_DROP = General.getBoolean("MultipleItemDrop", true);
        FORCE_INVENTORY_UPDATE = General.getBoolean("ForceInventoryUpdate", false);
        LAZY_CACHE = General.getBoolean("LazyCache", true);
        CACHE_CHAR_NAMES = General.getBoolean("CacheCharNames", true);
        MIN_NPC_ANIMATION = General.getInt("MinNPCAnimation", 10);
        MAX_NPC_ANIMATION = General.getInt("MaxNPCAnimation", 20);
        MIN_MONSTER_ANIMATION = General.getInt("MinMonsterAnimation", 5);
        MAX_MONSTER_ANIMATION = General.getInt("MaxMonsterAnimation", 20);
        GRIDS_ALWAYS_ON = General.getBoolean("GridsAlwaysOn", false);
        GRID_NEIGHBOR_TURNON_TIME = General.getInt("GridNeighborTurnOnTime", 1);
        GRID_NEIGHBOR_TURNOFF_TIME = General.getInt("GridNeighborTurnOffTime", 90);
        PEACE_ZONE_MODE = General.getInt("PeaceZoneMode", 0);
        DEFAULT_GLOBAL_CHAT = General.getString("GlobalChat", "ON");
        DEFAULT_TRADE_CHAT = General.getString("TradeChat", "ON");
        ENABLE_WORLD_CHAT = General.getBoolean("WorldChatEnabled", false);
        MINIMUM_CHAT_LEVEL = General.getInt("MinimumChatLevel", 20);
        ALLOW_WAREHOUSE = General.getBoolean("AllowWarehouse", true);
        WAREHOUSE_CACHE = General.getBoolean("WarehouseCache", false);
        WAREHOUSE_CACHE_TIME = General.getInt("WarehouseCacheTime", 15);
        ALLOW_REFUND = General.getBoolean("AllowRefund", true);
        ALLOW_MAIL = General.getBoolean("AllowMail", true);
        ALLOW_ATTACHMENTS = General.getBoolean("AllowAttachments", true);
        ALLOW_WEAR = General.getBoolean("AllowWear", true);
        WEAR_DELAY = General.getInt("WearDelay", 5);
        WEAR_PRICE = General.getInt("WearPrice", 10);
        INSTANCE_FINISH_TIME = General.getInt("DefaultFinishTime", 5);
        RESTORE_PLAYER_INSTANCE = General.getBoolean("RestorePlayerInstance", false);
        EJECT_DEAD_PLAYER_TIME = General.getInt("EjectDeadPlayerTime", 1);
        ALLOW_WATER = General.getBoolean("AllowWater", true);
        ALLOW_FISHING = General.getBoolean("AllowFishing", true);
        ALLOW_MANOR = General.getBoolean("AllowManor", true);
        ALLOW_BOAT = General.getBoolean("AllowBoat", true);
        BOAT_BROADCAST_RADIUS = General.getInt("BoatBroadcastRadius", 20000);
        ALLOW_CURSED_WEAPONS = General.getBoolean("AllowCursedWeapons", true);
        SERVER_NEWS = General.getBoolean("ShowServerNews", false);
        ENABLE_COMMUNITY_BOARD = General.getBoolean("EnableCommunityBoard", true);
        BBS_DEFAULT = General.getString("BBSDefault", "_bbshome");
        USE_SAY_FILTER = General.getBoolean("UseChatFilter", false);
        CHAT_FILTER_CHARS = General.getString("ChatFilterChars", "^_^");
        final String[] propertySplit4 = General.getString("BanChatChannels", "GENERAL;SHOUT;WORLD;TRADE;HERO_VOICE").trim().split(";");
        BAN_CHAT_CHANNELS = new HashSet<>();
        try {
            for (String chatId : propertySplit4) {
                BAN_CHAT_CHANNELS.add(Enum.valueOf(ChatType.class, chatId));
            }
        } catch (NumberFormatException nfe) {
            LOGGER.warn("There was an error while parsing ban chat channels: ", nfe);
        }
        WORLD_CHAT_MIN_LEVEL = General.getInt("WorldChatMinLevel", 95);
        WORLD_CHAT_POINTS_PER_DAY = General.getInt("WorldChatPointsPerDay", 10);
        WORLD_CHAT_INTERVAL = General.getDuration("WorldChatInterval", "20secs", Duration.ofSeconds(20));
        ALT_MANOR_REFRESH_TIME = General.getInt("AltManorRefreshTime", 20);
        ALT_MANOR_REFRESH_MIN = General.getInt("AltManorRefreshMin", 0);
        ALT_MANOR_APPROVE_TIME = General.getInt("AltManorApproveTime", 4);
        ALT_MANOR_APPROVE_MIN = General.getInt("AltManorApproveMin", 30);
        ALT_MANOR_MAINTENANCE_MIN = General.getInt("AltManorMaintenanceMin", 6);
        ALT_MANOR_SAVE_ALL_ACTIONS = General.getBoolean("AltManorSaveAllActions", false);
        ALT_MANOR_SAVE_PERIOD_RATE = General.getInt("AltManorSavePeriodRate", 2);
        ALT_ITEM_AUCTION_ENABLED = General.getBoolean("AltItemAuctionEnabled", true);
        ALT_ITEM_AUCTION_EXPIRED_AFTER = General.getInt("AltItemAuctionExpiredAfter", 14);
        ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID = General.getInt("AltItemAuctionTimeExtendsOnBid", 0) * 1000;
        DEFAULT_PUNISH = IllegalActionPunishmentType.findByName(General.getString("DefaultPunish", "KICK"));
        DEFAULT_PUNISH_PARAM = General.getInt("DefaultPunishParam", 0);
        ONLY_GM_ITEMS_FREE = General.getBoolean("OnlyGMItemsFree", true);
        JAIL_IS_PVP = General.getBoolean("JailIsPvp", false);
        JAIL_DISABLE_CHAT = General.getBoolean("JailDisableChat", true);
        JAIL_DISABLE_TRANSACTION = General.getBoolean("JailDisableTransaction", false);
        CUSTOM_NPC_DATA = General.getBoolean("CustomNpcData", false);
        CUSTOM_TELEPORT_TABLE = General.getBoolean("CustomTeleportTable", false);
        CUSTOM_SKILLS_LOAD = General.getBoolean("CustomSkillsLoad", false);
        CUSTOM_ITEMS_LOAD = General.getBoolean("CustomItemsLoad", false);
        CUSTOM_MULTISELL_LOAD = General.getBoolean("CustomMultisellLoad", false);
        CUSTOM_BUYLIST_LOAD = General.getBoolean("CustomBuyListLoad", false);
        ALT_BIRTHDAY_GIFT = General.getInt("AltBirthdayGift", 22187);
        ALT_BIRTHDAY_MAIL_SUBJECT = General.getString("AltBirthdayMailSubject", "Happy Birthday!");
        ALT_BIRTHDAY_MAIL_TEXT = General.getString("AltBirthdayMailText", "Hello Adventurer!! Seeing as you're one year older now, I thought I would send you some birthday cheer :) Please find your birthday pack attached. May these gifts bring you joy and happiness on this very special day." + EOL + EOL + "Sincerely, Alegria");
        ENABLE_BLOCK_CHECKER_EVENT = General.getBoolean("EnableBlockCheckerEvent", false);
        MIN_BLOCK_CHECKER_TEAM_MEMBERS = General.getInt("BlockCheckerMinTeamMembers", 2);
        if (MIN_BLOCK_CHECKER_TEAM_MEMBERS < 1) {
            MIN_BLOCK_CHECKER_TEAM_MEMBERS = 1;
        } else if (MIN_BLOCK_CHECKER_TEAM_MEMBERS > 6) {
            MIN_BLOCK_CHECKER_TEAM_MEMBERS = 6;
        }
        HBCE_FAIR_PLAY = General.getBoolean("HBCEFairPlay", false);

        BOTREPORT_ENABLE = General.getBoolean("EnableBotReportButton", false);
        BOTREPORT_RESETPOINT_HOUR = General.getString("BotReportPointsResetHour", "00:00").split(":");
        BOTREPORT_REPORT_DELAY = General.getInt("BotReportDelay", 30) * 60000;
        BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS = General.getBoolean("AllowReportsFromSameClanMembers", false);
        ENABLE_FALLING_DAMAGE = General.getBoolean("EnableFallingDamage", true);

        // Load FloodProtector config file
        final PropertiesParser FloodProtectors = new PropertiesParser(FLOOD_PROTECTOR_CONFIG_FILE);

        loadFloodProtectorConfigs(FloodProtectors);

        // Load NPC config file (if exists)
        final PropertiesParser NPC = new PropertiesParser(NPC_CONFIG_FILE);

        ANNOUNCE_MAMMON_SPAWN = NPC.getBoolean("AnnounceMammonSpawn", false);
        ALT_MOB_AGRO_IN_PEACEZONE = NPC.getBoolean("AltMobAgroInPeaceZone", true);
        ALT_ATTACKABLE_NPCS = NPC.getBoolean("AltAttackableNpcs", true);
        ALT_GAME_VIEWNPC = NPC.getBoolean("AltGameViewNpc", false);
        MAX_DRIFT_RANGE = NPC.getInt("MaxDriftRange", 300);
        SHOW_NPC_LVL = NPC.getBoolean("ShowNpcLevel", false);
        SHOW_CREST_WITHOUT_QUEST = NPC.getBoolean("ShowCrestWithoutQuest", false);
        ENABLE_RANDOM_ENCHANT_EFFECT = NPC.getBoolean("EnableRandomEnchantEffect", false);
        MIN_NPC_LVL_DMG_PENALTY = NPC.getInt("MinNPCLevelForDmgPenalty", 78);
        NPC_DMG_PENALTY = parseConfigLine(NPC.getString("DmgPenaltyForLvLDifferences", "0.7, 0.6, 0.6, 0.55"));
        NPC_CRIT_DMG_PENALTY = parseConfigLine(NPC.getString("CritDmgPenaltyForLvLDifferences", "0.75, 0.65, 0.6, 0.58"));
        NPC_SKILL_DMG_PENALTY = parseConfigLine(NPC.getString("SkillDmgPenaltyForLvLDifferences", "0.8, 0.7, 0.65, 0.62"));
        MIN_NPC_LVL_MAGIC_PENALTY = NPC.getInt("MinNPCLevelForMagicPenalty", 78);
        NPC_SKILL_CHANCE_PENALTY = parseConfigLine(NPC.getString("SkillChancePenaltyForLvLDifferences", "2.5, 3.0, 3.25, 3.5"));
        DEFAULT_CORPSE_TIME = NPC.getInt("DefaultCorpseTime", 7);
        SPOILED_CORPSE_EXTEND_TIME = NPC.getInt("SpoiledCorpseExtendTime", 10);
        CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY = NPC.getInt("CorpseConsumeSkillAllowedTimeBeforeDecay", 2000);
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

        RATE_XP = RatesSettings.getFloat("RateXp", 1);
        RATE_SP = RatesSettings.getFloat("RateSp", 1);
        RATE_PARTY_XP = RatesSettings.getFloat("RatePartyXp", 1);
        RATE_PARTY_SP = RatesSettings.getFloat("RatePartySp", 1);

        RATE_INSTANCE_XP = RatesSettings.getFloat("RateInstanceXp", -1);
        if (RATE_INSTANCE_XP < 0) {
            RATE_INSTANCE_XP = RATE_XP;
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

        RATE_VITALITY_EXP_MULTIPLIER = RatesSettings.getFloat("RateVitalityExpMultiplier", 2);
        VITALITY_MAX_ITEMS_ALLOWED = RatesSettings.getInt("VitalityMaxItemsAllowed", 999);
        RATE_VITALITY_LOST = RatesSettings.getFloat("RateVitalityLost", 1);
        RATE_VITALITY_GAIN = RatesSettings.getFloat("RateVitalityGain", 1);
        RATE_KARMA_LOST = RatesSettings.getFloat("RateKarmaLost", -1);
        if (RATE_KARMA_LOST == -1) {
            RATE_KARMA_LOST = RATE_XP;
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
        ANNOUNCE_GAINAK_SIEGE = PVPSettings.getBoolean("AnnounceGainakSiege", false);

        // Load Olympiad config file (if exists)
        final PropertiesParser Olympiad = new PropertiesParser(OLYMPIAD_CONFIG_FILE);

        ALT_OLY_START_TIME = Olympiad.getInt("AltOlyStartTime", 18);
        ALT_OLY_MIN = Olympiad.getInt("AltOlyMin", 0);
        ALT_OLY_CPERIOD = Olympiad.getLong("AltOlyCPeriod", 21600000);
        ALT_OLY_BATTLE = Olympiad.getLong("AltOlyBattle", 300000);
        ALT_OLY_WPERIOD = Olympiad.getLong("AltOlyWPeriod", 604800000);
        ALT_OLY_VPERIOD = Olympiad.getLong("AltOlyVPeriod", 86400000);
        ALT_OLY_START_POINTS = Olympiad.getInt("AltOlyStartPoints", 10);
        ALT_OLY_WEEKLY_POINTS = Olympiad.getInt("AltOlyWeeklyPoints", 10);
        ALT_OLY_CLASSED = Olympiad.getInt("AltOlyClassedParticipants", 11);
        ALT_OLY_NONCLASSED = Olympiad.getInt("AltOlyNonClassedParticipants", 11);
        ALT_OLY_REG_DISPLAY = Olympiad.getInt("AltOlyRegistrationDisplayNumber", 100);
        ALT_OLY_CLASSED_REWARD = parseItemsList(Olympiad.getString("AltOlyClassedReward", "13722,50"));
        ALT_OLY_NONCLASSED_REWARD = parseItemsList(Olympiad.getString("AltOlyNonClassedReward", "13722,40"));
        ALT_OLY_TEAM_REWARD = parseItemsList(Olympiad.getString("AltOlyTeamReward", "13722,85"));
        ALT_OLY_COMP_RITEM = Olympiad.getInt("AltOlyCompRewItem", 45584);
        ALT_OLY_MIN_MATCHES = Olympiad.getInt("AltOlyMinMatchesForPoints", 15);
        ALT_OLY_MARK_PER_POINT = Olympiad.getInt("AltOlyMarkPerPoint", 20);
        ALT_OLY_HERO_POINTS = Olympiad.getInt("AltOlyHeroPoints", 30);
        ALT_OLY_RANK1_POINTS = Olympiad.getInt("AltOlyRank1Points", 60);
        ALT_OLY_RANK2_POINTS = Olympiad.getInt("AltOlyRank2Points", 50);
        ALT_OLY_RANK3_POINTS = Olympiad.getInt("AltOlyRank3Points", 45);
        ALT_OLY_RANK4_POINTS = Olympiad.getInt("AltOlyRank4Points", 40);
        ALT_OLY_RANK5_POINTS = Olympiad.getInt("AltOlyRank5Points", 30);
        ALT_OLY_MAX_POINTS = Olympiad.getInt("AltOlyMaxPoints", 10);
        ALT_OLY_DIVIDER_CLASSED = Olympiad.getInt("AltOlyDividerClassed", 5);
        ALT_OLY_DIVIDER_NON_CLASSED = Olympiad.getInt("AltOlyDividerNonClassed", 5);
        ALT_OLY_MAX_WEEKLY_MATCHES = Olympiad.getInt("AltOlyMaxWeeklyMatches", 70);
        ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED = Olympiad.getInt("AltOlyMaxWeeklyMatchesNonClassed", 60);
        ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED = Olympiad.getInt("AltOlyMaxWeeklyMatchesClassed", 30);
        ALT_OLY_MAX_WEEKLY_MATCHES_TEAM = Olympiad.getInt("AltOlyMaxWeeklyMatchesTeam", 10);
        ALT_OLY_LOG_FIGHTS = Olympiad.getBoolean("AltOlyLogFights", false);
        ALT_OLY_SHOW_MONTHLY_WINNERS = Olympiad.getBoolean("AltOlyShowMonthlyWinners", true);
        ALT_OLY_ANNOUNCE_GAMES = Olympiad.getBoolean("AltOlyAnnounceGames", true);
        final String[] olyRestrictedItems = Olympiad.getString("AltOlyRestrictedItems", "6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,9388,9389,9390,17049,17050,17051,17052,17053,17054,17055,17056,17057,17058,17059,17060,17061,20759,20775,20776,20777,20778,14774").split(",");
        LIST_OLY_RESTRICTED_ITEMS = new ArrayList<>(olyRestrictedItems.length);
        for (String id : olyRestrictedItems) {
            LIST_OLY_RESTRICTED_ITEMS.add(Integer.parseInt(id));
        }
        ALT_OLY_ENCHANT_LIMIT = Olympiad.getInt("AltOlyEnchantLimit", -1);
        ALT_OLY_WAIT_TIME = Olympiad.getInt("AltOlyWaitTime", 60);
        ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS = Olympiad.getBoolean("AltOlyUseCustomPeriodSettings", false);
        ALT_OLY_PERIOD = Olympiad.getString("AltOlyPeriod", "MONTH");
        ALT_OLY_PERIOD_MULTIPLIER = Olympiad.getInt("AltOlyPeriodMultiplier", 1);
        ALT_OLY_COMPETITION_DAYS = new ArrayList<>();
        for (String s : Olympiad.getString("AltOlyCompetitionDays", "1,2,3,4,5,6,7").split(",")) {
            ALT_OLY_COMPETITION_DAYS.add(Integer.parseInt(s));
        }

        // Grand bosses
        final PropertiesParser GrandBossSettings = new PropertiesParser(GRANDBOSS_CONFIG_FILE);

        ANTHARAS_WAIT_TIME = GrandBossSettings.getInt("AntharasWaitTime", 30);
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

        // Gracia Seeds
        final PropertiesParser GraciaSeedsSettings = new PropertiesParser(GRACIASEEDS_CONFIG_FILE);

        // Seed of Destruction
        SOD_TIAT_KILL_COUNT = GraciaSeedsSettings.getInt("TiatKillCountForNextState", 10);
        SOD_STAGE_2_LENGTH = GraciaSeedsSettings.getLong("Stage2Length", 720) * 60000;

        try {
            //@formatter:off
            FILTER_LIST = Files.lines(Paths.get(CHAT_FILTER_FILE), StandardCharsets.UTF_8)
                    .map(String::trim)
                    .filter(line -> (!line.isEmpty() && (line.charAt(0) != '#')))
                    .collect(Collectors.toList());
            //@formatter:on
            LOGGER.info("Loaded " + FILTER_LIST.size() + " Filter Words.");
        } catch (IOException e) {
            LOGGER.warn("Error while loading chat filter words!", e);
        }

        final PropertiesParser geoData = new PropertiesParser(GEOENGINE_CONFIG_FILE);

        GEODATA_PATH = geoData.getString("GeoDataPath", "./data/geodata/");
        COORD_SYNCHRONIZE = geoData.getInt("CoordSynchronize", -1);

        PART_OF_CHARACTER_HEIGHT = geoData.getInt("PartOfCharacterHeight", 75);
        MAX_OBSTACLE_HEIGHT = geoData.getInt("MaxObstacleHeight", 32);

        PATHFINDING = geoData.getBoolean("PathFinding", true);
        PATHFIND_BUFFERS = geoData.getString("PathFindBuffers", "100x6;128x6;192x6;256x4;320x4;384x4;500x2");
        BASE_WEIGHT = geoData.getInt("BaseWeight", 10);
        DIAGONAL_WEIGHT = geoData.getInt("DiagonalWeight", 14);
        OBSTACLE_MULTIPLIER = geoData.getInt("ObstacleMultiplier", 10);
        HEURISTIC_WEIGHT = geoData.getInt("HeuristicWeight", 20);
        MAX_ITERATIONS = geoData.getInt("MaxIterations", 3500);

        // Load AllowedPlayerRaces config file (if exists)
        final PropertiesParser AllowedPlayerRaces = new PropertiesParser(CUSTOM_ALLOWED_PLAYER_RACES_CONFIG_FILE);

        ALLOW_HUMAN = AllowedPlayerRaces.getBoolean("AllowHuman", true);
        ALLOW_ELF = AllowedPlayerRaces.getBoolean("AllowElf", true);
        ALLOW_DARKELF = AllowedPlayerRaces.getBoolean("AllowDarkElf", true);
        ALLOW_ORC = AllowedPlayerRaces.getBoolean("AllowOrc", true);
        ALLOW_DWARF = AllowedPlayerRaces.getBoolean("AllowDwarf", true);
        ALLOW_KAMAEL = AllowedPlayerRaces.getBoolean("AllowKamael", true);
        ALLOW_ERTHEIA = AllowedPlayerRaces.getBoolean("AllowErtheia", true);

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
        for (String s : AutoPotions.getString("AutoCpItemIds", "0").split(",")) {
            AUTO_CP_ITEM_IDS.add(Integer.parseInt(s));
        }
        AUTO_HP_ITEM_IDS = new ArrayList<>();
        for (String s : AutoPotions.getString("AutoHpItemIds", "0").split(",")) {
            AUTO_HP_ITEM_IDS.add(Integer.parseInt(s));
        }
        AUTO_MP_ITEM_IDS = new ArrayList<>();
        for (String s : AutoPotions.getString("AutoMpItemIds", "0").split(",")) {
            AUTO_MP_ITEM_IDS.add(Integer.parseInt(s));
        }

        // Load Banking config file (if exists)
        final PropertiesParser Banking = new PropertiesParser(CUSTOM_BANKING_CONFIG_FILE);

        BANKING_SYSTEM_ENABLED = Banking.getBoolean("BankingEnabled", false);
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
        MONSTER_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("MonsterClanHelpRange", 1.0);
        RAIDBOSS_HP_MULTIPLIER = BoostNpcStats.getDouble("RaidbossHP", 1.0);
        RAIDBOSS_MP_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMP", 1.0);
        RAIDBOSS_PATK_MULTIPLIER = BoostNpcStats.getDouble("RaidbossPAtk", 1.0);
        RAIDBOSS_MATK_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMAtk", 1.0);
        RAIDBOSS_PDEF_MULTIPLIER = BoostNpcStats.getDouble("RaidbossPDef", 1.0);
        RAIDBOSS_MDEF_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMDef", 1.0);
        RAIDBOSS_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("RaidbossAggroRange", 1.0);
        RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("RaidbossClanHelpRange", 1.0);
        GUARD_HP_MULTIPLIER = BoostNpcStats.getDouble("GuardHP", 1.0);
        GUARD_MP_MULTIPLIER = BoostNpcStats.getDouble("GuardMP", 1.0);
        GUARD_PATK_MULTIPLIER = BoostNpcStats.getDouble("GuardPAtk", 1.0);
        GUARD_MATK_MULTIPLIER = BoostNpcStats.getDouble("GuardMAtk", 1.0);
        GUARD_PDEF_MULTIPLIER = BoostNpcStats.getDouble("GuardPDef", 1.0);
        GUARD_MDEF_MULTIPLIER = BoostNpcStats.getDouble("GuardMDef", 1.0);
        GUARD_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("GuardAggroRange", 1.0);
        GUARD_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("GuardClanHelpRange", 1.0);
        DEFENDER_HP_MULTIPLIER = BoostNpcStats.getDouble("DefenderHP", 1.0);
        DEFENDER_MP_MULTIPLIER = BoostNpcStats.getDouble("DefenderMP", 1.0);
        DEFENDER_PATK_MULTIPLIER = BoostNpcStats.getDouble("DefenderPAtk", 1.0);
        DEFENDER_MATK_MULTIPLIER = BoostNpcStats.getDouble("DefenderMAtk", 1.0);
        DEFENDER_PDEF_MULTIPLIER = BoostNpcStats.getDouble("DefenderPDef", 1.0);
        DEFENDER_MDEF_MULTIPLIER = BoostNpcStats.getDouble("DefenderMDef", 1.0);
        DEFENDER_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("DefenderAggroRange", 1.0);
        DEFENDER_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("DefenderClanHelpRange", 1.0);

        // Load ChampionMonster config file (if exists)
        final PropertiesParser ChampionMonster = new PropertiesParser(CUSTOM_CHAMPION_MONSTERS_CONFIG_FILE);

        CHAMPION_ENABLE = ChampionMonster.getBoolean("ChampionEnable", false);
        CHAMPION_PASSIVE = ChampionMonster.getBoolean("ChampionPassive", false);
        CHAMPION_FREQUENCY = ChampionMonster.getInt("ChampionFrequency", 0);
        CHAMP_TITLE = ChampionMonster.getString("ChampionTitle", "Champion");
        SHOW_CHAMPION_AURA = ChampionMonster.getBoolean("ChampionAura", true);
        CHAMP_MIN_LVL = ChampionMonster.getInt("ChampionMinLevel", 20);
        CHAMP_MAX_LVL = ChampionMonster.getInt("ChampionMaxLevel", 60);
        CHAMPION_HP = ChampionMonster.getInt("ChampionHp", 7);
        CHAMPION_HP_REGEN = ChampionMonster.getFloat("ChampionHpRegen", 1);
        CHAMPION_REWARDS_EXP_SP = ChampionMonster.getFloat("ChampionRewardsExpSp", 8);
        CHAMPION_REWARDS_CHANCE = ChampionMonster.getFloat("ChampionRewardsChance", 8);
        CHAMPION_REWARDS_AMOUNT = ChampionMonster.getFloat("ChampionRewardsAmount", 1);
        CHAMPION_ADENAS_REWARDS_CHANCE = ChampionMonster.getFloat("ChampionAdenasRewardsChance", 1);
        CHAMPION_ADENAS_REWARDS_AMOUNT = ChampionMonster.getFloat("ChampionAdenasRewardsAmount", 1);
        CHAMPION_ATK = ChampionMonster.getFloat("ChampionAtk", 1);
        CHAMPION_SPD_ATK = ChampionMonster.getFloat("ChampionSpdAtk", 1);
        CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = ChampionMonster.getInt("ChampionRewardLowerLvlItemChance", 0);
        CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = ChampionMonster.getInt("ChampionRewardHigherLvlItemChance", 0);
        CHAMPION_REWARD_ID = ChampionMonster.getInt("ChampionRewardItemID", 6393);
        CHAMPION_REWARD_QTY = ChampionMonster.getInt("ChampionRewardItemQty", 1);
        CHAMPION_ENABLE_VITALITY = ChampionMonster.getBoolean("ChampionEnableVitality", false);
        CHAMPION_ENABLE_IN_INSTANCES = ChampionMonster.getBoolean("ChampionEnableInInstances", false);

        // Load ChatModeration config file (if exists)
        final PropertiesParser ChatModeration = new PropertiesParser(CUSTOM_CHAT_MODERATION_CONFIG_FILE);

        CHAT_ADMIN = ChatModeration.getBoolean("ChatAdmin", true);

        // Load CommunityBoard config file (if exists)
        final PropertiesParser CommunityBoard = new PropertiesParser(CUSTOM_COMMUNITY_BOARD_CONFIG_FILE);

        CUSTOM_CB_ENABLED = CommunityBoard.getBoolean("CustomCommunityBoard", false);
        COMMUNITYBOARD_CURRENCY = CommunityBoard.getInt("CommunityCurrencyId", 57);
        COMMUNITYBOARD_ENABLE_MULTISELLS = CommunityBoard.getBoolean("CommunityEnableMultisells", true);
        COMMUNITYBOARD_ENABLE_TELEPORTS = CommunityBoard.getBoolean("CommunityEnableTeleports", true);
        COMMUNITYBOARD_ENABLE_BUFFS = CommunityBoard.getBoolean("CommunityEnableBuffs", true);
        COMMUNITYBOARD_ENABLE_HEAL = CommunityBoard.getBoolean("CommunityEnableHeal", true);
        COMMUNITYBOARD_TELEPORT_PRICE = CommunityBoard.getInt("CommunityTeleportPrice", 0);
        COMMUNITYBOARD_BUFF_PRICE = CommunityBoard.getInt("CommunityBuffPrice", 0);
        COMMUNITYBOARD_HEAL_PRICE = CommunityBoard.getInt("CommunityHealPrice", 0);
        COMMUNITYBOARD_KARMA_DISABLED = CommunityBoard.getBoolean("CommunityKarmaDisabled", true);
        COMMUNITYBOARD_CAST_ANIMATIONS = CommunityBoard.getBoolean("CommunityCastAnimations", false);
        COMMUNITY_PREMIUM_SYSTEM_ENABLED = CommunityBoard.getBoolean("CommunityPremiumSystem", false);
        COMMUNITY_PREMIUM_COIN_ID = CommunityBoard.getInt("CommunityPremiumBuyCoinId", 57);
        COMMUNITY_PREMIUM_PRICE_PER_DAY = CommunityBoard.getInt("CommunityPremiumPricePerDay", 1000000);
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
        DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP = DualboxCheck.getInt("DualboxCheckMaxOlympiadParticipantsPerIP", 0);
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

        // Load FindPvP config file (if exists)
        final PropertiesParser FindPvP = new PropertiesParser(CUSTOM_FIND_PVP_CONFIG_FILE);
        ENABLE_FIND_PVP = FindPvP.getBoolean("EnableFindPvP", false);

        // Load MultilingualSupport config file (if exists)
        final PropertiesParser MultilingualSupport = new PropertiesParser(CUSTOM_MULTILANGUAL_SUPPORT_CONFIG_FILE);

        MULTILANG_DEFAULT = MultilingualSupport.getString("MultiLangDefault", "en");
        MULTILANG_ENABLE = MultilingualSupport.getBoolean("MultiLangEnable", false);
        final String[] allowed = MultilingualSupport.getString("MultiLangAllowed", MULTILANG_DEFAULT).split(";");
        MULTILANG_ALLOWED = new ArrayList<>(allowed.length);
        for (String lang : allowed) {
            MULTILANG_ALLOWED.add(lang);
        }
        if (!MULTILANG_ALLOWED.contains(MULTILANG_DEFAULT)) {
            LOGGER.warn("MultiLang[Config.load()]: default language: " + MULTILANG_DEFAULT + " is not in allowed list !");
        }
        MULTILANG_VOICED_ALLOW = MultilingualSupport.getBoolean("MultiLangVoiceCommand", true);

        // Load OfflineTrade config file (if exists)
        final PropertiesParser OfflineTrade = new PropertiesParser(CUSTOM_OFFLINE_TRADE_CONFIG_FILE);

        OFFLINE_TRADE_ENABLE = OfflineTrade.getBoolean("OfflineTradeEnable", false);
        OFFLINE_CRAFT_ENABLE = OfflineTrade.getBoolean("OfflineCraftEnable", false);
        OFFLINE_MODE_IN_PEACE_ZONE = OfflineTrade.getBoolean("OfflineModeInPeaceZone", false);
        OFFLINE_MODE_NO_DAMAGE = OfflineTrade.getBoolean("OfflineModeNoDamage", false);
        OFFLINE_SET_NAME_COLOR = OfflineTrade.getBoolean("OfflineSetNameColor", false);
        OFFLINE_NAME_COLOR = Integer.decode("0x" + OfflineTrade.getString("OfflineNameColor", "808080"));
        OFFLINE_FAME = OfflineTrade.getBoolean("OfflineFame", true);
        RESTORE_OFFLINERS = OfflineTrade.getBoolean("RestoreOffliners", false);
        OFFLINE_MAX_DAYS = OfflineTrade.getInt("OfflineMaxDays", 10);
        OFFLINE_DISCONNECT_FINISHED = OfflineTrade.getBoolean("OfflineDisconnectFinished", true);
        STORE_OFFLINE_TRADE_IN_REALTIME = OfflineTrade.getBoolean("StoreOfflineTradeInRealtime", true);

        // Load PasswordChange config file (if exists)
        final PropertiesParser PasswordChange = new PropertiesParser(CUSTOM_PASSWORD_CHANGE_CONFIG_FILE);

        ALLOW_CHANGE_PASSWORD = PasswordChange.getBoolean("AllowChangePassword", false);

        // Load PcCafe config file (if exists)
        final PropertiesParser PcCafe = new PropertiesParser(CUSTOM_PC_CAFE_CONFIG_FILE);

        PC_CAFE_ENABLED = PcCafe.getBoolean("PcCafeEnabled", false);
        PC_CAFE_ONLY_PREMIUM = PcCafe.getBoolean("PcCafeOnlyPremium", false);
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

        // Load PremiumSystem config file (if exists)
        final PropertiesParser PremiumSystem = new PropertiesParser(CUSTOM_PREMIUM_SYSTEM_CONFIG_FILE);

        PREMIUM_SYSTEM_ENABLED = PremiumSystem.getBoolean("EnablePremiumSystem", false);
        PREMIUM_RATE_XP = PremiumSystem.getFloat("PremiumRateXp", 2);
        PREMIUM_RATE_SP = PremiumSystem.getFloat("PremiumRateSp", 2);
        PREMIUM_RATE_DROP_CHANCE = PremiumSystem.getFloat("PremiumRateDropChance", 2);
        PREMIUM_RATE_DROP_AMOUNT = PremiumSystem.getFloat("PremiumRateDropAmount", 1);
        PREMIUM_RATE_SPOIL_CHANCE = PremiumSystem.getFloat("PremiumRateSpoilChance", 2);
        PREMIUM_RATE_SPOIL_AMOUNT = PremiumSystem.getFloat("PremiumRateSpoilAmount", 1);
        final String[] premiumDropChanceMultiplier = PremiumSystem.getString("PremiumRateDropChanceByItemId", "").split(";");
        PREMIUM_RATE_DROP_CHANCE_BY_ID = new HashMap<>(premiumDropChanceMultiplier.length);
        if (!premiumDropChanceMultiplier[0].isEmpty()) {
            for (String item : premiumDropChanceMultiplier) {
                final String[] itemSplit = item.split(",");
                if (itemSplit.length != 2) {
                    LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> PremiumRateDropChanceByItemId \"", item, "\""));
                } else {
                    try {
                        PREMIUM_RATE_DROP_CHANCE_BY_ID.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!item.isEmpty()) {
                            LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> PremiumRateDropChanceByItemId \"", item, "\""));
                        }
                    }
                }
            }
        }
        final String[] premiumDropAmountMultiplier = PremiumSystem.getString("PremiumRateDropAmountByItemId", "").split(";");
        PREMIUM_RATE_DROP_AMOUNT_BY_ID = new HashMap<>(premiumDropAmountMultiplier.length);
        if (!premiumDropAmountMultiplier[0].isEmpty()) {
            for (String item : premiumDropAmountMultiplier) {
                final String[] itemSplit = item.split(",");
                if (itemSplit.length != 2) {
                    LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> PremiumRateDropAmountByItemId \"", item, "\""));
                } else {
                    try {
                        PREMIUM_RATE_DROP_AMOUNT_BY_ID.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!item.isEmpty()) {
                            LOGGER.warn(StringUtil.concat("Config.load(): invalid config property -> PremiumRateDropAmountByItemId \"", item, "\""));
                        }
                    }
                }
            }
        }

        // Load PrivateStoreRange config file (if exists)
        final PropertiesParser PrivateStoreRange = new PropertiesParser(CUSTOM_PRIVATE_STORE_RANGE_CONFIG_FILE);

        SHOP_MIN_RANGE_FROM_PLAYER = PrivateStoreRange.getInt("ShopMinRangeFromPlayer", 50);
        SHOP_MIN_RANGE_FROM_NPC = PrivateStoreRange.getInt("ShopMinRangeFromNpc", 100);

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

        // Load ServerTime config file (if exists)
        final PropertiesParser ServerTime = new PropertiesParser(CUSTOM_SERVER_TIME_CONFIG_FILE);

        DISPLAY_SERVER_TIME = ServerTime.getBoolean("DisplayServerTime", false);

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

        // Load WalkerBotProtection config file (if exists)
        final PropertiesParser WalkerBotProtection = new PropertiesParser(CUSTOM_WALKER_BOT_PROTECTION_CONFIG_FILE);

        L2WALKER_PROTECTION = WalkerBotProtection.getBoolean("L2WalkerProtection", false);
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
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SUBCLASS, "Subclass", 20);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_DROP_ITEM, "DropItem", 10);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", 5);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MULTISELL, "MultiSell", 1);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_TRANSACTION, "Transaction", 10);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANUFACTURE, "Manufacture", 3);
        loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANOR, "Manor", 30);
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
        config.PUNISHMENT_TIME = properties.getInt("FloodProtector" + configString + "PunishmentTime", 0) * 60000;
    }

    public static int getServerTypeId(String[] serverTypes) {
        int serverType = 0;
        for (String cType : serverTypes) {
            switch (cType.trim().toLowerCase()) {
                case "normal": {
                    serverType |= 0x01;
                    break;
                }
                case "relax": {
                    serverType |= 0x02;
                    break;
                }
                case "test": {
                    serverType |= 0x04;
                    break;
                }
                case "broad": {
                    serverType |= 0x08;
                    break;
                }
                case "restricted": {
                    serverType |= 0x10;
                    break;
                }
                case "event": {
                    serverType |= 0x20;
                    break;
                }
                case "free": {
                    serverType |= 0x40;
                    break;
                }
                case "world": {
                    serverType |= 0x100;
                    break;
                }
                case "new": {
                    serverType |= 0x200;
                    break;
                }
                case "classic": {
                    serverType |= 0x400;
                    break;
                }
            }
        }
        return serverType;
    }

    /**
     * @param line the string line to parse
     * @return a parsed float map
     */
    private static Map<Integer, Float> parseConfigLine(String line) {
        final String[] propertySplit = line.split(",");
        final Map<Integer, Float> ret = new HashMap<>(propertySplit.length);
        int i = 0;
        for (String value : propertySplit) {
            ret.put(i++, Float.parseFloat(value));
        }
        return ret;
    }

    /**
     * Parse a config value from its string representation to a two-dimensional int array.<br>
     * The format of the value to be parsed should be as follows: "item1Id,item1Amount;item2Id,item2Amount;...itemNId,itemNAmount".
     *
     * @param line the value of the parameter to parse
     * @return the parsed list or {@code null} if nothing was parsed
     */
    private static List<ItemHolder> parseItemsList(String line) {
        final String[] propertySplit = line.split(";");
        if (propertySplit.length == 0) {
            // nothing to do here
            return null;
        }

        String[] valueSplit;
        final List<ItemHolder> result = new ArrayList<>(propertySplit.length);
        for (String value : propertySplit) {
            valueSplit = value.split(",");
            if (valueSplit.length != 2) {
                LOGGER.warn("parseItemsList[Config.load()]: invalid entry -> " + valueSplit[0] + ", should be itemId,itemNumber. Skipping to the next entry in the list.");
                continue;
            }

            int itemId = -1;
            try {
                itemId = Integer.parseInt(valueSplit[0]);
            } catch (NumberFormatException e) {
                LOGGER.warn("parseItemsList[Config.load()]: invalid itemId -> " + valueSplit[0] + ", value must be an integer. Skipping to the next entry in the list.");
                continue;
            }
            int count = -1;
            try {
                count = Integer.parseInt(valueSplit[1]);
            } catch (NumberFormatException e) {
                LOGGER.warn("parseItemsList[Config.load()]: invalid item number -> " + valueSplit[1] + ", value must be an integer. Skipping to the next entry in the list.");
                continue;
            }
            if ((itemId > 0) && (count > 0)) {
                result.add(new ItemHolder(itemId, count));
            }
        }
        return result;
    }

    public enum IdFactoryType {
        BitSet,
        Stack
    }

    private static class IPConfigData extends IGameXmlReader{
        private static final List<String> _subnets = new ArrayList<>(5);
        private static final List<String>
                _hosts = new ArrayList<>(5);

        public IPConfigData() {
            load();
        }

        @Override
        protected Path getSchemaFilePath() {
            return Path.of("./config/xsd/ipconfig.xsd");
        }

        @Override
        public void load() {
            final File f = new File(IPCONFIG_FILE);
            if (f.exists()) {
                LOGGER.info("Network Config: ipconfig.xml exists using manual configuration...");
                parseFile(new File(IPCONFIG_FILE));
            } else
            // Auto configuration...
            {
                LOGGER.info("Network Config: ipconfig.xml doesn't exists using automatic configuration...");
                autoIpConfig();
            }
        }

        @Override
        public void parseDocument(Document doc, File f) {
            NamedNodeMap attrs;
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("gameserver".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("define".equalsIgnoreCase(d.getNodeName())) {
                            attrs = d.getAttributes();
                            _subnets.add(attrs.getNamedItem("subnet").getNodeValue());
                            _hosts.add(attrs.getNamedItem("address").getNodeValue());

                            if (_hosts.size() != _subnets.size()) {
                                LOGGER.warn("Failed to Load " + IPCONFIG_FILE + " File - subnets does not match server addresses.");
                            }
                        }
                    }

                    final Node att = n.getAttributes().getNamedItem("address");
                    if (att == null) {
                        LOGGER.warn("Failed to load " + IPCONFIG_FILE + " file - default server address is missing.");
                        _hosts.add("127.0.0.1");
                    } else {
                        _hosts.add(att.getNodeValue());
                    }
                    _subnets.add("0.0.0.0/0");
                }
            }
        }

        protected void autoIpConfig() {
            String externalIp;
            try {
                final URL autoIp = new URL("https://api.ipify.org/");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(autoIp.openStream()))) {
                    externalIp = in.readLine();
                }
            } catch (IOException e) {
                LOGGER.info("Failed to connect to api.ipify.org please check your internet connection using 127.0.0.1!");
                externalIp = "127.0.0.1";
            }

            try {
                final Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();

                while (niList.hasMoreElements()) {
                    final NetworkInterface ni = niList.nextElement();

                    if (!ni.isUp() || ni.isVirtual()) {
                        continue;
                    }

                    if (!ni.isLoopback() && ((ni.getHardwareAddress() == null) || (ni.getHardwareAddress().length != 6))) {
                        continue;
                    }

                    for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                        if (ia.getAddress() instanceof Inet6Address) {
                            continue;
                        }

                        final String hostAddress = ia.getAddress().getHostAddress();
                        final int subnetPrefixLength = ia.getNetworkPrefixLength();
                        final int subnetMaskInt = IntStream.rangeClosed(1, subnetPrefixLength).reduce((r, e) -> (r << 1) + 1).orElse(0) << (32 - subnetPrefixLength);
                        final int hostAddressInt = Arrays.stream(hostAddress.split("\\.")).mapToInt(Integer::parseInt).reduce((r, e) -> (r << 8) + e).orElse(0);
                        final int subnetAddressInt = hostAddressInt & subnetMaskInt;
                        final String subnetAddress = ((subnetAddressInt >> 24) & 0xFF) + "." + ((subnetAddressInt >> 16) & 0xFF) + "." + ((subnetAddressInt >> 8) & 0xFF) + "." + (subnetAddressInt & 0xFF);
                        final String subnet = subnetAddress + '/' + subnetPrefixLength;
                        if (!_subnets.contains(subnet) && !subnet.equals("0.0.0.0/0")) {
                            _subnets.add(subnet);
                            _hosts.add(hostAddress);
                            LOGGER.info("Network Config: Adding new subnet: " + subnet + " address: " + hostAddress);
                        }
                    }
                }

                // External host and subnet
                _hosts.add(externalIp);
                _subnets.add("0.0.0.0/0");
                LOGGER.info("Network Config: Adding new subnet: 0.0.0.0/0 address: " + externalIp);
            } catch (SocketException e) {
                LOGGER.info("Network Config: Configuration failed please configure manually using ipconfig.xml", e);
                System.exit(0);
            }
        }

        protected List<String> getSubnets() {
            if (_subnets.isEmpty()) {
                return Collections.singletonList("0.0.0.0/0");
            }
            return _subnets;
        }

        protected List<String> getHosts() {
            if (_hosts.isEmpty()) {
                return Collections.singletonList("127.0.0.1");
            }
            return _hosts;
        }
    }
}
