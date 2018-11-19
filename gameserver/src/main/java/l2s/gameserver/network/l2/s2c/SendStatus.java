package l2s.gameserver.network.l2.s2c;

import java.util.Calendar;
import java.util.Locale;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;

public final class SendStatus extends L2GameServerPacket
{
	private static final long MIN_UPDATE_PERIOD = 30000;
	private static int online_players = 0;
	private static int max_online_players = 0;
	private static int online_priv_store = 0;
	private static long last_update = 0;

	public SendStatus()
	{
		if(System.currentTimeMillis() - last_update < MIN_UPDATE_PERIOD)
			return;

		last_update = System.currentTimeMillis();
		if(!Config.ENABLE_L2_TOP_OVERONLINE)
		{
			int i = 0;
			int j = 0;
			for(Player player : GameObjectsStorage.getPlayers())
			{
				i++;
				if(player.isInStoreMode() && (!Config.SENDSTATUS_TRADE_JUST_OFFLINE || player.isInOfflineMode()))
					j++;
			}
			online_players = i;
			online_priv_store = (int) Math.floor(j * Config.SENDSTATUS_TRADE_MOD);
			max_online_players = Math.max(max_online_players, online_players);
		}
		else
		{
			max_online_players = Config.L2TOP_MAX_ONLINE;
			final int hour = Calendar.getInstance(new Locale("ru", "RU")).get(Calendar.HOUR_OF_DAY);

			if (hour >= 0 && hour < 6)
				online_players = Rnd.get(Config.MIN_ONLINE_0_5_AM, Config.MAX_ONLINE_0_5_AM);
			else if (hour >= 6 && hour < 12)
				online_players = Rnd.get(Config.MIN_ONLINE_6_11_AM, Config.MAX_ONLINE_6_11_AM);
			else if (hour >= 12 && hour < 19)
				online_players = Rnd.get(Config.MIN_ONLINE_12_6_PM, Config.MAX_ONLINE_12_6_PM);
			else
				online_players = Rnd.get(Config.MIN_ONLINE_7_11_PM, Config.MAX_ONLINE_7_11_PM);
			int weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 1;
			online_players += (weekDay < 5 ? Config.ADD_ONLINE_ON_SIMPLE_DAY : Config.ADD_ONLINE_ON_WEEKEND);
			online_priv_store = Rnd.get(Config.L2TOP_MIN_TRADERS, Config.L2TOP_MAX_TRADERS);
		}
	}

	@Override
	protected final boolean writeOpcodes()
	{
		writeC(0x00);
		return true;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(0x01); // World ID
		writeD(max_online_players); // Max Online
		writeD(online_players); // Current Online
		writeD(online_players); // Current Online
		writeD(online_priv_store); // Priv.Store Chars

		// SEND TRASH
		writeD(0x002C0030);
		for(int x = 0; x < 10; x++)
			writeH(41 + Rnd.get(17));
		writeD(43 + Rnd.get(17));
		int z = 36219 + Rnd.get(1987);
		writeD(z);
		writeD(z);
		writeD(37211 + Rnd.get(2397));
		writeD(0x00);
		writeD(0x02);
	}
}