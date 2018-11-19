package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.utils.Functions;

public class AdminInstance implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_instance,
		admin_instance_id,
		admin_collapse,
		admin_reset_reuse,
		admin_reset_reuse_all,
		admin_set_reuse
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanTeleport)
			return false;

		switch(command)
		{
			case admin_instance:
				listOfInstances(activeChar);
				break;
			case admin_instance_id:
				if(wordList.length > 1)
					listOfCharsForInstance(activeChar, wordList[1]);
				break;
			case admin_collapse:
				if(!activeChar.getReflection().isDefault())
					activeChar.getReflection().collapse();
				else
					activeChar.sendMessage("Cannot collapse default reflection!");
				break;
			case admin_reset_reuse:
				if(wordList.length > 1 && activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
				{
					Player p = activeChar.getTarget().getPlayer();
					p.removeInstanceReuse(Integer.parseInt(wordList[1]));
					Functions.sendDebugMessage(activeChar, "Instance reuse has been removed");
				}
				break;
			case admin_reset_reuse_all:
				if(activeChar.getTarget() != null && activeChar.getTarget().isPlayer())
				{
					Player p = activeChar.getTarget().getPlayer();
					p.removeAllInstanceReuses();
					Functions.sendDebugMessage(activeChar, "All instance reuses has been removed");
				}
				break;
			case admin_set_reuse:
				if(activeChar.getReflection() != null)
					activeChar.getReflection().setReenterTime(System.currentTimeMillis());
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void listOfInstances(Player activeChar)
	{
		HtmlMessage adminReply = new HtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>List of Instances</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br><br>");

		for(Reflection reflection : ReflectionManager.getInstance().getAll())
		{
			if(reflection == null || reflection.isDefault() || reflection.isCollapseStarted())
				continue;
			int countPlayers = 0;
			if(reflection.getPlayers() != null)
				countPlayers = reflection.getPlayers().size();
			replyMSG.append("<a action=\"bypass -h admin_instance_id ").append(reflection.getId()).append(" \">").append(reflection.getName()).append("(").append(countPlayers).append(" players). Id: ").append(reflection.getId()).append("</a><br>");
		}

		replyMSG.append("<button value=\"Refresh\" action=\"bypass -h admin_instance\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void listOfCharsForInstance(Player activeChar, String sid)
	{
		Reflection reflection = ReflectionManager.getInstance().get(Integer.parseInt(sid));

		HtmlMessage adminReply = new HtmlMessage(5);
		StringBuffer replyMSG = new StringBuffer("<html><title>Instance Menu</title><body><br>");
		if(reflection != null)
		{
			replyMSG.append("<table width=260><tr>");
			replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("<td width=180><center>List of players in ").append(reflection.getName()).append("</center></td>");
			replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_instance\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
			replyMSG.append("</tr></table><br><br>");

			for(Player player : reflection.getPlayers())
				replyMSG.append("<a action=\"bypass -h admin_teleportto ").append(player.getName()).append(" \">").append(player.getName()).append("</a><br>");
		}
		else
		{
			replyMSG.append("Instance not active.<br>");
			replyMSG.append("<a action=\"bypass -h admin_instance\">Back to list.</a><br>");
		}

		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
}