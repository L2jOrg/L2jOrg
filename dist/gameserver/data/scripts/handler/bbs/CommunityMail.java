package handler.bbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMailArrivedPacket;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommunityMail extends ScriptsCommunityHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityMail.class);
	private static final int MESSAGE_PER_PAGE = 10;

	private final Listener _listener = new Listener();

	@Override
	public void onInit()
	{
		super.onInit();

		if(Config.BBS_ENABLED)
		{
			CharListenerList.addGlobal(_listener);
			_log.info("CommunityBoard: Private Mail service loaded.");
		}
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_maillist_",
			"_mailsearch_",
			"_mailread_",
			"_maildelete_"
		};
	}

	@Override
	protected void doBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		player.setSessionVar("add_fav", null);
		if("maillist".equals(cmd))
		{
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int byTitle = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken() : "";

			String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_mail_list.htm", player);

			int inbox = 0;
			int send = 0;

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT count(*) as cnt FROM `bbs_mail` WHERE `box_type` = 0 and `to_object_id` = ?");
				statement.setInt(1, player.getObjectId());
				rset = statement.executeQuery();
				if(rset.next())
					inbox = rset.getInt("cnt");
				statement.close();

				statement = con.prepareStatement("SELECT count(*) as cnt FROM `bbs_mail` WHERE `box_type` = 1 and `from_object_id` = ?");
				statement.setInt(1, player.getObjectId());
				rset = statement.executeQuery();
				if(rset.next())
					send = rset.getInt("cnt");
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}

			List<MailData> mailList = null;

			switch(type)
			{
				case 0:
					html = html.replace("%inbox_link%", "[&$917;]");
					html = html.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
					html = html.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
					html = html.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
					html = html.replace("%TREE%", "&$917;");
					html = html.replace("%writer_header%", "&$911;");
					mailList = getMailList(player, type, search, byTitle == 1);
					break;
				case 1:
					html = html.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
					html = html.replace("%sentbox_link%", "[&$918;]");
					html = html.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
					html = html.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
					html = html.replace("%TREE%", "&$918;");
					html = html.replace("%writer_header%", "&$909;");
					mailList = getMailList(player, type, search, byTitle == 1);
					break;
				case 2:
					html = html.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
					html = html.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
					html = html.replace("%archive_link%", "[&$919;]");
					html = html.replace("%temp_archive_link%", "<a action=\"bypass _maillist_3_1_0_\">[&$920;]</a>");
					html = html.replace("%TREE%", "&$919;");
					html = html.replace("%writer_header%", "&$911;");
					break;
				case 3:
					html = html.replace("%inbox_link%", "<a action=\"bypass _maillist_0_1_0_\">[&$917;]</a>");
					html = html.replace("%sentbox_link%", "<a action=\"bypass _maillist_1_1_0_\">[&$918;]</a>");
					html = html.replace("%archive_link%", "<a action=\"bypass _maillist_2_1_0_\">[&$919;]</a>");
					html = html.replace("%temp_archive_link%", "[&$920;]");
					html = html.replace("%TREE%", "&$920;");
					html = html.replace("%writer_header%", "&$909;");
					break;
			}

			if(mailList != null)
			{
				int start = (page - 1) * MESSAGE_PER_PAGE;
				int end = Math.min(page * MESSAGE_PER_PAGE, mailList.size());

				if(page == 1)
				{
					html = html.replace("%ACTION_GO_LEFT%", "");
					html = html.replace("%GO_LIST%", "");
					html = html.replace("%NPAGE%", "1");
				}
				else
				{
					html = html.replace("%ACTION_GO_LEFT%", "bypass _maillist_" + type + "_" + (page - 1) + "_" + byTitle + "_" + search);
					html = html.replace("%NPAGE%", String.valueOf(page));
					StringBuilder goList = new StringBuilder("");
					for(int i = page > 10 ? page - 10 : 1; i < page; i++)
						goList.append("<td><a action=\"bypass _maillist_").append(type).append("_").append(i).append("_").append(byTitle).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");

					html = html.replace("%GO_LIST%", goList.toString());
				}

				int pages = Math.max(mailList.size() / MESSAGE_PER_PAGE, 1);
				if(mailList.size() > pages * MESSAGE_PER_PAGE)
					pages++;

				if(pages > page)
				{
					html = html.replace("%ACTION_GO_RIGHT%", "bypass _maillist_" + type + "_" + (page + 1) + "_" + byTitle + "_" + search);
					int ep = Math.min(page + 10, pages);
					StringBuilder goList = new StringBuilder("");
					for(int i = page + 1; i <= ep; i++)
						goList.append("<td><a action=\"bypass _maillist_").append(type).append("_").append(i).append("_").append(byTitle).append("_").append(search).append("\"> ").append(i).append(" </a> </td>\n\n");

					html = html.replace("%GO_LIST2%", goList.toString());
				}
				else
				{
					html = html.replace("%ACTION_GO_RIGHT%", "");
					html = html.replace("%GO_LIST2%", "");
				}

				StringBuilder ml = new StringBuilder("");
				String tpl = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_mailtpl.htm", player);
				for(int i = start; i < end; i++)
				{
					MailData md = mailList.get(i);
					String mailtpl = tpl;
					mailtpl = mailtpl.replace("%action%", "bypass _mailread_" + md.messageId + "_" + type + "_" + page + "_" + byTitle + "_" + search);
					mailtpl = mailtpl.replace("%writer%", md.author);
					mailtpl = mailtpl.replace("%title%", md.title);
					mailtpl = mailtpl.replace("%post_date%", md.postDate);
					ml.append(mailtpl);
				}

				html = html.replace("%MAIL_LIST%", ml.toString());
			}
			else
			{
				html = html.replace("%ACTION_GO_LEFT%", "");
				html = html.replace("%GO_LIST%", "");
				html = html.replace("%NPAGE%", "1");
				html = html.replace("%GO_LIST2%", "");
				html = html.replace("%ACTION_GO_RIGHT%", "");
				html = html.replace("%MAIL_LIST%", "");
			}

			html = html.replace("%mailbox_type%", String.valueOf(type));
			html = html.replace("%incomming_mail_no%", String.valueOf(inbox));
			html = html.replace("%sent_mail_no%", String.valueOf(send));
			html = html.replace("%archived_mail_no%", "0");
			html = html.replace("%temp_mail_no%", "0");

			ShowBoardPacket.separateAndSend(html, player);
		}
		else if("mailread".equals(cmd))
		{
			int messageId = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			int page = Integer.parseInt(st.nextToken());
			int byTitle = Integer.parseInt(st.nextToken());
			String search = st.hasMoreTokens() ? st.nextToken() : "";

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT * FROM `bbs_mail` WHERE `message_id` = ? and `box_type` = ? and `to_object_id` = ?");
				statement.setInt(1, messageId);
				statement.setInt(2, type);
				statement.setInt(3, player.getObjectId());
				rset = statement.executeQuery();
				if(rset.next())
				{
					String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/bbs_mail_read.htm", player);

					switch(type)
					{
						case 0:
							html = html.replace("%TREE%", "<a action=\"bypass _maillist_0_1_0_\">&$917;</a>");
							break;
						case 1:
							html = html.replace("%TREE%", "<a action=\"bypass _maillist_1_1_0__\">&$918;</a>");
							break;
						case 2:
							html = html.replace("%TREE%", "<a action=\"bypass _maillist_2_1_0__\">&$919;</a>");
							break;
						case 3:
							html = html.replace("%TREE%", "<a action=\"bypass _maillist_3_1_0__\">&$920;</a>");
							break;
					}

					html = html.replace("%writer%", rset.getString("from_name"));
					html = html.replace("%post_date%", String.format("%1$te-%1$tm-%1$tY", new Date(rset.getInt("post_date") * 1000L)));
					html = html.replace("%del_date%", String.format("%1$te-%1$tm-%1$tY", new Date((rset.getInt("post_date") + 90 * 24 * 60 * 60) * 1000L)));
					html = html.replace("%char_name%", rset.getString("to_name"));
					html = html.replace("%title%", rset.getString("title"));
					html = html.replace("%CONTENT%", rset.getString("message").replace("\n", "<br1>"));
					html = html.replace("%GOTO_LIST_LINK%", "bypass _maillist_" + type + "_" + page + "_" + byTitle + "_" + search);
					html = html.replace("%message_id%", String.valueOf(messageId));
					html = html.replace("%mailbox_type%", String.valueOf(type));
					player.setSessionVar("add_fav", bypass + "&" + rset.getString("title"));

					statement.close();

					statement = con.prepareStatement("UPDATE `bbs_mail` SET `read` = `read` + 1 WHERE message_id = ?");
					statement.setInt(1, messageId);
					statement.execute();

					ShowBoardPacket.separateAndSend(html, player);
					return;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}

			onBypassCommand(player, "_maillist_" + type + "_" + page + "_" + byTitle + "_" + search);
		}
		else if("maildelete".equals(cmd))
		{
			int type = Integer.parseInt(st.nextToken());
			int messageId = Integer.parseInt(st.nextToken());

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("DELETE FROM `bbs_mail` WHERE `box_type` = ? and `message_id` = ? and `to_object_id` = ?");
				statement.setInt(1, type);
				statement.setInt(2, messageId);
				statement.setInt(3, player.getObjectId());
				statement.execute();
			}
			catch(Exception e)
			{}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			onBypassCommand(player, "_maillist_" + type + "_1_0_");
		}
	}

	@Override
	protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		if("mailsearch".equals(cmd))
			onBypassCommand(player, "_maillist_" + st.nextToken() + "_1_" + ("Title".equals(arg3) ? "1_" : "0_") + (arg5 != null ? arg5 : ""));
	}

	private class Listener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();

				statement = con.prepareStatement("SELECT * FROM `bbs_mail` WHERE `box_type` = 0 and `read` = 0 and `to_object_id` = ?");
				statement.setInt(1, player.getObjectId());
				rset = statement.executeQuery();
				if(rset.next())
				{
					player.sendPacket(SystemMsg.YOUVE_GOT_MAIL);
					player.sendPacket(ExMailArrivedPacket.STATIC);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}
		}
	}

	private static List<MailData> getMailList(Player player, int type, String search, boolean byTitle)
	{
		List<MailData> list = new ArrayList<MailData>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM `bbs_mail` WHERE `to_object_id` = ? and `post_date` < ?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, (int) (System.currentTimeMillis() / 1000) - 90 * 24 * 60 * 60);
			statement.execute();
			statement.close();

			String column_name = type == 0 ? "from_name" : "to_name";
			statement = con.prepareStatement("SELECT * FROM `bbs_mail` WHERE `box_type` = ? and `to_object_id` = ? ORDER BY post_date DESC");
			statement.setInt(1, type);
			statement.setInt(2, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
				if(search.isEmpty())
					list.add(new MailData(rset.getString(column_name), rset.getString("title"), rset.getInt("post_date"), rset.getInt("message_id")));
				else if(byTitle && !search.isEmpty() && rset.getString("title").toLowerCase().contains(search.toLowerCase()))
					list.add(new MailData(rset.getString(column_name), rset.getString("title"), rset.getInt("post_date"), rset.getInt("message_id")));
				else if(!byTitle && !search.isEmpty() && rset.getString(column_name).toLowerCase().contains(search.toLowerCase()))
					list.add(new MailData(rset.getString(column_name), rset.getString("title"), rset.getInt("post_date"), rset.getInt("message_id")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return list;
	}

	private static class MailData
	{
		public String author;
		public String title;
		public String postDate;
		public int messageId;

		public MailData(String _author, String _title, int _postDate, int _messageId)
		{
			author = _author;
			title = _title;
			postDate = String.format(String.format("%1$te-%1$tm-%1$tY", new Date(_postDate * 1000L)));
			messageId = _messageId;
		}
	}
}
