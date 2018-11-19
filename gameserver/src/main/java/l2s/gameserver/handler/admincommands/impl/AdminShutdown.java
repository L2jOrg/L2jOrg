package l2s.gameserver.handler.admincommands.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.math.NumberUtils;
import l2s.commons.lang.StatsUtils;
import l2s.gameserver.Config;
import l2s.gameserver.GameTimeController;
import l2s.gameserver.Shutdown;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.HtmlMessage;

public class AdminShutdown implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_server_shutdown,
		admin_server_restart,
		admin_server_abort
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanRestart)
			return false;

		try
		{
			switch(command)
			{
				case admin_server_shutdown:
					Shutdown.getInstance().schedule(NumberUtils.toInt(wordList[1], -1), Shutdown.SHUTDOWN);
					break;
				case admin_server_restart:
					Shutdown.getInstance().schedule(NumberUtils.toInt(wordList[1], -1), Shutdown.RESTART);
					break;
				case admin_server_abort:
					Shutdown.getInstance().cancel();
					break;
			}
		}
		catch(Exception e)
		{
			sendHtmlForm(activeChar);
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void sendHtmlForm(Player activeChar)
	{
		HtmlMessage adminReply = new HtmlMessage(5);

		int t = GameTimeController.getInstance().getGameTime();
		int h = t / 60;
		int m = t % 60;
		SimpleDateFormat format = new SimpleDateFormat("h:mm a");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Server Management Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<table width=300>");
		replyMSG.append("<tr><td width=100>Players Online:</td><td width=200>" + GameObjectsStorage.getPlayers().size() + "</td></tr>");
		replyMSG.append("<tr><td width=100>Used Memory:</td><td width=200>" + StatsUtils.getMemUsedMb() + "</td></tr>");
		replyMSG.append("<tr><td width=100>Server Rates:</td><td width=200></td></tr>");

		replyMSG.append("<tr><td width=100>&nbsp;&nbsp;&nbsp;XP:</td><td width=200>x" + Config.RATE_XP_BY_LVL[1]);
		if(Config.RATE_XP_BY_LVL[1] != Config.RATE_XP_BY_LVL[Config.ALT_MAX_LEVEL])
			replyMSG.append(" - " + Config.RATE_XP_BY_LVL[Config.ALT_MAX_LEVEL]);
		replyMSG.append("</td></tr>");

		replyMSG.append("<tr><td width=100>&nbsp;&nbsp;&nbsp;SP:</td><td width=200>x" + Config.RATE_SP_BY_LVL[1]);
		if(Config.RATE_SP_BY_LVL[1] != Config.RATE_SP_BY_LVL[Config.ALT_MAX_LEVEL])
			replyMSG.append(" - " + Config.RATE_SP_BY_LVL[Config.ALT_MAX_LEVEL]);
		replyMSG.append("</td></tr>");

		replyMSG.append("<tr><td width=100>&nbsp;&nbsp;&nbsp;Adena:</td><td width=200>x" + Config.RATE_DROP_ADENA_BY_LVL[1]);
		if(Config.RATE_DROP_ADENA_BY_LVL[1] != Config.RATE_DROP_ADENA_BY_LVL[Config.ALT_MAX_LEVEL])
			replyMSG.append(" - " + Config.RATE_DROP_ADENA_BY_LVL[Config.ALT_MAX_LEVEL]);
		replyMSG.append("</td></tr>");

		replyMSG.append("<tr><td width=100>&nbsp;&nbsp;&nbsp;Drop:</td><td width=200>x" + Config.RATE_DROP_ITEMS_BY_LVL[1]);
		if(Config.RATE_DROP_ITEMS_BY_LVL[1] != Config.RATE_DROP_ITEMS_BY_LVL[Config.ALT_MAX_LEVEL])
			replyMSG.append(" - " + Config.RATE_DROP_ITEMS_BY_LVL[Config.ALT_MAX_LEVEL]);
		replyMSG.append("</td></tr>");

		replyMSG.append("<tr><td width=100>&nbsp;&nbsp;&nbsp;Spoil:</td><td width=200>x" + Config.RATE_DROP_SPOIL_BY_LVL[1]);
		if(Config.RATE_DROP_SPOIL_BY_LVL[1] != Config.RATE_DROP_SPOIL_BY_LVL[Config.ALT_MAX_LEVEL])
			replyMSG.append(" - " + Config.RATE_DROP_SPOIL_BY_LVL[Config.ALT_MAX_LEVEL]);
		replyMSG.append("</td></tr>");

		replyMSG.append("<tr><td width=100>Game Time:</td><td width=200>" + format.format(cal.getTime()) + "</td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td>Enter in seconds the time till the server shutdowns bellow:</td></tr>");
		replyMSG.append("<br>");
		replyMSG.append("<tr><td><center>Seconds till: <edit var=\"shutdown_time\" width=60></center></td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<button value=\"Shutdown\" action=\"bypass -h admin_server_shutdown $shutdown_time\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Restart\" action=\"bypass -h admin_server_restart $shutdown_time\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>");
		replyMSG.append("<button value=\"Abort\" action=\"bypass -h admin_server_abort\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</td></tr></table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
}