package handler.bbs.custom;

import org.l2j.commons.collections.CollectionUtils;
import org.l2j.commons.configuration.ExProperties;
import org.l2j.commons.string.StringArrayUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.listener.script.OnLoadScriptListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bonux
**/
public class BBSConfig implements OnLoadScriptListener {

	private static final String PROPERTIES_FILE = "config/bbs.properties";

	public static boolean GLOBAL_USE_FUNCTIONS_CONFIGS;
	public static boolean CAN_USE_FUNCTIONS_WHEN_DEAD;
	public static boolean CAN_USE_FUNCTIONS_IN_A_BATTLE;
	public static boolean CAN_USE_FUNCTIONS_IN_PVP;
	public static boolean CAN_USE_FUNCTIONS_IN_INVISIBLE;
	public static boolean CAN_USE_FUNCTIONS_ON_OLLYMPIAD;
	public static boolean CAN_USE_FUNCTIONS_IF_FLIGHT;
	public static boolean CAN_USE_FUNCTIONS_IF_IN_VEHICLE;
	public static boolean CAN_USE_FUNCTIONS_IF_MOUNTED;
	public static boolean CAN_USE_FUNCTIONS_IF_CANNOT_MOVE;
	public static boolean CAN_USE_FUNCTIONS_WHEN_IN_TRADE;
	public static boolean CAN_USE_FUNCTIONS_WHEN_FISHING;
	public static boolean CAN_USE_FUNCTIONS_IF_TELEPORTING;
	public static boolean CAN_USE_FUNCTIONS_IN_DUEL;
	public static boolean CAN_USE_FUNCTIONS_WHEN_IS_PK;
	public static boolean CAN_USE_FUNCTIONS_CLAN_LEADERS_ONLY;
	public static boolean CAN_USE_FUNCTIONS_ON_SIEGE;
	public static boolean CAN_USE_FUNCTIONS_IN_PEACE_ZONE_ONLY;
	public static boolean CAN_USE_FUNCTIONS_IN_EVENTS;

	public static boolean BUFF_SERVICE_ALLOW_RESTORE;
	public static boolean BUFF_SERVICE_ALLOW_CANCEL_BUFFS;
	public static int BUFF_SERVICE_COST_ITEM_ID;
	public static long BUFF_SERVICE_COST_ITEM_COUNT;
	public static int BUFF_SERVICE_MAX_BUFFS_IN_SET;
	public static int BUFF_SERVICE_MAX_BUFF_SETS_PER_CHAR;
	public static int[][] BUFF_SERVICE_AVAILABLE_SKILLS_FOR_BUFF;
	public static int BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF;
	public static int BUFF_SERVICE_ASSIGN_BUFF_TIME;
	public static int BUFF_SERVICE_ASSIGN_BUFF_TIME_MUSIC;
	public static int BUFF_SERVICE_ASSIGN_BUFF_TIME_SPECIAL;
	public static double BUFF_SERVICE_BUFF_TIME_MODIFIER;
	public static double BUFF_SERVICE_BUFF_TIME_MODIFIER_MUSIC;
	public static double BUFF_SERVICE_BUFF_TIME_MODIFIER_SPECIAL;

	public static int TELEPORT_SERVICE_COST_ITEM_ID;
	public static long TELEPORT_SERVICE_COST_ITEM_COUNT;
	public static int TELEPORT_SERVICE_BM_SAVE_COST_ITEM_ID;
	public static long TELEPORT_SERVICE_BM_SAVE_COST_ITEM_COUNT;
	public static int TELEPORT_SERVICE_BM_SAVE_LIMIT;
	public static int TELEPORT_SERVICE_BM_COST_ITEM_ID;
	public static long TELEPORT_SERVICE_BM_COST_ITEM_COUNT;
	public static boolean TELEPORT_SERVICE_TELEPORT_IF_PK;

	public static int OCCUPATION_SERVICE_COST_ITEM_ID_1;
	public static long OCCUPATION_SERVICE_COST_ITEM_COUNT_1;
	public static int OCCUPATION_SERVICE_COST_ITEM_ID_2;
	public static long OCCUPATION_SERVICE_COST_ITEM_COUNT_2;
	public static int OCCUPATION_SERVICE_COST_ITEM_ID_3;
	public static long OCCUPATION_SERVICE_COST_ITEM_COUNT_3;

	public static int CHANGE_PLAYER_NAME_SERVICE_COST_ITEM_ID;
	public static long CHANGE_PLAYER_NAME_SERVICE_COST_ITEM_COUNT;

	public static int CHANGE_PET_NAME_SERVICE_COST_ITEM_ID;
	public static long CHANGE_PET_NAME_SERVICE_COST_ITEM_COUNT;

	public static int CHANGE_CLAN_NAME_SERVICE_COST_ITEM_ID;
	public static long CHANGE_CLAN_NAME_SERVICE_COST_ITEM_COUNT;

	public static int COLOR_NAME_SERVICE_COST_ITEM_ID;
	public static long COLOR_NAME_SERVICE_COST_ITEM_COUNT;
	public static String[] COLOR_NAME_SERVICE_COLORS;

	public static int COLOR_TITLE_SERVICE_COST_ITEM_ID;
	public static long COLOR_TITLE_SERVICE_COST_ITEM_COUNT;
	public static String[] COLOR_TITLE_SERVICE_COLORS;

	public static int ADD_WINDOW_SERVICE_TYPE;
	public static int ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY;
	public static long ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_1_DAY;
	public static int[] ADD_WINDOW_SERVICE_PERIOD_VARIATIONS;
	public static int ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_FOREVER;
	public static long ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_FOREVER;

	public static int CHANGE_SEX_SERVICE_COST_ITEM_ID;
	public static long CHANGE_SEX_SERVICE_COST_ITEM_COUNT;

	public static int EXPAND_INVENTORY_SERVICE_COST_ITEM_ID;
	public static long EXPAND_INVENTORY_SERVICE_COST_ITEM_COUNT;

	public static int EXPAND_WAREHOUSE_SERVICE_COST_ITEM_ID;
	public static long EXPAND_WAREHOUSE_SERVICE_COST_ITEM_COUNT;

	public static int EXPAND_CLANWAREHOUSE_SERVICE_COST_ITEM_ID;
	public static long EXPAND_CLANWAREHOUSE_SERVICE_COST_ITEM_COUNT;

	public static long[][] CLAN_REPUTATION_SERVICE_PRICES_LIST;

	public static int KARMA_PK_SERVICE_COST_ITEM_ID;
	public static long KARMA_PK_SERVICE_COST_ITEM_COUNT;

	public static int STATISTIC_REFRESH_TIME;
	public static int STATISTIC_TOP_PK_COUNT;
	public static int STATISTIC_TOP_PVP_COUNT;
	public static int STATISTIC_TOP_LVL_COUNT;
	public static int STATISTIC_TOP_ADENA_COUNT;
	public static int STATISTIC_TOP_ONLINE_COUNT;
	public static int STATISTIC_TOP_ITEM_COUNT;
	public static int STATISTIC_TOP_OLYMPIAD_COUNT;
	public static int STATISTIC_BY_ITEM_ID;

	@Override
	public void onLoad()
	{
		ExProperties properties = Config.load(PROPERTIES_FILE);

		// Global
		GLOBAL_USE_FUNCTIONS_CONFIGS = properties.getProperty("GLOBAL_USE_FUNCTIONS_CONFIGS", false);
		CAN_USE_FUNCTIONS_WHEN_DEAD = properties.getProperty("CAN_USE_FUNCTIONS_WHEN_DEAD", true);
		CAN_USE_FUNCTIONS_IN_A_BATTLE = properties.getProperty("CAN_USE_FUNCTIONS_IN_A_BATTLE", true);
		CAN_USE_FUNCTIONS_IN_PVP = properties.getProperty("CAN_USE_FUNCTIONS_IN_PVP", true);
		CAN_USE_FUNCTIONS_IN_INVISIBLE = properties.getProperty("CAN_USE_FUNCTIONS_IN_INVISIBLE", true);
		CAN_USE_FUNCTIONS_ON_OLLYMPIAD = properties.getProperty("CAN_USE_FUNCTIONS_ON_OLLYMPIAD", true);
		CAN_USE_FUNCTIONS_IF_FLIGHT = properties.getProperty("CAN_USE_FUNCTIONS_IF_FLIGHT", true);
		CAN_USE_FUNCTIONS_IF_IN_VEHICLE = properties.getProperty("CAN_USE_FUNCTIONS_IF_IN_VEHICLE", true);
		CAN_USE_FUNCTIONS_IF_MOUNTED = properties.getProperty("CAN_USE_FUNCTIONS_IF_MOUNTED", true);
		CAN_USE_FUNCTIONS_IF_CANNOT_MOVE = properties.getProperty("CAN_USE_FUNCTIONS_IF_CANNOT_MOVE", true);
		CAN_USE_FUNCTIONS_WHEN_IN_TRADE = properties.getProperty("CAN_USE_FUNCTIONS_WHEN_IN_TRADE", true);
		CAN_USE_FUNCTIONS_WHEN_FISHING = properties.getProperty("CAN_USE_FUNCTIONS_WHEN_FISHING", true);
		CAN_USE_FUNCTIONS_IF_TELEPORTING = properties.getProperty("CAN_USE_FUNCTIONS_IF_TELEPORTING", true);
		CAN_USE_FUNCTIONS_IN_DUEL = properties.getProperty("CAN_USE_FUNCTIONS_IN_DUEL", true);
		CAN_USE_FUNCTIONS_WHEN_IS_PK = properties.getProperty("CAN_USE_FUNCTIONS_WHEN_IS_PK", true);
		CAN_USE_FUNCTIONS_CLAN_LEADERS_ONLY = properties.getProperty("CAN_USE_FUNCTIONS_CLAN_LEADERS_ONLY", false);
		CAN_USE_FUNCTIONS_ON_SIEGE = properties.getProperty("CAN_USE_FUNCTIONS_ON_SIEGE", true);
		CAN_USE_FUNCTIONS_IN_PEACE_ZONE_ONLY = properties.getProperty("CAN_USE_FUNCTIONS_IN_PEACE_ZONE_ONLY", false);
		CAN_USE_FUNCTIONS_IN_EVENTS = properties.getProperty("CAN_USE_FUNCTIONS_IN_EVENTS", false);

		// Buff service
		BUFF_SERVICE_ALLOW_RESTORE = properties.getProperty("BUFF_SERVICE_ALLOW_RESTORE", true);
		BUFF_SERVICE_ALLOW_CANCEL_BUFFS = properties.getProperty("BUFF_SERVICE_ALLOW_CANCEL_BUFFS", true);
		BUFF_SERVICE_COST_ITEM_ID = properties.getProperty("BUFF_SERVICE_COST_ITEM_ID", 57);
		BUFF_SERVICE_COST_ITEM_COUNT = properties.getProperty("BUFF_SERVICE_COST_ITEM_COUNT", 1000L);
		BUFF_SERVICE_MAX_BUFFS_IN_SET = properties.getProperty("BUFF_SERVICE_MAX_BUFFS_IN_SET", 20);
		BUFF_SERVICE_MAX_BUFF_SETS_PER_CHAR = properties.getProperty("BUFF_SERVICE_MAX_BUFF_SETS_PER_CHAR", 8);
		BUFF_SERVICE_AVAILABLE_SKILLS_FOR_BUFF = StringArrayUtils.stringToIntArray2X(properties.getProperty("BUFF_SERVICE_AVAILABLE_SKILLS_FOR_BUFF", ""), ";", "-");
		BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF = properties.getProperty("BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF", 84);
		BUFF_SERVICE_ASSIGN_BUFF_TIME = properties.getProperty("BUFF_SERVICE_ASSIGN_BUFF_TIME", 0);
		BUFF_SERVICE_ASSIGN_BUFF_TIME_MUSIC = properties.getProperty("BUFF_SERVICE_ASSIGN_BUFF_TIME_MUSIC", 0);
		BUFF_SERVICE_ASSIGN_BUFF_TIME_SPECIAL = properties.getProperty("BUFF_SERVICE_ASSIGN_BUFF_TIME_SPECIAL", 0);
		BUFF_SERVICE_BUFF_TIME_MODIFIER = properties.getProperty("BUFF_SERVICE_BUFF_TIME_MODIFIER", 1.0);
		BUFF_SERVICE_BUFF_TIME_MODIFIER_MUSIC = properties.getProperty("BUFF_SERVICE_BUFF_TIME_MODIFIER_MUSIC", 1.0);
		BUFF_SERVICE_BUFF_TIME_MODIFIER_SPECIAL = properties.getProperty("BUFF_SERVICE_BUFF_TIME_MODIFIER_SPECIAL", 1.0);

		// Teleport service
		TELEPORT_SERVICE_COST_ITEM_ID = properties.getProperty("TELEPORT_SERVICE_COST_ITEM_ID", 57);
		TELEPORT_SERVICE_COST_ITEM_COUNT = properties.getProperty("TELEPORT_SERVICE_COST_ITEM_COUNT", 10000);
		TELEPORT_SERVICE_BM_SAVE_COST_ITEM_ID = properties.getProperty("TELEPORT_SERVICE_BM_SAVE_COST_ITEM_ID", 57);
		TELEPORT_SERVICE_BM_SAVE_COST_ITEM_COUNT = properties.getProperty("TELEPORT_SERVICE_BM_SAVE_COST_ITEM_COUNT", 1000000);
		TELEPORT_SERVICE_BM_SAVE_LIMIT = properties.getProperty("TELEPORT_SERVICE_BM_SAVE_LIMIT", 10);
		TELEPORT_SERVICE_BM_COST_ITEM_ID = properties.getProperty("TELEPORT_SERVICE_BM_COST_ITEM_ID", 57);
		TELEPORT_SERVICE_BM_COST_ITEM_COUNT = properties.getProperty("TELEPORT_SERVICE_BM_COST_ITEM_COUNT", 100000);
		TELEPORT_SERVICE_TELEPORT_IF_PK = properties.getProperty("TELEPORT_SERVICE_TELEPORT_IF_PK", false);

		// Occupation purchase service
		OCCUPATION_SERVICE_COST_ITEM_ID_1 = properties.getProperty("OCCUPATION_SERVICE_COST_ITEM_ID_1", 57);
		OCCUPATION_SERVICE_COST_ITEM_COUNT_1 = properties.getProperty("OCCUPATION_SERVICE_COST_ITEM_COUNT_1", 10000L);
		OCCUPATION_SERVICE_COST_ITEM_ID_2 = properties.getProperty("OCCUPATION_SERVICE_COST_ITEM_ID_2", 57);
		OCCUPATION_SERVICE_COST_ITEM_COUNT_2 = properties.getProperty("OCCUPATION_SERVICE_COST_ITEM_COUNT_2", 1000000L);
		OCCUPATION_SERVICE_COST_ITEM_ID_3 = properties.getProperty("OCCUPATION_SERVICE_COST_ITEM_ID_3", 57);
		OCCUPATION_SERVICE_COST_ITEM_COUNT_3 = properties.getProperty("OCCUPATION_SERVICE_COST_ITEM_COUNT_3", 100000000L);

		// Change player name service
		CHANGE_PLAYER_NAME_SERVICE_COST_ITEM_ID = properties.getProperty("CHANGE_PLAYER_NAME_SERVICE_COST_ITEM_ID", 57);
		CHANGE_PLAYER_NAME_SERVICE_COST_ITEM_COUNT = properties.getProperty("CHANGE_PLAYER_NAME_SERVICE_COST_ITEM_COUNT", 100000000L);

		// Change per name service
		CHANGE_PET_NAME_SERVICE_COST_ITEM_ID = properties.getProperty("CHANGE_PET_NAME_SERVICE_COST_ITEM_ID", 57);
		CHANGE_PET_NAME_SERVICE_COST_ITEM_COUNT = properties.getProperty("CHANGE_PET_NAME_SERVICE_COST_ITEM_COUNT", 100000000L);

		// Change clan name service
		CHANGE_CLAN_NAME_SERVICE_COST_ITEM_ID = properties.getProperty("CHANGE_CLAN_NAME_SERVICE_COST_ITEM_ID", 57);
		CHANGE_CLAN_NAME_SERVICE_COST_ITEM_COUNT = properties.getProperty("CHANGE_CLAN_NAME_SERVICE_COST_ITEM_COUNT", 100000000L);

		// Color name service
		COLOR_NAME_SERVICE_COST_ITEM_ID = properties.getProperty("COLOR_NAME_SERVICE_COST_ITEM_ID", 57);
		COLOR_NAME_SERVICE_COST_ITEM_COUNT = properties.getProperty("COLOR_NAME_SERVICE_COST_ITEM_COUNT", 100000000L);
		COLOR_NAME_SERVICE_COLORS = properties.getProperty("COLOR_NAME_SERVICE_COLORS", new String[0], ";");

		// Color title service
		COLOR_TITLE_SERVICE_COST_ITEM_ID = properties.getProperty("COLOR_TITLE_SERVICE_COST_ITEM_ID", 57);
		COLOR_TITLE_SERVICE_COST_ITEM_COUNT = properties.getProperty("COLOR_TITLE_SERVICE_COST_ITEM_COUNT", 100000000L);
		COLOR_TITLE_SERVICE_COLORS = properties.getProperty("COLOR_TITLE_SERVICE_COLORS", new String[0], ";");

		// Additional active windows service
		ADD_WINDOW_SERVICE_TYPE = properties.getProperty("ADD_WINDOW_SERVICE_TYPE", 0);
		ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY = properties.getProperty("ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_1_DAY", 0);
		ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_1_DAY = properties.getProperty("ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_1_DAY", 100000000L);
		ADD_WINDOW_SERVICE_PERIOD_VARIATIONS = properties.getProperty("ADD_WINDOW_SERVICE_PERIOD_VARIATIONS", new int[]{ 1 }, ",");
		ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_FOREVER = properties.getProperty("ADD_WINDOW_SERVICE_COST_ITEM_ID_PER_FOREVER", 0);
		ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_FOREVER = properties.getProperty("ADD_WINDOW_SERVICE_COST_ITEM_COUNT_PER_FOREVER", 10000000000L);

		// Change sex service
		CHANGE_SEX_SERVICE_COST_ITEM_ID = properties.getProperty("CHANGE_SEX_SERVICE_COST_ITEM_ID", 57);
		CHANGE_SEX_SERVICE_COST_ITEM_COUNT = properties.getProperty("CHANGE_SEX_SERVICE_COST_ITEM_COUNT", 100000000L);

		// Expand inventory service
		EXPAND_INVENTORY_SERVICE_COST_ITEM_ID = properties.getProperty("EXPAND_INVENTORY_SERVICE_COST_ITEM_ID", 57);
		EXPAND_INVENTORY_SERVICE_COST_ITEM_COUNT = properties.getProperty("EXPAND_INVENTORY_SERVICE_COST_ITEM_COUNT", 100000L);

		// Expand warehouse service
		EXPAND_WAREHOUSE_SERVICE_COST_ITEM_ID = properties.getProperty("EXPAND_WAREHOUSE_SERVICE_COST_ITEM_ID", 57);
		EXPAND_WAREHOUSE_SERVICE_COST_ITEM_COUNT = properties.getProperty("EXPAND_WAREHOUSE_SERVICE_COST_ITEM_COUNT", 100000L);

		// Expand clan warehouse service
		EXPAND_CLANWAREHOUSE_SERVICE_COST_ITEM_ID = properties.getProperty("EXPAND_CLANWAREHOUSE_SERVICE_COST_ITEM_ID", 57);
		EXPAND_CLANWAREHOUSE_SERVICE_COST_ITEM_COUNT = properties.getProperty("EXPAND_CLANWAREHOUSE_SERVICE_COST_ITEM_COUNT", 100000L);

		CLAN_REPUTATION_SERVICE_PRICES_LIST = new long[0][];

		List<long[]> priceList = CollectionUtils.pooledList();

		Pattern p = Pattern.compile("([0-9]+)-([0-9]+)\\[([0-9]+)\\];?", Pattern.DOTALL);
		Matcher m = p.matcher(properties.getProperty("CLAN_REPUTATION_SERVICE_PRICES_LIST", ""));
		while(m.find()) {
			priceList.add(new long[]{ Long.parseLong(m.group(1)), Long.parseLong(m.group(2)), Long.parseLong(m.group(3)) });
		}

		CLAN_REPUTATION_SERVICE_PRICES_LIST = priceList.toArray(new long[0][]);
		CollectionUtils.recycle(priceList);

		// Expand warehouse service
		KARMA_PK_SERVICE_COST_ITEM_ID = properties.getProperty("KARMA_PK_SERVICE_COST_ITEM_ID", 57);
		KARMA_PK_SERVICE_COST_ITEM_COUNT = properties.getProperty("KARMA_PK_SERVICE_COST_ITEM_COUNT", 100000L);

		// Game statistic
		STATISTIC_REFRESH_TIME = properties.getProperty("STATISTIC_REFRESH_TIME", 180);
		STATISTIC_TOP_PK_COUNT = properties.getProperty("STATISTIC_TOP_PK_COUNT", 20);
		STATISTIC_TOP_PVP_COUNT = properties.getProperty("STATISTIC_TOP_PVP_COUNT", 20);
		STATISTIC_TOP_LVL_COUNT = properties.getProperty("STATISTIC_TOP_LVL_COUNT", 20);
		STATISTIC_TOP_ADENA_COUNT = properties.getProperty("STATISTIC_TOP_ADENA_COUNT", 20);
		STATISTIC_TOP_ONLINE_COUNT = properties.getProperty("STATISTIC_TOP_ONLINE_COUNT", 20);
		STATISTIC_TOP_ITEM_COUNT = properties.getProperty("STATISTIC_TOP_ITEM_COUNT", 20);
		STATISTIC_TOP_OLYMPIAD_COUNT = properties.getProperty("STATISTIC_TOP_OLYMPIAD_COUNT", 20);
		STATISTIC_BY_ITEM_ID = properties.getProperty("STATISTIC_BY_ITEM_ID", 4037);
	}
}