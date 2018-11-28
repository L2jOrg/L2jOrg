package bosses;

import org.l2j.commons.configuration.ExProperties;
import org.l2j.commons.string.StringArrayUtils;
import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.listener.script.OnLoadScriptListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class BossesConfig implements OnLoadScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(BossesConfig.class);

	private static final String PROPERTIES_FILE = "config/bosses.properties";

	// Baium
	public static SchedulingPattern BAIUM_RESPAWN_TIME_PATTERN;
	public static int BAIUM_SLEEP_TIME;
	public static int[][] BAIUM_ENTERANCE_NECESSARY_ITEMS;
	public static boolean BAIUM_ENTERANCE_CAN_CONSUME_NECESSARY_ITEMS;

	@Override
	public void onLoad()
	{
		ExProperties properties = Config.load(PROPERTIES_FILE);

		// Baium
		BAIUM_RESPAWN_TIME_PATTERN = new SchedulingPattern(properties.getProperty("BAIUM_RESPAWN_TIME_PATTERN", "00 16 * * 7"));
		BAIUM_SLEEP_TIME = properties.getProperty("BAIUM_SLEEP_TIME", 30);
		BAIUM_ENTERANCE_NECESSARY_ITEMS = StringArrayUtils.stringToIntArray2X(properties.getProperty("BAIUM_ENTERANCE_NECESSARY_ITEMS", "4295-1"), ";", "-");
		BAIUM_ENTERANCE_CAN_CONSUME_NECESSARY_ITEMS = properties.getProperty("BAIUM_ENTERANCE_CAN_CONSUME_NECESSARY_ITEMS", true);
	}
}