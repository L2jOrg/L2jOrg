package handler.bbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMailArrivedPacket;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommunityClan extends ScriptsCommunityHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityClan.class);
	private static final int CLANS_PER_PAGE = 10;

	private final Listener _listener = new Listener();

	@Override
	public void onInit()
	{
		super.onInit();

		if(Config.BBS_ENABLED)
		{
			CharListenerList.addGlobal(_listener);
			_log.info("CommunityBoard: Clan Community service loaded.");
		}
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbsclan",
			"_bbsclanhome_",
			"_bbsclanlist_",
			"_bbsclansearch",
			"_bbsclanadmin",
			"_mailwritepledgeform",
			"_announcepledgewriteform",
			"_announcepledgeswitchshowflag",
			"_announcepledgewrite",
			"_bbsclanwriteintro",
			"_bbsclanwritemail"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		player.setSessionVar("add_fav", null);
		if("bbsclan".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan != null && clan.getLevel() > 1)
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			onBypassCommand(player, "_bbsclanlist_1_0_");
		}
		else if("bbsclanlist".equals(cmd))
		{
			int page = Integer.parseInt(st.nextToken());
			int byCL = Integer.parseInt(st.nextToken());

			String search = st.hasMoreTokens() ? st.nextToken() : "";

			HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/bbs_clanlist.htm", player);
			String html = tpls.get(0);

			Clan playerClan = player.getClan();
			if(playerClan != null)
			{
				String my_clan = tpls.get(1);
				my_clan = my_clan.replace("%PLEDGE_ID%", String.valueOf(playerClan.getClanId()));
				my_clan = my_clan.replace("%MY_PLEDGE_NAME%", playerClan.getLevel() > 1 ? playerClan.getName() : "");
				html = html.replace("<?my_clan_link?>", my_clan);
			}
			else
				html = html.replace("<?my_clan_link?>", "");

			List<Clan> clanList = getClanList(search, byCL == 1);

			int start = (page - 1) * CLANS_PER_PAGE;
			int end = Math.min(page * CLANS_PER_PAGE, clanList.size());

			if(page == 1)
			{
				html = html.replace("%ACTION_GO_LEFT%", "");
				html = html.replace("%GO_LIST%", "");
				html = html.replace("%NPAGE%", "1");
			}
			else
			{
				html = html.replace("%ACTION_GO_LEFT%", "bypass _bbsclanlist_" + (page - 1) + "_" + byCL + "_" + search);
				html = html.replace("%NPAGE%", String.valueOf(page));
				StringBuilder goList = new StringBuilder("");
				for(int i = page > 10 ? page - 10 : 1; i < page; i++)
					goList.append("<td><a action=\"bypass _bbsclanlist_").append(i).append("_").append(byCL).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");

				html = html.replace("%GO_LIST%", goList.toString());
			}

			int pages = Math.max(clanList.size() / CLANS_PER_PAGE, 1);
			if(clanList.size() > pages * CLANS_PER_PAGE)
				pages++;

			if(pages > page)
			{
				html = html.replace("%ACTION_GO_RIGHT%", "bypass _bbsclanlist_" + (page + 1) + "_" + byCL + "_" + search);
				int ep = Math.min(page + 10, pages);
				StringBuilder goList = new StringBuilder("");
				for(int i = page + 1; i <= ep; i++)
					goList.append("<td><a action=\"bypass _bbsclanlist_").append(i).append("_").append(byCL).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");

				html = html.replace("%GO_LIST2%", goList.toString());
			}
			else
			{
				html = html.replace("%ACTION_GO_RIGHT%", "");
				html = html.replace("%GO_LIST2%", "");
			}

			StringBuilder cl = new StringBuilder("");
			String tpl = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_clantpl.htm", player);
			for(int i = start; i < end; i++)
			{
				Clan clan = clanList.get(i);
				String clantpl = tpl;
				clantpl = clantpl.replace("%action_clanhome%", "bypass _bbsclanhome_" + clan.getClanId());
				clantpl = clantpl.replace("%clan_name%", clan.getName());
				clantpl = clantpl.replace("%clan_owner%", clan.getLeaderName());
				clantpl = clantpl.replace("%skill_level%", String.valueOf(clan.getLevel()));
				clantpl = clantpl.replace("%member_count%", String.valueOf(clan.getAllSize()));
				cl.append(clantpl);
			}

			html = html.replace("%CLAN_LIST%", cl.toString());

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("bbsclanhome".equals(cmd))
		{
			int clanId = Integer.parseInt(st.nextToken());
			if(clanId == 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.NOT_JOINED_IN_ANY_CLAN));
				onBypassCommand(player, "_bbsclanlist_1_0");
				return;
			}

			Clan clan = ClanTable.getInstance().getClan(clanId);
			if(clan == null)
			{
				onBypassCommand(player, "_bbsclanlist_1_0");
				return;
			}

			if(clan.getLevel() < 2)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER));
				onBypassCommand(player, "_bbsclanlist_1_0");
				return;
			}

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			String intro = "";
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type = 2");
				statement.setInt(1, clanId);
				rset = statement.executeQuery();
				if(rset.next())
					intro = rset.getString("notice");
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}

			HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/bbs_clan.htm", player);
			String html = tpls.get(0);

			html = html.replace("%PLEDGE_ID%", String.valueOf(clanId));
			html = html.replace("%ACTION_ANN%", "");
			html = html.replace("%ACTION_FREE%", "");

			if(player.getClanId() == clanId && player.isClanLeader())
				html = html.replace("<?menu?>", tpls.get(1));
			else
				html = html.replace("<?menu?>", "");

			html = html.replace("%CLAN_INTRO%", intro.replace("\n", "<br1>"));
			html = html.replace("%CLAN_NAME%", clan.getName());
			html = html.replace("%SKILL_LEVEL%", String.valueOf(clan.getLevel()));
			html = html.replace("%CLAN_MEMBERS%", String.valueOf(clan.getAllSize()));
			html = html.replace("%OWNER_NAME%", clan.getLeaderName());
			html = html.replace("%ALLIANCE_NAME%", clan.getAlliance() != null ? clan.getAlliance().getAllyName() : "");

			html = html.replace("%ANN_LIST%", "");
			html = html.replace("%THREAD_LIST%", "");

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("bbsclanadmin".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_clanadmin.htm", player);

			html = html.replace("%PLEDGE_ID%", String.valueOf(clan.getClanId()));
			html = html.replace("%ACTION_ANN%", "");
			html = html.replace("%ACTION_FREE%", "");
			html = html.replace("%CLAN_NAME%", clan.getName());
			html = html.replace("%per_list%", "");

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			String intro = "";
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type = 2");
				statement.setInt(1, clan.getClanId());
				rset = statement.executeQuery();
				if(rset.next())
					intro = rset.getString("notice");
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}

			List<String> args = new ArrayList<String>();
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0"); // account data ?
			args.add("");
			args.add("0"); // account data ?
			args.add("");
			args.add("0"); // account data ?
			args.add("");
			args.add("");
			args.add(intro);
			args.add("");
			args.add("");
			args.add("0");
			args.add("0");
			args.add("");

			ShowBoardPacket.separateAndSend(html, args, player);
		}
		else if("mailwritepledgeform".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_pledge_mail_write.htm", player);

			html = html.replace("%PLEDGE_ID%", String.valueOf(clan.getClanId()));
			html = html.replace("%pledge_id%", String.valueOf(clan.getClanId()));
			html = html.replace("%pledge_name%", clan.getName());

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("announcepledgewriteform".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			HtmTemplates tpls = HtmCache.getInstance().getTemplates("scripts/handler/bbs/bbs_clanannounce.htm", player);
			String html = tpls.get(0);

			html = html.replace("%PLEDGE_ID%", String.valueOf(clan.getClanId()));
			html = html.replace("%ACTION_ANN%", "");
			html = html.replace("%ACTION_FREE%", "");

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			String notice = "";
			int type = 0;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type != 2");
				statement.setInt(1, clan.getClanId());
				rset = statement.executeQuery();
				if(rset.next())
				{
					notice = rset.getString("notice");
					type = rset.getInt("type");
				}
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}

			if(type == 0)
				html = html.replace("<?usage?>", tpls.get(1));
			else
				html = html.replace("<?usage?>", tpls.get(2));

			html = html.replace("%flag%", String.valueOf(type));

			List<String> args = new ArrayList<String>();
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0");
			args.add("0"); // account data ?
			args.add("");
			args.add("0"); // account data ?
			args.add("");
			args.add("0"); // account data ?
			args.add("");
			args.add("");
			args.add(notice);
			args.add("");
			args.add("");
			args.add("0");
			args.add("0");
			args.add("");

			ShowBoardPacket.separateAndSend(html, args, player);
		}
		else if("announcepledgeswitchshowflag".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			int type = Integer.parseInt(st.nextToken());

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("UPDATE `bbs_clannotice` SET type = ? WHERE `clan_id` = ? and type = ?");
				statement.setInt(1, type);
				statement.setInt(2, clan.getClanId());
				statement.setInt(3, type == 1 ? 0 : 1);
				statement.execute();
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			clan.setNotice(type == 0 ? "" : null);
			onBypassCommand(player, "_announcepledgewriteform");
		}
	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		if("bbsclansearch".equals(cmd))
		{
			if(arg3 == null)
				arg3 = "";

			onBypassCommand(player, "_bbsclanlist_1_" + ("Ruler".equals(arg4) ? "1" : "0") + "_" + arg3);
		}
		else if("bbsclanwriteintro".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2 || !player.isClanLeader() || arg3 == null || arg3.isEmpty())
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			arg3 = arg3.replace("<", "");
			arg3 = arg3.replace(">", "");
			arg3 = arg3.replace("&", "");
			arg3 = arg3.replace("$", "");

			if(arg3.length() > 3000)
				arg3 = arg3.substring(0, 3000);

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("REPLACE INTO `bbs_clannotice`(clan_id, type, notice) VALUES(?, ?, ?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, 2);
				statement.setString(3, arg3);
				statement.execute();
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
		}
		else if("bbsclanwritemail".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			if(arg3 == null || arg4 == null)
			{
				player.sendPacket(SystemMsg.THE_MESSAGE_WAS_NOT_SENT);
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			arg3 = arg3.replace("<", "");
			arg3 = arg3.replace(">", "");
			arg3 = arg3.replace("&", "");
			arg3 = arg3.replace("$", "");

			arg5 = arg5.replace("<", "");
			arg5 = arg5.replace(">", "");
			arg5 = arg5.replace("&", "");
			arg5 = arg5.replace("$", "");

			if(arg3.isEmpty() || arg4.isEmpty())
			{
				player.sendPacket(SystemMsg.THE_MESSAGE_WAS_NOT_SENT);
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			if(arg3.length() > 128)
				arg3 = arg3.substring(0, 128);

			if(arg4.length() > 3000)
				arg5 = arg5.substring(0, 3000);

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO `bbs_mail`(to_name, to_object_id, from_name, from_object_id, title, message, post_date, box_type) VALUES(?, ?, ?, ?, ?, ?, ?, 0)");
				for(UnitMember clm : clan)
				{
					statement.setString(1, clan.getName());
					statement.setInt(2, clm.getObjectId());
					statement.setString(3, player.getName());
					statement.setInt(4, player.getObjectId());
					statement.setString(5, arg3);
					statement.setString(6, arg5);
					statement.setInt(7, (int) (System.currentTimeMillis() / 1000));
					statement.execute();
				}
				statement.close();

				statement = con.prepareStatement("INSERT INTO `bbs_mail`(to_name, to_object_id, from_name, from_object_id, title, message, post_date, box_type) VALUES(?, ?, ?, ?, ?, ?, ?, 1)");
				statement.setString(1, clan.getName());
				statement.setInt(2, player.getObjectId());
				statement.setString(3, player.getName());
				statement.setInt(4, player.getObjectId());
				statement.setString(5, arg3);
				statement.setString(6, arg5);
				statement.setInt(7, (int) (System.currentTimeMillis() / 1000));
				statement.execute();
			}
			catch(Exception e)
			{
				player.sendPacket(SystemMsg.THE_MESSAGE_WAS_NOT_SENT);
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			player.sendPacket(SystemMsg.YOUVE_SENT_MAIL);

			for(Player member : clan.getOnlineMembers(0))
			{
				member.sendPacket(SystemMsg.YOUVE_GOT_MAIL);
				member.sendPacket(ExMailArrivedPacket.STATIC);
			}

			onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
		}
		else if("announcepledgewrite".equals(cmd))
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2 || !player.isClanLeader())
			{
				onBypassCommand(player, "_bbsclanhome_" + player.getClanId());
				return;
			}

			if(arg3 == null || arg3.isEmpty())
			{
				onBypassCommand(player, "_announcepledgewriteform");
				return;
			}

			arg3 = arg3.replace("<", "");
			arg3 = arg3.replace(">", "");
			arg3 = arg3.replace("&", "");
			arg3 = arg3.replace("$", "");

			if(arg3.isEmpty())
			{
				onBypassCommand(player, "_announcepledgewriteform");
				return;
			}

			if(arg3.length() > 3000)
				arg3 = arg3.substring(0, 3000);

			int type = Integer.parseInt(st.nextToken());

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("REPLACE INTO `bbs_clannotice`(clan_id, type, notice) VALUES(?, ?, ?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, type);
				statement.setString(3, arg3);
				statement.execute();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				onBypassCommand(player, "_announcepledgewriteform");
				return;
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			if(type == 1)
				clan.setNotice(arg3.replace("\n", "<br1>"));
			else
				clan.setNotice("");

			player.sendPacket(SystemMsg.YOUR_CLAN_NOTICE_HAS_BEEN_SAVED);
			onBypassCommand(player, "_announcepledgewriteform");
		}
	}

	private class Listener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			Clan clan = player.getClan();
			if(clan == null || clan.getLevel() < 2)
				return;

			if(clan.getNotice() == null)
			{
				Connection con = null;
				PreparedStatement statement = null;
				ResultSet rset = null;
				String notice = "";
				int type = 0;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("SELECT * FROM `bbs_clannotice` WHERE `clan_id` = ? and type != 2");
					statement.setInt(1, clan.getClanId());
					rset = statement.executeQuery();
					if(rset.next())
					{
						notice = rset.getString("notice");
						type = rset.getInt("type");
					}
				}
				catch(Exception e)
				{}
				finally
				{
					DbUtils.closeQuietly(con, statement, rset);
				}

				clan.setNotice(type == 1 ? notice.replace("\n", "<br1>\n") : "");
			}

			if(!clan.getNotice().isEmpty())
			{
				String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/clan_popup.htm", player);
				html = html.replace("%pledge_name%", clan.getName());
				html = html.replace("%content%", clan.getNotice());

				player.sendPacket(new HtmlMessage(0).setHtml(html));
			}
		}
	}

	public static List<Clan> getClanList(String search, boolean byCL)
	{
		ArrayList<Clan> clanList = new ArrayList<Clan>();

		Clan[] clans = ClanTable.getInstance().getClans();
		Arrays.sort(clans, new ClansComparator<Clan>());
		for(Clan clan : clans)
			if(clan.getLevel() > 1)
				clanList.add(clan);

		if(search != null && !search.isEmpty())
		{
			ArrayList<Clan> searchList = new ArrayList<Clan>();
			for(Clan clan : clanList)
				if(byCL && clan.getLeaderName().toLowerCase().contains(search.toLowerCase()))
					searchList.add(clan);
				else if(!byCL && clan.getName().toLowerCase().contains(search.toLowerCase()))
					searchList.add(clan);

			clanList = searchList;
		}

		return clanList;
	}

	private static class ClansComparator<T> implements Comparator<T>
	{
		@Override
		public int compare(Object o1, Object o2)
		{
			if(o1 instanceof Clan && o2 instanceof Clan)
			{
				Clan p1 = (Clan) o1;
				Clan p2 = (Clan) o2;
				return p1.getName().compareTo(p2.getName());
			}
			return 0;
		}
	}
}
