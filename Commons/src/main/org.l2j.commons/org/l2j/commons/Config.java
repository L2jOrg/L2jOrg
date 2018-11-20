package org.l2j.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.<br>
 * It's initialized at the very begin of startup, and later JIT will optimize away debug/unused code.
 *
 * @author mkizub
 */
public final class Config {
    protected static final Logger _log = LoggerFactory.getLogger(Config.class.getName());

    /**
     * Debug/release mode
     */
    public static boolean DEBUG;
    /**
     * Enable/disable assertions
     */
    public static boolean ASSERT;
    /**
     * Enable/disable code 'in progress'
     */
    public static boolean DEVELOPER;

    /**
     * Set if this server is a test server used for development
     */
    public static boolean TEST_SERVER;

    /**
     * Game Server ports
     */
    public static int PORT_GAME;

    /**
     * Hostname of the Game Server
     */
    public static String GAMESERVER_HOSTNAME;

    // Access to database
    /**
     * Driver to access to database
     */
    public static String DATABASE_DRIVER;
    /**
     * Path to access to database
     */
    public static String DATABASE_URL;
    /**
     * Database login
     */
    public static String DATABASE_LOGIN;
    /**
     * Database password
     */
    public static String DATABASE_PASSWORD;
    /**
     * Maximum number of connections to the database
     */
    public static int DATABASE_MAX_CONNECTIONS;

    public static int DATABASE_MAX_IDLE_TIME;

    /**
     * Maximum number of players allowed to play simultaneously on server
     */
    public static int MAXIMUM_ONLINE_USERS;

    // Setting for serverList
    /**
     * Displays [] in front of server name ?
     */
    public static boolean SERVER_LIST_BRACKET;
    /**
     * Displays a clock next to the server name ?
     */
    public static boolean SERVER_LIST_CLOCK;
    /**
     * Display test server in the list of servers ?
     */
    public static boolean SERVER_LIST_TESTSERVER;
    /**
     * Set the server as gm only at startup ?
     */
    public static boolean SERVER_GMONLY;

    // Thread pools size
    /**
     * Thread pool size effect
     */
    public static int THREAD_P_EFFECTS;
    /**
     * Thread pool size general
     */
    public static int THREAD_P_GENERAL;
    /**
     * Packet max thread
     */
    public static int GENERAL_PACKET_THREAD_CORE_SIZE;
    public static int IO_PACKET_THREAD_CORE_SIZE;
    /**
     * General max thread
     */
    public static int GENERAL_THREAD_CORE_SIZE;
    /**
     * AI max thread
     */
    public static int AI_MAX_THREAD;

    /**
     * Accept auto-loot ?
     */
    public static boolean AUTO_LOOT;
    public static boolean AUTO_LOOT_HERBS;

    /**
     * Character name template
     */
    public static String CNAME_TEMPLATE;
    /**
     * Pet name template
     */
    public static String PET_NAME_TEMPLATE;
    /**
     * Maximum number of characters per account
     */
    public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;

    /**
     * Global chat state
     */
    public static String DEFAULT_GLOBAL_CHAT;
    /**
     * Trade chat state
     */
    public static String DEFAULT_TRADE_CHAT;
    /**
     * For test servers - everybody has admin rights
     */
    public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
    /**
     * Alternative game crafting
     */
    public static boolean ALT_GAME_CREATION;
    /**
     * Alternative game crafting speed mutiplier - default 0 (fastest but still not instant)
     */
    public static double ALT_GAME_CREATION_SPEED;
    /**
     * Alternative game crafting XP rate multiplier - default 1
     */
    public static double ALT_GAME_CREATION_XP_RATE;
    /**
     * Alternative game crafting SP rate multiplier - default 1
     */
    public static double ALT_GAME_CREATION_SP_RATE;
    /**
     * Alternative setting to blacksmith use of recipes to craft - default true
     */
    public static boolean ALT_BLACKSMITH_USE_RECIPES;

    /**
     * Remove Castle circlets after clan lose his castle? - default true
     */
    public static boolean REMOVE_CASTLE_CIRCLETS;
    /**
     * Alternative game weight limit multiplier - default 1
     */
    public static double ALT_WEIGHT_LIMIT;

    /**
     * Alternative game skill learning
     */
    public static boolean ALT_GAME_SKILL_LEARN;
    /**
     * Alternative auto skill learning
     */
    public static boolean AUTO_LEARN_SKILLS;
    /**
     * Cancel attack bow by hit
     */
    public static boolean ALT_GAME_CANCEL_BOW;
    /**
     * Cancel cast by hit
     */
    public static boolean ALT_GAME_CANCEL_CAST;

    /**
     * Alternative game - use tiredness, instead of CP
     */
    public static boolean ALT_GAME_TIREDNESS;
    public static int ALT_PARTY_RANGE;
    public static int ALT_PARTY_RANGE2;
    /**
     * Alternative shield defence
     */
    public static boolean ALT_GAME_SHIELD_BLOCKS;
    /**
     * Alternative Perfect shield defence rate
     */
    public static int ALT_PERFECT_SHLD_BLOCK;
    /**
     * Alternative game mob ATTACK AI
     */
    public static boolean ALT_GAME_MOB_ATTACK_AI;
    public static boolean ALT_MOB_AGRO_IN_PEACEZONE;

    /**
     * Alternative freight modes - Freights can be withdrawed from any village
     */
    public static boolean ALT_GAME_FREIGHTS;
    /**
     * Alternative freight modes - Sets the price value for each freightened item
     */
    public static int ALT_GAME_FREIGHT_PRICE;

    /**
     * Fast or slow multiply coefficient for skill hit time
     */
    public static float ALT_GAME_SKILL_HIT_RATE;

    /**
     * Alternative gameing - loss of XP on death
     */
    public static boolean ALT_GAME_DELEVEL;

    /**
     * Alternative gameing - magic dmg failures
     */
    public static boolean ALT_GAME_MAGICFAILURES;

    /**
     * Alternative gaming - reader must be in a castle-owning clan or ally to sign up for Dawn.
     */
    public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;

    /**
     * Alternative gaming - allow clan-based castle ownage check rather than ally-based.
     */
    public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;

    /**
     * Alternative gaming - allow free teleporting around the world.
     */
    public static boolean ALT_GAME_FREE_TELEPORT;

    /**
     * Disallow recommend character twice or more a day ?
     */
    public static boolean ALT_RECOMMEND;

    /**
     * Alternative gaming - allow sub-class addition without quest completion.
     */
    public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;

    /**
     * View npc stats/drop by shift-cliking it for nongm-players
     */
    public static boolean ALT_GAME_VIEWNPC;

    /**
     * Minimum number of reader to participate in SevenSigns Festival
     */
    public static int ALT_FESTIVAL_MIN_PLAYER;

    /**
     * Maximum of reader contrib during Festival
     */
    public static int ALT_MAXIMUM_PLAYER_CONTRIB;

    /**
     * Festival Manager start time.
     */
    public static long ALT_FESTIVAL_MANAGER_START;

    /**
     * Festival Length
     */
    public static long ALT_FESTIVAL_LENGTH;

    /**
     * Festival Cycle Length
     */
    public static long ALT_FESTIVAL_CYCLE_LENGTH;

    /**
     * Festival First Spawn
     */
    public static long ALT_FESTIVAL_FIRST_SPAWN;

    /**
     * Festival First Swarm
     */
    public static long ALT_FESTIVAL_FIRST_SWARM;

    /**
     * Festival Second Spawn
     */
    public static long ALT_FESTIVAL_SECOND_SPAWN;

    /**
     * Festival Second Swarm
     */
    public static long ALT_FESTIVAL_SECOND_SWARM;

    /**
     * Festival Chest Spawn
     */
    public static long ALT_FESTIVAL_CHEST_SPAWN;

    /**
     * Number of members needed to request a clan war
     */
    public static int ALT_CLAN_MEMBERS_FOR_WAR;

    /**
     * Number of days before joining a new clan
     */
    public static int ALT_CLAN_JOIN_DAYS;
    /**
     * Number of days before creating a new clan
     */
    public static int ALT_CLAN_CREATE_DAYS;
    /**
     * Number of days it takes to dissolve a clan
     */
    public static int ALT_CLAN_DISSOLVE_DAYS;
    /**
     * Number of days before joining a new alliance when clan voluntarily leave an alliance
     */
    public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
    /**
     * Number of days before joining a new alliance when clan was dismissed from an alliance
     */
    public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
    /**
     * Number of days before accepting a new clan for alliance when clan was dismissed from an alliance
     */
    public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
    /**
     * Number of days before creating a new alliance when dissolved an alliance
     */
    public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;

    /**
     * Alternative gaming - all new characters always are newbies.
     */
    public static boolean ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE;

    /**
     * Alternative gaming - clan members with see privilege can also withdraw from clan warehouse.
     */
    public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;

    /**
     * Maximum number of clans in ally
     */
    public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
    /**
     * Life Crystal needed to learn clan skill
     */
    public static boolean LIFE_CRYSTAL_NEEDED;
    /**
     * Spell Book needed to learn skill
     */
    public static boolean SP_BOOK_NEEDED;
    /**
     * Spell Book needet to enchant skill
     */
    public static boolean ES_SP_BOOK_NEEDED;
    /**
     * Logging Chat Window
     */
    public static boolean LOG_CHAT;
    /**
     * Logging Item Window
     */
    public static boolean LOG_ITEMS;

    /**
     * Alternative privileges for admin
     */
    public static boolean ALT_PRIVILEGES_ADMIN;
    /**
     * Alternative secure check privileges
     */
    public static boolean ALT_PRIVILEGES_SECURE_CHECK;
    /**
     * Alternative default level for privileges
     */
    public static int ALT_PRIVILEGES_DEFAULT_LEVEL;

    /**
     * Olympiad Competition Starting time
     */
    public static int ALT_OLY_START_TIME;
    /**
     * Olympiad Minutes
     */
    public static int ALT_OLY_MIN;

    /**
     * Olympiad Competition Period
     */
    public static long ALT_OLY_CPERIOD;

    /**
     * Olympiad Battle Period
     */
    public static long ALT_OLY_BATTLE;

    /**
     * Olympiad Battle Wait
     */
    public static long ALT_OLY_BWAIT;

    /**
     * Olympiad Inital Wait
     */
    public static long ALT_OLY_IWAIT;

    /**
     * Olympaid Weekly Period
     */
    public static long ALT_OLY_WPERIOD;

    /**
     * Olympaid Validation Period
     */
    public static long ALT_OLY_VPERIOD;

    /**
     * Manor Refresh Starting time
     */
    public static int ALT_MANOR_REFRESH_TIME;

    /**
     * Manor Refresh Min
     */
    public static int ALT_MANOR_REFRESH_MIN;

    /**
     * Manor Next Period Approve Starting time
     */
    public static int ALT_MANOR_APPROVE_TIME;

    /**
     * Manor Next Period Approve Min
     */
    public static int ALT_MANOR_APPROVE_MIN;

    /**
     * Manor Maintenance Time
     */
    public static int ALT_MANOR_MAINTENANCE_PERIOD;

    /**
     * Manor Save All Actions
     */
    public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;

    /**
     * Manor Save Period Rate
     */
    public static int ALT_MANOR_SAVE_PERIOD_RATE;

    /**
     * Initial Lottery prize
     */
    public static int ALT_LOTTERY_PRIZE;

    /**
     * Lottery Ticket Price
     */
    public static int ALT_LOTTERY_TICKET_PRICE;

    /**
     * What part of jackpot amount should receive characters who pick 5 wining numbers
     */
    public static float ALT_LOTTERY_5_NUMBER_RATE;

    /**
     * What part of jackpot amount should receive characters who pick 4 wining numbers
     */
    public static float ALT_LOTTERY_4_NUMBER_RATE;

    /**
     * What part of jackpot amount should receive characters who pick 3 wining numbers
     */
    public static float ALT_LOTTERY_3_NUMBER_RATE;

    /**
     * How much adena receive characters who pick two or less of the winning number
     */
    public static int ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;

    /**
     * Minimum siz e of a party that may enter dimensional rift
     */
    public static int RIFT_MIN_PARTY_SIZE;

    /**
     * Time in ms the party has to wait until the mobs spawn when entering a room
     */
    public static int RIFT_SPAWN_DELAY;

    /**
     * Amount of random rift jumps before party is ported back
     */
    public static int RIFT_MAX_JUMPS;

    /**
     * Random time between two jumps in dimensional rift - in seconds
     */
    public static int RIFT_AUTO_JUMPS_TIME_MIN;
    public static int RIFT_AUTO_JUMPS_TIME_MAX;

    /**
     * Dimensional Fragment cost for entering rift
     */
    public static int RIFT_ENTER_COST_RECRUIT;
    public static int RIFT_ENTER_COST_SOLDIER;
    public static int RIFT_ENTER_COST_OFFICER;
    public static int RIFT_ENTER_COST_CAPTAIN;
    public static int RIFT_ENTER_COST_COMMANDER;
    public static int RIFT_ENTER_COST_HERO;

    /**
     * time multiplier for boss room
     */
    public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;

    /* **************************************************************************
     * GM CONFIG General GM AccessLevel *************************************************************************
     */
    /**
     * General GM access level
     */
    public static int GM_ACCESSLEVEL;
    /**
     * General GM Minimal AccessLevel
     */
    public static int GM_MIN;
    /**
     * Minimum privileges level for a GM to do Alt+G
     */
    public static int GM_ALTG_MIN_LEVEL;
    /**
     * General GM AccessLevel to change announcements
     */
    public static int GM_ANNOUNCE;
    /**
     * General GM AccessLevel can /ban /unban
     */
    public static int GM_BAN;
    /**
     * General GM AccessLevel can /ban /unban for chat
     */
    public static int GM_BAN_CHAT;
    /**
     * General GM AccessLevel can /create_item and /gmshop
     */
    public static int GM_CREATE_ITEM;
    /**
     * General GM AccessLevel can /delete
     */
    public static int GM_DELETE;
    /**
     * General GM AccessLevel can /kick /disconnect
     */
    public static int GM_KICK;
    /**
     * General GM AccessLevel for access to GMMenu
     */
    public static int GM_MENU;
    /**
     * General GM AccessLevel to use god mode command
     */
    public static int GM_GODMODE;
    /**
     * General GM AccessLevel with character edit rights
     */
    public static int GM_CHAR_EDIT;
    /**
     * General GM AccessLevel with edit rights for other characters
     */
    public static int GM_CHAR_EDIT_OTHER;
    /**
     * General GM AccessLevel with character view rights
     */
    public static int GM_CHAR_VIEW;
    /**
     * General GM AccessLevel with NPC edit rights
     */
    public static int GM_NPC_EDIT;
    public static int GM_NPC_VIEW;
    /**
     * General GM AccessLevel to teleport to any location
     */
    public static int GM_TELEPORT;
    /**
     * General GM AccessLevel to teleport character to any location
     */
    public static int GM_TELEPORT_OTHER;
    /**
     * General GM AccessLevel to restart server
     */
    public static int GM_RESTART;
    /**
     * General GM AccessLevel for MonsterRace
     */
    public static int GM_MONSTERRACE;
    /**
     * General GM AccessLevel to ride Wyvern
     */
    public static int GM_RIDER;
    /**
     * General GM AccessLevel to unstuck without 5min delay
     */
    public static int GM_ESCAPE;
    /**
     * General GM AccessLevel to resurect fixed after death
     */
    public static int GM_FIXED;
    /**
     * General GM AccessLevel to create Path Nodes
     */
    public static int GM_CREATE_NODES;
    /**
     * General GM AccessLevel with Enchant rights
     */
    public static int GM_ENCHANT;
    /**
     * General GM AccessLevel to close/open Doors
     */
    public static int GM_DOOR;
    /**
     * General GM AccessLevel with Resurrection rights
     */
    public static int GM_RES;
    /**
     * General GM AccessLevel to attack in the peace zone
     */
    public static int GM_PEACEATTACK;
    /**
     * General GM AccessLevel to heal
     */
    public static int GM_HEAL;
    /**
     * General GM AccessLevel to unblock IPs detected as hack IPs
     */
    public static int GM_UNBLOCK;
    /**
     * General GM AccessLevel to use Cache commands
     */
    public static int GM_CACHE;
    /**
     * General GM AccessLevel to use test&st commands
     */
    public static int GM_TALK_BLOCK;
    public static int GM_TEST;
    /**
     * Disable transaction on AccessLevel
     **/
    public static boolean GM_DISABLE_TRANSACTION;
    /**
     * GM transactions disabled from this range
     */
    public static int GM_TRANSACTION_MIN;
    /**
     * GM transactions disabled to this range
     */
    public static int GM_TRANSACTION_MAX;
    /**
     * Minimum level to allow a GM giving damage
     */
    public static int GM_CAN_GIVE_DAMAGE;
    /**
     * Minimum level to don't give Exp/Sp in party
     */
    public static int GM_DONT_TAKE_EXPSP;
    /**
     * Minimum level to don't take aggro
     */
    public static int GM_DONT_TAKE_AGGRO;

    public static int GM_REPAIR = 75;

    /* Rate control */
    /**
     * Rate for eXperience Point rewards
     */
    public static float RATE_XP;
    /**
     * Rate for Skill Point rewards
     */
    public static float RATE_SP;
    /**
     * Rate for party eXperience Point rewards
     */
    public static float RATE_PARTY_XP;
    /**
     * Rate for party Skill Point rewards
     */
    public static float RATE_PARTY_SP;
    /**
     * Rate for Quest rewards (XP and SP)
     */
    public static float RATE_QUESTS_REWARD;
    /**
     * Rate for drop adena
     */
    public static float RATE_DROP_ADENA;
    /**
     * Rate for cost of consumable
     */
    public static float RATE_CONSUMABLE_COST;
    /**
     * Rate for dropped items
     */
    public static float RATE_DROP_ITEMS;
    /**
     * Rate for spoiled items
     */
    public static float RATE_DROP_SPOIL;
    /**
     * Rate for manored items
     */
    public static int RATE_DROP_MANOR;
    /**
     * Rate for quest items
     */
    public static float RATE_DROP_QUEST;
    /**
     * Rate for karma and experience lose
     */
    public static float RATE_KARMA_EXP_LOST;
    /**
     * Rate siege guards prices
     */
    public static float RATE_SIEGE_GUARDS_PRICE;
    /*
     * Alternative Xp/Sp rewards, if not 0, then calculated as 2^((mob.level-reader.level) / coef), A few examples for "AltGameExponentXp = 5." and "AltGameExponentSp = 3." diff = 0 (reader and mob has the same level), XP bonus rate = 1, SP bonus rate = 1 diff = 3 (mob is 3 levels above), XP bonus
     * rate = 1.52, SP bonus rate = 2 diff = 5 (mob is 5 levels above), XP bonus rate = 2, SP bonus rate = 3.17 diff = -8 (mob is 8 levels below), XP bonus rate = 0.4, SP bonus rate = 0.16
     */
    /**
     * Alternative eXperience Point rewards
     */
    public static float ALT_GAME_EXPONENT_XP;
    /**
     * Alternative Spirit Point rewards
     */
    public static float ALT_GAME_EXPONENT_SP;

    /**
     * Rate Common herbs
     */
    public static float RATE_DROP_COMMON_HERBS;
    /**
     * Rate MP/HP herbs
     */
    public static float RATE_DROP_MP_HP_HERBS;
    /**
     * Rate Common herbs
     */
    public static float RATE_DROP_GREATER_HERBS;
    /**
     * Rate Common herbs
     */
    public static float RATE_DROP_SUPERIOR_HERBS;
    /**
     * Rate Common herbs
     */
    public static float RATE_DROP_SPECIAL_HERBS;

    // Player Drop Rate control
    /**
     * Limit for reader drop
     */
    public static int PLAYER_DROP_LIMIT;
    /**
     * Rate for drop
     */
    public static int PLAYER_RATE_DROP;
    /**
     * Rate for reader's item drop
     */
    public static int PLAYER_RATE_DROP_ITEM;
    /**
     * Rate for reader's equipment drop
     */
    public static int PLAYER_RATE_DROP_EQUIP;
    /**
     * Rate for reader's equipment and weapon drop
     */
    public static int PLAYER_RATE_DROP_EQUIP_WEAPON;

    // Pet Rates (Multipliers)
    /**
     * Rate for experience rewards of the pet
     */
    public static float PET_XP_RATE;
    /**
     * Rate for food consumption of the pet
     */
    public static int PET_FOOD_RATE;
    /**
     * Rate for experience rewards of the Sin Eater
     */
    public static float SINEATER_XP_RATE;

    // Karma Drop Rate control
    /**
     * Karma drop limit
     */
    public static int KARMA_DROP_LIMIT;
    /**
     * Karma drop rate
     */
    public static int KARMA_RATE_DROP;
    /**
     * Karma drop rate for item
     */
    public static int KARMA_RATE_DROP_ITEM;
    /**
     * Karma drop rate for equipment
     */
    public static int KARMA_RATE_DROP_EQUIP;
    /**
     * Karma drop rate for equipment and weapon
     */
    public static int KARMA_RATE_DROP_EQUIP_WEAPON;

    /**
     * Time after which item will auto-destroy
     */
    public static int AUTODESTROY_ITEM_AFTER;
    /**
     * Auto destroy herb time
     */
    public static int HERB_AUTO_DESTROY_TIME;
    /**
     * List of items that will not be destroyed (separated by ",")
     */
    public static String PROTECTED_ITEMS;
    /**
     * List of items that will not be destroyed
     */
    public static List<Integer> LIST_PROTECTED_ITEMS = new LinkedList<>();

    /**
     * Auto destroy nonequipable items dropped by players
     */
    public static boolean DESTROY_DROPPED_PLAYER_ITEM;
    /**
     * Auto destroy equipable items dropped by players
     */
    public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
    /**
     * Save items on ground for restoration on server restart
     */
    public static boolean SAVE_DROPPED_ITEM;
    /**
     * Empty table ItemsOnGround after load all items
     */
    public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
    /**
     * Time interval to save into db items on ground
     */
    public static int SAVE_DROPPED_ITEM_INTERVAL;
    /**
     * Clear all items stored in ItemsOnGround table
     */
    public static boolean CLEAR_DROPPED_ITEM_TABLE;

    /**
     * Accept precise drop calculation ?
     */
    public static boolean PRECISE_DROP_CALCULATION;
    /**
     * Accept multi-items drop ?
     */
    public static boolean MULTIPLE_ITEM_DROP;

    /**
     * This is setting of experimental Client <--> Server Player coordinates synchronization<br>
     * <b><u>Valeurs :</u></b> <li>0 - no synchronization at all</li> <li>1 - parcial synchronization Client --> Server only * using this option it is difficult for players to bypass obstacles</li> <li>2 - parcial synchronization Server --> Client only</li> <li>3 - full synchronization Client <-->
     * Server</li> <li>-1 - Old system: will synchronize Z only</li>
     */
    public static int COORD_SYNCHRONIZE;

    /**
     * Period in days after which character is deleted
     */
    public static int DELETE_DAYS;

    /**
     * Datapack root directory
     */
    public static File DATAPACK_ROOT;

    /**
     * Maximum range mobs can randomly go from spawn point
     */
    public static int MAX_DRIFT_RANGE;

    /**
     * Allow fishing ?
     */
    public static boolean ALLOWFISHING;

    /**
     * Allow Manor system
     */
    public static boolean ALLOW_MANOR;

    /**
     * Jail config
     **/
    public static boolean JAIL_IS_PVP;
    public static boolean JAIL_DISABLE_CHAT;
    public static String LANGUAGE;
    public static int SERVER_TYPE;

    /**
     * Enumeration describing values for Allowing the use of L2Walker client
     */
    public static enum L2WalkerAllowed {
        True,
        False,
        GM
    }

    /**
     * Allow the use of L2Walker client ?
     */
    public static L2WalkerAllowed ALLOW_L2WALKER_CLIENT;
    /**
     * Auto-ban client that use L2Walker ?
     */
    public static boolean AUTOBAN_L2WALKER_ACC;
    /**
     * Revision of L2Walker
     */
    public static int L2WALKER_REVISION;

    /**
     * FloodProtector initial capacity
     */
    public static int FLOODPROTECTOR_INITIALSIZE;

    /**
     * Allow Discard item ?
     */
    public static boolean ALLOW_DISCARDITEM;
    /**
     * Allow freight ?
     */
    public static boolean ALLOW_FREIGHT;
    /**
     * Allow warehouse ?
     */
    public static boolean ALLOW_WAREHOUSE;
    /**
     * Allow warehouse cache?
     */
    public static boolean WAREHOUSE_CACHE;
    /**
     * How long store WH datas
     */
    public static int WAREHOUSE_CACHE_TIME;
    /**
     * Allow wear ? (try on in shop)
     */
    public static boolean ALLOW_WEAR;
    /**
     * Duration of the try on after which items are taken back
     */
    public static int WEAR_DELAY;
    /**
     * Price of the try on of one item
     */
    public static int WEAR_PRICE;
    /**
     * Allow lottery ?
     */
    public static boolean ALLOW_LOTTERY;
    /**
     * Allow race ?
     */
    public static boolean ALLOW_RACE;
    /**
     * Allow water ?
     */
    public static boolean ALLOW_WATER;
    /**
     * Allow rent pet ?
     */
    public static boolean ALLOW_RENTPET;
    /**
     * Allow boat ?
     */
    public static boolean ALLOW_BOAT;
    /**
     * Allow cursed weapons ?
     */
    public static boolean ALLOW_CURSED_WEAPONS;

    // WALKER NPC
    public static boolean ALLOW_NPC_WALKERS;

    /**
     * Time after which a packet is considered as lost
     */
    public static int PACKET_LIFETIME;

    // Pets
    /**
     * Speed of Weverns
     */
    public static int WYVERN_SPEED;
    /**
     * Speed of Striders
     */
    public static int STRIDER_SPEED;
    /**
     * Allow Wyvern Upgrader ?
     */
    public static boolean ALLOW_WYVERN_UPGRADER;

    // protocol revision
    /**
     * Minimal protocol revision
     */
    public static int MIN_PROTOCOL_REVISION;
    /**
     * Maximal protocol revision
     */
    public static int MAX_PROTOCOL_REVISION;

    // random animation interval
    /**
     * Minimal time between 2 animations of a NPC
     */
    public static int MIN_NPC_ANIMATION;
    /**
     * Maximal time between 2 animations of a NPC
     */
    public static int MAX_NPC_ANIMATION;
    /**
     * Minimal time between animations of a MONSTER
     */
    public static int MIN_MONSTER_ANIMATION;
    /**
     * Maximal time between animations of a MONSTER
     */
    public static int MAX_MONSTER_ANIMATION;

    /**
     * Activate position recorder ?
     */
    public static boolean ACTIVATE_POSITION_RECORDER;
    /**
     * Use 3D Map ?
     */
    public static boolean USE_3D_MAP;

    // Community Board
    /**
     * Type of community
     */
    public static String COMMUNITY_TYPE;
    public static String BBS_DEFAULT;
    /**
     * Show level of the community board ?
     */
    public static boolean SHOW_LEVEL_COMMUNITYBOARD;
    /**
     * Show status of the community board ?
     */
    public static boolean SHOW_STATUS_COMMUNITYBOARD;
    /**
     * Size of the name page on the community board
     */
    public static int NAME_PAGE_SIZE_COMMUNITYBOARD;
    /**
     * Name per row on community board
     */
    public static int NAME_PER_ROW_COMMUNITYBOARD;

    // Configuration files
    /**
     * Properties file that allows selection of new Classes for storage of World Objects. <br>
     * This may help servers with large amounts of players recieving error messages related to the <i>L2ObjectHashMap</i> and <i>L2ObejctHashSet</i> classes.
     */
    /**
     * Properties file for game server (connection and ingame) configurations
     */
    public static final String CONFIGURATION_FILE = "./config/server.properties";
    /**
     * Properties file for game server options
     */
    public static final String OPTIONS_FILE = "./config/options.properties";
    /**
     * Properties file for login server configurations
     */
    public static final String LOGIN_CONFIGURATION_FILE = "./config/authserver.properties";
    /**
     * Properties file for the ID factory
     */
    public static final String ID_CONFIG_FILE = "./config/idfactory.properties";
    /**
     * Properties file for other configurations
     */
    public static final String OTHER_CONFIG_FILE = "./config/other.properties";
    /**
     * Properties file for rates configurations
     */
    public static final String RATES_CONFIG_FILE = "./config/rates.properties";
    /**
     * Properties file for alternative configuration
     */
    public static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";
    /**
     * Properties file for PVP configurations
     */
    public static final String PVP_CONFIG_FILE = "./config/pvp.properties";
    /**
     * Properties file for GM access configurations
     */
    public static final String GM_ACCESS_FILE = "./config/GMAccess.properties";
    /**
     * Properties file for telnet configuration
     */
    public static final String TELNET_FILE = "./config/telnet.properties";
    /**
     * Properties file for siege configuration
     */
    public static final String SIEGE_CONFIGURATION_FILE = "./config/siege.properties";
    /**
     * XML file for banned IP
     */
    public static final String BANNED_IP_XML = "./config/banned.xml";
    /**
     * Text file containing hexadecimal value of server ID
     */
    public static final String HEXID_FILE = "./config/hexid.txt";
    /**
     * Properties file for alternative configure GM commands access level.<br>
     * Note that this file only read if "AltPrivilegesAdmin = True"
     */
    public static final String COMMAND_PRIVILEGES_FILE = "./config/command-privileges.properties";
    /**
     * Properties file for AI configurations
     */
    public static final String AI_FILE = "./config/ai.properties";
    /**
     * Properties file for 7 Signs Festival
     */
    public static final String SEVENSIGNS_FILE = "./config/sevensigns.properties";
    public static final String CLANHALL_CONFIG_FILE = "./config/clanhall.properties";
    public static final String L2JMOD_CONFIG_FILE = "./config/l2jmods.properties";
    public static int MAX_ITEM_IN_PACKET;

    public static boolean CHECK_KNOWN;

    /**
     * Game Server login port
     */
    public static int GAME_SERVER_LOGIN_PORT;
    /**
     * Game Server login Host
     */
    public static String GAME_SERVER_LOGIN_HOST;
    /**
     * Internal Hostname
     */
    public static String INTERNAL_HOSTNAME;
    /**
     * External Hostname
     */
    public static String EXTERNAL_HOSTNAME;
    public static int PATH_NODE_RADIUS;
    public static int NEW_NODE_ID;
    public static int SELECTED_NODE_ID;
    public static int LINKED_NODE_ID;
    public static String NEW_NODE_TYPE;

    /**
     * Show "data/html/servnews.htm" whenever a character enters world.
     */
    public static boolean SERVER_NEWS;
    /**
     * Show L2Monster level and aggro ?
     */
    public static boolean SHOW_NPC_LVL;

    /**
     * Force full item inventory packet to be sent for any item change ?<br>
     * <u><i>Note:</i></u> This can increase network traffic
     */
    public static boolean FORCE_INVENTORY_UPDATE;
    /**
     * Disable the use of guards against agressive monsters ?
     */
    public static boolean ALLOW_GUARDS;
    /**
     * Allow use Event Managers for change occupation ?
     */
    public static boolean ALLOW_CLASS_MASTERS;
    /**
     * Time between 2 updates of IP
     */
    public static int IP_UPDATE_TIME;

    /**
     * Zone Setting
     */
    public static int ZONE_TOWN;

    /**
     * Crafting Enabled?
     */
    public static boolean IS_CRAFTING_ENABLED;

    // Inventory slots limit
    /**
     * Maximum inventory slots limits for non dwarf characters
     */
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    /**
     * Maximum inventory slots limits for dwarf characters
     */
    public static int INVENTORY_MAXIMUM_DWARF;
    /**
     * Maximum inventory slots limits for GM
     */
    public static int INVENTORY_MAXIMUM_GM;

    // Warehouse slots limits
    /**
     * Maximum inventory slots limits for non dwarf warehouse
     */
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    /**
     * Maximum inventory slots limits for dwarf warehouse
     */
    public static int WAREHOUSE_SLOTS_DWARF;
    /**
     * Maximum inventory slots limits for clan warehouse
     */
    public static int WAREHOUSE_SLOTS_CLAN;
    /**
     * Maximum inventory slots limits for freight
     */
    public static int FREIGHT_SLOTS;

    // Karma System Variables
    /**
     * Minimum karma gain/loss
     */
    public static int KARMA_MIN_KARMA;
    /**
     * Maximum karma gain/loss
     */
    public static int KARMA_MAX_KARMA;
    /**
     * Number to divide the xp recieved by, to calculate karma lost on xp gain/lost
     */
    public static int KARMA_XP_DIVIDER;
    /**
     * The Minimum Karma lost if 0 karma is to be removed
     */
    public static int KARMA_LOST_BASE;
    /**
     * Can a GM drop item ?
     */
    public static boolean KARMA_DROP_GM;
    /**
     * Should award a pvp point for killing a reader with karma ?
     */
    public static boolean KARMA_AWARD_PK_KILL;
    /**
     * Minimum PK required to drop
     */
    public static int KARMA_PK_LIMIT;

    /**
     * List of pet items that cannot be dropped (seperated by ",") when PVP
     */
    public static String KARMA_NONDROPPABLE_PET_ITEMS;
    /**
     * List of items that cannot be dropped (seperated by ",") when PVP
     */
    public static String KARMA_NONDROPPABLE_ITEMS;
    /**
     * List of pet items that cannot be dropped when PVP
     */
    public static List<Integer> KARMA_LIST_NONDROPPABLE_PET_ITEMS = new LinkedList<>();
    /**
     * List of items that cannot be dropped when PVP
     */
    public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new LinkedList<>();

    /**
     * List of items that cannot be dropped (seperated by ",")
     */
    public static String NONDROPPABLE_ITEMS;
    /**
     * List of items that cannot be dropped
     */
    public static List<Integer> LIST_NONDROPPABLE_ITEMS = new LinkedList<>();

    /**
     * List of NPCs that rent pets (seperated by ",")
     */
    public static String PET_RENT_NPC;
    /**
     * List of NPCs that rent pets
     */
    public static List<Integer> LIST_PET_RENT_NPC = new LinkedList<>();

    /**
     * Duration (in ms) while a reader stay in PVP mode after hitting an innocent
     */
    public static int PVP_NORMAL_TIME;
    /**
     * Duration (in ms) while a reader stay in PVP mode after hitting a purple reader
     */
    public static int PVP_PVP_TIME;

    // Karma Punishment
    /**
     * Allow reader with karma to be killed in peace zone ?
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
    /**
     * Allow reader with karma to shop ?
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    /**
     * Allow reader with karma to use gatekeepers ?
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
    /**
     * Allow reader with karma to use SOE or Return skill ?
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
    /**
     * Allow reader with karma to trade ?
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
    /**
     * Allow reader with karma to use warehouse ?
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;

    /** define L2JMODS */
    /**
     * Champion Mod
     */
    public static boolean L2JMOD_CHAMPION_ENABLE;
    public static int L2JMOD_CHAMPION_FREQUENCY;
    public static int L2JMOD_CHAMP_MIN_LVL;
    public static int L2JMOD_CHAMP_MAX_LVL;
    public static int L2JMOD_CHAMPION_HP;
    public static int L2JMOD_CHAMPION_REWARDS;
    public static int L2JMOD_CHAMPION_ADENAS_REWARDS;
    public static float L2JMOD_CHAMPION_HP_REGEN;
    public static float L2JMOD_CHAMPION_ATK;
    public static float L2JMOD_CHAMPION_SPD_ATK;
    public static int L2JMOD_CHAMPION_REWARD;
    public static int L2JMOD_CHAMPION_REWARD_ID;
    public static int L2JMOD_CHAMPION_REWARD_QTY;

    /**
     * Team vs. Team Event Engine
     */
    public static boolean TVT_EVENT_ENABLED;
    public static int TVT_EVENT_INTERVAL;
    public static int TVT_EVENT_PARTICIPATION_TIME;
    public static int TVT_EVENT_RUNNING_TIME;
    public static int TVT_EVENT_PARTICIPATION_NPC_ID;
    public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[3];
    public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
    public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
    public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
    public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
    public static String TVT_EVENT_TEAM_1_NAME;
    public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
    public static String TVT_EVENT_TEAM_2_NAME;
    public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
    public static List<int[]> TVT_EVENT_REWARDS = new LinkedList<>();
    public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
    public static boolean TVT_EVENT_POTIONS_ALLOWED;
    public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
    public static List<Integer> TVT_EVENT_DOOR_IDS = new LinkedList<>();
    public static byte TVT_EVENT_MIN_LVL;
    public static byte TVT_EVENT_MAX_LVL;

    /**
     * L2JMOD Wedding system
     */
    public static boolean L2JMOD_ALLOW_WEDDING;
    public static int L2JMOD_WEDDING_PRICE;
    public static boolean L2JMOD_WEDDING_PUNISH_INFIDELITY;
    public static boolean L2JMOD_WEDDING_TELEPORT;
    public static int L2JMOD_WEDDING_TELEPORT_PRICE;
    public static int L2JMOD_WEDDING_TELEPORT_DURATION;
    public static boolean L2JMOD_WEDDING_SAMESEX;
    public static boolean L2JMOD_WEDDING_FORMALWEAR;
    public static int L2JMOD_WEDDING_DIVORCE_COSTS;

    // Packet information
    /**
     * Count the amount of packets per minute ?
     */
    public static boolean COUNT_PACKETS = false;
    /**
     * Dump packet count ?
     */
    public static boolean DUMP_PACKET_COUNTS = false;
    /**
     * Time interval between 2 dumps
     */
    public static int DUMP_INTERVAL_SECONDS = 60;

    /**
     * Check for bad ID ?
     */
    public static boolean BAD_ID_CHECKING;

    /**
     * Allow lesser effects to be canceled if stronger effects are used when effects of the same stack group are used.<br>
     * New effects that are added will be canceled if they are of lesser priority to the old one.
     */
    public static boolean EFFECT_CANCELING;

    /**
     * Auto-delete invalid quest data ?
     */
    public static boolean AUTODELETE_INVALID_QUEST_DATA;

    /**
     * Chance that an item will succesfully be enchanted
     */
    public static int ENCHANT_CHANCE_WEAPON;
    public static int ENCHANT_CHANCE_ARMOR;
    public static int ENCHANT_CHANCE_JEWELRY;
    /**
     * Maximum level of enchantment
     */
    public static int ENCHANT_MAX_WEAPON;
    public static int ENCHANT_MAX_ARMOR;
    public static int ENCHANT_MAX_JEWELRY;
    /**
     * maximum level of safe enchantment for normal items
     */
    public static int ENCHANT_SAFE_MAX;
    /**
     * maximum level of safe enchantment for full body armor
     */
    public static int ENCHANT_SAFE_MAX_FULL;

    // Character multipliers
    /**
     * Multiplier for character HP regeneration
     */
    public static double HP_REGEN_MULTIPLIER;
    /**
     * Mutilplier for character MP regeneration
     */
    public static double MP_REGEN_MULTIPLIER;
    /**
     * Multiplier for character CP regeneration
     */
    public static double CP_REGEN_MULTIPLIER;

    // Raid Boss multipliers
    /**
     * Multiplier for Raid boss HP regeneration
     */
    public static double RAID_HP_REGEN_MULTIPLIER;
    /**
     * Mulitplier for Raid boss MP regeneration
     */
    public static double RAID_MP_REGEN_MULTIPLIER;
    /**
     * Multiplier for Raid boss defense multiplier
     */
    public static double RAID_DEFENCE_MULTIPLIER;
    /**
     * Raid Boss Minin Spawn Timer
     */
    public static double RAID_MINION_RESPAWN_TIMER;
    /**
     * Mulitplier for Raid boss minimum time respawn
     */
    public static float RAID_MIN_RESPAWN_MULTIPLIER;
    /**
     * Mulitplier for Raid boss maximum time respawn
     */
    public static float RAID_MAX_RESPAWN_MULTIPLIER;
    /**
     * Amount of adenas when starting a new character
     */
    public static int STARTING_ADENA;

    /**
     * Deep Blue Mobs' Drop Rules Enabled
     */
    public static boolean DEEPBLUE_DROP_RULES;
    public static int UNSTUCK_INTERVAL;

    /**
     * Is telnet enabled ?
     */
    public static boolean IS_TELNET_ENABLED;

    /**
     * Death Penalty chance
     */
    public static int DEATH_PENALTY_CHANCE;

    /**
     * Player Protection control
     */
    public static int PLAYER_SPAWN_PROTECTION;
    public static int PLAYER_FAKEDEATH_UP_PROTECTION;

    /**
     * Define Party XP cutoff point method - Possible values: level and percentage
     */
    public static String PARTY_XP_CUTOFF_METHOD;
    /**
     * Define the cutoff point value for the "level" method
     */
    public static int PARTY_XP_CUTOFF_LEVEL;
    /**
     * Define the cutoff point value for the "percentage" method
     */
    public static double PARTY_XP_CUTOFF_PERCENT;

    /**
     * Percent CP is restore on respawn
     */
    public static double RESPAWN_RESTORE_CP;
    /**
     * Percent HP is restore on respawn
     */
    public static double RESPAWN_RESTORE_HP;
    /**
     * Percent MP is restore on respawn
     */
    public static double RESPAWN_RESTORE_MP;
    /**
     * Allow randomizing of the respawn point in towns.
     */
    public static boolean RESPAWN_RANDOM_ENABLED;
    /**
     * The maximum offset from the base respawn point to allow.
     */
    public static int RESPAWN_RANDOM_MAX_OFFSET;

    /**
     * Maximum number of available slots for pvt stores (sell/buy) - Dwarves
     */
    public static int MAX_PVTSTORE_SLOTS_DWARF;
    /**
     * Maximum number of available slots for pvt stores (sell/buy) - Others
     */
    public static int MAX_PVTSTORE_SLOTS_OTHER;

    /**
     * Store skills cooltime on char exit/relogin
     */
    public static boolean STORE_SKILL_COOLTIME;

    /**
     * Force GameGuard authorization in loginserver
     */
    public static boolean FORCE_GGAUTH;

    /**
     * Default punishment for illegal actions
     */
    public static int DEFAULT_PUNISH;
    /**
     * Parameter for default punishment
     */
    public static int DEFAULT_PUNISH_PARAM;

    /**
     * Accept new game server ?
     */
    public static boolean ACCEPT_NEW_GAMESERVER;

    /**
     * Server ID used with the HexID
     */
    public static int SERVER_ID;
    /**
     * Hexadecimal ID of the game server
     */
    public static byte[] HEX_ID;

    /**
     * Accept alternate ID for server ?
     */
    public static boolean ACCEPT_ALTERNATE_ID;
    /**
     * ID for request to the server
     */
    public static int REQUEST_ID;
    public static boolean RESERVE_HOST_ON_LOGIN = false;

    public static int MINIMUM_UPDATE_DISTANCE;
    public static int KNOWNLIST_FORGET_DELAY;
    public static int MINIMUN_UPDATE_TIME;

    public static boolean ANNOUNCE_MAMMON_SPAWN;
    public static boolean LAZY_CACHE;

    /**
     * Enable colored name for GM ?
     */
    public static boolean GM_NAME_COLOR_ENABLED;
    /**
     * Color of GM name
     */
    public static int GM_NAME_COLOR;
    /**
     * Color of admin name
     */
    public static int ADMIN_NAME_COLOR;
    /**
     * Place an aura around the GM ?
     */
    public static boolean GM_HERO_AURA;
    /**
     * Set the GM invulnerable at startup ?
     */
    public static boolean GM_STARTUP_INVULNERABLE;
    /**
     * Set the GM invisible at startup ?
     */
    public static boolean GM_STARTUP_INVISIBLE;
    /**
     * Set silence to GM at startup ?
     */
    public static boolean GM_STARTUP_SILENCE;
    /**
     * Add GM in the GM list at startup ?
     */
    public static boolean GM_STARTUP_AUTO_LIST;
    /**
     * Change the way admin panel is shown
     */
    public static String GM_ADMIN_MENU_STYLE;

    /**
     * Allow petition ?
     */
    public static boolean PETITIONING_ALLOWED;
    /**
     * Maximum number of petitions per reader
     */
    public static int MAX_PETITIONS_PER_PLAYER;
    /**
     * Maximum number of petitions pending
     */
    public static int MAX_PETITIONS_PENDING;

    /**
     * Bypass exploit protection ?
     */
    public static boolean BYPASS_VALIDATION;

    /**
     * Only GM buy items for free
     **/
    public static boolean ONLY_GM_ITEMS_FREE;

    /**
     * GM Audit ?
     */
    public static boolean GMAUDIT;

    public static boolean FLOOD_PROTECTION;
    public static int FAST_CONNECTION_LIMIT;
    public static int NORMAL_CONNECTION_TIME;
    public static int FAST_CONNECTION_TIME;
    public static int MAX_CONNECTION_PER_IP;

    /**
     * Enforce gameguard query on character login ?
     */
    public static boolean GAMEGUARD_ENFORCE;
    /**
     * Don't allow reader to perform trade,talk with npc and move until gameguard reply received ?
     */
    public static boolean GAMEGUARD_PROHIBITACTION;

    /**
     * Recipebook limits
     */
    public static int DWARF_RECIPE_LIMIT;
    public static int COMMON_RECIPE_LIMIT;

    /**
     * Grid Options
     */
    public static boolean GRIDS_ALWAYS_ON;
    public static int GRID_NEIGHBOR_TURNON_TIME;
    public static int GRID_NEIGHBOR_TURNOFF_TIME;

    /**
     * Clan Hall function related configs
     */
    public static long CH_TELE_FEE_RATIO;
    public static int CH_TELE1_FEE;
    public static int CH_TELE2_FEE;
    public static long CH_ITEM_FEE_RATIO;
    public static int CH_ITEM1_FEE;
    public static int CH_ITEM2_FEE;
    public static int CH_ITEM3_FEE;
    public static long CH_MPREG_FEE_RATIO;
    public static int CH_MPREG1_FEE;
    public static int CH_MPREG2_FEE;
    public static int CH_MPREG3_FEE;
    public static int CH_MPREG4_FEE;
    public static int CH_MPREG5_FEE;
    public static long CH_HPREG_FEE_RATIO;
    public static int CH_HPREG1_FEE;
    public static int CH_HPREG2_FEE;
    public static int CH_HPREG3_FEE;
    public static int CH_HPREG4_FEE;
    public static int CH_HPREG5_FEE;
    public static int CH_HPREG6_FEE;
    public static int CH_HPREG7_FEE;
    public static int CH_HPREG8_FEE;
    public static int CH_HPREG9_FEE;
    public static int CH_HPREG10_FEE;
    public static int CH_HPREG11_FEE;
    public static int CH_HPREG12_FEE;
    public static int CH_HPREG13_FEE;
    public static long CH_EXPREG_FEE_RATIO;
    public static int CH_EXPREG1_FEE;
    public static int CH_EXPREG2_FEE;
    public static int CH_EXPREG3_FEE;
    public static int CH_EXPREG4_FEE;
    public static int CH_EXPREG5_FEE;
    public static int CH_EXPREG6_FEE;
    public static int CH_EXPREG7_FEE;
    public static long CH_SUPPORT_FEE_RATIO;
    public static int CH_SUPPORT1_FEE;
    public static int CH_SUPPORT2_FEE;
    public static int CH_SUPPORT3_FEE;
    public static int CH_SUPPORT4_FEE;
    public static int CH_SUPPORT5_FEE;
    public static int CH_SUPPORT6_FEE;
    public static int CH_SUPPORT7_FEE;
    public static int CH_SUPPORT8_FEE;
    public static long CH_CURTAIN_FEE_RATIO;
    public static int CH_CURTAIN1_FEE;
    public static int CH_CURTAIN2_FEE;
    public static long CH_FRONT_FEE_RATIO;
    public static int CH_FRONT1_FEE;
    public static int CH_FRONT2_FEE;

    /**
     * GeoData 0/1/2
     */
    public static int GEODATA;
    /**
     * Force loading GeoData to psychical memory
     */
    public static boolean FORCE_GEODATA;
    public static boolean ACCEPT_GEOEDITOR_CONN;

    /**
     * Max amount of buffs
     */
    public static byte BUFFS_MAX_AMOUNT;

    /**
     * Alt Settings for devs
     */
    public static boolean ALT_DEV_NO_QUESTS;
    public static boolean ALT_DEV_NO_SPAWNS;

    /**
     * This class initializes all global variables for configuration.<br>
     * If key doesn't appear in properties file, a default value is setting on by this class.
     *
     * @see #CONFIGURATION_FILE for configuring your server.
     */
    public static void load() {
        if (Server.serverMode == Server.MODE_GAMESERVER) {
            _log.info("loading gameserver config");
            try {
                Properties serverSettings = new Properties();
                InputStream is = new FileInputStream(new File(CONFIGURATION_FILE));
                serverSettings.load(is);
                is.close();

                GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname");
                PORT_GAME = Integer.parseInt(serverSettings.getProperty("GameserverPort", "7777"));

                EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "*");
                INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "*");

                GAME_SERVER_LOGIN_PORT = Integer.parseInt(serverSettings.getProperty("LoginPort", "9014"));
                GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");

                REQUEST_ID = Integer.parseInt(serverSettings.getProperty("RequestServerID", "0"));
                ACCEPT_ALTERNATE_ID = Boolean.parseBoolean(serverSettings.getProperty("AcceptAlternateID", "True"));

                DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
                DATABASE_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jdb");
                DATABASE_LOGIN = serverSettings.getProperty("Login", "root");
                DATABASE_PASSWORD = serverSettings.getProperty("Password", "");
                DATABASE_MAX_CONNECTIONS = Integer.parseInt(serverSettings.getProperty("MaximumDbConnections", "10"));
                DATABASE_MAX_IDLE_TIME = Integer.parseInt(serverSettings.getProperty("MaximumDbIdleTime", "0"));

                DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();

                CNAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", ".*");
                PET_NAME_TEMPLATE = serverSettings.getProperty("PetNameTemplate", ".*");

                MAX_CHARACTERS_NUMBER_PER_ACCOUNT = Integer.parseInt(serverSettings.getProperty("CharMaxNumber", "0"));
                MAXIMUM_ONLINE_USERS = Integer.parseInt(serverSettings.getProperty("MaximumOnlineUsers", "100"));

                MIN_PROTOCOL_REVISION = Integer.parseInt(serverSettings.getProperty("MinProtocolRevision", "660"));
                MAX_PROTOCOL_REVISION = Integer.parseInt(serverSettings.getProperty("MaxProtocolRevision", "665"));

                if (MIN_PROTOCOL_REVISION > MAX_PROTOCOL_REVISION) {
                    throw new Error("MinProtocolRevision is bigger than MaxProtocolRevision in server configuration file.");
                }

                LANGUAGE = serverSettings.getProperty("Language", "en_US");

                String serverTypes = serverSettings.getProperty("ServerType", "Classic");
                for (String type : serverTypes.split(",")) {
                    try {
                        ServerType serverType = ServerType.valueOf(type.trim().toUpperCase());
                        SERVER_TYPE |= serverType.getId();
                    } catch(Exception e) {
                        _log.warn(e.getLocalizedMessage(), e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + CONFIGURATION_FILE + " File.");
            }
            try {
                Properties optionsSettings = new Properties();
                InputStream is = new FileInputStream(new File(OPTIONS_FILE));
                optionsSettings.load(is);
                is.close();

                EVERYBODY_HAS_ADMIN_RIGHTS = Boolean.parseBoolean(optionsSettings.getProperty("EverybodyHasAdminRights", "false"));

                DEBUG = Boolean.parseBoolean(optionsSettings.getProperty("Debug", "false"));
                ASSERT = Boolean.parseBoolean(optionsSettings.getProperty("Assert", "false"));
                DEVELOPER = Boolean.parseBoolean(optionsSettings.getProperty("Developer", "false"));
                TEST_SERVER = Boolean.parseBoolean(optionsSettings.getProperty("TestServer", "false"));
                SERVER_LIST_TESTSERVER = Boolean.parseBoolean(optionsSettings.getProperty("TestServer", "false"));

                SERVER_LIST_BRACKET = Boolean.valueOf(optionsSettings.getProperty("ServerListBrackets", "false"));
                SERVER_LIST_CLOCK = Boolean.valueOf(optionsSettings.getProperty("ServerListClock", "false"));
                SERVER_GMONLY = Boolean.valueOf(optionsSettings.getProperty("ServerGMOnly", "false"));

                AUTODESTROY_ITEM_AFTER = Integer.parseInt(optionsSettings.getProperty("AutoDestroyDroppedItemAfter", "0"));
                HERB_AUTO_DESTROY_TIME = Integer.parseInt(optionsSettings.getProperty("AutoDestroyHerbTime", "15")) * 1000;
                PROTECTED_ITEMS = optionsSettings.getProperty("ListOfProtectedItems");
                LIST_PROTECTED_ITEMS = new LinkedList<>();
                for (String id : PROTECTED_ITEMS.split(",")) {
                    LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
                }
                DESTROY_DROPPED_PLAYER_ITEM = Boolean.valueOf(optionsSettings.getProperty("DestroyPlayerDroppedItem", "false"));
                DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.valueOf(optionsSettings.getProperty("DestroyEquipableItem", "false"));
                SAVE_DROPPED_ITEM = Boolean.valueOf(optionsSettings.getProperty("SaveDroppedItem", "false"));
                EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.valueOf(optionsSettings.getProperty("EmptyDroppedItemTableAfterLoad", "false"));
                SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(optionsSettings.getProperty("SaveDroppedItemInterval", "0")) * 60000;
                CLEAR_DROPPED_ITEM_TABLE = Boolean.valueOf(optionsSettings.getProperty("ClearDroppedItemTable", "false"));

                PRECISE_DROP_CALCULATION = Boolean.valueOf(optionsSettings.getProperty("PreciseDropCalculation", "True"));
                MULTIPLE_ITEM_DROP = Boolean.valueOf(optionsSettings.getProperty("MultipleItemDrop", "True"));

                COORD_SYNCHRONIZE = Integer.parseInt(optionsSettings.getProperty("CoordSynchronize", "-1"));

                ONLY_GM_ITEMS_FREE = Boolean.valueOf(optionsSettings.getProperty("OnlyGMItemsFree", "True"));

                ALLOW_WAREHOUSE = Boolean.valueOf(optionsSettings.getProperty("AllowWarehouse", "True"));
                WAREHOUSE_CACHE = Boolean.valueOf(optionsSettings.getProperty("WarehouseCache", "False"));
                WAREHOUSE_CACHE_TIME = Integer.parseInt(optionsSettings.getProperty("WarehouseCacheTime", "15"));
                ALLOW_FREIGHT = Boolean.valueOf(optionsSettings.getProperty("AllowFreight", "True"));
                ALLOW_WEAR = Boolean.valueOf(optionsSettings.getProperty("AllowWear", "False"));
                WEAR_DELAY = Integer.parseInt(optionsSettings.getProperty("WearDelay", "5"));
                WEAR_PRICE = Integer.parseInt(optionsSettings.getProperty("WearPrice", "10"));
                ALLOW_LOTTERY = Boolean.valueOf(optionsSettings.getProperty("AllowLottery", "False"));
                ALLOW_RACE = Boolean.valueOf(optionsSettings.getProperty("AllowRace", "False"));
                ALLOW_WATER = Boolean.valueOf(optionsSettings.getProperty("AllowWater", "False"));
                ALLOW_RENTPET = Boolean.valueOf(optionsSettings.getProperty("AllowRentPet", "False"));
                FLOODPROTECTOR_INITIALSIZE = Integer.parseInt(optionsSettings.getProperty("FloodProtectorInitialSize", "50"));
                ALLOW_DISCARDITEM = Boolean.valueOf(optionsSettings.getProperty("AllowDiscardItem", "True"));
                ALLOWFISHING = Boolean.valueOf(optionsSettings.getProperty("AllowFishing", "False"));
                ALLOW_MANOR = Boolean.parseBoolean(optionsSettings.getProperty("AllowManor", "False"));
                ALLOW_BOAT = Boolean.valueOf(optionsSettings.getProperty("AllowBoat", "False"));
                ALLOW_NPC_WALKERS = Boolean.valueOf(optionsSettings.getProperty("AllowNpcWalkers", "true"));
                ALLOW_CURSED_WEAPONS = Boolean.valueOf(optionsSettings.getProperty("AllowCursedWeapons", "False"));

                ALLOW_L2WALKER_CLIENT = L2WalkerAllowed.valueOf(optionsSettings.getProperty("AllowL2Walker", "False"));
                L2WALKER_REVISION = Integer.parseInt(optionsSettings.getProperty("L2WalkerRevision", "537"));
                AUTOBAN_L2WALKER_ACC = Boolean.valueOf(optionsSettings.getProperty("AutobanL2WalkerAcc", "False"));

                ACTIVATE_POSITION_RECORDER = Boolean.valueOf(optionsSettings.getProperty("ActivatePositionRecorder", "False"));

                DEFAULT_GLOBAL_CHAT = optionsSettings.getProperty("GlobalChat", "ON");
                DEFAULT_TRADE_CHAT = optionsSettings.getProperty("TradeChat", "ON");

                LOG_CHAT = Boolean.valueOf(optionsSettings.getProperty("LogChat", "false"));
                LOG_ITEMS = Boolean.valueOf(optionsSettings.getProperty("LogItems", "false"));

                GMAUDIT = Boolean.valueOf(optionsSettings.getProperty("GMAudit", "False"));

                COMMUNITY_TYPE = optionsSettings.getProperty("CommunityType", "old").toLowerCase();
                BBS_DEFAULT = optionsSettings.getProperty("BBSDefault", "_bbshome");
                SHOW_LEVEL_COMMUNITYBOARD = Boolean.valueOf(optionsSettings.getProperty("ShowLevelOnCommunityBoard", "False"));
                SHOW_STATUS_COMMUNITYBOARD = Boolean.valueOf(optionsSettings.getProperty("ShowStatusOnCommunityBoard", "True"));
                NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(optionsSettings.getProperty("NamePageSizeOnCommunityBoard", "50"));
                NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(optionsSettings.getProperty("NamePerRowOnCommunityBoard", "5"));

                ZONE_TOWN = Integer.parseInt(optionsSettings.getProperty("ZoneTown", "0"));

                MAX_DRIFT_RANGE = Integer.parseInt(optionsSettings.getProperty("MaxDriftRange", "300"));

                MIN_NPC_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MinNPCAnimation", "10"));
                MAX_NPC_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MaxNPCAnimation", "20"));
                MIN_MONSTER_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MinMonsterAnimation", "5"));
                MAX_MONSTER_ANIMATION = Integer.parseInt(optionsSettings.getProperty("MaxMonsterAnimation", "20"));

                SERVER_NEWS = Boolean.valueOf(optionsSettings.getProperty("ShowServerNews", "False"));
                SHOW_NPC_LVL = Boolean.valueOf(optionsSettings.getProperty("ShowNpcLevel", "False"));

                FORCE_INVENTORY_UPDATE = Boolean.valueOf(optionsSettings.getProperty("ForceInventoryUpdate", "False"));

                AUTODELETE_INVALID_QUEST_DATA = Boolean.valueOf(optionsSettings.getProperty("AutoDeleteInvalidQuestData", "False"));

                THREAD_P_EFFECTS = Integer.parseInt(optionsSettings.getProperty("ThreadPoolSizeEffects", "6"));
                THREAD_P_GENERAL = Integer.parseInt(optionsSettings.getProperty("ThreadPoolSizeGeneral", "15"));
                GENERAL_PACKET_THREAD_CORE_SIZE = Integer.parseInt(optionsSettings.getProperty("GeneralPacketThreadCoreSize", "4"));
                IO_PACKET_THREAD_CORE_SIZE = Integer.parseInt(optionsSettings.getProperty("UrgentPacketThreadCoreSize", "2"));
                AI_MAX_THREAD = Integer.parseInt(optionsSettings.getProperty("AiMaxThread", "10"));
                GENERAL_THREAD_CORE_SIZE = Integer.parseInt(optionsSettings.getProperty("GeneralThreadCoreSize", "4"));

                DELETE_DAYS = Integer.parseInt(optionsSettings.getProperty("DeleteCharAfterDays", "7"));

                DEFAULT_PUNISH = Integer.parseInt(optionsSettings.getProperty("DefaultPunish", "2"));
                DEFAULT_PUNISH_PARAM = Integer.parseInt(optionsSettings.getProperty("DefaultPunishParam", "0"));

                LAZY_CACHE = Boolean.valueOf(optionsSettings.getProperty("LazyCache", "False"));

                PACKET_LIFETIME = Integer.parseInt(optionsSettings.getProperty("PacketLifeTime", "0"));

                BYPASS_VALIDATION = Boolean.valueOf(optionsSettings.getProperty("BypassValidation", "True"));

                GAMEGUARD_ENFORCE = Boolean.valueOf(optionsSettings.getProperty("GameGuardEnforce", "False"));
                GAMEGUARD_PROHIBITACTION = Boolean.valueOf(optionsSettings.getProperty("GameGuardProhibitAction", "False"));

                GRIDS_ALWAYS_ON = Boolean.parseBoolean(optionsSettings.getProperty("GridsAlwaysOn", "False"));
                GRID_NEIGHBOR_TURNON_TIME = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOnTime", "30"));
                GRID_NEIGHBOR_TURNOFF_TIME = Integer.parseInt(optionsSettings.getProperty("GridNeighborTurnOffTime", "300"));

                GEODATA = Integer.parseInt(optionsSettings.getProperty("GeoData", "0"));
                FORCE_GEODATA = Boolean.parseBoolean(optionsSettings.getProperty("ForceGeoData", "True"));
                ACCEPT_GEOEDITOR_CONN = Boolean.parseBoolean(optionsSettings.getProperty("AcceptGeoeditorConn", "False"));

                // ---------------------------------------------------
                // Configuration values not found in config files
                // ---------------------------------------------------

                USE_3D_MAP = Boolean.valueOf(optionsSettings.getProperty("Use3DMap", "False"));

                PATH_NODE_RADIUS = Integer.parseInt(optionsSettings.getProperty("PathNodeRadius", "50"));
                NEW_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
                SELECTED_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
                LINKED_NODE_ID = Integer.parseInt(optionsSettings.getProperty("NewNodeId", "7952"));
                NEW_NODE_TYPE = optionsSettings.getProperty("NewNodeType", "npc");

                COUNT_PACKETS = Boolean.valueOf(optionsSettings.getProperty("CountPacket", "false"));
                DUMP_PACKET_COUNTS = Boolean.valueOf(optionsSettings.getProperty("DumpPacketCounts", "false"));
                DUMP_INTERVAL_SECONDS = Integer.parseInt(optionsSettings.getProperty("PacketDumpInterval", "60"));

                MINIMUM_UPDATE_DISTANCE = Integer.parseInt(optionsSettings.getProperty("MaximumUpdateDistance", "50"));
                MINIMUN_UPDATE_TIME = Integer.parseInt(optionsSettings.getProperty("MinimumUpdateTime", "500"));
                CHECK_KNOWN = Boolean.valueOf(optionsSettings.getProperty("CheckKnownList", "false"));
                KNOWNLIST_FORGET_DELAY = Integer.parseInt(optionsSettings.getProperty("KnownListForgetDelay", "10000"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + OPTIONS_FILE + " File.");
            }

            // telnet
            try {
                Properties telnetSettings = new Properties();
                InputStream is = new FileInputStream(new File(TELNET_FILE));
                telnetSettings.load(is);
                is.close();

                IS_TELNET_ENABLED = Boolean.valueOf(telnetSettings.getProperty("EnableTelnet", "false"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + TELNET_FILE + " File.");
            }

            // id factory
            try {
                Properties idSettings = new Properties();
                InputStream is = new FileInputStream(new File(ID_CONFIG_FILE));
                idSettings.load(is);
                is.close();

                BAD_ID_CHECKING = Boolean.valueOf(idSettings.getProperty("BadIdChecking", "True"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + ID_CONFIG_FILE + " File.");
            }

            // other
            try {
                Properties otherSettings = new Properties();
                InputStream is = new FileInputStream(new File(OTHER_CONFIG_FILE));
                otherSettings.load(is);
                is.close();

                DEEPBLUE_DROP_RULES = Boolean.parseBoolean(otherSettings.getProperty("UseDeepBlueDropRules", "True"));
                ALLOW_GUARDS = Boolean.valueOf(otherSettings.getProperty("AllowGuards", "False"));
                EFFECT_CANCELING = Boolean.valueOf(otherSettings.getProperty("CancelLesserEffect", "True"));
                WYVERN_SPEED = Integer.parseInt(otherSettings.getProperty("WyvernSpeed", "100"));
                STRIDER_SPEED = Integer.parseInt(otherSettings.getProperty("StriderSpeed", "80"));
                ALLOW_WYVERN_UPGRADER = Boolean.valueOf(otherSettings.getProperty("AllowWyvernUpgrader", "False"));

                /* Inventory slots limits */
                INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForNoDwarf", "80"));
                INVENTORY_MAXIMUM_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForDwarf", "100"));
                INVENTORY_MAXIMUM_GM = Integer.parseInt(otherSettings.getProperty("MaximumSlotsForGMPlayer", "250"));
                MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));

                /* Inventory slots limits */
                WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForNoDwarf", "100"));
                WAREHOUSE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForDwarf", "120"));
                WAREHOUSE_SLOTS_CLAN = Integer.parseInt(otherSettings.getProperty("MaximumWarehouseSlotsForClan", "150"));
                FREIGHT_SLOTS = Integer.parseInt(otherSettings.getProperty("MaximumFreightSlots", "20"));

                /* chance to enchant an item over +3 */
                ENCHANT_CHANCE_WEAPON = Integer.parseInt(otherSettings.getProperty("EnchantChanceWeapon", "68"));
                ENCHANT_CHANCE_ARMOR = Integer.parseInt(otherSettings.getProperty("EnchantChanceArmor", "52"));
                ENCHANT_CHANCE_JEWELRY = Integer.parseInt(otherSettings.getProperty("EnchantChanceJewelry", "54"));
                /* limit on enchant */
                ENCHANT_MAX_WEAPON = Integer.parseInt(otherSettings.getProperty("EnchantMaxWeapon", "255"));
                ENCHANT_MAX_ARMOR = Integer.parseInt(otherSettings.getProperty("EnchantMaxArmor", "255"));
                ENCHANT_MAX_JEWELRY = Integer.parseInt(otherSettings.getProperty("EnchantMaxJewelry", "255"));
                /* limit of safe enchant normal */
                ENCHANT_SAFE_MAX = Integer.parseInt(otherSettings.getProperty("EnchantSafeMax", "3"));
                /* limit of safe enchant full */
                ENCHANT_SAFE_MAX_FULL = Integer.parseInt(otherSettings.getProperty("EnchantSafeMaxFull", "4"));

                /* if different from 100 (ie 100%) heal rate is modified acordingly */
                HP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("HpRegenMultiplier", "100")) / 100;
                MP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("MpRegenMultiplier", "100")) / 100;
                CP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("CpRegenMultiplier", "100")) / 100;

                RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("RaidHpRegenMultiplier", "100")) / 100;
                RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("RaidMpRegenMultiplier", "100")) / 100;
                RAID_DEFENCE_MULTIPLIER = Double.parseDouble(otherSettings.getProperty("RaidDefenceMultiplier", "100")) / 100;
                RAID_MINION_RESPAWN_TIMER = Integer.parseInt(otherSettings.getProperty("RaidMinionRespawnTime", "300000"));
                RAID_MIN_RESPAWN_MULTIPLIER = Float.parseFloat(otherSettings.getProperty("RaidMinRespawnMultiplier", "1.0"));
                RAID_MAX_RESPAWN_MULTIPLIER = Float.parseFloat(otherSettings.getProperty("RaidMaxRespawnMultiplier", "1.0"));

                STARTING_ADENA = Integer.parseInt(otherSettings.getProperty("StartingAdena", "100"));
                UNSTUCK_INTERVAL = Integer.parseInt(otherSettings.getProperty("UnstuckInterval", "300"));

                /* Player protection after teleport or login */
                PLAYER_SPAWN_PROTECTION = Integer.parseInt(otherSettings.getProperty("PlayerSpawnProtection", "0"));

                /* Player protection after recovering from fake death (works against mobs only) */
                PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(otherSettings.getProperty("PlayerFakeDeathUpProtection", "0"));

                /* Defines some Party XP related values */
                PARTY_XP_CUTOFF_METHOD = otherSettings.getProperty("PartyXpCutoffMethod", "percentage");
                PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(otherSettings.getProperty("PartyXpCutoffPercent", "3."));
                PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(otherSettings.getProperty("PartyXpCutoffLevel", "30"));

                /* Amount of HP, MP, and CP is restored */
                RESPAWN_RESTORE_CP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreCP", "0")) / 100;
                RESPAWN_RESTORE_HP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreHP", "70")) / 100;
                RESPAWN_RESTORE_MP = Double.parseDouble(otherSettings.getProperty("RespawnRestoreMP", "70")) / 100;

                RESPAWN_RANDOM_ENABLED = Boolean.parseBoolean(otherSettings.getProperty("RespawnRandomInTown", "False"));
                RESPAWN_RANDOM_MAX_OFFSET = Integer.parseInt(otherSettings.getProperty("RespawnRandomMaxOffset", "50"));

                /* Maximum number of available slots for pvt stores */
                MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsDwarf", "5"));
                MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(otherSettings.getProperty("MaxPvtStoreSlotsOther", "4"));

                STORE_SKILL_COOLTIME = Boolean.parseBoolean(otherSettings.getProperty("StoreSkillCooltime", "true"));

                PET_RENT_NPC = otherSettings.getProperty("ListPetRentNpc", "30827");
                LIST_PET_RENT_NPC = new LinkedList<>();
                for (String id : PET_RENT_NPC.split(",")) {
                    LIST_PET_RENT_NPC.add(Integer.parseInt(id));
                }
                NONDROPPABLE_ITEMS = otherSettings.getProperty("ListOfNonDroppableItems", "1147,425,1146,461,10,2368,7,6,2370,2369,5598");

                LIST_NONDROPPABLE_ITEMS = new LinkedList<>();
                for (String id : NONDROPPABLE_ITEMS.split(",")) {
                    LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id));
                }

                ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(otherSettings.getProperty("AnnounceMammonSpawn", "True"));

                ALT_PRIVILEGES_ADMIN = Boolean.parseBoolean(otherSettings.getProperty("AltPrivilegesAdmin", "False"));
                ALT_PRIVILEGES_SECURE_CHECK = Boolean.parseBoolean(otherSettings.getProperty("AltPrivilegesSecureCheck", "True"));
                ALT_PRIVILEGES_DEFAULT_LEVEL = Integer.parseInt(otherSettings.getProperty("AltPrivilegesDefaultLevel", "100"));

                GM_NAME_COLOR_ENABLED = Boolean.parseBoolean(otherSettings.getProperty("GMNameColorEnabled", "False"));
                GM_NAME_COLOR = Integer.decode("0x" + otherSettings.getProperty("GMNameColor", "FFFF00"));
                ADMIN_NAME_COLOR = Integer.decode("0x" + otherSettings.getProperty("AdminNameColor", "00FF00"));
                GM_HERO_AURA = Boolean.parseBoolean(otherSettings.getProperty("GMHeroAura", "True"));
                GM_STARTUP_INVULNERABLE = Boolean.parseBoolean(otherSettings.getProperty("GMStartupInvulnerable", "True"));
                GM_STARTUP_INVISIBLE = Boolean.parseBoolean(otherSettings.getProperty("GMStartupInvisible", "True"));
                GM_STARTUP_SILENCE = Boolean.parseBoolean(otherSettings.getProperty("GMStartupSilence", "True"));
                GM_STARTUP_AUTO_LIST = Boolean.parseBoolean(otherSettings.getProperty("GMStartupAutoList", "True"));
                GM_ADMIN_MENU_STYLE = otherSettings.getProperty("GMAdminMenuStyle", "modern");

                PETITIONING_ALLOWED = Boolean.parseBoolean(otherSettings.getProperty("PetitioningAllowed", "True"));
                MAX_PETITIONS_PER_PLAYER = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPerPlayer", "5"));
                MAX_PETITIONS_PENDING = Integer.parseInt(otherSettings.getProperty("MaxPetitionsPending", "25"));

                JAIL_IS_PVP = Boolean.valueOf(otherSettings.getProperty("JailIsPvp", "True"));
                JAIL_DISABLE_CHAT = Boolean.valueOf(otherSettings.getProperty("JailDisableChat", "True"));

                DEATH_PENALTY_CHANCE = Integer.parseInt(otherSettings.getProperty("DeathPenaltyChance", "20"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + OTHER_CONFIG_FILE + " File.");
            }

            // rates
            try {
                Properties ratesSettings = new Properties();
                InputStream is = new FileInputStream(new File(RATES_CONFIG_FILE));
                ratesSettings.load(is);
                is.close();

                RATE_XP = Float.parseFloat(ratesSettings.getProperty("RateXp", "1."));
                RATE_SP = Float.parseFloat(ratesSettings.getProperty("RateSp", "1."));
                RATE_PARTY_XP = Float.parseFloat(ratesSettings.getProperty("RatePartyXp", "1."));
                RATE_PARTY_SP = Float.parseFloat(ratesSettings.getProperty("RatePartySp", "1."));
                RATE_QUESTS_REWARD = Float.parseFloat(ratesSettings.getProperty("RateQuestsReward", "1."));
                RATE_DROP_ADENA = Float.parseFloat(ratesSettings.getProperty("RateDropAdena", "1."));
                RATE_CONSUMABLE_COST = Float.parseFloat(ratesSettings.getProperty("RateConsumableCost", "1."));
                RATE_DROP_ITEMS = Float.parseFloat(ratesSettings.getProperty("RateDropItems", "1."));
                RATE_DROP_SPOIL = Float.parseFloat(ratesSettings.getProperty("RateDropSpoil", "1."));
                RATE_DROP_MANOR = Integer.parseInt(ratesSettings.getProperty("RateDropManor", "1"));
                RATE_DROP_QUEST = Float.parseFloat(ratesSettings.getProperty("RateDropQuest", "1."));
                RATE_KARMA_EXP_LOST = Float.parseFloat(ratesSettings.getProperty("RateKarmaExpLost", "1."));
                RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(ratesSettings.getProperty("RateSiegeGuardsPrice", "1."));
                RATE_DROP_COMMON_HERBS = Float.parseFloat(ratesSettings.getProperty("RateCommonHerbs", "15."));
                RATE_DROP_MP_HP_HERBS = Float.parseFloat(ratesSettings.getProperty("RateHpMpHerbs", "10."));
                RATE_DROP_GREATER_HERBS = Float.parseFloat(ratesSettings.getProperty("RateGreaterHerbs", "4."));
                RATE_DROP_SUPERIOR_HERBS = Float.parseFloat(ratesSettings.getProperty("RateSuperiorHerbs", "0.8")) * 10;
                RATE_DROP_SPECIAL_HERBS = Float.parseFloat(ratesSettings.getProperty("RateSpecialHerbs", "0.2")) * 10;

                PLAYER_DROP_LIMIT = Integer.parseInt(ratesSettings.getProperty("PlayerDropLimit", "3"));
                PLAYER_RATE_DROP = Integer.parseInt(ratesSettings.getProperty("PlayerRateDrop", "5"));
                PLAYER_RATE_DROP_ITEM = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropItem", "70"));
                PLAYER_RATE_DROP_EQUIP = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquip", "25"));
                PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(ratesSettings.getProperty("PlayerRateDropEquipWeapon", "5"));

                PET_XP_RATE = Float.parseFloat(ratesSettings.getProperty("PetXpRate", "1."));
                PET_FOOD_RATE = Integer.parseInt(ratesSettings.getProperty("PetFoodRate", "1"));
                SINEATER_XP_RATE = Float.parseFloat(ratesSettings.getProperty("SinEaterXpRate", "1."));

                KARMA_DROP_LIMIT = Integer.parseInt(ratesSettings.getProperty("KarmaDropLimit", "10"));
                KARMA_RATE_DROP = Integer.parseInt(ratesSettings.getProperty("KarmaRateDrop", "70"));
                KARMA_RATE_DROP_ITEM = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropItem", "50"));
                KARMA_RATE_DROP_EQUIP = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquip", "40"));
                KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(ratesSettings.getProperty("KarmaRateDropEquipWeapon", "10"));

            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + RATES_CONFIG_FILE + " File.");
            }

            // alternative settings
            try {
                Properties altSettings = new Properties();
                InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
                altSettings.load(is);
                is.close();

                ALT_GAME_TIREDNESS = Boolean.parseBoolean(altSettings.getProperty("AltGameTiredness", "false"));
                ALT_GAME_CREATION = Boolean.parseBoolean(altSettings.getProperty("AltGameCreation", "false"));
                ALT_GAME_CREATION_SPEED = Double.parseDouble(altSettings.getProperty("AltGameCreationSpeed", "1"));
                ALT_GAME_CREATION_XP_RATE = Double.parseDouble(altSettings.getProperty("AltGameCreationRateXp", "1"));
                ALT_GAME_CREATION_SP_RATE = Double.parseDouble(altSettings.getProperty("AltGameCreationRateSp", "1"));
                ALT_WEIGHT_LIMIT = Double.parseDouble(altSettings.getProperty("AltWeightLimit", "1"));
                ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(altSettings.getProperty("AltBlacksmithUseRecipes", "true"));
                ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(altSettings.getProperty("AltGameSkillLearn", "false"));
                AUTO_LEARN_SKILLS = Boolean.parseBoolean(altSettings.getProperty("AutoLearnSkills", "false"));
                ALT_GAME_CANCEL_BOW = altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
                ALT_GAME_CANCEL_CAST = altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || altSettings.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
                ALT_GAME_SHIELD_BLOCKS = Boolean.parseBoolean(altSettings.getProperty("AltShieldBlocks", "false"));
                ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(altSettings.getProperty("AltPerfectShieldBlockRate", "10"));
                ALT_GAME_DELEVEL = Boolean.parseBoolean(altSettings.getProperty("Delevel", "true"));
                ALT_GAME_MAGICFAILURES = Boolean.parseBoolean(altSettings.getProperty("MagicFailures", "false"));
                ALT_GAME_MOB_ATTACK_AI = Boolean.parseBoolean(altSettings.getProperty("AltGameMobAttackAI", "false"));
                ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(altSettings.getProperty("AltMobAgroInPeaceZone", "true"));
                ALT_GAME_EXPONENT_XP = Float.parseFloat(altSettings.getProperty("AltGameExponentXp", "0."));
                ALT_GAME_EXPONENT_SP = Float.parseFloat(altSettings.getProperty("AltGameExponentSp", "0."));
                ALLOW_CLASS_MASTERS = Boolean.valueOf(altSettings.getProperty("AllowClassMasters", "False"));
                ALT_GAME_FREIGHTS = Boolean.parseBoolean(altSettings.getProperty("AltGameFreights", "false"));
                ALT_GAME_FREIGHT_PRICE = Integer.parseInt(altSettings.getProperty("AltGameFreightPrice", "1000"));
                ALT_PARTY_RANGE = Integer.parseInt(altSettings.getProperty("AltPartyRange", "1600"));
                ALT_PARTY_RANGE2 = Integer.parseInt(altSettings.getProperty("AltPartyRange2", "1400"));
                REMOVE_CASTLE_CIRCLETS = Boolean.parseBoolean(altSettings.getProperty("RemoveCastleCirclets", "true"));
                IS_CRAFTING_ENABLED = Boolean.parseBoolean(altSettings.getProperty("CraftingEnabled", "true"));
                LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(altSettings.getProperty("LifeCrystalNeeded", "true"));
                SP_BOOK_NEEDED = Boolean.parseBoolean(altSettings.getProperty("SpBookNeeded", "true"));
                ES_SP_BOOK_NEEDED = Boolean.parseBoolean(altSettings.getProperty("EnchantSkillSpBookNeeded", "true"));
                AUTO_LOOT = altSettings.getProperty("AutoLoot").equalsIgnoreCase("True");
                AUTO_LOOT_HERBS = altSettings.getProperty("AutoLootHerbs").equalsIgnoreCase("True");
                ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.valueOf(altSettings.getProperty("AltKarmaPlayerCanBeKilledInPeaceZone", "false"));
                ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.valueOf(altSettings.getProperty("AltKarmaPlayerCanShop", "true"));
                ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.valueOf(altSettings.getProperty("AltKarmaPlayerCanUseGK", "false"));
                ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.valueOf(altSettings.getProperty("AltKarmaPlayerCanTeleport", "true"));
                ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.valueOf(altSettings.getProperty("AltKarmaPlayerCanTrade", "true"));
                ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.valueOf(altSettings.getProperty("AltKarmaPlayerCanUseWareHouse", "true"));
                ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(altSettings.getProperty("AltFreeTeleporting", "False"));
                ALT_RECOMMEND = Boolean.parseBoolean(altSettings.getProperty("AltRecommend", "False"));
                ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.parseBoolean(altSettings.getProperty("AltSubClassWithoutQuests", "False"));
                ALT_GAME_VIEWNPC = Boolean.parseBoolean(altSettings.getProperty("AltGameViewNpc", "False"));
                ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE = Boolean.parseBoolean(altSettings.getProperty("AltNewCharAlwaysIsNewbie", "False"));
                ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.parseBoolean(altSettings.getProperty("AltMembersCanWithdrawFromClanWH", "False"));
                ALT_MAX_NUM_OF_CLANS_IN_ALLY = Integer.parseInt(altSettings.getProperty("AltMaxNumOfClansInAlly", "3"));
                DWARF_RECIPE_LIMIT = Integer.parseInt(altSettings.getProperty("DwarfRecipeLimit", "50"));
                COMMON_RECIPE_LIMIT = Integer.parseInt(altSettings.getProperty("CommonRecipeLimit", "50"));

                ALT_CLAN_MEMBERS_FOR_WAR = Integer.parseInt(altSettings.getProperty("AltClanMembersForWar", "15"));
                ALT_CLAN_JOIN_DAYS = Integer.parseInt(altSettings.getProperty("DaysBeforeJoinAClan", "5"));
                ALT_CLAN_CREATE_DAYS = Integer.parseInt(altSettings.getProperty("DaysBeforeCreateAClan", "10"));
                ALT_CLAN_DISSOLVE_DAYS = Integer.parseInt(altSettings.getProperty("DaysToPassToDissolveAClan", "7"));
                ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = Integer.parseInt(altSettings.getProperty("DaysBeforeJoinAllyWhenLeaved", "1"));
                ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = Integer.parseInt(altSettings.getProperty("DaysBeforeJoinAllyWhenDismissed", "1"));
                ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = Integer.parseInt(altSettings.getProperty("DaysBeforeAcceptNewClanWhenDismissed", "1"));
                ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = Integer.parseInt(altSettings.getProperty("DaysBeforeCreateNewAllyWhenDissolved", "10"));

                ALT_OLY_START_TIME = Integer.parseInt(altSettings.getProperty("AltOlyStartTime", "18"));
                ALT_OLY_MIN = Integer.parseInt(altSettings.getProperty("AltOlyMin", "00"));
                ALT_OLY_CPERIOD = Long.parseLong(altSettings.getProperty("AltOlyCPeriod", "21600000"));
                ALT_OLY_BATTLE = Long.parseLong(altSettings.getProperty("AltOlyBattle", "360000"));
                ALT_OLY_BWAIT = Long.parseLong(altSettings.getProperty("AltOlyBWait", "600000"));
                ALT_OLY_IWAIT = Long.parseLong(altSettings.getProperty("AltOlyIWait", "300000"));
                ALT_OLY_WPERIOD = Long.parseLong(altSettings.getProperty("AltOlyWPeriod", "604800000"));
                ALT_OLY_VPERIOD = Long.parseLong(altSettings.getProperty("AltOlyVPeriod", "86400000"));

                ALT_MANOR_REFRESH_TIME = Integer.parseInt(altSettings.getProperty("AltManorRefreshTime", "20"));
                ALT_MANOR_REFRESH_MIN = Integer.parseInt(altSettings.getProperty("AltManorRefreshMin", "00"));
                ALT_MANOR_APPROVE_TIME = Integer.parseInt(altSettings.getProperty("AltManorApproveTime", "6"));
                ALT_MANOR_APPROVE_MIN = Integer.parseInt(altSettings.getProperty("AltManorApproveMin", "00"));
                ALT_MANOR_MAINTENANCE_PERIOD = Integer.parseInt(altSettings.getProperty("AltManorMaintenancePeriod", "360000"));
                ALT_MANOR_SAVE_ALL_ACTIONS = Boolean.parseBoolean(altSettings.getProperty("AltManorSaveAllActions", "false"));
                ALT_MANOR_SAVE_PERIOD_RATE = Integer.parseInt(altSettings.getProperty("AltManorSavePeriodRate", "2"));

                ALT_LOTTERY_PRIZE = Integer.parseInt(altSettings.getProperty("AltLotteryPrize", "50000"));
                ALT_LOTTERY_TICKET_PRICE = Integer.parseInt(altSettings.getProperty("AltLotteryTicketPrice", "2000"));
                ALT_LOTTERY_5_NUMBER_RATE = Float.parseFloat(altSettings.getProperty("AltLottery5NumberRate", "0.6"));
                ALT_LOTTERY_4_NUMBER_RATE = Float.parseFloat(altSettings.getProperty("AltLottery4NumberRate", "0.2"));
                ALT_LOTTERY_3_NUMBER_RATE = Float.parseFloat(altSettings.getProperty("AltLottery3NumberRate", "0.2"));
                ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = Integer.parseInt(altSettings.getProperty("AltLottery2and1NumberPrize", "200"));
                BUFFS_MAX_AMOUNT = Byte.parseByte(altSettings.getProperty("maxbuffamount", "24"));

                ALT_DEV_NO_QUESTS = Boolean.parseBoolean(altSettings.getProperty("AltDevNoQuests", "False"));
                ALT_DEV_NO_SPAWNS = Boolean.parseBoolean(altSettings.getProperty("AltDevNoSpawns", "False"));

                // Dimensional Rift Config
                RIFT_MIN_PARTY_SIZE = Integer.parseInt(altSettings.getProperty("RiftMinPartySize", "5"));
                RIFT_MAX_JUMPS = Integer.parseInt(altSettings.getProperty("MaxRiftJumps", "4"));
                RIFT_SPAWN_DELAY = Integer.parseInt(altSettings.getProperty("RiftSpawnDelay", "10000"));
                RIFT_AUTO_JUMPS_TIME_MIN = Integer.parseInt(altSettings.getProperty("AutoJumpsDelayMin", "480"));
                RIFT_AUTO_JUMPS_TIME_MAX = Integer.parseInt(altSettings.getProperty("AutoJumpsDelayMax", "600"));
                RIFT_ENTER_COST_RECRUIT = Integer.parseInt(altSettings.getProperty("RecruitCost", "18"));
                RIFT_ENTER_COST_SOLDIER = Integer.parseInt(altSettings.getProperty("SoldierCost", "21"));
                RIFT_ENTER_COST_OFFICER = Integer.parseInt(altSettings.getProperty("OfficerCost", "24"));
                RIFT_ENTER_COST_CAPTAIN = Integer.parseInt(altSettings.getProperty("CaptainCost", "27"));
                RIFT_ENTER_COST_COMMANDER = Integer.parseInt(altSettings.getProperty("CommanderCost", "30"));
                RIFT_ENTER_COST_HERO = Integer.parseInt(altSettings.getProperty("HeroCost", "33"));
                RIFT_BOSS_ROOM_TIME_MUTIPLY = Float.parseFloat(altSettings.getProperty("BossRoomTimeMultiply", "1.5"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + ALT_SETTINGS_FILE + " File.");
            }

            // Seven Signs Config
            try {
                Properties SevenSettings = new Properties();
                InputStream is = new FileInputStream(new File(SEVENSIGNS_FILE));
                SevenSettings.load(is);
                is.close();

                ALT_GAME_REQUIRE_CASTLE_DAWN = Boolean.parseBoolean(SevenSettings.getProperty("AltRequireCastleForDawn", "False"));
                ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.parseBoolean(SevenSettings.getProperty("AltRequireClanCastle", "False"));
                ALT_FESTIVAL_MIN_PLAYER = Integer.parseInt(SevenSettings.getProperty("AltFestivalMinPlayer", "5"));
                ALT_MAXIMUM_PLAYER_CONTRIB = Integer.parseInt(SevenSettings.getProperty("AltMaxPlayerContrib", "1000000"));
                ALT_FESTIVAL_MANAGER_START = Long.parseLong(SevenSettings.getProperty("AltFestivalManagerStart", "120000"));
                ALT_FESTIVAL_LENGTH = Long.parseLong(SevenSettings.getProperty("AltFestivalLength", "1080000"));
                ALT_FESTIVAL_CYCLE_LENGTH = Long.parseLong(SevenSettings.getProperty("AltFestivalCycleLength", "2280000"));
                ALT_FESTIVAL_FIRST_SPAWN = Long.parseLong(SevenSettings.getProperty("AltFestivalFirstSpawn", "120000"));
                ALT_FESTIVAL_FIRST_SWARM = Long.parseLong(SevenSettings.getProperty("AltFestivalFirstSwarm", "300000"));
                ALT_FESTIVAL_SECOND_SPAWN = Long.parseLong(SevenSettings.getProperty("AltFestivalSecondSpawn", "540000"));
                ALT_FESTIVAL_SECOND_SWARM = Long.parseLong(SevenSettings.getProperty("AltFestivalSecondSwarm", "720000"));
                ALT_FESTIVAL_CHEST_SPAWN = Long.parseLong(SevenSettings.getProperty("AltFestivalChestSpawn", "900000"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + SEVENSIGNS_FILE + " File.");
            }

            // clanhall settings
            try {
                Properties clanhallSettings = new Properties();
                InputStream is = new FileInputStream(new File(CLANHALL_CONFIG_FILE));
                clanhallSettings.load(is);
                is.close();
                CH_TELE_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeRation", "86400000"));
                CH_TELE1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl1", "86400000"));
                CH_TELE2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallTeleportFunctionFeeLvl2", "86400000"));
                CH_SUPPORT_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallSupportFunctionFeeRation", "86400000"));
                CH_SUPPORT1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl1", "86400000"));
                CH_SUPPORT2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl2", "86400000"));
                CH_SUPPORT3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl3", "86400000"));
                CH_SUPPORT4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl4", "86400000"));
                CH_SUPPORT5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl5", "86400000"));
                CH_SUPPORT6_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl6", "86400000"));
                CH_SUPPORT7_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl7", "86400000"));
                CH_SUPPORT8_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallSupportFeeLvl8", "86400000"));
                CH_MPREG_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFunctionFeeRation", "86400000"));
                CH_MPREG1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl1", "86400000"));
                CH_MPREG2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl2", "86400000"));
                CH_MPREG3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl3", "86400000"));
                CH_MPREG4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl4", "86400000"));
                CH_MPREG5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallMpRegenerationFeeLvl5", "86400000"));
                CH_HPREG_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFunctionFeeRation", "86400000"));
                CH_HPREG1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl1", "86400000"));
                CH_HPREG2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl2", "86400000"));
                CH_HPREG3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl3", "86400000"));
                CH_HPREG4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl4", "86400000"));
                CH_HPREG5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl5", "86400000"));
                CH_HPREG6_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl6", "86400000"));
                CH_HPREG7_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl7", "86400000"));
                CH_HPREG8_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl8", "86400000"));
                CH_HPREG9_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl9", "86400000"));
                CH_HPREG10_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl10", "86400000"));
                CH_HPREG11_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl11", "86400000"));
                CH_HPREG12_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl12", "86400000"));
                CH_HPREG13_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallHpRegenerationFeeLvl13", "86400000"));
                CH_EXPREG_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFunctionFeeRation", "86400000"));
                CH_EXPREG1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl1", "86400000"));
                CH_EXPREG2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl2", "86400000"));
                CH_EXPREG3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl3", "86400000"));
                CH_EXPREG4_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl4", "86400000"));
                CH_EXPREG5_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl5", "86400000"));
                CH_EXPREG6_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl6", "86400000"));
                CH_EXPREG7_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallExpRegenerationFeeLvl7", "86400000"));
                CH_ITEM_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeRation", "86400000"));
                CH_ITEM1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl1", "86400000"));
                CH_ITEM2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl2", "86400000"));
                CH_ITEM3_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallItemCreationFunctionFeeLvl3", "86400000"));
                CH_CURTAIN_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeRation", "86400000"));
                CH_CURTAIN1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl1", "86400000"));
                CH_CURTAIN2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallCurtainFunctionFeeLvl2", "86400000"));
                CH_FRONT_FEE_RATIO = Long.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeRation", "86400000"));
                CH_FRONT1_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", "86400000"));
                CH_FRONT2_FEE = Integer.valueOf(clanhallSettings.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", "86400000"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + CLANHALL_CONFIG_FILE + " File.");
            }
            try {
                Properties L2JModSettings = new Properties();
                InputStream is = new FileInputStream(new File(L2JMOD_CONFIG_FILE));
                L2JModSettings.load(is);
                is.close();

                L2JMOD_CHAMPION_ENABLE = Boolean.parseBoolean(L2JModSettings.getProperty("ChampionEnable", "false"));
                L2JMOD_CHAMPION_FREQUENCY = Integer.parseInt(L2JModSettings.getProperty("ChampionFrequency", "0"));
                L2JMOD_CHAMP_MIN_LVL = Integer.parseInt(L2JModSettings.getProperty("ChampionMinLevel", "20"));
                L2JMOD_CHAMP_MAX_LVL = Integer.parseInt(L2JModSettings.getProperty("ChampionMaxLevel", "60"));
                L2JMOD_CHAMPION_HP = Integer.parseInt(L2JModSettings.getProperty("ChampionHp", "7"));
                L2JMOD_CHAMPION_HP_REGEN = Float.parseFloat(L2JModSettings.getProperty("ChampionHpRegen", "1."));
                L2JMOD_CHAMPION_REWARDS = Integer.parseInt(L2JModSettings.getProperty("ChampionRewards", "8"));
                L2JMOD_CHAMPION_ADENAS_REWARDS = Integer.parseInt(L2JModSettings.getProperty("ChampionAdenasRewards", "1"));
                L2JMOD_CHAMPION_ATK = Float.parseFloat(L2JModSettings.getProperty("ChampionAtk", "1."));
                L2JMOD_CHAMPION_SPD_ATK = Float.parseFloat(L2JModSettings.getProperty("ChampionSpdAtk", "1."));
                L2JMOD_CHAMPION_REWARD = Integer.parseInt(L2JModSettings.getProperty("ChampionRewardItem", "0"));
                L2JMOD_CHAMPION_REWARD_ID = Integer.parseInt(L2JModSettings.getProperty("ChampionRewardItemID", "6393"));
                L2JMOD_CHAMPION_REWARD_QTY = Integer.parseInt(L2JModSettings.getProperty("ChampionRewardItemQty", "1"));

                TVT_EVENT_ENABLED = Boolean.parseBoolean(L2JModSettings.getProperty("TvTEventEnabled", "false"));
                TVT_EVENT_INTERVAL = Integer.parseInt(L2JModSettings.getProperty("TvTEventInterval", "18000"));
                TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(L2JModSettings.getProperty("TvTEventParticipationTime", "3600"));
                TVT_EVENT_RUNNING_TIME = Integer.parseInt(L2JModSettings.getProperty("TvTEventRunningTime", "1800"));
                TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(L2JModSettings.getProperty("TvTEventParticipationNpcId", "0"));

                /** L2JMOD Wedding system */
                L2JMOD_ALLOW_WEDDING = Boolean.valueOf(L2JModSettings.getProperty("AllowWedding", "False"));
                L2JMOD_WEDDING_PRICE = Integer.parseInt(L2JModSettings.getProperty("WeddingPrice", "250000000"));
                L2JMOD_WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingPunishInfidelity", "True"));
                L2JMOD_WEDDING_TELEPORT = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingTeleport", "True"));
                L2JMOD_WEDDING_TELEPORT_PRICE = Integer.parseInt(L2JModSettings.getProperty("WeddingTeleportPrice", "50000"));
                L2JMOD_WEDDING_TELEPORT_DURATION = Integer.parseInt(L2JModSettings.getProperty("WeddingTeleportDuration", "60"));
                L2JMOD_WEDDING_SAMESEX = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingAllowSameSex", "False"));
                L2JMOD_WEDDING_FORMALWEAR = Boolean.parseBoolean(L2JModSettings.getProperty("WeddingFormalWear", "True"));
                L2JMOD_WEDDING_DIVORCE_COSTS = Integer.parseInt(L2JModSettings.getProperty("WeddingDivorceCosts", "20"));

                if (TVT_EVENT_PARTICIPATION_NPC_ID == 0) {
                    TVT_EVENT_ENABLED = false;
                    _log.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
                } else {
                    String[] propertySplit = L2JModSettings.getProperty("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");

                    if (propertySplit.length < 3) {
                        TVT_EVENT_ENABLED = false;
                        _log.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
                    } else {
                        TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
                        TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
                        TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);

                        TVT_EVENT_MIN_PLAYERS_IN_TEAMS = Integer.parseInt(L2JModSettings.getProperty("TvTEventMinPlayersInTeams", "1"));
                        TVT_EVENT_MAX_PLAYERS_IN_TEAMS = Integer.parseInt(L2JModSettings.getProperty("TvTEventMaxPlayersInTeams", "20"));
                        TVT_EVENT_MIN_LVL = (byte) Integer.parseInt(L2JModSettings.getProperty("TvTEventMinPlayerLevel", "1"));
                        TVT_EVENT_MAX_LVL = (byte) Integer.parseInt(L2JModSettings.getProperty("TvTEventMaxPlayerLevel", "80"));
                        TVT_EVENT_RESPAWN_TELEPORT_DELAY = Integer.parseInt(L2JModSettings.getProperty("TvTEventRespawnTeleportDelay", "20"));
                        TVT_EVENT_START_LEAVE_TELEPORT_DELAY = Integer.parseInt(L2JModSettings.getProperty("TvTEventStartLeaveTeleportDelay", "20"));

                        TVT_EVENT_TEAM_1_NAME = L2JModSettings.getProperty("TvTEventTeam1Name", "Team1");
                        propertySplit = L2JModSettings.getProperty("TvTEventTeam1Coordinates", "0,0,0").split(",");

                        if (propertySplit.length < 3) {
                            TVT_EVENT_ENABLED = false;
                            _log.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
                        } else {
                            TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
                            TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
                            TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(propertySplit[2]);

                            TVT_EVENT_TEAM_2_NAME = L2JModSettings.getProperty("TvTEventTeam2Name", "Team2");
                            propertySplit = L2JModSettings.getProperty("TvTEventTeam2Coordinates", "0,0,0").split(",");

                            if (propertySplit.length < 3) {
                                TVT_EVENT_ENABLED = false;
                                _log.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
                            } else {
                                TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
                                TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
                                TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
                                propertySplit = L2JModSettings.getProperty("TvTEventReward", "57,100000").split(";");

                                for (String reward : propertySplit) {
                                    String[] rewardSplit = reward.split(",");

                                    if (rewardSplit.length != 2) {
                                        _log.warn("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"" + reward + "\"");
                                    } else {
                                        try {
                                            TVT_EVENT_REWARDS.add(new int[]
                                                    {
                                                            Integer.valueOf(rewardSplit[0]),
                                                            Integer.valueOf(rewardSplit[1])
                                                    });
                                        } catch (NumberFormatException nfe) {
                                            if (!reward.equals("")) {
                                                _log.error("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"" + reward + "\"");
                                            }
                                        }
                                    }
                                }

                                TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = Boolean.parseBoolean(L2JModSettings.getProperty("TvTEventTargetTeamMembersAllowed", "true"));
                                TVT_EVENT_POTIONS_ALLOWED = Boolean.parseBoolean(L2JModSettings.getProperty("TvTEventPotionsAllowed", "false"));
                                TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = Boolean.parseBoolean(L2JModSettings.getProperty("TvTEventSummonByItemAllowed", "false"));
                                propertySplit = L2JModSettings.getProperty("TvTEventDoorsCloseOpenOnStartEnd", "").split(";");

                                for (String door : propertySplit) {
                                    try {
                                        TVT_EVENT_DOOR_IDS.add(Integer.valueOf(door));
                                    } catch (NumberFormatException nfe) {
                                        if (!door.equals("")) {
                                            _log.error("TvTEventEngine[Config.load()]: invalid config property -> TvTEventDoorsCloseOpenOnStartEnd \"" + door + "\"");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + L2JMOD_CONFIG_FILE + " File.");
            }
            // pvp config
            try {
                Properties pvpSettings = new Properties();
                InputStream is = new FileInputStream(new File(PVP_CONFIG_FILE));
                pvpSettings.load(is);
                is.close();

                /* KARMA SYSTEM */
                KARMA_MIN_KARMA = Integer.parseInt(pvpSettings.getProperty("MinKarma", "240"));
                KARMA_MAX_KARMA = Integer.parseInt(pvpSettings.getProperty("MaxKarma", "10000"));
                KARMA_XP_DIVIDER = Integer.parseInt(pvpSettings.getProperty("XPDivider", "260"));
                KARMA_LOST_BASE = Integer.parseInt(pvpSettings.getProperty("BaseKarmaLost", "0"));

                KARMA_DROP_GM = Boolean.parseBoolean(pvpSettings.getProperty("CanGMDropEquipment", "false"));
                KARMA_AWARD_PK_KILL = Boolean.parseBoolean(pvpSettings.getProperty("AwardPKKillPVPPoint", "true"));

                KARMA_PK_LIMIT = Integer.parseInt(pvpSettings.getProperty("MinimumPKRequiredToDrop", "5"));

                KARMA_NONDROPPABLE_PET_ITEMS = pvpSettings.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650");
                KARMA_NONDROPPABLE_ITEMS = pvpSettings.getProperty("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621");

                KARMA_LIST_NONDROPPABLE_PET_ITEMS = new LinkedList<>();
                for (String id : KARMA_NONDROPPABLE_PET_ITEMS.split(",")) {
                    KARMA_LIST_NONDROPPABLE_PET_ITEMS.add(Integer.parseInt(id));
                }

                KARMA_LIST_NONDROPPABLE_ITEMS = new LinkedList<>();
                for (String id : KARMA_NONDROPPABLE_ITEMS.split(",")) {
                    KARMA_LIST_NONDROPPABLE_ITEMS.add(Integer.parseInt(id));
                }

                PVP_NORMAL_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsNormalTime", "15000"));
                PVP_PVP_TIME = Integer.parseInt(pvpSettings.getProperty("PvPVsPvPTime", "30000"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + PVP_CONFIG_FILE + " File.");
            }

            // access levels
            try {
                Properties gmSettings = new Properties();
                InputStream is = new FileInputStream(new File(GM_ACCESS_FILE));
                gmSettings.load(is);
                is.close();

                GM_ACCESSLEVEL = Integer.parseInt(gmSettings.getProperty("GMAccessLevel", "100"));
                GM_MIN = Integer.parseInt(gmSettings.getProperty("GMMinLevel", "100"));
                GM_ALTG_MIN_LEVEL = Integer.parseInt(gmSettings.getProperty("GMCanAltG", "100"));
                GM_ANNOUNCE = Integer.parseInt(gmSettings.getProperty("GMCanAnnounce", "100"));
                GM_BAN = Integer.parseInt(gmSettings.getProperty("GMCanBan", "100"));
                GM_BAN_CHAT = Integer.parseInt(gmSettings.getProperty("GMCanBanChat", "100"));
                GM_CREATE_ITEM = Integer.parseInt(gmSettings.getProperty("GMCanShop", "100"));
                GM_DELETE = Integer.parseInt(gmSettings.getProperty("GMCanDelete", "100"));
                GM_KICK = Integer.parseInt(gmSettings.getProperty("GMCanKick", "100"));
                GM_MENU = Integer.parseInt(gmSettings.getProperty("GMMenu", "100"));
                GM_GODMODE = Integer.parseInt(gmSettings.getProperty("GMGodMode", "100"));
                GM_CHAR_EDIT = Integer.parseInt(gmSettings.getProperty("GMCanEditChar", "100"));
                GM_CHAR_EDIT_OTHER = Integer.parseInt(gmSettings.getProperty("GMCanEditCharOther", "100"));
                GM_CHAR_VIEW = Integer.parseInt(gmSettings.getProperty("GMCanViewChar", "100"));
                GM_NPC_EDIT = Integer.parseInt(gmSettings.getProperty("GMCanEditNPC", "100"));
                GM_NPC_VIEW = Integer.parseInt(gmSettings.getProperty("GMCanViewNPC", "100"));
                GM_TELEPORT = Integer.parseInt(gmSettings.getProperty("GMCanTeleport", "100"));
                GM_TELEPORT_OTHER = Integer.parseInt(gmSettings.getProperty("GMCanTeleportOther", "100"));
                GM_RESTART = Integer.parseInt(gmSettings.getProperty("GMCanRestart", "100"));
                GM_MONSTERRACE = Integer.parseInt(gmSettings.getProperty("GMMonsterRace", "100"));
                GM_RIDER = Integer.parseInt(gmSettings.getProperty("GMRider", "100"));
                GM_ESCAPE = Integer.parseInt(gmSettings.getProperty("GMFastUnstuck", "100"));
                GM_FIXED = Integer.parseInt(gmSettings.getProperty("GMResurectFixed", "100"));
                GM_CREATE_NODES = Integer.parseInt(gmSettings.getProperty("GMCreateNodes", "100"));
                GM_ENCHANT = Integer.parseInt(gmSettings.getProperty("GMEnchant", "100"));
                GM_DOOR = Integer.parseInt(gmSettings.getProperty("GMDoor", "100"));
                GM_RES = Integer.parseInt(gmSettings.getProperty("GMRes", "100"));
                GM_PEACEATTACK = Integer.parseInt(gmSettings.getProperty("GMPeaceAttack", "100"));
                GM_HEAL = Integer.parseInt(gmSettings.getProperty("GMHeal", "100"));
                GM_UNBLOCK = Integer.parseInt(gmSettings.getProperty("GMUnblock", "100"));
                GM_CACHE = Integer.parseInt(gmSettings.getProperty("GMCache", "100"));
                GM_TALK_BLOCK = Integer.parseInt(gmSettings.getProperty("GMTalkBlock", "100"));
                GM_TEST = Integer.parseInt(gmSettings.getProperty("GMTest", "100"));

                String gmTrans = gmSettings.getProperty("GMDisableTransaction", "False");

                if (!gmTrans.equalsIgnoreCase("false")) {
                    String[] params = gmTrans.split(",");
                    GM_DISABLE_TRANSACTION = true;
                    GM_TRANSACTION_MIN = Integer.parseInt(params[0]);
                    GM_TRANSACTION_MAX = Integer.parseInt(params[1]);
                } else {
                    GM_DISABLE_TRANSACTION = false;
                }
                GM_CAN_GIVE_DAMAGE = Integer.parseInt(gmSettings.getProperty("GMCanGiveDamage", "90"));
                GM_DONT_TAKE_AGGRO = Integer.parseInt(gmSettings.getProperty("GMDontTakeAggro", "90"));
                GM_DONT_TAKE_EXPSP = Integer.parseInt(gmSettings.getProperty("GMDontGiveExpSp", "90"));

            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + GM_ACCESS_FILE + " File.");
            }

            try {
                Properties Settings = new Properties();
                InputStream is = new FileInputStream(HEXID_FILE);
                Settings.load(is);
                is.close();
                SERVER_ID = Integer.parseInt(Settings.getProperty("ServerID"));
                HEX_ID = new BigInteger(Settings.getProperty("HexID"), 16).toByteArray();
            } catch (Exception e) {
                _log.warn("Could not load HexID file (" + HEXID_FILE + "). Hopefully login will give us one.");
            }
        } else if (Server.serverMode == Server.MODE_LOGINSERVER) {
            _log.info("loading login config");
            try {
                Properties serverSettings = new Properties();
                InputStream is = new FileInputStream(new File(LOGIN_CONFIGURATION_FILE));
                serverSettings.load(is);
                is.close();

                DEBUG = Boolean.parseBoolean(serverSettings.getProperty("Debug", "false"));
                DEVELOPER = Boolean.parseBoolean(serverSettings.getProperty("Developer", "false"));
                ASSERT = Boolean.parseBoolean(serverSettings.getProperty("Assert", "false"));

                REQUEST_ID = Integer.parseInt(serverSettings.getProperty("RequestServerID", "0"));
                ACCEPT_ALTERNATE_ID = Boolean.parseBoolean(serverSettings.getProperty("AcceptAlternateID", "True"));

                GM_MIN = Integer.parseInt(serverSettings.getProperty("GMMinLevel", "100"));

                DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile(); // FIXME: in login?

                INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "localhost");
                EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "localhost");

                DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
                DATABASE_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jdb");
                DATABASE_LOGIN = serverSettings.getProperty("Login", "root");
                DATABASE_PASSWORD = serverSettings.getProperty("Password", "");
                DATABASE_MAX_CONNECTIONS = Integer.parseInt(serverSettings.getProperty("MaximumDbConnections", "10"));
                DATABASE_MAX_IDLE_TIME = Integer.parseInt(serverSettings.getProperty("MaximumDbIdleTime", "0"));

                IP_UPDATE_TIME = Integer.parseInt(serverSettings.getProperty("IpUpdateTime", "15"));
                FORCE_GGAUTH = Boolean.parseBoolean(serverSettings.getProperty("ForceGGAuth", "false"));

                FLOOD_PROTECTION = Boolean.parseBoolean(serverSettings.getProperty("EnableFloodProtection", "True"));
                FAST_CONNECTION_LIMIT = Integer.parseInt(serverSettings.getProperty("FastConnectionLimit", "15"));
                NORMAL_CONNECTION_TIME = Integer.parseInt(serverSettings.getProperty("NormalConnectionTime", "700"));
                FAST_CONNECTION_TIME = Integer.parseInt(serverSettings.getProperty("FastConnectionTime", "350"));
                MAX_CONNECTION_PER_IP = Integer.parseInt(serverSettings.getProperty("MaxConnectionPerIP", "50"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("Failed to Load " + LOGIN_CONFIGURATION_FILE + " File.");
            }

        } else {
            _log.error("Could not Load Config: server mode was not set");
        }

    }

    /**
     * Set a new value to a game parameter from the admin console.
     *
     * @param pName  (String) : name of the parameter to change
     * @param pValue (String) : new value of the parameter
     * @return boolean : true if modification has been made
     */
    public static boolean setParameterValue(String pName, String pValue) {
        // Server settings
        if (pName.equalsIgnoreCase("RateXp")) {
            RATE_XP = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateSp")) {
            RATE_SP = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RatePartyXp")) {
            RATE_PARTY_XP = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RatePartySp")) {
            RATE_PARTY_SP = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateQuestsReward")) {
            RATE_QUESTS_REWARD = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateDropAdena")) {
            RATE_DROP_ADENA = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateConsumableCost")) {
            RATE_CONSUMABLE_COST = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateDropItems")) {
            RATE_DROP_ITEMS = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateDropSpoil")) {
            RATE_DROP_SPOIL = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateDropManor")) {
            RATE_DROP_MANOR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("RateDropQuest")) {
            RATE_DROP_QUEST = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateKarmaExpLost")) {
            RATE_KARMA_EXP_LOST = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("RateSiegeGuardsPrice")) {
            RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("PlayerDropLimit")) {
            PLAYER_DROP_LIMIT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PlayerRateDrop")) {
            PLAYER_RATE_DROP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PlayerRateDropItem")) {
            PLAYER_RATE_DROP_ITEM = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PlayerRateDropEquip")) {
            PLAYER_RATE_DROP_EQUIP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PlayerRateDropEquipWeapon")) {
            PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KarmaDropLimit")) {
            KARMA_DROP_LIMIT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KarmaRateDrop")) {
            KARMA_RATE_DROP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KarmaRateDropItem")) {
            KARMA_RATE_DROP_ITEM = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KarmaRateDropEquip")) {
            KARMA_RATE_DROP_EQUIP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KarmaRateDropEquipWeapon")) {
            KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AutoDestroyDroppedItemAfter")) {
            AUTODESTROY_ITEM_AFTER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DestroyPlayerDroppedItem")) {
            DESTROY_DROPPED_PLAYER_ITEM = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("DestroyEquipableItem")) {
            DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("SaveDroppedItem")) {
            SAVE_DROPPED_ITEM = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("EmptyDroppedItemTableAfterLoad")) {
            EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("SaveDroppedItemInterval")) {
            SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClearDroppedItemTable")) {
            CLEAR_DROPPED_ITEM_TABLE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("PreciseDropCalculation")) {
            PRECISE_DROP_CALCULATION = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MultipleItemDrop")) {
            MULTIPLE_ITEM_DROP = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("CoordSynchronize")) {
            COORD_SYNCHRONIZE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DeleteCharAfterDays")) {
            DELETE_DAYS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AllowDiscardItem")) {
            ALLOW_DISCARDITEM = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowFreight")) {
            ALLOW_FREIGHT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowWarehouse")) {
            ALLOW_WAREHOUSE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowWear")) {
            ALLOW_WEAR = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("WearDelay")) {
            WEAR_DELAY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WearPrice")) {
            WEAR_PRICE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AllowWater")) {
            ALLOW_WATER = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowRentPet")) {
            ALLOW_RENTPET = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowBoat")) {
            ALLOW_BOAT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowCursedWeapons")) {
            ALLOW_CURSED_WEAPONS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowManor")) {
            ALLOW_MANOR = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("BypassValidation")) {
            BYPASS_VALIDATION = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("CommunityType")) {
            COMMUNITY_TYPE = pValue.toLowerCase();
        } else if (pName.equalsIgnoreCase("BBSDefault")) {
            BBS_DEFAULT = pValue;
        } else if (pName.equalsIgnoreCase("ShowLevelOnCommunityBoard")) {
            SHOW_LEVEL_COMMUNITYBOARD = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ShowStatusOnCommunityBoard")) {
            SHOW_STATUS_COMMUNITYBOARD = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("NamePageSizeOnCommunityBoard")) {
            NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("NamePerRowOnCommunityBoard")) {
            NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ShowServerNews")) {
            SERVER_NEWS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ShowNpcLevel")) {
            SHOW_NPC_LVL = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ForceInventoryUpdate")) {
            FORCE_INVENTORY_UPDATE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AutoDeleteInvalidQuestData")) {
            AUTODELETE_INVALID_QUEST_DATA = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaximumOnlineUsers")) {
            MAXIMUM_ONLINE_USERS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ZoneTown")) {
            ZONE_TOWN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumUpdateDistance")) {
            MINIMUM_UPDATE_DISTANCE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MinimumUpdateTime")) {
            MINIMUN_UPDATE_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("CheckKnownList")) {
            CHECK_KNOWN = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("KnownListForgetDelay")) {
            KNOWNLIST_FORGET_DELAY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("UseDeepBlueDropRules")) {
            DEEPBLUE_DROP_RULES = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AllowGuards")) {
            ALLOW_GUARDS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("CancelLesserEffect")) {
            EFFECT_CANCELING = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("WyvernSpeed")) {
            WYVERN_SPEED = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StriderSpeed")) {
            STRIDER_SPEED = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumSlotsForNoDwarf")) {
            INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumSlotsForDwarf")) {
            INVENTORY_MAXIMUM_DWARF = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumSlotsForGMPlayer")) {
            INVENTORY_MAXIMUM_GM = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForNoDwarf")) {
            WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForDwarf")) {
            WAREHOUSE_SLOTS_DWARF = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumWarehouseSlotsForClan")) {
            WAREHOUSE_SLOTS_CLAN = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaximumFreightSlots")) {
            FREIGHT_SLOTS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceWeapon")) {
            ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceArmor")) {
            ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceJewelry")) {
            ENCHANT_CHANCE_JEWELRY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantMaxWeapon")) {
            ENCHANT_MAX_WEAPON = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantMaxArmor")) {
            ENCHANT_MAX_ARMOR = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantMaxJewelry")) {
            ENCHANT_MAX_JEWELRY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantSafeMax")) {
            ENCHANT_SAFE_MAX = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantSafeMaxFull")) {
            ENCHANT_SAFE_MAX_FULL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("HpRegenMultiplier")) {
            HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("MpRegenMultiplier")) {
            MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("CpRegenMultiplier")) {
            CP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RaidHpRegenMultiplier")) {
            RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RaidMpRegenMultiplier")) {
            RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RaidDefenceMultiplier")) {
            RAID_DEFENCE_MULTIPLIER = Double.parseDouble(pValue) / 100;
        } else if (pName.equalsIgnoreCase("RaidMinionRespawnTime")) {
            RAID_MINION_RESPAWN_TIMER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StartingAdena")) {
            STARTING_ADENA = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("UnstuckInterval")) {
            UNSTUCK_INTERVAL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PlayerSpawnProtection")) {
            PLAYER_SPAWN_PROTECTION = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PlayerFakeDeathUpProtection")) {
            PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PartyXpCutoffMethod")) {
            PARTY_XP_CUTOFF_METHOD = pValue;
        } else if (pName.equalsIgnoreCase("PartyXpCutoffPercent")) {
            PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("PartyXpCutoffLevel")) {
            PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("RespawnRestoreCP")) {
            RESPAWN_RESTORE_CP = Double.parseDouble(pValue) / 100;
        } else if (pName.equalsIgnoreCase("RespawnRestoreHP")) {
            RESPAWN_RESTORE_HP = Double.parseDouble(pValue) / 100;
        } else if (pName.equalsIgnoreCase("RespawnRestoreMP")) {
            RESPAWN_RESTORE_MP = Double.parseDouble(pValue) / 100;
        } else if (pName.equalsIgnoreCase("MaxPvtStoreSlotsDwarf")) {
            MAX_PVTSTORE_SLOTS_DWARF = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaxPvtStoreSlotsOther")) {
            MAX_PVTSTORE_SLOTS_OTHER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("StoreSkillCooltime")) {
            STORE_SKILL_COOLTIME = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AnnounceMammonSpawn")) {
            ANNOUNCE_MAMMON_SPAWN = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameTiredness")) {
            ALT_GAME_TIREDNESS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameCreation")) {
            ALT_GAME_CREATION = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameCreationSpeed")) {
            ALT_GAME_CREATION_SPEED = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("AltGameCreationXpRate")) {
            ALT_GAME_CREATION_XP_RATE = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("AltGameCreationSpRate")) {
            ALT_GAME_CREATION_SP_RATE = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("AltWeightLimit")) {
            ALT_WEIGHT_LIMIT = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("AltBlacksmithUseRecipes")) {
            ALT_BLACKSMITH_USE_RECIPES = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameSkillLearn")) {
            ALT_GAME_SKILL_LEARN = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("RemoveCastleCirclets")) {
            REMOVE_CASTLE_CIRCLETS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameCancelByHit")) {
            ALT_GAME_CANCEL_BOW = pValue.equalsIgnoreCase("bow") || pValue.equalsIgnoreCase("all");
            ALT_GAME_CANCEL_CAST = pValue.equalsIgnoreCase("cast") || pValue.equalsIgnoreCase("all");
        } else if (pName.equalsIgnoreCase("AltShieldBlocks")) {
            ALT_GAME_SHIELD_BLOCKS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltPerfectShieldBlockRate")) {
            ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Delevel")) {
            ALT_GAME_DELEVEL = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MagicFailures")) {
            ALT_GAME_MAGICFAILURES = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameMobAttackAI")) {
            ALT_GAME_MOB_ATTACK_AI = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltMobAgroInPeaceZone")) {
            ALT_MOB_AGRO_IN_PEACEZONE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameExponentXp")) {
            ALT_GAME_EXPONENT_XP = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("AltGameExponentSp")) {
            ALT_GAME_EXPONENT_SP = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("AllowClassMasters")) {
            ALLOW_CLASS_MASTERS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameFreights")) {
            ALT_GAME_FREIGHTS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltGameFreightPrice")) {
            ALT_GAME_FREIGHT_PRICE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AltPartyRange")) {
            ALT_PARTY_RANGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AltPartyRange2")) {
            ALT_PARTY_RANGE2 = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("CraftingEnabled")) {
            IS_CRAFTING_ENABLED = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("LifeCrystalNeeded")) {
            LIFE_CRYSTAL_NEEDED = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("SpBookNeeded")) {
            SP_BOOK_NEEDED = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AutoLoot")) {
            AUTO_LOOT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AutoLootHerbs")) {
            AUTO_LOOT_HERBS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltKarmaPlayerCanBeKilledInPeaceZone")) {
            ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltKarmaPlayerCanShop")) {
            ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltKarmaPlayerCanUseGK")) {
            ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTeleport")) {
            ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltKarmaPlayerCanTrade")) {
            ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltKarmaPlayerCanUseWareHouse")) {
            ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltRequireCastleForDawn")) {
            ALT_GAME_REQUIRE_CASTLE_DAWN = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltRequireClanCastle")) {
            ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltFreeTeleporting")) {
            ALT_GAME_FREE_TELEPORT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltSubClassWithoutQuests")) {
            ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltNewCharAlwaysIsNewbie")) {
            ALT_GAME_NEW_CHAR_ALWAYS_IS_NEWBIE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AltMembersCanWithdrawFromClanWH")) {
            ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("DwarfRecipeLimit")) {
            DWARF_RECIPE_LIMIT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("CommonRecipeLimit")) {
            COMMON_RECIPE_LIMIT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionEnable")) {
            L2JMOD_CHAMPION_ENABLE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ChampionFrequency")) {
            L2JMOD_CHAMPION_FREQUENCY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionMinLevel")) {
            L2JMOD_CHAMP_MIN_LVL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionMaxLevel")) {
            L2JMOD_CHAMP_MAX_LVL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionHp")) {
            L2JMOD_CHAMPION_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionHpRegen")) {
            L2JMOD_CHAMPION_HP_REGEN = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("ChampionRewards")) {
            L2JMOD_CHAMPION_REWARDS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionAdenasRewards")) {
            L2JMOD_CHAMPION_ADENAS_REWARDS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionAtk")) {
            L2JMOD_CHAMPION_ATK = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("ChampionSpdAtk")) {
            L2JMOD_CHAMPION_SPD_ATK = Float.parseFloat(pValue);
        } else if (pName.equalsIgnoreCase("ChampionRewardItem")) {
            L2JMOD_CHAMPION_REWARD = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionRewardItemID")) {
            L2JMOD_CHAMPION_REWARD_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ChampionRewardItemQty")) {
            L2JMOD_CHAMPION_REWARD_QTY = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AllowWedding")) {
            L2JMOD_ALLOW_WEDDING = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("WeddingPrice")) {
            L2JMOD_WEDDING_PRICE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WeddingPunishInfidelity")) {
            L2JMOD_WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("WeddingTeleport")) {
            L2JMOD_WEDDING_TELEPORT = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("WeddingTeleportPrice")) {
            L2JMOD_WEDDING_TELEPORT_PRICE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WeddingTeleportDuration")) {
            L2JMOD_WEDDING_TELEPORT_DURATION = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WeddingAllowSameSex")) {
            L2JMOD_WEDDING_SAMESEX = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("WeddingFormalWear")) {
            L2JMOD_WEDDING_FORMALWEAR = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("WeddingDivorceCosts")) {
            L2JMOD_WEDDING_DIVORCE_COSTS = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TvTEventEnabled")) {
            TVT_EVENT_ENABLED = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("TvTEventInterval")) {
            TVT_EVENT_INTERVAL = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TvTEventParticipationTime")) {
            TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TvTEventRunningTime")) {
            TVT_EVENT_RUNNING_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TvTEventParticipationNpcId")) {
            TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MinKarma")) {
            KARMA_MIN_KARMA = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaxKarma")) {
            KARMA_MAX_KARMA = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("XPDivider")) {
            KARMA_XP_DIVIDER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BaseKarmaLost")) {
            KARMA_LOST_BASE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("CanGMDropEquipment")) {
            KARMA_DROP_GM = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AwardPKKillPVPPoint")) {
            KARMA_AWARD_PK_KILL = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MinimumPKRequiredToDrop")) {
            KARMA_PK_LIMIT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PvPVsNormalTime")) {
            PVP_NORMAL_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PvPVsPvPTime")) {
            PVP_PVP_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("GlobalChat")) {
            DEFAULT_GLOBAL_CHAT = pValue;
        } else if (pName.equalsIgnoreCase("TradeChat")) {
            DEFAULT_TRADE_CHAT = pValue;
        } else if (pName.equalsIgnoreCase("MenuStyle")) {
            GM_ADMIN_MENU_STYLE = pValue;
        } else {
            return false;
        }
        return true;
    }

    // it has no instances
    private Config() {
    }

    /**
     * Save hexadecimal ID of the server in the properties file.
     *
     * @param serverId
     * @param string   (String) : hexadecimal ID of the server to store
     * @see #HEXID_FILE
     * @see #saveHexid(int, String, String)
     */
    public static void saveHexid(int serverId, String string) {
        Config.saveHexid(serverId, string, HEXID_FILE);
    }

    /**
     * Save hexadecimal ID of the server in the properties file.
     *
     * @param serverId
     * @param hexId    (String) : hexadecimal ID of the server to store
     * @param fileName (String) : name of the properties file
     */
    public static void saveHexid(int serverId, String hexId, String fileName) {
        try {
            Properties hexSetting = new Properties();
            File file = new File(fileName);
            // Create a new empty file only if it doesn't exist
            file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            hexSetting.setProperty("ServerID", String.valueOf(serverId));
            hexSetting.setProperty("HexID", hexId);
            hexSetting.store(out, "the hexID to auth into login");
            out.close();
        } catch (Exception e) {
            _log.warn("Failed to save hex id to " + fileName + " File.");
            e.printStackTrace();
        }
    }

}
