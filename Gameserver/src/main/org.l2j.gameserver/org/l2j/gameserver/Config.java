package org.l2j.gameserver;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import io.github.joealisson.primitive.sets.IntSet;
import io.github.joealisson.primitive.sets.impl.HashIntSet;
import org.l2j.commons.configuration.ExProperties;
import org.l2j.commons.string.StringArrayUtils;
import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.model.base.PlayerAccess;
import org.l2j.gameserver.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.l2j.commons.util.Util.INT_ARRAY_EMPTY;
import static org.l2j.commons.util.Util.STRING_ARRAY_EMPTY;

public class Config
{
    private static final Logger _log = LoggerFactory.getLogger(Config.class);

    public static final int NCPUS = Runtime.getRuntime().availableProcessors();

    /** Configuration files */
    public static final String ANTIFLOOD_CONFIG_FILE = "config/antiflood.properties";
    public static final String OTHER_CONFIG_FILE = "config/other.properties";
    public static final String RESIDENCE_CONFIG_FILE = "config/residence.properties";
    public static final String SPOIL_CONFIG_FILE = "config/spoil.properties";
    public static final String ALT_SETTINGS_FILE = "config/altsettings.properties";
    public static final String FORMULAS_CONFIGURATION_FILE = "config/formulas.properties";
    public static final String PVP_CONFIG_FILE = "config/pvp.properties";
    public static final String TELNET_CONFIGURATION_FILE = "config/telnet.properties";
    public static final String SERVER_CONFIGURATION_FILE = "config/server.properties";
    public static final String AI_CONFIG_FILE = "config/ai.properties";
    public static final String GEODATA_CONFIG_FILE = "config/geodata.properties";
    public static final String SERVICES_FILE = "config/services.properties";
    public static final String OLYMPIAD = "config/olympiad.properties";

    public static final String EXT_FILE = "config/ext.properties";
    public static final String BBS_FILE = "config/bbs.properties";

    public static final String PVP_MANAGER_FILE = "config/pvp_manager.properties";

    public static final String TRAINING_CAMP_CONFIG_FILE = "config/training_camp.properties";
    public static final String BOT_FILE = "config/anti_bot_system.properties";
    public static final String SCHEME_BUFFER_FILE = "config/npcbuffer.properties";
    public static final String ANUSEWORDS_CONFIG_FILE = "config/abusewords.txt";

    public static final String GM_PERSONAL_ACCESS_FILE = "config/GMAccess.xml";
    public static final String GM_ACCESS_FILES_DIR = "config/GMAccess.d/";

    //anti bot stuff
    public static boolean ENABLE_ANTI_BOT_SYSTEM;
    public static int MINIMUM_TIME_QUESTION_ASK;
    public static int MAXIMUM_TIME_QUESTION_ASK;
    public static int MINIMUM_BOT_POINTS_TO_STOP_ASKING;
    public static int MAXIMUM_BOT_POINTS_TO_STOP_ASKING;
    public static int MAX_BOT_POINTS;
    public static int MINIMAL_BOT_RATING_TO_BAN;
    public static int AUTO_BOT_BAN_JAIL_TIME;
    public static boolean ANNOUNCE_AUTO_BOT_BAN;
    public static boolean ON_WRONG_QUESTION_KICK;

    public static int HTM_CACHE_MODE;
    public static int SHUTDOWN_ANN_TYPE;

    public static boolean DATABASE_AUTOUPDATE;

    // Database additional options
    public static boolean AUTOSAVE;

    public static long USER_INFO_INTERVAL;

    public static long BROADCAST_CHAR_INFO_INTERVAL;

    public static int MIN_HIT_TIME;

    public static int SUB_START_LEVEL;
    public static int START_CLAN_LEVEL;

    public static boolean ENABLE_L2_TOP_OVERONLINE;
    public static int L2TOP_MAX_ONLINE;
    public static int MIN_ONLINE_0_5_AM;
    public static int MAX_ONLINE_0_5_AM;
    public static int MIN_ONLINE_6_11_AM;
    public static int MAX_ONLINE_6_11_AM;
    public static int MIN_ONLINE_12_6_PM;
    public static int MAX_ONLINE_12_6_PM;
    public static int MIN_ONLINE_7_11_PM;
    public static int MAX_ONLINE_7_11_PM;
    public static int ADD_ONLINE_ON_SIMPLE_DAY;
    public static int ADD_ONLINE_ON_WEEKEND;
    public static int L2TOP_MIN_TRADERS;
    public static int L2TOP_MAX_TRADERS;

    public static int ALT_OLY_BY_SAME_BOX_NUMBER;
    public static boolean OLYMPIAD_ENABLE_ENCHANT_LIMIT;
    public static int OLYMPIAD_WEAPON_ENCHANT_LIMIT;
    public static int OLYMPIAD_ARMOR_ENCHANT_LIMIT;
    public static int OLYMPIAD_JEWEL_ENCHANT_LIMIT;

    public static boolean REFLECT_DAMAGE_CAPPED_BY_PDEF;

    public static int EFFECT_TASK_MANAGER_COUNT;

    public static int SKILLS_CAST_TIME_MIN_PHYSICAL;
    public static int SKILLS_CAST_TIME_MIN_MAGICAL;
    public static boolean ENABLE_CRIT_HEIGHT_BONUS;

    public static int MAXIMUM_ONLINE_USERS;

    public static int CLAN_WAR_MINIMUM_CLAN_LEVEL;
    public static int CLAN_WAR_MINIMUM_PLAYERS_DECLARE;
    public static int CLAN_WAR_PREPARATION_DAYS_PERIOD;
    public static int CLAN_WAR_REPUTATION_SCORE_PER_KILL;

    public static boolean DONTLOADSPAWN;
    public static boolean DONTLOADQUEST;
    public static int MAX_REFLECTIONS_COUNT;

    public static int SHIFT_BY;
    public static int SHIFT_BY_Z;
    public static int MAP_MIN_Z;
    public static int MAP_MAX_Z;

    /** ChatBan */
    public static int CHAT_MESSAGE_MAX_LEN;
    public static boolean ABUSEWORD_BANCHAT;
    public static int[] BAN_CHANNEL_LIST = new int[18];
    public static boolean ABUSEWORD_REPLACE;
    public static String ABUSEWORD_REPLACE_STRING;
    public static int ABUSEWORD_BANTIME;
    public static Pattern ABUSEWORD_PATTERN = null;
    public static boolean BANCHAT_ANNOUNCE;
    public static boolean BANCHAT_ANNOUNCE_FOR_ALL_WORLD;
    public static boolean BANCHAT_ANNOUNCE_NICK;

    public static boolean PREMIUM_ACCOUNT_ENABLED;
    public static int FREE_PA_TYPE;
    public static int FREE_PA_DELAY;
    public static boolean ENABLE_FREE_PA_NOTIFICATION;

    public static boolean ALT_SELL_ITEM_ONE_ADENA;

    public static int MAX_SIEGE_CLANS;

    public static boolean INCLUDE_RAID_DROP;

    public static boolean SAVING_SPS;
    public static boolean MANAHEAL_SPS_BONUS;

    public static int ALT_ADD_RECIPES;
    public static int ALT_MAX_ALLY_SIZE;

    public static int ALT_PARTY_DISTRIBUTION_RANGE;
    public static double[] ALT_PARTY_BONUS;
    public static double[] ALT_PARTY_CLAN_BONUS;
    public static int[] ALT_PARTY_LVL_DIFF_PENALTY;


    public static double ALT_POLE_DAMAGE_MODIFIER;

    public static double ALT_M_SIMPLE_DAMAGE_MOD;
    public static double ALT_P_DAMAGE_MOD;
    public static double ALT_M_CRIT_DAMAGE_MOD;
    public static double ALT_P_CRIT_DAMAGE_MOD;
    public static double ALT_P_CRIT_CHANCE_MOD;
    public static double ALT_M_CRIT_CHANCE_MOD;

    public static double SERVITOR_P_ATK_MODIFIER;
    public static double SERVITOR_M_ATK_MODIFIER;
    public static double SERVITOR_P_DEF_MODIFIER;
    public static double SERVITOR_M_DEF_MODIFIER;
    public static double SERVITOR_P_SKILL_POWER_MODIFIER;
    public static double SERVITOR_M_SKILL_POWER_MODIFIER;

    public static double ALT_BLOW_DAMAGE_MOD;
    public static double ALT_BLOW_CRIT_RATE_MODIFIER;
    public static double ALT_VAMPIRIC_CHANCE;

    public static boolean ALT_REMOVE_SKILLS_ON_DELEVEL;

    public static int LIGHT_CASTLE_SELL_TAX_PERCENT;
    public static int DARK_CASTLE_SELL_TAX_PERCENT;
    public static int LIGHT_CASTLE_BUY_TAX_PERCENT;
    public static int DARK_CASTLE_BUY_TAX_PERCENT;

    public static boolean ALT_PCBANG_POINTS_ENABLED;
    public static boolean PC_BANG_POINTS_BY_ACCOUNT;
    public static boolean ALT_PCBANG_POINTS_ONLY_PREMIUM;
    public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
    public static int ALT_PCBANG_POINTS_BONUS;
    public static int ALT_PCBANG_POINTS_DELAY;
    public static int ALT_PCBANG_POINTS_MIN_LVL;
    public static IntSet ALT_ALLOWED_MULTISELLS_IN_PCBANG = new HashIntSet();

    public static boolean ALT_DEBUG_ENABLED;
    public static boolean ALT_DEBUG_PVP_ENABLED;
    public static boolean ALT_DEBUG_PVP_DUEL_ONLY;
    public static boolean ALT_DEBUG_PVE_ENABLED;

    /** Thread pools size */
    public static int SCHEDULED_THREAD_POOL_SIZE;
    public static int EXECUTOR_THREAD_POOL_SIZE;

    public static boolean ENABLE_RUNNABLE_STATS;

    public static boolean AUTO_LOOT;
    public static boolean AUTO_LOOT_HERBS;
    public static boolean AUTO_LOOT_ONLY_ADENA;
    public static boolean AUTO_LOOT_INDIVIDUAL;
    public static boolean AUTO_LOOT_FROM_RAIDS;
    public static IntSet AUTO_LOOT_ITEM_ID_LIST = new HashIntSet();

    /** Auto-loot for/from players with karma also? */
    public static boolean AUTO_LOOT_PK;

    public static int CNAME_MAXLEN = 32;

    public static String APASSWD_TEMPLATE;

    public static boolean BAN_FOR_CFG_USAGE;

    public static int EXCELLENT_SHIELD_BLOCK_CHANCE;
    public static int EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE;

    public static double ALT_RAID_RESPAWN_MULTIPLIER;

    public static int DEFAULT_RAID_MINIONS_RESPAWN_DELAY;

    public static boolean ALLOW_AUGMENTATION;

    public static boolean ALT_ALLOW_DROP_AUGMENTED;

    public static boolean ALT_GAME_UNREGISTER_RECIPE;

    /** Petition manager */
    public static boolean PETITIONING_ALLOWED;
    public static int MAX_PETITIONS_PER_PLAYER;
    public static int MAX_PETITIONS_PENDING;

    /** Show mob stats/droplist to players? */
    public static boolean ALT_GAME_SHOW_DROPLIST;
    public static boolean ALLOW_NPC_SHIFTCLICK;
    public static boolean SHOW_TARGET_PLAYER_INVENTORY_ON_SHIFT_CLICK;
    public static boolean ALLOW_VOICED_COMMANDS;

    public static boolean ALLOW_AUTOHEAL_COMMANDS;
    public static int[] ALT_DISABLED_MULTISELL;
    public static int[] ALT_SHOP_PRICE_LIMITS;
    public static int[] ALT_SHOP_UNALLOWED_ITEMS;

    public static int[] ALT_ALLOWED_PET_POTIONS;

    public static double MIN_ABNORMAL_SUCCESS_RATE;
    public static double MAX_ABNORMAL_SUCCESS_RATE;
    public static boolean ALT_SAVE_UNSAVEABLE;
    public static int ALT_SAVE_EFFECTS_REMAINING_TIME;
    public static boolean ALT_SHOW_REUSE_MSG;
    public static boolean ALT_DELETE_SA_BUFFS;

    /** Титул при создании чара */
    public static boolean CHAR_TITLE;
    public static String ADD_CHAR_TITLE;

    /** Таймаут на использование social action */
    public static boolean ALT_SOCIAL_ACTION_REUSE;

    /** Отключение книг для изучения скилов */
    public static Set<AcquireType> DISABLED_SPELLBOOKS_FOR_ACQUIRE_TYPES;

    /** Alternative gameing - loss of XP on death */
    public static boolean ALT_GAME_DELEVEL;
    public static boolean ALLOW_DELEVEL_COMMAND;

    public static int ALT_MAX_LEVEL;
    public static int ALT_MAX_SUB_LEVEL;

    public static boolean ALT_NO_LASTHIT;

    public static boolean ALT_PET_HEAL_BATTLE_ONLY;

    public static int ALT_BUFF_LIMIT;

    public static int MULTISELL_SIZE;

    public static boolean ALLOW_CHANGE_PASSWORD_COMMAND;
    public static boolean ALLOW_CHANGE_PHONE_NUMBER_COMMAND;

    public static boolean FORCIBLY_SPECIFY_PHONE_NUMBER;

    public static int SERVICES_CHANGE_NICK_COLOR_PRICE;
    public static int SERVICES_CHANGE_NICK_COLOR_ITEM;
    public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;
    public static boolean SERVICES_BASH_ENABLED;
    public static boolean SERVICES_BASH_SKIP_DOWNLOAD;
    public static int SERVICES_BASH_RELOAD_TIME;

    public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;
    public static int SERVICES_EXPAND_INVENTORY_PRICE;
    public static int SERVICES_EXPAND_INVENTORY_ITEM;
    public static int SERVICES_EXPAND_INVENTORY_MAX;

    public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;
    public static int SERVICES_EXPAND_WAREHOUSE_PRICE;
    public static int SERVICES_EXPAND_WAREHOUSE_ITEM;

    public static boolean SERVICES_EXPAND_CWH_ENABLED;
    public static int SERVICES_EXPAND_CWH_PRICE;
    public static int SERVICES_EXPAND_CWH_ITEM;

    public static boolean SERVICES_PARNASSUS_NOTAX;

    public static boolean SERVICES_RIDE_HIRE_ENABLED;

    public static long SERVICES_ROULETTE_MIN_BET;
    public static long SERVICES_ROULETTE_MAX_BET;

    public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
    public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;

    public static boolean ALLOW_IP_LOCK;
    public static boolean AUTO_LOCK_IP_ON_LOGIN;
    public static boolean ALLOW_HWID_LOCK;
    public static boolean AUTO_LOCK_HWID_ON_LOGIN;
    public static int HWID_LOCK_MASK;

    /** Olympaid Comptetition Period */
    public static long ALT_OLY_CPERIOD;
    /** Olympaid Weekly Period */
    public static long ALT_OLY_WPERIOD;
    /** Olympaid Validation Period */
    public static long ALT_OLY_VPERIOD;

    public static boolean CLASSED_GAMES_ENABLED;
    public static long OLYMPIAD_REGISTRATION_DELAY;

    public static boolean ENABLE_OLYMPIAD;
    public static boolean ENABLE_OLYMPIAD_SPECTATING;
    public static SchedulingPattern OLYMIAD_END_PERIOD_TIME;
    public static SchedulingPattern OLYMPIAD_START_TIME;

    public static int OLYMPIAD_MIN_LEVEL;

    public static int CLASS_GAME_MIN;
    public static int NONCLASS_GAME_MIN;

    public static int GAME_MAX_LIMIT;
    public static int GAME_CLASSES_COUNT_LIMIT;
    public static int GAME_NOCLASSES_COUNT_LIMIT;


    public static int ALT_OLY_BATTLE_REWARD_ITEM;
    public static int OLYMPIAD_CLASSED_WINNER_REWARD_COUNT;
    public static int OLYMPIAD_NONCLASSED_WINNER_REWARD_COUNT;
    public static int OLYMPIAD_CLASSED_LOOSER_REWARD_COUNT;
    public static int OLYMPIAD_NONCLASSED_LOOSER_REWARD_COUNT;
    public static int ALT_OLY_COMP_RITEM;
    public static int ALT_OLY_GP_PER_POINT;
    public static int ALT_OLY_HERO_POINTS;
    public static int ALT_OLY_RANK1_POINTS;
    public static int ALT_OLY_RANK2_POINTS;
    public static int ALT_OLY_RANK3_POINTS;
    public static int ALT_OLY_RANK4_POINTS;
    public static int ALT_OLY_RANK5_POINTS;
    public static int OLYMPIAD_ALL_LOOSE_POINTS_BONUS;
    public static int OLYMPIAD_1_OR_MORE_WIN_POINTS_BONUS;
    public static int OLYMPIAD_STADIAS_COUNT;
    public static int OLYMPIAD_BATTLES_FOR_REWARD;
    public static int OLYMPIAD_POINTS_DEFAULT;
    public static int OLYMPIAD_POINTS_WEEKLY;
    public static boolean OLYMPIAD_OLDSTYLE_STAT;
    public static int OLYMPIAD_BEGINIG_DELAY;

    public static long NONOWNER_ITEM_PICKUP_DELAY;

    public static Map<Integer, PlayerAccess> gmlist = new HashMap<Integer, PlayerAccess>();

    public static boolean EX_USE_QUEST_REWARD_PENALTY_PER;
    public static int EX_F2P_QUEST_REWARD_PENALTY_PER;
    public static IntSet EX_F2P_QUEST_REWARD_PENALTY_QUESTS;

    public static int RATE_FISH_DROP_COUNT;

    /** Player Drop Rate control */
    public static boolean KARMA_DROP_GM;
    public static boolean KARMA_NEEDED_TO_DROP;

    public static int RATE_KARMA_LOST_STATIC;

    public static int KARMA_DROP_ITEM_LIMIT;

    public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;

    public static double KARMA_DROPCHANCE_BASE;
    public static double KARMA_DROPCHANCE_MOD;
    public static double NORMAL_DROPCHANCE_BASE;
    public static int DROPCHANCE_EQUIPMENT;
    public static int DROPCHANCE_EQUIPPED_WEAPON;
    public static int DROPCHANCE_ITEM;

    public static int AUTODESTROY_ITEM_AFTER;
    public static int AUTODESTROY_PLAYER_ITEM_AFTER;

    public static int CHARACTER_DELETE_AFTER_HOURS;

    public static File GEODATA_ROOT;

    public static double BUFFTIME_MODIFIER;
    public static int[] BUFFTIME_MODIFIER_SKILLS;
    public static double CLANHALL_BUFFTIME_MODIFIER;
    public static double SONGDANCETIME_MODIFIER;

    public static double MAXLOAD_MODIFIER;
    public static double GATEKEEPER_MODIFIER;
    public static int GATEKEEPER_FREE;
    public static int CRUMA_GATEKEEPER_LVL;

    public static double ALT_CHAMPION_CHANCE1;
    public static double ALT_CHAMPION_CHANCE2;
    public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
    public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
    public static int ALT_CHAMPION_MIN_LEVEL;
    public static int ALT_CHAMPION_TOP_LEVEL;

    public static boolean ALLOW_DISCARDITEM;
    public static boolean ALLOW_MAIL;
    public static boolean ALLOW_WAREHOUSE;
    public static boolean ALLOW_WATER;

    public static boolean ALLOW_ITEMS_REFUND;


    /** protocol revision */
    public static IntSet AVAILABLE_PROTOCOL_REVISIONS;

    /** random animation interval */
    public static int MIN_NPC_ANIMATION;
    public static int MAX_NPC_ANIMATION;

    public static boolean USE_CLIENT_LANG;

    /** Время запланированного на определенное время суток рестарта */
    public static String RESTART_AT_TIME;

    public static boolean RETAIL_MULTISELL_ENCHANT_TRANSFER;

    // Security
    public static boolean EX_SECOND_AUTH_ENABLED;
    public static int EX_SECOND_AUTH_MAX_ATTEMPTS;
    public static int EX_SECOND_AUTH_BAN_TIME;

    public static boolean EX_USE_PREMIUM_HENNA_SLOT;
    public static boolean EX_USE_AUTO_SOUL_SHOT;
    public static boolean EX_USE_TO_DO_LIST;
    public static boolean EX_USE_PLEDGE_BONUS;

    public static boolean ALT_EASY_RECIPES;
    public static boolean ALT_USE_TRANSFORM_IN_EPIC_ZONE;
    public static boolean ALT_ANNONCE_RAID_BOSSES_REVIVAL;

    public static boolean SPAWN_VITAMIN_MANAGER;

    public static IntObjectMap<int[]> ALLOW_CLASS_MASTERS_LIST = new HashIntObjectMap<int[]>();
    public static boolean ALLOW_EVENT_GATEKEEPER;

    /** Inventory slots limits */
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    public static int INVENTORY_MAXIMUM_DWARF;
    public static int INVENTORY_MAXIMUM_GM;
    public static int QUEST_INVENTORY_MAXIMUM;

    /** Warehouse slots limits */
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    public static int WAREHOUSE_SLOTS_DWARF;
    public static int WAREHOUSE_SLOTS_CLAN;

    public static int FREIGHT_SLOTS;

    /** Spoil Rates */
    public static double BASE_SPOIL_RATE;
    public static double MINIMUM_SPOIL_RATE;
    public static boolean SHOW_HTML_WELCOME;

    /** Karma System Variables */
    public static int KARMA_MIN_KARMA;
    public static int KARMA_RATE_KARMA_LOST;
    public static int KARMA_LOST_BASE;
    public static int KARMA_PENALTY_START_KARMA;
    public static int KARMA_PENALTY_DURATION_DEFAULT;
    public static double KARMA_PENALTY_DURATION_INCREASE;
    public static int KARMA_DOWN_TIME_MULTIPLE;
    public static int KARMA_CRIMINAL_DURATION_MULTIPLE;

    public static int MIN_PK_TO_ITEMS_DROP;
    public static boolean DROP_ITEMS_ON_DIE;
    public static boolean DROP_ITEMS_AUGMENTED;

    public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();
    public static List<RaidGlobalDrop> RAID_GLOBAL_DROP = new ArrayList<RaidGlobalDrop>();

    public static List<Integer> LIST_OF_SELLABLE_ITEMS = new ArrayList<Integer>();
    public static List<Integer> LIST_OF_TRABLE_ITEMS = new ArrayList<Integer>();

    public static int PVP_TIME;

    /** Karma Punishment */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;

    public static boolean REGEN_SIT_WAIT;

    public static int RAID_MAX_LEVEL_DIFF;
    public static boolean PARALIZE_ON_RAID_DIFF;

    public static int STARTING_LVL;
    public static long STARTING_SP;

    /** Deep Blue Mobs' Drop Rules Enabled */
    public static boolean DEEPBLUE_DROP_RULES;
    public static int DEEPBLUE_DROP_MAXDIFF;
    public static int DEEPBLUE_DROP_RAID_MAXDIFF;
    public static boolean UNSTUCK_SKILL;

    /** telnet enabled */
    public static boolean IS_TELNET_ENABLED;
    public static String TELNET_DEFAULT_ENCODING;
    public static String TELNET_PASSWORD;
    public static String TELNET_HOSTNAME;
    public static int TELNET_PORT;

    /** Percent CP is restore on respawn */
    public static double RESPAWN_RESTORE_CP;
    /** Percent HP is restore on respawn */
    public static double RESPAWN_RESTORE_HP;
    /** Percent MP is restore on respawn */
    public static double RESPAWN_RESTORE_MP;

    /** Maximum number of available slots for pvt stores (sell/buy) - Dwarves */
    public static int MAX_PVTSTORE_SLOTS_DWARF;
    /** Maximum number of available slots for pvt stores (sell/buy) - Others */
    public static int MAX_PVTSTORE_SLOTS_OTHER;
    public static int MAX_PVTCRAFT_SLOTS;

    public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;
    public static double SENDSTATUS_TRADE_MOD;

    public static boolean ALT_CH_UNLIM_MP;
    public static boolean ALT_NO_FAME_FOR_DEAD;

    public static double RESIDENCE_LEASE_FUNC_MULTIPLIER;

    public static int GM_NAME_COLOUR;
    public static boolean GM_HERO_AURA;
    public static int NORMAL_NAME_COLOUR;
    public static int CLANLEADER_NAME_COLOUR;

    /** AI */
    public static int AI_TASK_MANAGER_COUNT;
    public static long AI_TASK_ATTACK_DELAY;
    public static long AI_TASK_ACTIVE_DELAY;
    public static boolean BLOCK_ACTIVE_TASKS;
    public static boolean ALWAYS_TELEPORT_HOME;
    public static boolean RND_WALK;
    public static int RND_WALK_RATE;
    public static int RND_ANIMATION_RATE;

    public static int AGGRO_CHECK_INTERVAL;
    public static long NONAGGRO_TIME_ONTELEPORT;
    public static long NONPVP_TIME_ONTELEPORT;

    /** Maximum range mobs can randomly go from spawn point */
    public static int MAX_DRIFT_RANGE;

    /** Maximum range mobs can pursue agressor from spawn point */
    public static int MAX_PURSUE_RANGE;
    public static int MAX_PURSUE_UNDERGROUND_RANGE;
    public static int MAX_PURSUE_RANGE_RAID;

    public static boolean AUTO_LEARN_SKILLS;

    public static int MOVE_PACKET_DELAY;
    public static int ATTACK_PACKET_DELAY;

    public static boolean DAMAGE_FROM_FALLING;

    /** Community Board */
    public static boolean BBS_ENABLED;
    public static String BBS_DEFAULT_PAGE;
    public static String BBS_COPYRIGHT;
    public static boolean BBS_WAREHOUSE_ENABLED;
    public static boolean BBS_SELL_ITEMS_ENABLED;
    public static boolean BBS_AUGMENTATION_ENABLED;

    /** Wedding Options */
    public static boolean ALLOW_WEDDING;
    public static int WEDDING_PRICE;
    public static boolean WEDDING_PUNISH_INFIDELITY;

    public static int WEDDING_TELEPORT_PRICE;
    public static int WEDDING_TELEPORT_INTERVAL;
    public static boolean WEDDING_SAMESEX;
    public static boolean WEDDING_FORMALWEAR;
    public static int WEDDING_DIVORCE_COSTS;

    public static int FOLLOW_RANGE;

    public static int ALT_PET_INVENTORY_LIMIT;

    /**limits of stats **/
    public static int LIM_PATK;
    public static int LIM_MATK;
    public static int LIM_PDEF;
    public static int LIM_MDEF;
    public static int LIM_MATK_SPD;
    public static int LIM_PATK_SPD;
    public static int LIM_CRIT_DAM;
    public static int LIM_CRIT;
    public static int LIM_MCRIT;
    public static int LIM_ACCURACY;
    public static int LIM_EVASION;
    public static int LIM_MOVE;
    public static int LIM_FAME;

    public static int HP_LIMIT;
    public static int MP_LIMIT;
    public static int CP_LIMIT;

    public static double PLAYER_P_ATK_MODIFIER;
    public static double PLAYER_M_ATK_MODIFIER;
    public static double ALT_NPC_PATK_MODIFIER;
    public static double ALT_NPC_MATK_MODIFIER;
    public static double ALT_NPC_MAXHP_MODIFIER;
    public static double ALT_NPC_MAXMP_MODIFIER;

    public static boolean ALLOW_TALK_WHILE_SITTING;

    public static int MAXIMUM_MEMBERS_IN_PARTY;

    public static boolean PARTY_LEADER_ONLY_CAN_INVITE;

    //reflect configs
    public static long REFLECT_MIN_RANGE;
    public static double REFLECT_AND_BLOCK_DAMAGE_CHANCE_CAP;
    public static double REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE_CAP;
    public static double REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE_CAP;
    public static double REFLECT_DAMAGE_PERCENT_CAP;
    public static double REFLECT_BOW_DAMAGE_PERCENT_CAP;
    public static double REFLECT_PSKILL_DAMAGE_PERCENT_CAP;
    public static double REFLECT_MSKILL_DAMAGE_PERCENT_CAP;

    public static double SERVICES_TRADE_TAX;
    public static double SERVICES_OFFSHORE_TRADE_TAX;
    public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;
    public static boolean SERVICES_TRADE_ONLY_FAR;
    public static int SERVICES_TRADE_RADIUS;
    public static int SERVICES_TRADE_MIN_LEVEL;

    public static boolean SERVICES_ENABLE_NO_CARRIER;
    public static int SERVICES_NO_CARRIER_DEFAULT_TIME;
    public static int SERVICES_NO_CARRIER_MAX_TIME;
    public static int SERVICES_NO_CARRIER_MIN_TIME;

    public static boolean ALT_SHOW_SERVER_TIME;

    /** Geodata config */
    public static int GEO_X_FIRST, GEO_Y_FIRST, GEO_X_LAST, GEO_Y_LAST;
    public static boolean ALLOW_GEODATA;
    public static boolean ALLOW_FALL_FROM_WALLS;
    public static boolean ALLOW_KEYBOARD_MOVE;
    public static boolean COMPACT_GEO;
    public static int CLIENT_Z_SHIFT;
    public static int MAX_Z_DIFF;
    public static int MIN_LAYER_HEIGHT;
    public static int REGION_EDGE_MAX_Z_DIFF;

    /** Geodata (Pathfind) config */
    public static int PATHFIND_BOOST;
    public static int PATHFIND_MAP_MUL;
    public static boolean PATHFIND_DIAGONAL;
    public static boolean PATH_CLEAN;
    public static int PATHFIND_MAX_Z_DIFF;
    public static long PATHFIND_MAX_TIME;
    public static String PATHFIND_BUFFERS;
    public static int NPC_PATH_FIND_MAX_HEIGHT;
    public static int PLAYABLE_PATH_FIND_MAX_HEIGHT;

    public static boolean DEBUG;

    public static int WEAR_DELAY;

    public static boolean DISABLE_CRYSTALIZATION_ITEMS;

    public static boolean EX_NEW_PETITION_SYSTEM;
    public static boolean EX_JAPAN_MINIGAME;
    public static boolean EX_LECTURE_MARK;

    public static boolean ALLOW_MONSTER_RACE;
    public static boolean ONLY_ONE_SIEGE_PER_CLAN;

    public static boolean ALLOW_USE_DOORMANS_IN_SIEGE_BY_OWNERS;

    public static boolean DISABLE_VAMPIRIC_VS_MOB_ON_PVP;

    public static boolean NPC_RANDOM_ENCHANT;

    public static boolean ENABLE_PARTY_SEARCH;

    //pvp manager
    public static boolean ALLOW_PVP_REWARD;
    public static boolean PVP_REWARD_SEND_SUCC_NOTIF;
    public static int[] PVP_REWARD_REWARD_IDS;
    public static long[] PVP_REWARD_COUNTS;
    public static boolean PVP_REWARD_RANDOM_ONE;
    public static int PVP_REWARD_DELAY_ONE_KILL;
    public static int PVP_REWARD_MIN_PL_PROFF;
    public static int PVP_REWARD_MIN_PL_UPTIME_MINUTE;
    public static int PVP_REWARD_MIN_PL_LEVEL;
    public static boolean PVP_REWARD_PK_GIVE;
    public static boolean PVP_REWARD_ON_EVENT_GIVE;
    public static boolean PVP_REWARD_ONLY_BATTLE_ZONE;

    public static boolean PVP_REWARD_SAME_PARTY_GIVE;
    public static boolean PVP_REWARD_SAME_CLAN_GIVE;
    public static boolean PVP_REWARD_SAME_ALLY_GIVE;
    public static boolean PVP_REWARD_SAME_HWID_GIVE;
    public static boolean PVP_REWARD_SAME_IP_GIVE;
    public static boolean PVP_REWARD_SPECIAL_ANTI_TWINK_TIMER;
    public static int PVP_REWARD_HR_NEW_CHAR_BEFORE_GET_ITEM;
    public static boolean PVP_REWARD_CHECK_EQUIP;
    public static int PVP_REWARD_WEAPON_GRADE_TO_CHECK;
    public static boolean PVP_REWARD_LOG_KILLS;
    public static boolean DISALLOW_MSG_TO_PL;

    public static int ALL_CHAT_USE_MIN_LEVEL;
    public static int ALL_CHAT_USE_MIN_LEVEL_WITHOUT_PA;
    public static int ALL_CHAT_USE_DELAY;
    public static int SHOUT_CHAT_USE_MIN_LEVEL;
    public static int SHOUT_CHAT_USE_MIN_LEVEL_WITHOUT_PA;
    public static int SHOUT_CHAT_USE_DELAY;
    public static int TRADE_CHAT_USE_MIN_LEVEL;
    public static int TRADE_CHAT_USE_MIN_LEVEL_WITHOUT_PA;
    public static int TRADE_CHAT_USE_DELAY;
    public static int HERO_CHAT_USE_MIN_LEVEL;
    public static int HERO_CHAT_USE_MIN_LEVEL_WITHOUT_PA;
    public static int HERO_CHAT_USE_DELAY;
    public static int PRIVATE_CHAT_USE_MIN_LEVEL;
    public static int PRIVATE_CHAT_USE_MIN_LEVEL_WITHOUT_PA;
    public static int PRIVATE_CHAT_USE_DELAY;
    public static int MAIL_USE_MIN_LEVEL;
    public static int MAIL_USE_MIN_LEVEL_WITHOUT_PA;
    public static int MAIL_USE_DELAY;

    public static int IM_PAYMENT_ITEM_ID;
    public static int IM_MAX_ITEMS_IN_RECENT_LIST;

    public static boolean ALT_SHOW_MONSTERS_LVL;
    public static boolean ALT_SHOW_MONSTERS_AGRESSION;

    public static int BEAUTY_SHOP_COIN_ITEM_ID;

    public static boolean ALT_TELEPORT_TO_TOWN_DURING_SIEGE;

    public static int ALT_CLAN_LEAVE_PENALTY_TIME;
    public static int ALT_CLAN_CREATE_PENALTY_TIME;

    public static int ALT_EXPELLED_MEMBER_PENALTY_TIME;
    public static int ALT_LEAVED_ALLY_PENALTY_TIME;
    public static int ALT_DISSOLVED_ALLY_PENALTY_TIME;

    public static boolean RAID_DROP_GLOBAL_ITEMS;
    public static int MIN_RAID_LEVEL_TO_DROP;

    public static int NPC_DIALOG_PLAYER_DELAY;

    public static double PHYSICAL_MIN_CHANCE_TO_HIT;
    public static double PHYSICAL_MAX_CHANCE_TO_HIT;

    public static double MAGIC_MIN_CHANCE_TO_HIT_MISS;
    public static double MAGIC_MAX_CHANCE_TO_HIT_MISS;

    public static boolean ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC;

    public static double MAX_BLOW_RATE_ON_BEHIND;
    public static double MAX_BLOW_RATE_ON_FRONT_AND_SIDE;

    public static double BLOW_SKILL_CHANCE_MOD_ON_BEHIND;
    public static double BLOW_SKILL_CHANCE_MOD_ON_FRONT;

    public static double BLOW_SKILL_DEX_CHANCE_MOD;
    public static double NORMAL_SKILL_DEX_CHANCE_MOD;


    public static boolean ENABLE_STUN_BREAK_ON_ATTACK;
    public static double CRIT_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL;
    public static double NORMAL_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL;
    public static double CRIT_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL;
    public static double NORMAL_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL;
    public static double CRIT_STUN_BREAK_CHANCE_ON_REGULAR_HIT;
    public static double NORMAL_STUN_BREAK_CHANCE_ON_REGULAR_HIT;


    public static String CLAN_DELETE_TIME;
    public static String CLAN_CHANGE_LEADER_TIME;
    public static int CLAN_MAX_LEVEL;
    public static int[] CLAN_LVL_UP_SP_COST;
    public static int[] CLAN_LVL_UP_RP_COST;
    public static int[] CLAN_LVL_UP_MIN_MEMBERS;
    public static long[][][][] CLAN_LVL_UP_ITEMS_REQUIRED;
    public static boolean[] CLAN_LVL_UP_NEED_CASTLE;
    public static int CLAN_ATTENDANCE_REWARD_1;
    public static int CLAN_ATTENDANCE_REWARD_2;
    public static int CLAN_ATTENDANCE_REWARD_3;
    public static int CLAN_ATTENDANCE_REWARD_4;
    public static int CLAN_HUNTING_REWARD_1;
    public static int CLAN_HUNTING_REWARD_2;
    public static int CLAN_HUNTING_REWARD_3;
    public static int CLAN_HUNTING_REWARD_4;
    public static double CLAN_HUNTING_PROGRESS_RATE;

    public static int ALT_MUSIC_LIMIT;
    public static int ALT_DEBUFF_LIMIT;
    public static int ALT_TRIGGER_LIMIT;

    // Buffer Scheme NPC
    public static boolean NpcBuffer_VIP;
    public static int NpcBuffer_VIP_ALV;
    public static boolean NpcBuffer_EnableBuff;
    public static boolean NpcBuffer_EnableScheme;
    public static boolean NpcBuffer_EnableHeal;
    public static boolean NpcBuffer_EnableBuffs;
    public static boolean NpcBuffer_EnableResist;
    public static boolean NpcBuffer_EnableSong;
    public static boolean NpcBuffer_EnableDance;
    public static boolean NpcBuffer_EnableChant;
    public static boolean NpcBuffer_EnableOther;
    public static boolean NpcBuffer_EnableSpecial;
    public static boolean NpcBuffer_EnableCubic;
    public static boolean NpcBuffer_EnableCancel;
    public static boolean NpcBuffer_EnableBuffSet;
    public static boolean NpcBuffer_EnableBuffPK;
    public static boolean NpcBuffer_EnableFreeBuffs;
    public static boolean NpcBuffer_EnableTimeOut;
    public static int NpcBuffer_TimeOutTime;
    public static int NpcBuffer_MinLevel;
    public static int NpcBuffer_PriceCancel;
    public static int NpcBuffer_PriceHeal;
    public static int NpcBuffer_PriceBuffs;
    public static int NpcBuffer_PriceResist;
    public static int NpcBuffer_PriceSong;
    public static int NpcBuffer_PriceDance;
    public static int NpcBuffer_PriceChant;
    public static int NpcBuffer_PriceOther;
    public static int NpcBuffer_PriceSpecial;
    public static int NpcBuffer_PriceCubic;
    public static int NpcBuffer_PriceSet;
    public static int NpcBuffer_PriceScheme;
    public static int NpcBuffer_MaxScheme;

    public static int SPECIAL_ITEM_ID;
    public static long SPECIAL_ITEM_COUNT;
    public static double SPECIAL_ITEM_DROP_CHANCE;

    public static int ALT_DELEVEL_ON_DEATH_PENALTY_MIN_LEVEL;

    public static boolean ALT_PETS_NOT_STARVING;

    public static int MAX_ACTIVE_ACCOUNTS_ON_ONE_IP;
    public static String[] MAX_ACTIVE_ACCOUNTS_IGNORED_IP;
    public static int MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID;

    public static int[] MONSTER_LEVEL_DIFF_EXP_PENALTY;

    public static boolean SHOW_TARGET_EFFECTS;

    public static int CANCEL_SKILLS_HIGH_CHANCE_CAP;
    public static int CANCEL_SKILLS_LOW_CHANCE_CAP;

    public static long SP_LIMIT;

    public static int ELEMENT_ATTACK_LIMIT;

    public static double[] PERCENT_LOST_ON_DEATH;
    public static double PERCENT_LOST_ON_DEATH_MOD_IN_PEACE_ZONE;
    public static double PERCENT_LOST_ON_DEATH_MOD_IN_PVP;
    public static double PERCENT_LOST_ON_DEATH_MOD_IN_WAR;
    public static double PERCENT_LOST_ON_DEATH_MOD_FOR_PK;

    public static boolean FISHING_ONLY_PREMIUM_ACCOUNTS;
    public static int FISHING_MINIMUM_LEVEL;

    public static boolean BOTREPORT_ENABLED;
    public static int BOTREPORT_REPORT_DELAY;
    public static String BOTREPORT_REPORTS_RESET_TIME;
    public static boolean BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS;

    public static boolean VIP_ATTENDANCE_REWARDS_ENABLED;
    public static boolean VIP_ATTENDANCE_REWARDS_REWARD_BY_ACCOUNT = true;

    public static boolean ALT_SAVE_PRIVATE_STORE;

    public static boolean EXPERTISE_PENALTY_ENABLED;

    public static boolean MULTICLASS_SYSTEM_ENABLED;
    public static boolean MULTICLASS_SYSTEM_SHOW_LEARN_LIST_ON_OPEN_SKILL_LIST;
    public static double MULTICLASS_SYSTEM_NON_CLASS_SP_MODIFIER;
    public static double MULTICLASS_SYSTEM_1ST_CLASS_SP_MODIFIER;
    public static double MULTICLASS_SYSTEM_2ND_CLASS_SP_MODIFIER;
    public static double MULTICLASS_SYSTEM_3RD_CLASS_SP_MODIFIER;
    public static int MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID_BASED_ON_SP;
    public static int MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID_BASED_ON_SP;
    public static int MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID_BASED_ON_SP;
    public static int MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID_BASED_ON_SP;
    public static double MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
    public static double MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
    public static double MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
    public static double MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP;
    public static int MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID;
    public static int MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID;
    public static int MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID;
    public static int MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID;
    public static long MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT;
    public static long MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT;
    public static long MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT;
    public static long MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT;

    public static int BATTLE_ZONE_AROUND_RAID_BOSSES_RANGE;


    public static boolean TRAINING_CAMP_ENABLE;
    public static boolean TRAINING_CAMP_PREMIUM_ONLY;
    public static int TRAINING_CAMP_MAX_DURATION;
    public static int TRAINING_CAMP_MIN_LEVEL;
    public static int TRAINING_CAMP_MAX_LEVEL;

    public static void loadServerConfig()
    {
        ExProperties serverSettings = load(SERVER_CONFIGURATION_FILE);

        RAID_MAX_LEVEL_DIFF = serverSettings.getProperty("RaidMaxLevelDiff", 8);
        PARALIZE_ON_RAID_DIFF = serverSettings.getProperty("ParalizeOnRaidLevelDiff", true);

        AUTODESTROY_ITEM_AFTER = serverSettings.getProperty("AutoDestroyDroppedItemAfter", 0);
        AUTODESTROY_PLAYER_ITEM_AFTER = serverSettings.getProperty("AutoDestroyPlayerDroppedItemAfter", 0);
        CHARACTER_DELETE_AFTER_HOURS = serverSettings.getProperty("DeleteCharAfterHours", 168);

        ALLOW_DISCARDITEM = serverSettings.getProperty("AllowDiscardItem", true);
        ALLOW_MAIL = serverSettings.getProperty("AllowMail", true);
        ALLOW_WAREHOUSE = serverSettings.getProperty("AllowWarehouse", true);
        ALLOW_WATER = serverSettings.getProperty("AllowWater", true);
        ALLOW_ITEMS_REFUND = serverSettings.getProperty("ALLOW_ITEMS_REFUND", true);;

        AVAILABLE_PROTOCOL_REVISIONS = new HashIntSet();
        AVAILABLE_PROTOCOL_REVISIONS.addAll(serverSettings.getProperty("AvailableProtocolRevisions", new int[0]));

        MIN_NPC_ANIMATION = serverSettings.getProperty("MinNPCAnimation", 5);
        MAX_NPC_ANIMATION = serverSettings.getProperty("MaxNPCAnimation", 90);

        AUTOSAVE = serverSettings.getProperty("Autosave", true);

        MAXIMUM_ONLINE_USERS = serverSettings.getProperty("MaximumOnlineUsers", 3000);

        DATABASE_AUTOUPDATE = serverSettings.getProperty("DATABASE_AUTOUPDATE", false);

        USER_INFO_INTERVAL = serverSettings.getProperty("UserInfoInterval", 100L);
        BROADCAST_CHAR_INFO_INTERVAL = serverSettings.getProperty("BroadcastCharInfoInterval", 100L);

        EFFECT_TASK_MANAGER_COUNT = serverSettings.getProperty("EffectTaskManagers", 2);

        SCHEDULED_THREAD_POOL_SIZE = serverSettings.getProperty("ScheduledThreadPoolSize", NCPUS * 4);
        EXECUTOR_THREAD_POOL_SIZE = serverSettings.getProperty("ExecutorThreadPoolSize", NCPUS * 2);

        ENABLE_RUNNABLE_STATS = serverSettings.getProperty("EnableRunnableStats", false);

        CHAT_MESSAGE_MAX_LEN = serverSettings.getProperty("ChatMessageLimit", 1000);
        ABUSEWORD_BANCHAT = serverSettings.getProperty("ABUSEWORD_BANCHAT", false);
        int counter = 0;
        for(int id : serverSettings.getProperty("ABUSEWORD_BAN_CHANNEL", new int[] { 0 }))
        {
            BAN_CHANNEL_LIST[counter] = id;
            counter++;
        }
        ABUSEWORD_REPLACE = serverSettings.getProperty("ABUSEWORD_REPLACE", false);
        ABUSEWORD_REPLACE_STRING = serverSettings.getProperty("ABUSEWORD_REPLACE_STRING", "_-_");
        BANCHAT_ANNOUNCE = serverSettings.getProperty("BANCHAT_ANNOUNCE", true);
        BANCHAT_ANNOUNCE_FOR_ALL_WORLD = serverSettings.getProperty("BANCHAT_ANNOUNCE_FOR_ALL_WORLD", true);
        BANCHAT_ANNOUNCE_NICK = serverSettings.getProperty("BANCHAT_ANNOUNCE_NICK", true);
        ABUSEWORD_BANTIME = serverSettings.getProperty("ABUSEWORD_UNBAN_TIMER", 30);

        USE_CLIENT_LANG = serverSettings.getProperty("UseClientLang", false);

        RESTART_AT_TIME = serverSettings.getProperty("AutoRestartAt", "0 5 * * *");
        SHIFT_BY = serverSettings.getProperty("HShift", 12);

        RETAIL_MULTISELL_ENCHANT_TRANSFER = serverSettings.getProperty("RetailMultisellItemExchange", true);
        SHIFT_BY_Z = serverSettings.getProperty("VShift", 11);
        MAP_MIN_Z = serverSettings.getProperty("MapMinZ", -32768);
        MAP_MAX_Z = serverSettings.getProperty("MapMaxZ", 32767);

        MOVE_PACKET_DELAY = serverSettings.getProperty("MovePacketDelay", 100);
        ATTACK_PACKET_DELAY = serverSettings.getProperty("AttackPacketDelay", 500);

        DAMAGE_FROM_FALLING = serverSettings.getProperty("DamageFromFalling", true);

        ALLOW_WEDDING = serverSettings.getProperty("AllowWedding", false);
        WEDDING_PRICE = serverSettings.getProperty("WeddingPrice", 500000);
        WEDDING_PUNISH_INFIDELITY = serverSettings.getProperty("WeddingPunishInfidelity", true);

        WEDDING_TELEPORT_PRICE = serverSettings.getProperty("WeddingTeleportPrice", 500000);
        WEDDING_TELEPORT_INTERVAL = serverSettings.getProperty("WeddingTeleportInterval", 120);
        WEDDING_SAMESEX = serverSettings.getProperty("WeddingAllowSameSex", true);
        WEDDING_FORMALWEAR = serverSettings.getProperty("WeddingFormalWear", true);
        WEDDING_DIVORCE_COSTS = serverSettings.getProperty("WeddingDivorceCosts", 20);

        DONTLOADSPAWN = serverSettings.getProperty("StartWithoutSpawn", false);
        DONTLOADQUEST = serverSettings.getProperty("StartWithoutQuest", false);

        MAX_REFLECTIONS_COUNT = serverSettings.getProperty("MaxReflectionsCount", 300);

        WEAR_DELAY = serverSettings.getProperty("WearDelay", 5);

        HTM_CACHE_MODE = serverSettings.getProperty("HtmCacheMode", HtmCache.LAZY);
        SHUTDOWN_ANN_TYPE = serverSettings.getProperty("ShutdownAnnounceType", Shutdown.OFFLIKE_ANNOUNCES);
        APASSWD_TEMPLATE = serverSettings.getProperty("PasswordTemplate", "[A-Za-z0-9]{4,16}");

        ALLOW_MONSTER_RACE = serverSettings.getProperty("AllowMonsterRace", false);

        MAX_ACTIVE_ACCOUNTS_ON_ONE_IP = serverSettings.getProperty("MAX_ACTIVE_ACCOUNTS_ON_ONE_IP", -1);
        MAX_ACTIVE_ACCOUNTS_IGNORED_IP = serverSettings.getProperty("MAX_ACTIVE_ACCOUNTS_IGNORED_IP", new String[0], ";");
        MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID = serverSettings.getProperty("MAX_ACTIVE_ACCOUNTS_ON_ONE_HWID", -1);
    }

    public static void loadTelnetConfig()
    {
        ExProperties telnetSettings = load(TELNET_CONFIGURATION_FILE);

        IS_TELNET_ENABLED = telnetSettings.getProperty("EnableTelnet", false);
        TELNET_DEFAULT_ENCODING = telnetSettings.getProperty("TelnetEncoding", "UTF-8");
        TELNET_PORT = telnetSettings.getProperty("Port", 7000);
        TELNET_HOSTNAME = telnetSettings.getProperty("BindAddress", "127.0.0.1");
        TELNET_PASSWORD = telnetSettings.getProperty("Password", "");
    }

    public static void loadResidenceConfig()
    {
        ExProperties residenceSettings = load(RESIDENCE_CONFIG_FILE);

        RESIDENCE_LEASE_FUNC_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseFuncMultiplier", 1.);

        LIGHT_CASTLE_SELL_TAX_PERCENT = residenceSettings.getProperty("LIGHT_CASTLE_SELL_TAX_PERCENT", 0);
        DARK_CASTLE_SELL_TAX_PERCENT = residenceSettings.getProperty("DARK_CASTLE_SELL_TAX_PERCENT", 15);
        LIGHT_CASTLE_BUY_TAX_PERCENT = residenceSettings.getProperty("LIGHT_CASTLE_BUY_TAX_PERCENT", 5);
        DARK_CASTLE_BUY_TAX_PERCENT = residenceSettings.getProperty("DARK_CASTLE_BUY_TAX_PERCENT", 10);
    }

    public static void loadAntiFloodConfig()
    {
        ExProperties properties = load(ANTIFLOOD_CONFIG_FILE);

        ALL_CHAT_USE_MIN_LEVEL = properties.getProperty("ALL_CHAT_USE_MIN_LEVEL", 1);
        ALL_CHAT_USE_MIN_LEVEL_WITHOUT_PA = properties.getProperty("ALL_CHAT_USE_MIN_LEVEL_WITHOUT_PA", 1);
        ALL_CHAT_USE_DELAY = properties.getProperty("ALL_CHAT_USE_DELAY", 0);

        SHOUT_CHAT_USE_MIN_LEVEL = properties.getProperty("SHOUT_CHAT_USE_MIN_LEVEL", 1);
        SHOUT_CHAT_USE_MIN_LEVEL_WITHOUT_PA = properties.getProperty("SHOUT_CHAT_USE_MIN_LEVEL_WITHOUT_PA", 1);
        SHOUT_CHAT_USE_DELAY = properties.getProperty("SHOUT_CHAT_USE_DELAY", 0);

        TRADE_CHAT_USE_MIN_LEVEL = properties.getProperty("TRADE_CHAT_USE_MIN_LEVEL", 1);
        TRADE_CHAT_USE_MIN_LEVEL_WITHOUT_PA = properties.getProperty("TRADE_CHAT_USE_MIN_LEVEL_WITHOUT_PA", 1);
        TRADE_CHAT_USE_DELAY = properties.getProperty("TRADE_CHAT_USE_DELAY", 0);

        HERO_CHAT_USE_MIN_LEVEL = properties.getProperty("HERO_CHAT_USE_MIN_LEVEL", 1);
        HERO_CHAT_USE_MIN_LEVEL_WITHOUT_PA = properties.getProperty("HERO_CHAT_USE_MIN_LEVEL_WITHOUT_PA", 1);
        HERO_CHAT_USE_DELAY = properties.getProperty("HERO_CHAT_USE_DELAY", 0);

        PRIVATE_CHAT_USE_MIN_LEVEL = properties.getProperty("PRIVATE_CHAT_USE_MIN_LEVEL", 1);
        PRIVATE_CHAT_USE_MIN_LEVEL_WITHOUT_PA = properties.getProperty("PRIVATE_CHAT_USE_MIN_LEVEL_WITHOUT_PA", 1);
        PRIVATE_CHAT_USE_DELAY = properties.getProperty("PRIVATE_CHAT_USE_DELAY", 0);

        MAIL_USE_MIN_LEVEL = properties.getProperty("MAIL_USE_MIN_LEVEL", 1);
        MAIL_USE_MIN_LEVEL_WITHOUT_PA = properties.getProperty("MAIL_USE_MIN_LEVEL_WITHOUT_PA", 1);
        MAIL_USE_DELAY = properties.getProperty("MAIL_USE_DELAY", 0);
    }

    public static void loadOtherConfig()
    {
        ExProperties otherSettings = load(OTHER_CONFIG_FILE);

        DEEPBLUE_DROP_RULES = otherSettings.getProperty("UseDeepBlueDropRules", true);
        DEEPBLUE_DROP_MAXDIFF = otherSettings.getProperty("DeepBlueDropMaxDiff", 8);
        DEEPBLUE_DROP_RAID_MAXDIFF = otherSettings.getProperty("DeepBlueDropRaidMaxDiff", 2);

        /* Inventory slots limits */
        INVENTORY_MAXIMUM_NO_DWARF = otherSettings.getProperty("MaximumSlotsForNoDwarf", 80);
        INVENTORY_MAXIMUM_DWARF = otherSettings.getProperty("MaximumSlotsForDwarf", 100);
        INVENTORY_MAXIMUM_GM = otherSettings.getProperty("MaximumSlotsForGMPlayer", 250);
        QUEST_INVENTORY_MAXIMUM = otherSettings.getProperty("MaximumSlotsForQuests", 100);

        MULTISELL_SIZE = otherSettings.getProperty("MultisellPageSize", 40);

        /* Warehouse slots limits */
        WAREHOUSE_SLOTS_NO_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForNoDwarf", 100);
        WAREHOUSE_SLOTS_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForDwarf", 120);
        WAREHOUSE_SLOTS_CLAN = otherSettings.getProperty("MaximumWarehouseSlotsForClan", 200);
        FREIGHT_SLOTS = otherSettings.getProperty("MaximumFreightSlots", 10);

        REGEN_SIT_WAIT = otherSettings.getProperty("RegenSitWait", false);
        UNSTUCK_SKILL = otherSettings.getProperty("UnstuckSkill", true);

        /* Amount of HP, MP, and CP is restored */
        RESPAWN_RESTORE_CP = otherSettings.getProperty("RespawnRestoreCP", 0.) / 100;
        RESPAWN_RESTORE_HP = otherSettings.getProperty("RespawnRestoreHP", 65.) / 100;
        RESPAWN_RESTORE_MP = otherSettings.getProperty("RespawnRestoreMP", 0.) / 100;

        /* Maximum number of available slots for pvt stores */
        MAX_PVTSTORE_SLOTS_DWARF = otherSettings.getProperty("MaxPvtStoreSlotsDwarf", 5);
        MAX_PVTSTORE_SLOTS_OTHER = otherSettings.getProperty("MaxPvtStoreSlotsOther", 4);
        MAX_PVTCRAFT_SLOTS = otherSettings.getProperty("MaxPvtManufactureSlots", 20);

        SENDSTATUS_TRADE_JUST_OFFLINE = otherSettings.getProperty("SendStatusTradeJustOffline", false);
        SENDSTATUS_TRADE_MOD = otherSettings.getProperty("SendStatusTradeMod", 1.);


        GM_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("GMNameColour", "FFFFFF"));
        GM_HERO_AURA = otherSettings.getProperty("GMHeroAura", false);
        NORMAL_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("NormalNameColour", "FFFFFF"));
        CLANLEADER_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("ClanleaderNameColour", "FFFFFF"));
        SHOW_HTML_WELCOME = otherSettings.getProperty("ShowHTMLWelcome", false);

        MONSTER_LEVEL_DIFF_EXP_PENALTY = otherSettings.getProperty("MONSTER_LEVEL_DIFF_EXP_PENALTY", new int[] { 0, 0, 0, 3, 20, 39, 63, 78, 87, 92, 95, 100 });
    }

    public static void loadSpoilConfig()
    {
        ExProperties spoilSettings = load(SPOIL_CONFIG_FILE);

        BASE_SPOIL_RATE = spoilSettings.getProperty("BasePercentChanceOfSpoilSuccess", 78.);
        MINIMUM_SPOIL_RATE = spoilSettings.getProperty("MinimumPercentChanceOfSpoilSuccess", 1.);

    }

    public static void loadFormulasConfig()
    {
        ExProperties formulasSettings = load(FORMULAS_CONFIGURATION_FILE);

        MIN_ABNORMAL_SUCCESS_RATE = formulasSettings.getProperty("MIN_ABNORMAL_SUCCESS_RATE", 10.);
        MAX_ABNORMAL_SUCCESS_RATE = formulasSettings.getProperty("MAX_ABNORMAL_SUCCESS_RATE", 90.);


        LIM_PATK = formulasSettings.getProperty("LimitPatk", 20000);
        LIM_MATK = formulasSettings.getProperty("LimitMAtk", 25000);
        LIM_PDEF = formulasSettings.getProperty("LimitPDef", 15000);
        LIM_MDEF = formulasSettings.getProperty("LimitMDef", 15000);
        LIM_PATK_SPD = formulasSettings.getProperty("LimitPatkSpd", 1500);
        LIM_MATK_SPD = formulasSettings.getProperty("LimitMatkSpd", 1999);
        LIM_CRIT_DAM = formulasSettings.getProperty("LimitCriticalDamage", 500);
        LIM_CRIT = formulasSettings.getProperty("LimitCritical", 500);
        LIM_MCRIT = formulasSettings.getProperty("LimitMCritical", 20);
        LIM_ACCURACY = formulasSettings.getProperty("LimitAccuracy", 200);
        LIM_EVASION = formulasSettings.getProperty("LimitEvasion", 200);
        LIM_MOVE = formulasSettings.getProperty("LimitMove", 250);

        HP_LIMIT = formulasSettings.getProperty("HP_LIMIT", 150000);
        MP_LIMIT = formulasSettings.getProperty("MP_LIMIT", -1);
        CP_LIMIT = formulasSettings.getProperty("CP_LIMIT", -1);

        LIM_FAME = formulasSettings.getProperty("LimitFame", 50000);


        PLAYER_P_ATK_MODIFIER = formulasSettings.getProperty("PLAYER_P_ATK_MODIFIER", 1.0);
        PLAYER_M_ATK_MODIFIER = formulasSettings.getProperty("PLAYER_M_ATK_MODIFIER", 1.0);

        ALT_NPC_PATK_MODIFIER = formulasSettings.getProperty("NpcPAtkModifier", 1.0);
        ALT_NPC_MATK_MODIFIER = formulasSettings.getProperty("NpcMAtkModifier", 1.0);
        ALT_NPC_MAXHP_MODIFIER = formulasSettings.getProperty("NpcMaxHpModifier", 1.0);
        ALT_NPC_MAXMP_MODIFIER = formulasSettings.getProperty("NpcMaxMpModifier", 1.0);

        ALT_POLE_DAMAGE_MODIFIER = formulasSettings.getProperty("PoleDamageModifier", 1.0);
        ALT_M_SIMPLE_DAMAGE_MOD = formulasSettings.getProperty("mDamSimpleModifier", 1.0);
        ALT_P_DAMAGE_MOD = formulasSettings.getProperty("pDamMod", 1.0);
        ALT_M_CRIT_DAMAGE_MOD = formulasSettings.getProperty("mCritModifier", 1.0);
        ALT_P_CRIT_DAMAGE_MOD = formulasSettings.getProperty("pCritModifier", 1.0);
        ALT_P_CRIT_CHANCE_MOD = formulasSettings.getProperty("pCritModifierChance", 1.0);
        ALT_M_CRIT_CHANCE_MOD = formulasSettings.getProperty("mCritModifierChance", 1.0);

        SERVITOR_P_ATK_MODIFIER = formulasSettings.getProperty("SERVITOR_P_ATK_MODIFIER", 1.0);
        SERVITOR_M_ATK_MODIFIER = formulasSettings.getProperty("SERVITOR_M_ATK_MODIFIER", 1.0);
        SERVITOR_P_DEF_MODIFIER = formulasSettings.getProperty("SERVITOR_P_DEF_MODIFIER", 1.0);
        SERVITOR_M_DEF_MODIFIER = formulasSettings.getProperty("SERVITOR_M_DEF_MODIFIER", 1.0);
        SERVITOR_P_SKILL_POWER_MODIFIER = formulasSettings.getProperty("SERVITOR_P_SKILL_POWER_MODIFIER", 1.0);
        SERVITOR_M_SKILL_POWER_MODIFIER = formulasSettings.getProperty("SERVITOR_M_SKILL_POWER_MODIFIER", 1.0);

        ALT_BLOW_DAMAGE_MOD = formulasSettings.getProperty("blowDamageModifier", 1.0);
        ALT_BLOW_CRIT_RATE_MODIFIER = formulasSettings.getProperty("blowCritRateModifier", 1.0);
        ALT_VAMPIRIC_CHANCE = formulasSettings.getProperty("vampiricChance", 20.0);

        REFLECT_MIN_RANGE = formulasSettings.getProperty("ReflectMinimumRange", 600);
        REFLECT_AND_BLOCK_DAMAGE_CHANCE_CAP = formulasSettings.getProperty("reflectAndBlockDamCap", 60.);
        REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE_CAP = formulasSettings.getProperty("reflectAndBlockPSkillDamCap", 60.);
        REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE_CAP = formulasSettings.getProperty("reflectAndBlockMSkillDamCap", 60.);
        REFLECT_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectDamCap", 60.);
        REFLECT_BOW_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectBowDamCap", 60.);
        REFLECT_PSKILL_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectPSkillDamCap", 60.);
        REFLECT_MSKILL_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectMSkillDamCap", 60.);

        DISABLE_VAMPIRIC_VS_MOB_ON_PVP = formulasSettings.getProperty("disableVampiricAndDrainPvEInPvp", false);
        MIN_HIT_TIME = formulasSettings.getProperty("MinimumHitTime", -1);

        PHYSICAL_MIN_CHANCE_TO_HIT = formulasSettings.getProperty("PHYSICAL_MIN_CHANCE_TO_HIT", 27.5);
        PHYSICAL_MAX_CHANCE_TO_HIT = formulasSettings.getProperty("PHYSICAL_MAX_CHANCE_TO_HIT", 98.0);

        MAGIC_MIN_CHANCE_TO_HIT_MISS = formulasSettings.getProperty("MAGIC_MIN_CHANCE_TO_HIT_MISS", 5.0);
        MAGIC_MAX_CHANCE_TO_HIT_MISS = formulasSettings.getProperty("MAGIC_MAX_CHANCE_TO_HIT_MISS", 95.0);

        ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC = formulasSettings.getProperty("ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC", true);

        MAX_BLOW_RATE_ON_BEHIND = formulasSettings.getProperty("MAX_BLOW_RATE_ON_BEHIND", 100.);
        MAX_BLOW_RATE_ON_FRONT_AND_SIDE = formulasSettings.getProperty("MAX_BLOW_RATE_ON_FRONT_AND_SIDE", 80.);

        BLOW_SKILL_CHANCE_MOD_ON_BEHIND = formulasSettings.getProperty("BLOW_SKILL_CHANCE_MOD_ON_BEHIND", 5.);
        BLOW_SKILL_CHANCE_MOD_ON_FRONT = formulasSettings.getProperty("BLOW_SKILL_CHANCE_MOD_ON_FRONT", 4.);

        BLOW_SKILL_DEX_CHANCE_MOD = formulasSettings.getProperty("BLOW_SKILL_DEX_CHANCE_MOD", 1.);
        NORMAL_SKILL_DEX_CHANCE_MOD = formulasSettings.getProperty("NORMAL_SKILL_DEX_CHANCE_MOD", 1.);

        EXCELLENT_SHIELD_BLOCK_CHANCE = formulasSettings.getProperty("ExcellentShieldBlockChance", 5);
        EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE = formulasSettings.getProperty("ExcellentShieldBlockDamage", 1);

        SKILLS_CAST_TIME_MIN_PHYSICAL = formulasSettings.getProperty("MinCastTimePhysical", 396);
        SKILLS_CAST_TIME_MIN_MAGICAL = formulasSettings.getProperty("MinCastTimeMagical", 333);
        ENABLE_CRIT_HEIGHT_BONUS = formulasSettings.getProperty("EnableCritHeightBonus", true);

        ENABLE_STUN_BREAK_ON_ATTACK = formulasSettings.getProperty("EnableStunBreakOnAttack", true);
        CRIT_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL = formulasSettings.getProperty("CritStunBreakChanceOnMagicSkill", 66.67);
        NORMAL_STUN_BREAK_CHANCE_ON_MAGICAL_SKILL = formulasSettings.getProperty("NormalStunBreakChanceOnMagicSkill", 33.33);
        CRIT_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL = formulasSettings.getProperty("CritStunBreakChanceOnPhysSkill", 66.67);
        NORMAL_STUN_BREAK_CHANCE_ON_PHYSICAL_SKILL = formulasSettings.getProperty("NormalStunBreakChanceOnPhysSkill", 33.33);
        CRIT_STUN_BREAK_CHANCE_ON_REGULAR_HIT = formulasSettings.getProperty("CritStunBreakOnRegularHit", 33.33);
        NORMAL_STUN_BREAK_CHANCE_ON_REGULAR_HIT = formulasSettings.getProperty("NormalStunBreakOnRegularHit", 16.67);

        CANCEL_SKILLS_HIGH_CHANCE_CAP = formulasSettings.getProperty("CANCEL_SKILLS_HIGH_CHANCE_CAP", 75);
        CANCEL_SKILLS_LOW_CHANCE_CAP = formulasSettings.getProperty("CANCEL_SKILLS_LOW_CHANCE_CAP", 5);

        SP_LIMIT = formulasSettings.getProperty("SP_LIMIT", 5000000000L);

        ELEMENT_ATTACK_LIMIT = formulasSettings.getProperty("ELEMENT_ATTACK_LIMIT", 999);
    }



    public static void loadExtSettings()
    {
        ExProperties properties = load(EXT_FILE);

        EX_NEW_PETITION_SYSTEM = properties.getProperty("NewPetitionSystem", false);
        EX_JAPAN_MINIGAME = properties.getProperty("JapanMinigame", false);
        EX_LECTURE_MARK = properties.getProperty("LectureMark", false);

        EX_SECOND_AUTH_ENABLED = properties.getProperty("SecondAuthEnabled", false);
        EX_SECOND_AUTH_MAX_ATTEMPTS = properties.getProperty("SecondAuthMaxAttempts", 5);
        EX_SECOND_AUTH_BAN_TIME = properties.getProperty("SecondAuthBanTime", 480);

        EX_USE_QUEST_REWARD_PENALTY_PER = properties.getProperty("UseQuestRewardPenaltyPer", false);
        EX_F2P_QUEST_REWARD_PENALTY_PER = properties.getProperty("F2PQuestRewardPenaltyPer", 0);
        EX_F2P_QUEST_REWARD_PENALTY_QUESTS = new HashIntSet();
        EX_F2P_QUEST_REWARD_PENALTY_QUESTS.addAll(properties.getProperty("F2PQuestRewardPenaltyQuests", new int[0]));

        EX_USE_PREMIUM_HENNA_SLOT = properties.getProperty("UsePremiumHennaSlot", false);

        VIP_ATTENDANCE_REWARDS_ENABLED = properties.getProperty("UseVIPAttendance", false);

        EX_USE_AUTO_SOUL_SHOT = properties.getProperty("UseAutoSoulShot", true);

        EX_USE_TO_DO_LIST = properties.getProperty("UseToDoList", true);

        EX_USE_PLEDGE_BONUS = properties.getProperty("UsePledgeBonus", true);
    }

    public static void loadBBSSettings()
    {
        ExProperties properties = load(BBS_FILE);

        BBS_ENABLED = properties.getProperty("ENABLED", true);
        BBS_DEFAULT_PAGE = properties.getProperty("DEFAULT_PAGE", "_bbshome");
        BBS_COPYRIGHT = properties.getProperty("COPYRIGHT", "(c) L2j 2019");
        BBS_WAREHOUSE_ENABLED = properties.getProperty("WAREHOUSE_ENABLED", false);
        BBS_SELL_ITEMS_ENABLED = properties.getProperty("SELL_ITEMS_ENABLED", false);
        BBS_AUGMENTATION_ENABLED = properties.getProperty("AUGMENTATION_ENABLED", false);
    }

    public static void loadAltSettings()
    {
        ExProperties altSettings = load(ALT_SETTINGS_FILE);

        STARTING_LVL = altSettings.getProperty("StartingLvl", 1);
        STARTING_SP = altSettings.getProperty("StartingSP", 0L);

        ALT_GAME_DELEVEL = altSettings.getProperty("Delevel", true);
        ALLOW_DELEVEL_COMMAND = altSettings.getProperty("AllowDelevelCommand", false);
        ALT_SAVE_UNSAVEABLE = altSettings.getProperty("AltSaveUnsaveable", false);
        ALT_SAVE_EFFECTS_REMAINING_TIME = altSettings.getProperty("AltSaveEffectsRemainingTime", 5);
        ALT_SHOW_REUSE_MSG = altSettings.getProperty("AltShowSkillReuseMessage", true);
        ALT_DELETE_SA_BUFFS = altSettings.getProperty("AltDeleteSABuffs", false);
        AUTO_LOOT = altSettings.getProperty("AutoLoot", false);
        AUTO_LOOT_HERBS = altSettings.getProperty("AutoLootHerbs", false);
        AUTO_LOOT_ONLY_ADENA = altSettings.getProperty("AutoLootOnlyAdena", false);
        AUTO_LOOT_INDIVIDUAL = altSettings.getProperty("AutoLootIndividual", false);
        AUTO_LOOT_FROM_RAIDS = altSettings.getProperty("AutoLootFromRaids", false);

        String[] autoLootItemIdList = altSettings.getProperty("AutoLootItemIdList", "-1").split(";");
        for(String item : autoLootItemIdList)
        {
            if(item == null || item.isEmpty())
                continue;

            try
            {
                int itemId = Integer.parseInt(item);
                if(itemId > 0)
                    AUTO_LOOT_ITEM_ID_LIST.add(itemId);
            }
            catch(NumberFormatException e)
            {
                _log.error("", e);
            }
        }

        AUTO_LOOT_PK = altSettings.getProperty("AutoLootPK", false);
        ALT_GAME_KARMA_PLAYER_CAN_SHOP = altSettings.getProperty("AltKarmaPlayerCanShop", false);
        SAVING_SPS = altSettings.getProperty("SavingSpS", false);
        MANAHEAL_SPS_BONUS = altSettings.getProperty("ManahealSpSBonus", false);
        ALT_RAID_RESPAWN_MULTIPLIER = altSettings.getProperty("AltRaidRespawnMultiplier", 1.0);
        DEFAULT_RAID_MINIONS_RESPAWN_DELAY = altSettings.getProperty("DEFAULT_RAID_MINIONS_RESPAWN_DELAY", 120);
        ALLOW_AUGMENTATION = altSettings.getProperty("ALLOW_AUGMENTATION", true);
        ALT_ALLOW_DROP_AUGMENTED = altSettings.getProperty("AlowDropAugmented", true);
        ALT_GAME_UNREGISTER_RECIPE = altSettings.getProperty("AltUnregisterRecipe", true);
        ALT_GAME_SHOW_DROPLIST = altSettings.getProperty("AltShowDroplist", true);
        ALLOW_NPC_SHIFTCLICK = altSettings.getProperty("AllowShiftClick", true);
        SHOW_TARGET_PLAYER_INVENTORY_ON_SHIFT_CLICK = altSettings.getProperty("SHOW_TARGET_PLAYER_INVENTORY_ON_SHIFT_CLICK", false);
        ALLOW_VOICED_COMMANDS = altSettings.getProperty("AllowVoicedCommands", true);
        ALLOW_AUTOHEAL_COMMANDS = altSettings.getProperty("ALLOW_AUTOHEAL_COMMANDS", false);

        ALT_MAX_LEVEL = altSettings.getProperty("AltMaxLevel", 75);
        ALT_MAX_SUB_LEVEL = altSettings.getProperty("AltMaxSubLevel", 75);

        ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE = altSettings.getProperty("AltAllowOthersWithdrawFromClanWarehouse", false);
        ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER = altSettings.getProperty("AltAllowClanCommandOnlyForClanLeader", true);

        BAN_FOR_CFG_USAGE = altSettings.getProperty("BanForCfgUsageAgainsBots", false);

        ALT_ADD_RECIPES = altSettings.getProperty("AltAddRecipes", 0);

        PETITIONING_ALLOWED = altSettings.getProperty("PetitioningAllowed", true);
        MAX_PETITIONS_PER_PLAYER = altSettings.getProperty("MaxPetitionsPerPlayer", 5);
        MAX_PETITIONS_PENDING = altSettings.getProperty("MaxPetitionsPending", 25);
        AUTO_LEARN_SKILLS = altSettings.getProperty("AutoLearnSkills", false);
        ALT_SOCIAL_ACTION_REUSE = altSettings.getProperty("AltSocialActionReuse", false);

        DISABLED_SPELLBOOKS_FOR_ACQUIRE_TYPES = new HashSet<AcquireType>();
        for(String t : altSettings.getProperty("DISABLED_SPELLBOOKS_FOR_ACQUIRE_TYPES", STRING_ARRAY_EMPTY))
        {
            if(t.trim().isEmpty())
                continue;

            DISABLED_SPELLBOOKS_FOR_ACQUIRE_TYPES.add(AcquireType.valueOf(t.toUpperCase()));
        }

        ALT_BUFF_LIMIT = altSettings.getProperty("BuffLimit", 20);
        ALT_MUSIC_LIMIT = altSettings.getProperty("ALT_MUSIC_LIMIT", 12);
        ALT_DEBUFF_LIMIT = altSettings.getProperty("ALT_DEBUFF_LIMIT", 12);
        ALT_TRIGGER_LIMIT = altSettings.getProperty("ALT_TRIGGER_LIMIT", 12);

        NONOWNER_ITEM_PICKUP_DELAY = altSettings.getProperty("NonOwnerItemPickupDelay", 15L) * 1000L;
        ALT_NO_LASTHIT = altSettings.getProperty("NoLasthitOnRaid", false);

        ALT_PET_HEAL_BATTLE_ONLY = altSettings.getProperty("PetsHealOnlyInBattle", true);
        CHAR_TITLE = altSettings.getProperty("CharTitle", false);
        ADD_CHAR_TITLE = altSettings.getProperty("CharAddTitle", "");

        ALT_DISABLED_MULTISELL = altSettings.getProperty("DisabledMultisells", INT_ARRAY_EMPTY);
        ALT_SHOP_PRICE_LIMITS = altSettings.getProperty("ShopPriceLimits", INT_ARRAY_EMPTY);
        ALT_SHOP_UNALLOWED_ITEMS = altSettings.getProperty("ShopUnallowedItems", INT_ARRAY_EMPTY);

        ALT_ALLOWED_PET_POTIONS = altSettings.getProperty("AllowedPetPotions", new int[] { 735, 1060, 1061, 1062, 1374, 1375, 1539, 1540, 6035, 6036 });
        Arrays.sort(ALT_ALLOWED_PET_POTIONS);

        MAXIMUM_MEMBERS_IN_PARTY = altSettings.getProperty("MAXIMUM_MEMBERS_IN_PARTY", 9);
        PARTY_LEADER_ONLY_CAN_INVITE = altSettings.getProperty("PartyLeaderOnlyCanInvite", true);
        ALLOW_TALK_WHILE_SITTING = altSettings.getProperty("AllowTalkWhileSitting", true);

        BUFFTIME_MODIFIER = altSettings.getProperty("BuffTimeModifier", 1.0);
        BUFFTIME_MODIFIER_SKILLS = altSettings.getProperty("BuffTimeModifierSkills", new int[0]);
        CLANHALL_BUFFTIME_MODIFIER = altSettings.getProperty("ClanHallBuffTimeModifier", 1.0);
        SONGDANCETIME_MODIFIER = altSettings.getProperty("SongDanceTimeModifier", 1.0);
        MAXLOAD_MODIFIER = altSettings.getProperty("MaxLoadModifier", 1.0);
        GATEKEEPER_MODIFIER = altSettings.getProperty("GkCostMultiplier", 1.0);
        GATEKEEPER_FREE = altSettings.getProperty("GkFree", 40);
        CRUMA_GATEKEEPER_LVL = altSettings.getProperty("GkCruma", 65);

        ALT_CHAMPION_CHANCE1 = altSettings.getProperty("AltChampionChance1", 0.);
        ALT_CHAMPION_CHANCE2 = altSettings.getProperty("AltChampionChance2", 0.);
        ALT_CHAMPION_CAN_BE_AGGRO = altSettings.getProperty("AltChampionAggro", false);
        ALT_CHAMPION_CAN_BE_SOCIAL = altSettings.getProperty("AltChampionSocial", false);
        ALT_CHAMPION_MIN_LEVEL = altSettings.getProperty("AltChampionMinLevel", 40);
        ALT_CHAMPION_TOP_LEVEL = altSettings.getProperty("AltChampionTopLevel", 75);
        SPECIAL_ITEM_ID = altSettings.getProperty("ChampionSpecialItem", 0);
        SPECIAL_ITEM_COUNT = altSettings.getProperty("ChampionSpecialItemCount", 1);
        SPECIAL_ITEM_DROP_CHANCE = altSettings.getProperty("ChampionSpecialItemDropChance", 100.);

        ALT_PCBANG_POINTS_ENABLED = altSettings.getProperty("AltPcBangPointsEnabled", false);
        PC_BANG_POINTS_BY_ACCOUNT = altSettings.getProperty("PC_BANG_POINTS_BY_ACCOUNT", false);
        ALT_PCBANG_POINTS_ONLY_PREMIUM = altSettings.getProperty("AltPcBangPointsOnlyPA", false);
        ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE = altSettings.getProperty("AltPcBangPointsDoubleChance", 10.);
        ALT_PCBANG_POINTS_BONUS = altSettings.getProperty("AltPcBangPointsBonus", 0);
        ALT_PCBANG_POINTS_DELAY = altSettings.getProperty("AltPcBangPointsDelay", 20);
        ALT_PCBANG_POINTS_MIN_LVL = altSettings.getProperty("AltPcBangPointsMinLvl", 1);
        ALT_ALLOWED_MULTISELLS_IN_PCBANG.addAll(altSettings.getProperty("ALT_ALLOWED_MULTISELLS_IN_PCBANG", new int[0]));

        ALT_DEBUG_ENABLED = altSettings.getProperty("AltDebugEnabled", false);
        ALT_DEBUG_PVP_ENABLED = altSettings.getProperty("AltDebugPvPEnabled", false);
        ALT_DEBUG_PVP_DUEL_ONLY = altSettings.getProperty("AltDebugPvPDuelOnly", true);
        ALT_DEBUG_PVE_ENABLED = altSettings.getProperty("AltDebugPvEEnabled", false);

        ALT_MAX_ALLY_SIZE = altSettings.getProperty("AltMaxAllySize", 3);
        ALT_PARTY_DISTRIBUTION_RANGE = altSettings.getProperty("AltPartyDistributionRange", 1500);
        ALT_PARTY_BONUS = altSettings.getProperty("AltPartyBonus", new double[] { 1.00, 1.30, 1.35, 1.40, 1.55, 1.60, 1.70, 1.80, 2.00 });
        ALT_PARTY_CLAN_BONUS = altSettings.getProperty("ALT_PARTY_CLAN_BONUS", new double[] { 1.00 });
        ALT_PARTY_LVL_DIFF_PENALTY = altSettings.getProperty("ALT_PARTY_LVL_DIFF_PENALTY", new int[] { 100, 98, 95, 93, 91, 88, 86, 83, 81, 78, 23, 22, 21, 20, 19, 0 });


        ALT_REMOVE_SKILLS_ON_DELEVEL = altSettings.getProperty("AltRemoveSkillsOnDelevel", true);

        ALT_CH_UNLIM_MP = altSettings.getProperty("ALT_CH_UNLIM_MP", true);
        ALT_NO_FAME_FOR_DEAD = altSettings.getProperty("AltNoFameForDead", false);

        ALT_SHOW_SERVER_TIME = altSettings.getProperty("ShowServerTime", false);

        FOLLOW_RANGE = altSettings.getProperty("FollowRange", 100);

        ALT_PET_INVENTORY_LIMIT = altSettings.getProperty("AltPetInventoryLimit", 12);

        DISABLE_CRYSTALIZATION_ITEMS = altSettings.getProperty("DisableCrystalizationItems", false);

        SUB_START_LEVEL = altSettings.getProperty("SubClassStartLevel", 40);
        START_CLAN_LEVEL = altSettings.getProperty("ClanStartLevel", 0);

        ENABLE_L2_TOP_OVERONLINE = altSettings.getProperty("EnableL2TOPFakeOnline", false);
        L2TOP_MAX_ONLINE = altSettings.getProperty("L2TOPMaxOnline", 3000);
        MIN_ONLINE_0_5_AM = altSettings.getProperty("MinOnlineFrom00to05", 500);
        MAX_ONLINE_0_5_AM = altSettings.getProperty("MaxOnlineFrom00to05", 700);
        MIN_ONLINE_6_11_AM = altSettings.getProperty("MinOnlineFrom06to11", 700);
        MAX_ONLINE_6_11_AM = altSettings.getProperty("MaxOnlineFrom06to11", 1000);
        MIN_ONLINE_12_6_PM = altSettings.getProperty("MinOnlineFrom12to18", 1000);
        MAX_ONLINE_12_6_PM = altSettings.getProperty("MaxOnlineFrom12to18", 1500);
        MIN_ONLINE_7_11_PM = altSettings.getProperty("MinOnlineFrom19to23", 1500);
        MAX_ONLINE_7_11_PM = altSettings.getProperty("MaxOnlineFrom19to23", 2500);
        ADD_ONLINE_ON_SIMPLE_DAY = altSettings.getProperty("AddOnlineIfSimpleDay", 50);
        ADD_ONLINE_ON_WEEKEND = altSettings.getProperty("AddOnlineIfWeekend", 300);
        L2TOP_MIN_TRADERS = altSettings.getProperty("L2TOPMinTraders", 80);
        L2TOP_MAX_TRADERS = altSettings.getProperty("L2TOPMaxTraders", 190);
        ALT_SELL_ITEM_ONE_ADENA = altSettings.getProperty("AltSellItemOneAdena", false);

        MAX_SIEGE_CLANS = altSettings.getProperty("MaxSiegeClans", 20);
        ONLY_ONE_SIEGE_PER_CLAN = altSettings.getProperty("OneClanCanRegisterOnOneSiege", false);

        CLAN_WAR_MINIMUM_CLAN_LEVEL = altSettings.getProperty("CLAN_WAR_MINIMUM_CLAN_LEVEL", 3);
        CLAN_WAR_MINIMUM_PLAYERS_DECLARE = altSettings.getProperty("CLAN_WAR_MINIMUM_PLAYERS_DECLARE", 15);
        CLAN_WAR_PREPARATION_DAYS_PERIOD = altSettings.getProperty("CLAN_WAR_PREPARATION_DAYS_PERIOD", 3);
        CLAN_WAR_REPUTATION_SCORE_PER_KILL = altSettings.getProperty("CLAN_WAR_REPUTATION_SCORE_PER_KILL", 1);

        LIST_OF_SELLABLE_ITEMS = new ArrayList<Integer>();
        for(int id : altSettings.getProperty("ListOfAlwaysSellableItems", new int[] {57}))
            LIST_OF_SELLABLE_ITEMS.add(id);
        LIST_OF_TRABLE_ITEMS = new ArrayList<Integer>();
        for(int id : altSettings.getProperty("ListOfAlwaysTradableItems", new int[] {57}))
            LIST_OF_TRABLE_ITEMS.add(id);

        ALLOW_USE_DOORMANS_IN_SIEGE_BY_OWNERS = altSettings.getProperty("AllowUseDoormansInSiegeByOwners", true);

        NPC_RANDOM_ENCHANT = altSettings.getProperty("NpcRandomEnchant", false);
        ENABLE_PARTY_SEARCH = altSettings.getProperty("AllowPartySearch", false);

        ALT_SHOW_MONSTERS_AGRESSION = altSettings.getProperty("AltShowMonstersAgression", false);
        ALT_SHOW_MONSTERS_LVL = altSettings.getProperty("AltShowMonstersLvL", false);

        ALT_TELEPORT_TO_TOWN_DURING_SIEGE = altSettings.getProperty("ALT_TELEPORT_TO_TOWN_DURING_SIEGE", true);

        ALT_CLAN_LEAVE_PENALTY_TIME = altSettings.getProperty("ALT_CLAN_LEAVE_PENALTY_TIME", 24);
        ALT_CLAN_CREATE_PENALTY_TIME = altSettings.getProperty("ALT_CLAN_CREATE_PENALTY_TIME", 240);

        ALT_EXPELLED_MEMBER_PENALTY_TIME = altSettings.getProperty("ALT_EXPELLED_MEMBER_PENALTY_TIME", 24);
        ALT_LEAVED_ALLY_PENALTY_TIME = altSettings.getProperty("ALT_LEAVED_ALLY_PENALTY_TIME", 24);
        ALT_DISSOLVED_ALLY_PENALTY_TIME = altSettings.getProperty("ALT_DISSOLVED_ALLY_PENALTY_TIME", 24);

        MIN_RAID_LEVEL_TO_DROP = altSettings.getProperty("MinRaidLevelToDropItem", 0);

        RAID_DROP_GLOBAL_ITEMS = altSettings.getProperty("AltEnableGlobalRaidDrop", false);
        String[] infos = altSettings.getProperty("RaidGlobalDrop", new String[0], ";");
        for(String info : infos)
        {
            if(info.isEmpty())
                continue;

            String[] data = info.split(",");
            int id = Integer.parseInt(data[0]);
            long count = Long.parseLong(data[1]);
            double chance = Double.parseDouble(data[2]);
            RAID_GLOBAL_DROP.add(new RaidGlobalDrop(id, count, chance));
        }

        NPC_DIALOG_PLAYER_DELAY = altSettings.getProperty("NpcDialogPlayerDelay", 0);

        CLAN_DELETE_TIME = altSettings.getProperty("CLAN_DELETE_TIME", "0 5 * * 2");
        CLAN_CHANGE_LEADER_TIME = altSettings.getProperty("CLAN_CHANGE_LEADER_TIME", "0 5 * * 2");

        CLAN_MAX_LEVEL = altSettings.getProperty("CLAN_MAX_LEVEL", 11);

        CLAN_LVL_UP_SP_COST = new int[CLAN_MAX_LEVEL + 1];
        for(int i = 1; i < CLAN_LVL_UP_SP_COST.length; i++)
            CLAN_LVL_UP_SP_COST[i] = altSettings.getProperty("CLAN_LVL_UP_SP_COST_" + i, 0);

        CLAN_LVL_UP_RP_COST = new int[CLAN_MAX_LEVEL + 1];
        for(int i = 1; i < CLAN_LVL_UP_RP_COST.length; i++)
            CLAN_LVL_UP_RP_COST[i] = altSettings.getProperty("CLAN_LVL_UP_RP_COST_" + i, 0);

        CLAN_LVL_UP_MIN_MEMBERS = new int[CLAN_MAX_LEVEL + 1];
        for(int i = 1; i < CLAN_LVL_UP_MIN_MEMBERS.length; i++)
            CLAN_LVL_UP_MIN_MEMBERS[i] = altSettings.getProperty("CLAN_LVL_UP_MIN_MEMBERS_" + i, 1);

        CLAN_LVL_UP_ITEMS_REQUIRED = new long[CLAN_MAX_LEVEL + 1][][][]; // TOOD: [Bonux] Сделать поменьше уровней массива..)
        for(int i = 1; i < CLAN_LVL_UP_ITEMS_REQUIRED.length; i++)
        {
            String[] itemsByLvlVariations = altSettings.getProperty("CLAN_LVL_UP_ITEMS_REQUIRED_" + i, "0-0").split("\\|");
            CLAN_LVL_UP_ITEMS_REQUIRED[i] = new long[itemsByLvlVariations.length][][];
            for(int j = 0; j < itemsByLvlVariations.length; j++)
                CLAN_LVL_UP_ITEMS_REQUIRED[i][j] = StringArrayUtils.stringToLong2X(itemsByLvlVariations[j], ";", "-");
        }

        CLAN_LVL_UP_NEED_CASTLE = new boolean[CLAN_MAX_LEVEL + 1];
        for(int i = 1; i < CLAN_LVL_UP_NEED_CASTLE.length; i++)
            CLAN_LVL_UP_NEED_CASTLE[i] = altSettings.getProperty("CLAN_LVL_UP_NEED_CASTLE_" + i, false);

        CLAN_ATTENDANCE_REWARD_1 = altSettings.getProperty("CLAN_ATTENDANCE_REWARD_1", 55168);
        CLAN_ATTENDANCE_REWARD_2 = altSettings.getProperty("CLAN_ATTENDANCE_REWARD_2", 55169);
        CLAN_ATTENDANCE_REWARD_3 = altSettings.getProperty("CLAN_ATTENDANCE_REWARD_3", 55170);
        CLAN_ATTENDANCE_REWARD_4 = altSettings.getProperty("CLAN_ATTENDANCE_REWARD_4", 55171);

        CLAN_HUNTING_REWARD_1 = altSettings.getProperty("CLAN_HUNTING_REWARD_1", 70020);
        CLAN_HUNTING_REWARD_2 = altSettings.getProperty("CLAN_HUNTING_REWARD_2", 70021);
        CLAN_HUNTING_REWARD_3 = altSettings.getProperty("CLAN_HUNTING_REWARD_3", 70022);
        CLAN_HUNTING_REWARD_4 = altSettings.getProperty("CLAN_HUNTING_REWARD_4", 70023);

        CLAN_HUNTING_PROGRESS_RATE = altSettings.getProperty("CLAN_HUNTING_PROGRESS_RATE", 1.0);

        REFLECT_DAMAGE_CAPPED_BY_PDEF = altSettings.getProperty("ReflectDamageCappedByPDef", false);

        ALT_DELEVEL_ON_DEATH_PENALTY_MIN_LEVEL = altSettings.getProperty("ALT_DELEVEL_ON_DEATH_PENALTY_MIN_LEVEL", 10);

        ALT_PETS_NOT_STARVING = altSettings.getProperty("ALT_PETS_NOT_STARVING", false);
        SHOW_TARGET_EFFECTS = altSettings.getProperty("SHOW_TARGET_EFFECTS", false);

        PERCENT_LOST_ON_DEATH = new double[127];
        double prevPercentLost = 0.;
        for(int i = 1; i < PERCENT_LOST_ON_DEATH.length; i++)
        {
            double percent = altSettings.getProperty("PERCENT_LOST_ON_DEATH_LVL_" + i, prevPercentLost);
            PERCENT_LOST_ON_DEATH[i] = percent;
            if(percent != prevPercentLost)
                prevPercentLost = percent;
        }
        PERCENT_LOST_ON_DEATH_MOD_IN_PEACE_ZONE = altSettings.getProperty("PERCENT_LOST_ON_DEATH_MOD_IN_PEACE_ZONE", 0.0);
        PERCENT_LOST_ON_DEATH_MOD_IN_PVP = altSettings.getProperty("PERCENT_LOST_ON_DEATH_MOD_IN_PVP", 1.0);
        PERCENT_LOST_ON_DEATH_MOD_IN_WAR = altSettings.getProperty("PERCENT_LOST_ON_DEATH_MOD_IN_WAR", 0.25);
        PERCENT_LOST_ON_DEATH_MOD_FOR_PK = altSettings.getProperty("PERCENT_LOST_ON_DEATH_MOD_FOR_PK", 1.0);

        ALT_EASY_RECIPES = altSettings.getProperty("EasyRecipiesExtraFeature", false);

        ALT_USE_TRANSFORM_IN_EPIC_ZONE = altSettings.getProperty("ALT_USE_TRANSFORM_IN_EPIC_ZONE", true);

        ALT_ANNONCE_RAID_BOSSES_REVIVAL = altSettings.getProperty("ALT_ANNONCE_RAID_BOSSES_REVIVAL", false);

        ALT_SAVE_PRIVATE_STORE = altSettings.getProperty("ALT_SAVE_PRIVATE_STORE", false);

        EXPERTISE_PENALTY_ENABLED = altSettings.getProperty("EXPERTISE_PENALTY_ENABLED", true);

        MULTICLASS_SYSTEM_ENABLED = altSettings.getProperty("MULTICLASS_SYSTEM_ENABLED", false);
        MULTICLASS_SYSTEM_SHOW_LEARN_LIST_ON_OPEN_SKILL_LIST = altSettings.getProperty("MULTICLASS_SYSTEM_SHOW_LEARN_LIST_ON_OPEN_SKILL_LIST", false);
        MULTICLASS_SYSTEM_NON_CLASS_SP_MODIFIER = altSettings.getProperty("MULTICLASS_SYSTEM_NON_CLASS_SP_MODIFIER", 1.0);
        MULTICLASS_SYSTEM_1ST_CLASS_SP_MODIFIER = altSettings.getProperty("MULTICLASS_SYSTEM_1ST_CLASS_SP_MODIFIER", 1.0);
        MULTICLASS_SYSTEM_2ND_CLASS_SP_MODIFIER = altSettings.getProperty("MULTICLASS_SYSTEM_2ND_CLASS_SP_MODIFIER", 1.0);
        MULTICLASS_SYSTEM_3RD_CLASS_SP_MODIFIER = altSettings.getProperty("MULTICLASS_SYSTEM_3RD_CLASS_SP_MODIFIER", 1.0);
        MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID_BASED_ON_SP", 0);
        MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID_BASED_ON_SP", 0);
        MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID_BASED_ON_SP", 0);
        MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID_BASED_ON_SP", 0);
        MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP", 1.0);
        MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP", 1.0);
        MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP", 1.0);
        MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP = altSettings.getProperty("MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT_MODIFIER_BASED_ON_SP", 1.0);
        MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID = altSettings.getProperty("MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_ID", 0);
        MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID = altSettings.getProperty("MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_ID", 0);
        MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID = altSettings.getProperty("MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_ID", 0);
        MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID = altSettings.getProperty("MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_ID", 0);
        MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT = altSettings.getProperty("MULTICLASS_SYSTEM_NON_CLASS_COST_ITEM_COUNT", 0L);
        MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT = altSettings.getProperty("MULTICLASS_SYSTEM_1ST_CLASS_COST_ITEM_COUNT", 0L);
        MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT = altSettings.getProperty("MULTICLASS_SYSTEM_2ND_CLASS_COST_ITEM_COUNT", 0L);
        MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT = altSettings.getProperty("MULTICLASS_SYSTEM_3RD_CLASS_COST_ITEM_COUNT", 0L);

        BATTLE_ZONE_AROUND_RAID_BOSSES_RANGE = altSettings.getProperty("BATTLE_ZONE_AROUND_RAID_BOSSES_RANGE", 0);
    }

    public static void loadServicesSettings()
    {
        ExProperties servicesSettings = load(SERVICES_FILE);

        SPAWN_VITAMIN_MANAGER = servicesSettings.getProperty("SPAWN_VITAMIN_MANAGER", true);

        ALLOW_CLASS_MASTERS_LIST.clear();
        String allowClassMasters = servicesSettings.getProperty("AllowClassMasters", "false");
        if(!allowClassMasters.equalsIgnoreCase("false"))
        {
            String[] allowClassLvls = allowClassMasters.split(";");
            for(String allowClassLvl : allowClassLvls)
            {
                String[] allosClassLvlInfo = allowClassLvl.split(",");
                int classLvl = Integer.parseInt(allosClassLvlInfo[0]);
                if(ALLOW_CLASS_MASTERS_LIST.containsKey(classLvl))
                    continue;

                int[] needItemInfo = new int[]{ 0, 0 };
                if(allosClassLvlInfo.length >= 3)
                    needItemInfo = new int[]{ Integer.parseInt(allosClassLvlInfo[1]), Integer.parseInt(allosClassLvlInfo[2]) };
                ALLOW_CLASS_MASTERS_LIST.put(classLvl, needItemInfo);
            }
        }


        ALLOW_CHANGE_PASSWORD_COMMAND = servicesSettings.getProperty("ALLOW_CHANGE_PASSWORD_COMMAND", false);
        ALLOW_CHANGE_PHONE_NUMBER_COMMAND = servicesSettings.getProperty("ALLOW_CHANGE_PHONE_NUMBER_COMMAND", false);
        FORCIBLY_SPECIFY_PHONE_NUMBER = servicesSettings.getProperty("FORCIBLY_SPECIFY_PHONE_NUMBER", false);

        SERVICES_CHANGE_NICK_COLOR_PRICE = servicesSettings.getProperty("NickColorChangePrice", 100);
        SERVICES_CHANGE_NICK_COLOR_ITEM = servicesSettings.getProperty("NickColorChangeItem", 4037);
        SERVICES_CHANGE_NICK_COLOR_LIST = servicesSettings.getProperty("NickColorChangeList", new String[] { "00FF00" });

        SERVICES_BASH_ENABLED = servicesSettings.getProperty("BashEnabled", false);
        SERVICES_BASH_SKIP_DOWNLOAD = servicesSettings.getProperty("BashSkipDownload", false);
        SERVICES_BASH_RELOAD_TIME = servicesSettings.getProperty("BashReloadTime", 24);

        SERVICES_EXPAND_INVENTORY_ENABLED = servicesSettings.getProperty("ExpandInventoryEnabled", false);
        SERVICES_EXPAND_INVENTORY_PRICE = servicesSettings.getProperty("ExpandInventoryPrice", 1000);
        SERVICES_EXPAND_INVENTORY_ITEM = servicesSettings.getProperty("ExpandInventoryItem", 4037);
        SERVICES_EXPAND_INVENTORY_MAX = servicesSettings.getProperty("ExpandInventoryMax", 250);

        SERVICES_EXPAND_WAREHOUSE_ENABLED = servicesSettings.getProperty("ExpandWarehouseEnabled", false);
        SERVICES_EXPAND_WAREHOUSE_PRICE = servicesSettings.getProperty("ExpandWarehousePrice", 1000);
        SERVICES_EXPAND_WAREHOUSE_ITEM = servicesSettings.getProperty("ExpandWarehouseItem", 4037);

        SERVICES_EXPAND_CWH_ENABLED = servicesSettings.getProperty("ExpandCWHEnabled", false);
        SERVICES_EXPAND_CWH_PRICE = servicesSettings.getProperty("ExpandCWHPrice", 1000);
        SERVICES_EXPAND_CWH_ITEM = servicesSettings.getProperty("ExpandCWHItem", 4037);

        SERVICES_TRADE_TAX = servicesSettings.getProperty("TradeTax", 0.0);
        SERVICES_OFFSHORE_TRADE_TAX = servicesSettings.getProperty("OffshoreTradeTax", 0.0);

        SERVICES_OFFSHORE_NO_CASTLE_TAX = servicesSettings.getProperty("NoCastleTaxInOffshore", false);
        SERVICES_TRADE_ONLY_FAR = servicesSettings.getProperty("TradeOnlyFar", false);
        SERVICES_TRADE_MIN_LEVEL = servicesSettings.getProperty("MinLevelForTrade", 0);
        SERVICES_TRADE_RADIUS = servicesSettings.getProperty("TradeRadius", 30);

        SERVICES_ROULETTE_MIN_BET = servicesSettings.getProperty("RouletteMinBet", 1L);
        SERVICES_ROULETTE_MAX_BET = servicesSettings.getProperty("RouletteMaxBet", Long.MAX_VALUE);

        SERVICES_ENABLE_NO_CARRIER = servicesSettings.getProperty("EnableNoCarrier", false);
        SERVICES_NO_CARRIER_MIN_TIME = servicesSettings.getProperty("NoCarrierMinTime", 0);
        SERVICES_NO_CARRIER_MAX_TIME = servicesSettings.getProperty("NoCarrierMaxTime", 90);
        SERVICES_NO_CARRIER_DEFAULT_TIME = servicesSettings.getProperty("NoCarrierDefaultTime", 60);

        ALLOW_EVENT_GATEKEEPER = servicesSettings.getProperty("AllowEventGatekeeper", false);

        ALLOW_IP_LOCK = servicesSettings.getProperty("AllowLockIP", false);
        AUTO_LOCK_IP_ON_LOGIN = servicesSettings.getProperty("AUTO_LOCK_IP_ON_LOGIN", false);
        ALLOW_HWID_LOCK = servicesSettings.getProperty("AllowLockHwid", false);
        AUTO_LOCK_HWID_ON_LOGIN = servicesSettings.getProperty("AUTO_LOCK_HWID_ON_LOGIN", false);
        HWID_LOCK_MASK = servicesSettings.getProperty("HwidLockMask", 10);

        SERVICES_RIDE_HIRE_ENABLED = servicesSettings.getProperty("SERVICES_RIDE_HIRE_ENABLED", false);

    }

    public static void loadPvPSettings()
    {
        ExProperties pvpSettings = load(PVP_CONFIG_FILE);

        /* KARMA SYSTEM */
        KARMA_MIN_KARMA = pvpSettings.getProperty("MinKarma", 720);
        KARMA_RATE_KARMA_LOST = pvpSettings.getProperty("RateKarmaLost", -1);
        KARMA_LOST_BASE = pvpSettings.getProperty("BaseKarmaLost", 1200);

        KARMA_DROP_GM = pvpSettings.getProperty("CanGMDropEquipment", false);
        KARMA_NEEDED_TO_DROP = pvpSettings.getProperty("KarmaNeededToDrop", true);
        DROP_ITEMS_ON_DIE = pvpSettings.getProperty("DropOnDie", true);
        DROP_ITEMS_AUGMENTED = pvpSettings.getProperty("DropAugmented", false);

        KARMA_DROP_ITEM_LIMIT = pvpSettings.getProperty("MaxItemsDroppable", 10);
        MIN_PK_TO_ITEMS_DROP = pvpSettings.getProperty("MinPKToDropItems", 4);

        KARMA_RANDOM_DROP_LOCATION_LIMIT = pvpSettings.getProperty("MaxDropThrowDistance", 70);

        KARMA_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfPKDropBase", 20.);
        KARMA_DROPCHANCE_MOD = pvpSettings.getProperty("ChanceOfPKsDropMod", 1.);
        NORMAL_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfNormalDropBase", 30.);
        DROPCHANCE_EQUIPPED_WEAPON = pvpSettings.getProperty("ChanceOfDropWeapon", 3);
        DROPCHANCE_EQUIPMENT = pvpSettings.getProperty("ChanceOfDropEquippment", 17);
        DROPCHANCE_ITEM = pvpSettings.getProperty("ChanceOfDropOther", 80);

        KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();
        for(int id : pvpSettings.getProperty("ListOfNonDroppableItems", new int[] {
                57,
                1147,
                425,
                1146,
                461,
                10,
                2368,
                7,
                6,
                2370,
                2369,
                3500,
                3501,
                3502,
                4422,
                4423,
                4424,
                2375,
                6648,
                6649,
                6650,
                6842,
                6834,
                6835,
                6836,
                6837,
                6838,
                6839,
                6840,
                5575,
                7694,
                6841,
                8181 }))
            KARMA_LIST_NONDROPPABLE_ITEMS.add(id);

        PVP_TIME = pvpSettings.getProperty("PvPTime", 40000);
        RATE_KARMA_LOST_STATIC = pvpSettings.getProperty("KarmaLostStaticValue", -1);
    }

    public static void loadAISettings()
    {
        ExProperties aiSettings = load(AI_CONFIG_FILE);

        AI_TASK_MANAGER_COUNT = aiSettings.getProperty("AiTaskManagers", 1);
        AI_TASK_ATTACK_DELAY = aiSettings.getProperty("AiTaskDelay", 1000);
        AI_TASK_ACTIVE_DELAY = aiSettings.getProperty("AiTaskActiveDelay", 1000);
        BLOCK_ACTIVE_TASKS = aiSettings.getProperty("BlockActiveTasks", false);
        ALWAYS_TELEPORT_HOME = aiSettings.getProperty("AlwaysTeleportHome", false);

        RND_WALK = aiSettings.getProperty("RndWalk", true);
        RND_WALK_RATE = aiSettings.getProperty("RndWalkRate", 1);
        RND_ANIMATION_RATE = aiSettings.getProperty("RndAnimationRate", 2);

        AGGRO_CHECK_INTERVAL = aiSettings.getProperty("AggroCheckInterval", 250);
        NONAGGRO_TIME_ONTELEPORT = aiSettings.getProperty("NonAggroTimeOnTeleport", 15000);
        NONPVP_TIME_ONTELEPORT = aiSettings.getProperty("NonPvPTimeOnTeleport", 0);
        MAX_DRIFT_RANGE = aiSettings.getProperty("MaxDriftRange", 100);
        MAX_PURSUE_RANGE = aiSettings.getProperty("MaxPursueRange", 4000);
        MAX_PURSUE_UNDERGROUND_RANGE = aiSettings.getProperty("MaxPursueUndergoundRange", 2000);
        MAX_PURSUE_RANGE_RAID = aiSettings.getProperty("MaxPursueRangeRaid", 5000);
    }

    public static void loadGeodataSettings()
    {
        ExProperties geodataSettings = load(GEODATA_CONFIG_FILE);

        GEO_X_FIRST = geodataSettings.getProperty("GeoFirstX", 11);
        GEO_Y_FIRST = geodataSettings.getProperty("GeoFirstY", 10);
        GEO_X_LAST = geodataSettings.getProperty("GeoLastX", 26);
        GEO_Y_LAST = geodataSettings.getProperty("GeoLastY", 26);

        ALLOW_GEODATA = geodataSettings.getProperty("AllowGeodata", true);

        try
        {
            GEODATA_ROOT = new File(geodataSettings.getProperty("GeodataRoot", "./geodata/")).getCanonicalFile();
        }
        catch(IOException e)
        {
            _log.error("", e);
        }

        ALLOW_FALL_FROM_WALLS = geodataSettings.getProperty("AllowFallFromWalls", false);
        ALLOW_KEYBOARD_MOVE = geodataSettings.getProperty("AllowMoveWithKeyboard", true);
        COMPACT_GEO = geodataSettings.getProperty("CompactGeoData", false);
        CLIENT_Z_SHIFT = geodataSettings.getProperty("ClientZShift", 16);
        PATHFIND_BOOST = geodataSettings.getProperty("PathFindBoost", 2);
        PATHFIND_DIAGONAL = geodataSettings.getProperty("PathFindDiagonal", true);
        PATHFIND_MAP_MUL = geodataSettings.getProperty("PathFindMapMul", 2);
        PATH_CLEAN = geodataSettings.getProperty("PathClean", true);
        PATHFIND_MAX_Z_DIFF = geodataSettings.getProperty("PathFindMaxZDiff", 32);
        MAX_Z_DIFF = geodataSettings.getProperty("MaxZDiff", 64);
        MIN_LAYER_HEIGHT = geodataSettings.getProperty("MinLayerHeight", 64);
        REGION_EDGE_MAX_Z_DIFF = geodataSettings.getProperty("RegionEdgeMaxZDiff", 128);
        PATHFIND_MAX_TIME = geodataSettings.getProperty("PathFindMaxTime", 10000000);
        PATHFIND_BUFFERS = geodataSettings.getProperty("PathFindBuffers", "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512");
        NPC_PATH_FIND_MAX_HEIGHT = geodataSettings.getProperty("NPC_PATH_FIND_MAX_HEIGHT", 1024);
        PLAYABLE_PATH_FIND_MAX_HEIGHT = geodataSettings.getProperty("PLAYABLE_PATH_FIND_MAX_HEIGHT", 256);
    }

    public static void pvpManagerSettings()
    {
        ExProperties pvp_manager = load(PVP_MANAGER_FILE);

        ALLOW_PVP_REWARD = pvp_manager.getProperty("AllowPvPManager", true);
        PVP_REWARD_SEND_SUCC_NOTIF = pvp_manager.getProperty("SendNotification", true);

        PVP_REWARD_REWARD_IDS = pvp_manager.getProperty("PvPRewardsIDs", new int[]{57, 6673});
        PVP_REWARD_COUNTS = pvp_manager.getProperty("PvPRewardsCounts", new long[]{1, 2});
        if(PVP_REWARD_REWARD_IDS.length != PVP_REWARD_COUNTS.length)
            _log.warn("pvp_manager.properties: PvPRewardsIDs array length != PvPRewardsCounts array length");

        PVP_REWARD_RANDOM_ONE = pvp_manager.getProperty("GiveJustOneRandom", true);
        PVP_REWARD_DELAY_ONE_KILL = pvp_manager.getProperty("DelayBetweenKillsOneCharSec", 60);
        PVP_REWARD_MIN_PL_PROFF = pvp_manager.getProperty("ToRewardMinProff", 0);
        PVP_REWARD_MIN_PL_UPTIME_MINUTE = pvp_manager.getProperty("ToRewardMinPlayerUptimeMinutes", 60);
        PVP_REWARD_MIN_PL_LEVEL = pvp_manager.getProperty("ToRewardMinPlayerLevel", 75);
        PVP_REWARD_PK_GIVE = pvp_manager.getProperty("RewardPK", false);
        PVP_REWARD_ON_EVENT_GIVE = pvp_manager.getProperty("ToRewardIfInEvent", false);
        PVP_REWARD_ONLY_BATTLE_ZONE = pvp_manager.getProperty("ToRewardOnlyIfInBattleZone", false);

        PVP_REWARD_SAME_PARTY_GIVE = pvp_manager.getProperty("ToRewardIfInSameParty", false);
        PVP_REWARD_SAME_CLAN_GIVE = pvp_manager.getProperty("ToRewardIfInSameClan", false);
        PVP_REWARD_SAME_ALLY_GIVE = pvp_manager.getProperty("ToRewardIfInSameAlly", false);
        PVP_REWARD_SAME_HWID_GIVE = pvp_manager.getProperty("ToRewardIfInSameHWID", false);
        PVP_REWARD_SAME_IP_GIVE = pvp_manager.getProperty("ToRewardIfInSameIP", false);
        PVP_REWARD_SPECIAL_ANTI_TWINK_TIMER = pvp_manager.getProperty("SpecialAntiTwinkCharCreateDelay", false);
        PVP_REWARD_HR_NEW_CHAR_BEFORE_GET_ITEM = pvp_manager.getProperty("SpecialAntiTwinkDelayInHours", 24);
        PVP_REWARD_CHECK_EQUIP = pvp_manager.getProperty("EquipCheck", false);
        PVP_REWARD_WEAPON_GRADE_TO_CHECK = pvp_manager.getProperty("MinimumGradeToCheck", 0);
        PVP_REWARD_LOG_KILLS = pvp_manager.getProperty("LogKillsToDB", false);
        DISALLOW_MSG_TO_PL = pvp_manager.getProperty("DoNotShowMessagesToPlayers", false);
    }

    public static void loadOlympiadSettings()
    {
        ExProperties olympSettings = load(OLYMPIAD);

        ENABLE_OLYMPIAD = olympSettings.getProperty("EnableOlympiad", true);
        ENABLE_OLYMPIAD_SPECTATING = olympSettings.getProperty("EnableOlympiadSpectating", true);
        OLYMIAD_END_PERIOD_TIME = new SchedulingPattern(olympSettings.getProperty("OLYMIAD_END_PERIOD_TIME", "00 00 01 * *"));
        OLYMPIAD_START_TIME = new SchedulingPattern(olympSettings.getProperty("OLYMPIAD_START_TIME", "00 20 * * 5,6"));
        ALT_OLY_CPERIOD = olympSettings.getProperty("AltOlyCPeriod", 14400000);
        ALT_OLY_WPERIOD = olympSettings.getProperty("AltOlyWPeriod", 604800000);
        ALT_OLY_VPERIOD = olympSettings.getProperty("AltOlyVPeriod", 43200000);
        CLASSED_GAMES_ENABLED = olympSettings.getProperty("CLASSED_GAMES_ENABLED", false);
        OLYMPIAD_REGISTRATION_DELAY = olympSettings.getProperty("OLYMPIAD_REGISTRATION_DELAY", 1200000);

        OLYMPIAD_MIN_LEVEL = Math.max(40, olympSettings.getProperty("OLYMPIAD_MIN_LEVEL", 55));
        HtmlUtils.registerGlobalHtmlVariable("OLYMPIAD_MIN_LEVEL", OLYMPIAD_MIN_LEVEL);

        CLASS_GAME_MIN = olympSettings.getProperty("ClassGameMin", 10);
        NONCLASS_GAME_MIN = olympSettings.getProperty("NonClassGameMin", 20);

        GAME_MAX_LIMIT = olympSettings.getProperty("GameMaxLimit", 30);
        GAME_CLASSES_COUNT_LIMIT = olympSettings.getProperty("GameClassesCountLimit", 30);
        GAME_NOCLASSES_COUNT_LIMIT = olympSettings.getProperty("GameNoClassesCountLimit", 30);

        ALT_OLY_BATTLE_REWARD_ITEM = olympSettings.getProperty("AltOlyBattleRewItem", 45584);
        OLYMPIAD_CLASSED_WINNER_REWARD_COUNT = olympSettings.getProperty("OLYMPIAD_CLASSED_WINNER_REWARD_COUNT", 0);
        OLYMPIAD_NONCLASSED_WINNER_REWARD_COUNT = olympSettings.getProperty("OLYMPIAD_NONCLASSED_WINNER_REWARD_COUNT", 0);
        OLYMPIAD_CLASSED_LOOSER_REWARD_COUNT = olympSettings.getProperty("OLYMPIAD_CLASSED_LOOSER_REWARD_COUNT", 0);
        OLYMPIAD_NONCLASSED_LOOSER_REWARD_COUNT = olympSettings.getProperty("OLYMPIAD_NONCLASSED_LOOSER_REWARD_COUNT", 0);
        ALT_OLY_COMP_RITEM = olympSettings.getProperty("AltOlyCompRewItem", 45584);
        ALT_OLY_GP_PER_POINT = olympSettings.getProperty("AltOlyGPPerPoint", 20);
        ALT_OLY_HERO_POINTS = olympSettings.getProperty("AltOlyHeroPoints", 100);
        ALT_OLY_RANK1_POINTS = olympSettings.getProperty("AltOlyRank1Points", 200);
        ALT_OLY_RANK2_POINTS = olympSettings.getProperty("AltOlyRank2Points", 80);
        ALT_OLY_RANK3_POINTS = olympSettings.getProperty("AltOlyRank3Points", 50);
        ALT_OLY_RANK4_POINTS = olympSettings.getProperty("AltOlyRank4Points", 30);
        ALT_OLY_RANK5_POINTS = olympSettings.getProperty("AltOlyRank5Points", 15);
        OLYMPIAD_ALL_LOOSE_POINTS_BONUS = olympSettings.getProperty("OLYMPIAD_ALL_LOOSE_POINTS_BONUS", 0);
        OLYMPIAD_1_OR_MORE_WIN_POINTS_BONUS = olympSettings.getProperty("OLYMPIAD_1_OR_MORE_WIN_POINTS_BONUS", 10);
        OLYMPIAD_STADIAS_COUNT = olympSettings.getProperty("OlympiadStadiasCount", 160);
        OLYMPIAD_BATTLES_FOR_REWARD = olympSettings.getProperty("OlympiadBattlesForReward", 10);
        OLYMPIAD_POINTS_DEFAULT = olympSettings.getProperty("OlympiadPointsDefault", 10);
        OLYMPIAD_POINTS_WEEKLY = olympSettings.getProperty("OlympiadPointsWeekly", 10);
        OLYMPIAD_OLDSTYLE_STAT = olympSettings.getProperty("OlympiadOldStyleStat", false);

        OLYMPIAD_BEGINIG_DELAY = olympSettings.getProperty("OlympiadBeginingDelay", 120);

        ALT_OLY_BY_SAME_BOX_NUMBER = olympSettings.getProperty("OlympiadSameBoxesNumberLimitation", 0);

        OLYMPIAD_ENABLE_ENCHANT_LIMIT = olympSettings.getProperty("ENABLE_ENCHANT_LIMIT", false);
        OLYMPIAD_WEAPON_ENCHANT_LIMIT = olympSettings.getProperty("WEAPON_ENCHANT_LIMIT", 0);
        OLYMPIAD_ARMOR_ENCHANT_LIMIT = olympSettings.getProperty("ARMOR_ENCHANT_LIMIT", 0);
        OLYMPIAD_JEWEL_ENCHANT_LIMIT = olympSettings.getProperty("JEWEL_ENCHANT_LIMIT", 0);
    }

    public static void load()
    {
        loadServerConfig();
        loadTelnetConfig();
        loadResidenceConfig();
        loadAntiFloodConfig();
        loadOtherConfig();
        loadSpoilConfig();
        loadFormulasConfig();
        loadAltSettings();
        loadServicesSettings();
        loadPvPSettings();
        loadAISettings();
        loadGeodataSettings();
        loadOlympiadSettings();
        loadExtSettings();
        loadBBSSettings();
        loadSchemeBuffer();
        loadTrainingCampConfig();

        abuseLoad();
        loadGMAccess();
        pvpManagerSettings();
        loadAntiBotSettings();
    }

    private Config()
    {}

    public static void abuseLoad()
    {
        LineNumberReader lnr = null;
        try
        {
            StringBuilder abuses = new StringBuilder();
            String line;

            lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(ANUSEWORDS_CONFIG_FILE), "UTF-8"));

            int count = 0;
            while((line = lnr.readLine()) != null)
            {
                StringTokenizer st = new StringTokenizer(line, "\n\r");
                if(st.hasMoreTokens())
                {
                    abuses.append(st.nextToken());
                    abuses.append("|");
                    count++;
                }
            }

            if(count > 0)
            {
                String abusesGroup = abuses.toString();
                abusesGroup = abusesGroup.substring(0, abusesGroup.length() - 1);
                ABUSEWORD_PATTERN = Pattern.compile(".*(" + abusesGroup + ").*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            }

            _log.info("Abuse: Loaded " + count + " abuse words.");
        }
        catch(IOException e1)
        {
            _log.warn("Error reading abuse: " + e1);
        }
        finally
        {
            try
            {
                if(lnr != null)
                    lnr.close();
            }
            catch(Exception e2)
            {
                // nothing
            }
        }
    }

    public static void loadAntiBotSettings()
    {
        ExProperties botSettings = load(BOT_FILE);

        ENABLE_ANTI_BOT_SYSTEM = botSettings.getProperty("EnableAntiBotSystem", false);
        MINIMUM_TIME_QUESTION_ASK = botSettings.getProperty("MinimumTimeQuestionAsk", 60);
        MAXIMUM_TIME_QUESTION_ASK = botSettings.getProperty("MaximumTimeQuestionAsk", 120);
        MINIMUM_BOT_POINTS_TO_STOP_ASKING = botSettings.getProperty("MinimumBotPointsToStopAsking", 10);
        MAXIMUM_BOT_POINTS_TO_STOP_ASKING = botSettings.getProperty("MaximumBotPointsToStopAsking", 15);
        MAX_BOT_POINTS = botSettings.getProperty("MaxBotPoints", 15);
        MINIMAL_BOT_RATING_TO_BAN = botSettings.getProperty("MinimalBotPointsToBan", -5);
        AUTO_BOT_BAN_JAIL_TIME = botSettings.getProperty("AutoBanJailTime", 24);
        ANNOUNCE_AUTO_BOT_BAN = botSettings.getProperty("AnounceAutoBan", true);
        ON_WRONG_QUESTION_KICK = botSettings.getProperty("IfWrongKick", true);
    }

    public static void loadSchemeBuffer()
    {
        ExProperties npcbuffer = load(SCHEME_BUFFER_FILE);

        NpcBuffer_VIP = npcbuffer.getProperty("EnableVIP", false);
        NpcBuffer_VIP_ALV = npcbuffer.getProperty("VipAccesLevel", 1);
        NpcBuffer_EnableBuff = npcbuffer.getProperty("EnableBuffSection", true);
        NpcBuffer_EnableScheme = npcbuffer.getProperty("EnableScheme", true);
        NpcBuffer_EnableHeal = npcbuffer.getProperty("EnableHeal", true);
        NpcBuffer_EnableBuffs = npcbuffer.getProperty("EnableBuffs", true);
        NpcBuffer_EnableResist = npcbuffer.getProperty("EnableResist", true);
        NpcBuffer_EnableSong = npcbuffer.getProperty("EnableSongs", true);
        NpcBuffer_EnableDance = npcbuffer.getProperty("EnableDances", true);
        NpcBuffer_EnableChant = npcbuffer.getProperty("EnableChants", true);
        NpcBuffer_EnableOther = npcbuffer.getProperty("EnableOther", true);
        NpcBuffer_EnableSpecial = npcbuffer.getProperty("EnableSpecial", true);
        NpcBuffer_EnableCubic = npcbuffer.getProperty("EnableCubic", true);
        NpcBuffer_EnableCancel = npcbuffer.getProperty("EnableRemoveBuffs", true);
        NpcBuffer_EnableBuffSet = npcbuffer.getProperty("EnableBuffSet", true);
        NpcBuffer_EnableBuffPK = npcbuffer.getProperty("EnableBuffForPK", false);
        NpcBuffer_EnableFreeBuffs = npcbuffer.getProperty("EnableFreeBuffs", true);
        NpcBuffer_EnableTimeOut = npcbuffer.getProperty("EnableTimeOut", true);

        NpcBuffer_TimeOutTime = npcbuffer.getProperty("TimeoutTime", 10);
        NpcBuffer_MinLevel = npcbuffer.getProperty("MinimumLevel", 20);
        NpcBuffer_PriceCancel = npcbuffer.getProperty("RemoveBuffsPrice", 100000);
        NpcBuffer_PriceHeal = npcbuffer.getProperty("HealPrice", 100000);
        NpcBuffer_PriceBuffs = npcbuffer.getProperty("BuffsPrice", 100000);
        NpcBuffer_PriceResist = npcbuffer.getProperty("ResistPrice", 100000);
        NpcBuffer_PriceSong = npcbuffer.getProperty("SongPrice", 100000);
        NpcBuffer_PriceDance = npcbuffer.getProperty("DancePrice", 100000);
        NpcBuffer_PriceChant = npcbuffer.getProperty("ChantsPrice", 100000);
        NpcBuffer_PriceOther = npcbuffer.getProperty("OtherPrice", 100000);
        NpcBuffer_PriceSpecial = npcbuffer.getProperty("SpecialPrice", 100000);
        NpcBuffer_PriceCubic = npcbuffer.getProperty("CubicPrice", 100000);
        NpcBuffer_PriceSet = npcbuffer.getProperty("SetPrice", 100000);
        NpcBuffer_PriceScheme = npcbuffer.getProperty("SchemePrice", 100000);
        NpcBuffer_MaxScheme = npcbuffer.getProperty("MaxScheme", 4);

    }

    public static void loadTrainingCampConfig()
    {
        ExProperties properties = load(TRAINING_CAMP_CONFIG_FILE);

        TRAINING_CAMP_ENABLE = properties.getProperty("ENABLE", false);
        TRAINING_CAMP_PREMIUM_ONLY = properties.getProperty("PREMIUM_ONLY", true);
        TRAINING_CAMP_MAX_DURATION = properties.getProperty("MAX_DURATION", 18000);
        TRAINING_CAMP_MIN_LEVEL = properties.getProperty("MIN_LEVEL", 18);
        TRAINING_CAMP_MAX_LEVEL = properties.getProperty("MAX_LEVEL", 127);
    }

    public static void loadGMAccess()
    {
        gmlist.clear();
        loadGMAccess(new File(GM_PERSONAL_ACCESS_FILE));
        File dir = new File(GM_ACCESS_FILES_DIR);
        if(!dir.exists() || !dir.isDirectory())
        {
            _log.info("Dir " + dir.getAbsolutePath() + " not exists.");
            return;
        }
        for(File f : dir.listFiles())
            // hidden файлы НЕ игнорируем
            if(!f.isDirectory() && f.getName().endsWith(".xml"))
                loadGMAccess(f);
    }

    public static void loadGMAccess(File file)
    {
        try
        {
            Field fld;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Document doc = factory.newDocumentBuilder().parse(file);

            for(Node z = doc.getFirstChild(); z != null; z = z.getNextSibling())
                for(Node n = z.getFirstChild(); n != null; n = n.getNextSibling())
                {
                    if(!n.getNodeName().equalsIgnoreCase("char"))
                        continue;

                    PlayerAccess pa = new PlayerAccess();
                    for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                    {
                        Class<?> cls = pa.getClass();
                        String node = d.getNodeName();

                        if(node.equalsIgnoreCase("#text"))
                            continue;
                        try
                        {
                            fld = cls.getField(node);
                        }
                        catch(NoSuchFieldException e)
                        {
                            _log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
                            continue;
                        }

                        if(fld.getType().getName().equalsIgnoreCase("boolean"))
                            fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
                        else if(fld.getType().getName().equalsIgnoreCase("int"))
                            fld.setInt(pa, Integer.valueOf(d.getAttributes().getNamedItem("set").getNodeValue()));
                    }
                    gmlist.put(pa.PlayerID, pa);
                }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static ExProperties load(String filename)
    {
        return load(new File(filename));
    }

    public static ExProperties load(File file)
    {
        ExProperties result = new ExProperties();

        try
        {
            result.load(file);
        }
        catch(IOException e)
        {
            _log.error("Error loading config : " + file.getName() + "!");
        }

        return result;
    }

    public static boolean containsAbuseWord(String s)
    {
        if(ABUSEWORD_PATTERN == null)
            return false;
        return ABUSEWORD_PATTERN.matcher(s).matches();
    }

    public static String replaceAbuseWords(String text, String censore)
    {
        if(ABUSEWORD_PATTERN == null)
            return text;
        Matcher m = ABUSEWORD_PATTERN.matcher(text);
        while(m.find())
            text = text.replace(m.group(1), censore);
        return text;
    }

    public static class RaidGlobalDrop
    {
        int _id;
        long _count;
        double _chance;

        public RaidGlobalDrop(int id, long count, double chance)
        {
            _id = id;
            _count = count;
            _chance = chance;
        }

        public int getId()
        {
            return _id;
        }

        public long getCount()
        {
            return _count;
        }

        public double getChance()
        {
            return _chance;
        }
    }
}