package handler.bbs.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.actor.player.OnPlayerExitListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.PlayerAccess;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.Util;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handler.bbs.ScriptsCommunityHandler;

/**
 * @author Bonux
**/
public class CommunityStatistic extends ScriptsCommunityHandler
{
	private static class PkComparator implements Comparator<StatisticData>
	{
		@Override
		public int compare(StatisticData o1, StatisticData o2)
		{
			if(o2.pk == o1.pk)
				return o1.name.compareTo(o2.name);
			return Integer.compare(o2.pk, o1.pk);
		}
	}

	private static class PvpComparator implements Comparator<StatisticData>
	{
		@Override
		public int compare(StatisticData o1, StatisticData o2)
		{
			if(o2.pvp == o1.pvp)
				return o1.name.compareTo(o2.name);
			return Integer.compare(o2.pvp, o1.pvp);
		}
	}

	private static class LvlComparator implements Comparator<StatisticData>
	{
		@Override
		public int compare(StatisticData o1, StatisticData o2)
		{
			if(o2.lvl == o1.lvl)
				return o1.name.compareTo(o2.name);
			return Integer.compare(o2.lvl, o1.lvl);
		}
	}

	private static class AdenaComparator implements Comparator<StatisticData>
	{
		@Override
		public int compare(StatisticData o1, StatisticData o2)
		{
			if(o2.adena == o1.adena)
				return o1.name.compareTo(o2.name);
			return Long.compare(o2.adena, o1.adena);
		}
	}

	private static class OnlineComparator implements Comparator<StatisticData>
	{
		@Override
		public int compare(StatisticData o1, StatisticData o2)
		{
			if(o2.online == o1.online)
				return o1.name.compareTo(o2.name);
			return Integer.compare(o2.online, o1.online);
		}
	}

	private static class ItemComparator implements Comparator<StatisticData>
	{
		@Override
		public int compare(StatisticData o1, StatisticData o2)
		{
			if(o2.item == o1.item)
				return o1.name.compareTo(o2.name);
			return Long.compare(o2.item, o1.item);
		}
	}

	private static class OlympiadComparator implements Comparator<StatisticData>
	{
		@Override
		public int compare(StatisticData o1, StatisticData o2)
		{
			if(o2.olympiad == o1.olympiad)
				return o1.name.compareTo(o2.name);
			return Integer.compare(o2.olympiad, o1.olympiad);
		}
	}

	private static class StatisticData
	{
		public String name;
		public int pk;
		public int pvp;
		public int lvl;
		public long adena;
		public int online;
		public long item;
		public int olympiad;
	}

	private class OnPlayerEnterExitListener implements OnPlayerEnterListener, OnPlayerExitListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			updateStatistic(player);
		}

		@Override
		public void onPlayerExit(Player player)
		{
			updateStatistic(player);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(CommunityStatistic.class);

	private final IntObjectMap<StatisticData> _statistic = new CHashIntObjectMap<StatisticData>();

	private final PkComparator _pkComparator = new PkComparator();
	private final PvpComparator _pvpComparator = new PvpComparator();
	private final LvlComparator _lvlComparator = new LvlComparator();
	private final AdenaComparator _adenaComparator = new AdenaComparator();
	private final OnlineComparator _onlineComparator = new OnlineComparator();
	private final ItemComparator _itemComparator = new ItemComparator();
	private final OlympiadComparator _olympiadComparator = new OlympiadComparator();

	private long _nextUpdateStatisticTime = 0;

	private List<StatisticData> _pkStatisticTemp = new CopyOnWriteArrayList<StatisticData>();
	private List<StatisticData> _pvpStatisticTemp = new CopyOnWriteArrayList<StatisticData>();
	private List<StatisticData> _lvlStatisticTemp = new CopyOnWriteArrayList<StatisticData>();
	private List<StatisticData> _adenaStatisticTemp = new CopyOnWriteArrayList<StatisticData>();
	private List<StatisticData> _onlineStatisticTemp = new CopyOnWriteArrayList<StatisticData>();
	private List<StatisticData> _itemStatisticTemp = new CopyOnWriteArrayList<StatisticData>();
	private List<StatisticData> _olympiadStatisticTemp = new CopyOnWriteArrayList<StatisticData>();

	@Override
	public void onInit()
	{
		super.onInit();

		if(BBSConfig.STATISTIC_TOP_PK_COUNT > 0 || BBSConfig.STATISTIC_TOP_PVP_COUNT > 0 || BBSConfig.STATISTIC_TOP_LVL_COUNT > 0 || BBSConfig.STATISTIC_TOP_ADENA_COUNT > 0 || BBSConfig.STATISTIC_TOP_ONLINE_COUNT > 0 || BBSConfig.STATISTIC_TOP_ITEM_COUNT > 0 || BBSConfig.STATISTIC_TOP_OLYMPIAD_COUNT > 0)
		{
			loadStatistic();
			refreshStatistic();
			CharListenerList.addGlobal(new OnPlayerEnterExitListener());
		}
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_cbbsstatistic"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";

		if("cbbsstatistic".equals(cmd))
		{
			if(!st.hasMoreTokens())
				return;

			if(BBSConfig.STATISTIC_TOP_PK_COUNT <= 0 && BBSConfig.STATISTIC_TOP_PVP_COUNT <= 0 && BBSConfig.STATISTIC_TOP_LVL_COUNT <= 0 && BBSConfig.STATISTIC_TOP_ADENA_COUNT <= 0 && BBSConfig.STATISTIC_TOP_ONLINE_COUNT <= 0 && BBSConfig.STATISTIC_TOP_ITEM_COUNT <= 0 && BBSConfig.STATISTIC_TOP_OLYMPIAD_COUNT <= 0)
			{
				player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
				player.sendPacket(ShowBoardPacket.CLOSE);
				return;
			}

			refreshStatistic();

			HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/pages/statistic.htm", player);
			html = tpls.get(0);

			final String pkTopName = tpls.get(3);
			final String pvpTopName = tpls.get(4);
			final String lvlTopName = tpls.get(5);
			final String adenaTopName = tpls.get(6);
			final String onlineTopName = tpls.get(7);
			final String itemTopName = tpls.get(8);
			final String olympiadTopName = tpls.get(9);

			final String topButton = tpls.get(2);

			String pkTopButton = topButton.replace("<?button_name?>", pkTopName).replace("<?button_value?>", "pk");
			String pvpTopButton = topButton.replace("<?button_name?>", pvpTopName).replace("<?button_value?>", "pvp");
			String lvlTopButton = topButton.replace("<?button_name?>", lvlTopName).replace("<?button_value?>", "lvl");
			String adenaTopButton = topButton.replace("<?button_name?>", adenaTopName).replace("<?button_value?>", "adena");
			String onlineTopButton = topButton.replace("<?button_name?>", onlineTopName).replace("<?button_value?>", "online");
			String itemTopButton = topButton.replace("<?button_name?>", itemTopName).replace("<?button_value?>", "item");
			String olympiadTopButton = topButton.replace("<?button_name?>", olympiadTopName).replace("<?button_value?>", "olympiad");

			StringBuilder content = new StringBuilder();

			final String statisticRow1 = tpls.get(17);
			final String statisticRow2 = tpls.get(18);

			String cmd2 = st.nextToken();
			if("pk".equals(cmd2))
			{
				if(BBSConfig.STATISTIC_TOP_PK_COUNT <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				pkTopButton = tpls.get(1).replace("<?button_name?>", pkTopName);
				html = html.replace("<?statistic_value_name?>", tpls.get(10));

				int i = 0;
				for(StatisticData s : _pkStatisticTemp)
				{
					if(i == 0 || (i % 2) == 0)
						content.append(statisticRow1.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.pk)));
					else
						content.append(statisticRow2.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.pk)));
					i++;
				}
			}
			else if("pvp".equals(cmd2))
			{
				if(BBSConfig.STATISTIC_TOP_PVP_COUNT <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				pvpTopButton = tpls.get(1).replace("<?button_name?>", pvpTopName);
				html = html.replace("<?statistic_value_name?>", tpls.get(11));

				int i = 0;
				for(StatisticData s : _pvpStatisticTemp)
				{
					if(i == 0 || (i % 2) == 0)
						content.append(statisticRow1.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.pvp)));
					else
						content.append(statisticRow2.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.pvp)));
					i++;
				}
			}
			else if("lvl".equals(cmd2))
			{
				if(BBSConfig.STATISTIC_TOP_LVL_COUNT <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				lvlTopButton = tpls.get(1).replace("<?button_name?>", lvlTopName);
				html = html.replace("<?statistic_value_name?>", tpls.get(12));

				int i = 0;
				for(StatisticData s : _lvlStatisticTemp)
				{
					if(i == 0 || (i % 2) == 0)
						content.append(statisticRow1.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.lvl)));
					else
						content.append(statisticRow2.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.lvl)));
					i++;
				}
			}
			else if("adena".equals(cmd2))
			{
				if(BBSConfig.STATISTIC_TOP_ADENA_COUNT <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				adenaTopButton = tpls.get(1).replace("<?button_name?>", adenaTopName);
				html = html.replace("<?statistic_value_name?>", tpls.get(13));

				int i = 0;
				for(StatisticData s : _adenaStatisticTemp)
				{
					if(i == 0 || (i % 2) == 0)
						content.append(statisticRow1.replace("<?player_name?>", s.name).replace("<?statistic_value?>", Util.formatAdena(s.adena)));
					else
						content.append(statisticRow2.replace("<?player_name?>", s.name).replace("<?statistic_value?>", Util.formatAdena(s.adena)));
					i++;
				}
			}
			else if("online".equals(cmd2))
			{
				if(BBSConfig.STATISTIC_TOP_ONLINE_COUNT <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				onlineTopButton = tpls.get(1).replace("<?button_name?>", onlineTopName);
				html = html.replace("<?statistic_value_name?>", tpls.get(14));

				int i = 0;
				for(StatisticData s : _onlineStatisticTemp)
				{
					if(i == 0 || (i % 2) == 0)
						content.append(statisticRow1.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.online / 60 / 60)));
					else
						content.append(statisticRow2.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.online / 60 / 60)));
					i++;
				}
			}
			else if("item".equals(cmd2))
			{
				if(BBSConfig.STATISTIC_TOP_ITEM_COUNT <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				itemTopButton = tpls.get(1).replace("<?button_name?>", itemTopName);
				html = html.replace("<?statistic_value_name?>", tpls.get(15));

				int i = 0;
				for(StatisticData s : _itemStatisticTemp)
				{
					if(i == 0 || (i % 2) == 0)
						content.append(statisticRow1.replace("<?player_name?>", s.name).replace("<?statistic_value?>", Util.formatAdena(s.item)));
					else
						content.append(statisticRow2.replace("<?player_name?>", s.name).replace("<?statistic_value?>", Util.formatAdena(s.item)));
					i++;
				}
			}
			else if("olympiad".equals(cmd2))
			{
				if(BBSConfig.STATISTIC_TOP_OLYMPIAD_COUNT <= 0)
				{
					player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
					player.sendPacket(ShowBoardPacket.CLOSE);
					return;
				}

				olympiadTopButton = tpls.get(1).replace("<?button_name?>", olympiadTopName);
				html = html.replace("<?statistic_value_name?>", tpls.get(16));

				int i = 0;
				for(StatisticData s : _olympiadStatisticTemp)
				{
					if(i == 0 || (i % 2) == 0)
						content.append(statisticRow1.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.olympiad)));
					else
						content.append(statisticRow2.replace("<?player_name?>", s.name).replace("<?statistic_value?>", String.valueOf(s.olympiad)));
					i++;
				}
			}

			html = html.replace("<?top_pk_button?>", BBSConfig.STATISTIC_TOP_PK_COUNT > 0 ? pkTopButton : "");
			html = html.replace("<?top_pvp_button?>", BBSConfig.STATISTIC_TOP_PVP_COUNT > 0 ? pvpTopButton : "");
			html = html.replace("<?top_lvl_button?>", BBSConfig.STATISTIC_TOP_LVL_COUNT > 0 ? lvlTopButton : "");
			html = html.replace("<?top_adena_button?>", BBSConfig.STATISTIC_TOP_ADENA_COUNT > 0 ? adenaTopButton : "");
			html = html.replace("<?top_online_button?>", BBSConfig.STATISTIC_TOP_ONLINE_COUNT > 0 ? onlineTopButton : "");
			html = html.replace("<?top_item_button?>", BBSConfig.STATISTIC_TOP_ITEM_COUNT > 0 ? itemTopButton : "");
			html = html.replace("<?top_olympiad_button?>", BBSConfig.STATISTIC_TOP_OLYMPIAD_COUNT > 0 ? olympiadTopButton : "");

			html = html.replace("<?statistic?>", content.toString());
		}
		ShowBoardPacket.separateAndSend(html, player);
	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		//
	}

	private void loadStatistic()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT c.obj_Id, c.char_name, c.pkkills, c.pvpkills, c.onlinetime, cs.exp FROM characters AS c LEFT JOIN character_subclasses AS cs ON c.obj_Id=cs.char_obj_id AND cs.type=" + SubClassType.BASE_CLASS.ordinal());
			rset = statement.executeQuery();
			while(rset.next())
			{
				int objectId = rset.getInt("c.obj_Id");
				PlayerAccess access = Config.gmlist.get(objectId);
				if(access != null && access.IsGM)
					continue;

				StatisticData stat = new StatisticData();
				stat.name = rset.getString("c.char_name");
				stat.pk = rset.getInt("c.pkkills");
				stat.pvp = rset.getInt("c.pvpkills");
				stat.lvl = Experience.getLevel(rset.getLong("cs.exp"));
				stat.online = rset.getInt("c.onlinetime");
				_statistic.put(objectId, stat);
			}

			DbUtils.closeQuietly(statement, rset);

			statement = con.prepareStatement("SELECT owner_id, count FROM items WHERE item_id=" + ItemTemplate.ITEM_ID_ADENA + " AND (loc='INVENTORY' OR loc='PAPERDOLL' OR loc='WAREHOUSE')");
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatisticData stat = _statistic.get(rset.getInt("owner_id"));
				if(stat != null)
					stat.adena += rset.getLong("count");
			}

			DbUtils.closeQuietly(statement, rset);

			statement = con.prepareStatement("SELECT owner_id, count FROM items WHERE item_id=" + BBSConfig.STATISTIC_BY_ITEM_ID + " AND (loc='INVENTORY' OR loc='PAPERDOLL' OR loc='WAREHOUSE')");
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatisticData stat = _statistic.get(rset.getInt("owner_id"));
				if(stat != null)
					stat.item += rset.getLong("count");
			}

			DbUtils.closeQuietly(statement, rset);

			statement = con.prepareStatement("SELECT char_id, olympiad_points FROM olympiad_participants");
			rset = statement.executeQuery();
			while(rset.next())
			{
				StatisticData stat = _statistic.get(rset.getInt("char_id"));
				if(stat != null)
					stat.olympiad = rset.getInt("olympiad_points");
			}
		}
		catch(Exception e)
		{
			_log.error("CommunityStatistic.refreshStatistic(): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	private synchronized void refreshStatistic()
	{
		if(_nextUpdateStatisticTime > System.currentTimeMillis())
			return;

		_nextUpdateStatisticTime = System.currentTimeMillis() + BBSConfig.STATISTIC_REFRESH_TIME * 1000L;

		for(Player player : GameObjectsStorage.getPlayers())
			updateStatistic(player);

		Collection<StatisticData> statistic = _statistic.values();
		StatisticData[] array = statistic.toArray(new StatisticData[statistic.size()]);

		if(BBSConfig.STATISTIC_TOP_PK_COUNT > 0)
		{
			Arrays.sort(array, _pkComparator);
			_pkStatisticTemp.clear();
			for(int i = 0; i < Math.min(array.length, BBSConfig.STATISTIC_TOP_PK_COUNT); i++)
				_pkStatisticTemp.add(array[i]);
		}

		if(BBSConfig.STATISTIC_TOP_PVP_COUNT > 0)
		{
			Arrays.sort(array, _pvpComparator);
			_pvpStatisticTemp.clear();
			for(int i = 0; i < Math.min(array.length, BBSConfig.STATISTIC_TOP_PVP_COUNT); i++)
				_pvpStatisticTemp.add(array[i]);
		}

		if(BBSConfig.STATISTIC_TOP_LVL_COUNT > 0)
		{
			Arrays.sort(array, _lvlComparator);
			_lvlStatisticTemp.clear();
			for(int i = 0; i < Math.min(array.length, BBSConfig.STATISTIC_TOP_LVL_COUNT); i++)
				_lvlStatisticTemp.add(array[i]);
		}

		if(BBSConfig.STATISTIC_TOP_ADENA_COUNT > 0)
		{
			Arrays.sort(array, _adenaComparator);
			_adenaStatisticTemp.clear();
			for(int i = 0; i < Math.min(array.length, BBSConfig.STATISTIC_TOP_ADENA_COUNT); i++)
				_adenaStatisticTemp.add(array[i]);
		}

		if(BBSConfig.STATISTIC_TOP_ONLINE_COUNT > 0)
		{
			Arrays.sort(array, _onlineComparator);
			_onlineStatisticTemp.clear();
			for(int i = 0; i < Math.min(array.length, BBSConfig.STATISTIC_TOP_ONLINE_COUNT); i++)
				_onlineStatisticTemp.add(array[i]);
		}

		if(BBSConfig.STATISTIC_TOP_ITEM_COUNT > 0)
		{
			Arrays.sort(array, _itemComparator);
			_itemStatisticTemp.clear();
			for(int i = 0; i < Math.min(array.length, BBSConfig.STATISTIC_TOP_ITEM_COUNT); i++)
				_itemStatisticTemp.add(array[i]);
		}

		if(BBSConfig.STATISTIC_TOP_OLYMPIAD_COUNT > 0)
		{
			Arrays.sort(array, _olympiadComparator);
			_olympiadStatisticTemp.clear();
			for(int i = 0; i < Math.min(array.length, BBSConfig.STATISTIC_TOP_OLYMPIAD_COUNT); i++)
				_olympiadStatisticTemp.add(array[i]);
		}
	}

	private void updateStatistic(Player player)
	{
		if(player.isGM())
			return;

		StatisticData stat = _statistic.get(player.getObjectId());
		if(stat == null)
		{
			stat = new StatisticData();
			_statistic.put(player.getObjectId(), stat);
		}
		stat.name = player.getName();
		stat.pk = player.getPkKills();
		stat.pvp = player.getPvpKills();
		stat.lvl = player.getBaseSubClass() != null ? player.getBaseSubClass().getLevel() : player.getLevel();
		stat.online = player.getOnlineTime();
		stat.adena = player.getInventory().getCountOf(ItemTemplate.ITEM_ID_ADENA);
		stat.adena += player.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA);
		stat.item = player.getInventory().getCountOf(BBSConfig.STATISTIC_BY_ITEM_ID);
		stat.item += player.getWarehouse().getCountOf(BBSConfig.STATISTIC_BY_ITEM_ID);
		stat.olympiad = Olympiad.getParticipantPoints(player.getObjectId());
	}
}