package handler.voicecommands;

import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.database.mysql;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.utils.Functions;

public class CWHPrivileges extends ScriptVoiceCommandHandler
{
	private String[] COMMANDS = new String[] { "clan" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if(activeChar.getClan() == null)
			return false;
		if(command.equals("clan"))
		{
			if(Config.ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER && !activeChar.isClanLeader())
				return false;
			if(!((activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS))
				return false;
			if(args != null)
			{
				String[] param = args.split(" ");
				if(param.length > 0)
					if(param[0].equalsIgnoreCase("allowwh") && param.length > 1)
					{
						UnitMember cm = activeChar.getClan().getAnyMember(param[1]);
						if(cm != null && cm.getPlayer() != null) // цель онлайн
						{
							if(cm.getPlayer().getVarBoolean("canWhWithdraw"))
							{
								cm.getPlayer().unsetVar("canWhWithdraw");
								activeChar.sendMessage("Privilege removed successfully");
							}
							else
							{
								cm.getPlayer().setVar("canWhWithdraw", "1", -1);
								activeChar.sendMessage("Privilege given successfully");
							}
						}
						else if(cm != null) // цель оффлайн
						{
							int state = mysql.simple_get_int("value", "character_variables", "obj_id=" + cm.getObjectId() + " AND name LIKE 'canWhWithdraw'");
							if(state > 0)
							{
								mysql.set("DELETE FROM `character_variables` WHERE obj_id=" + cm.getObjectId() + " AND name LIKE 'canWhWithdraw' LIMIT 1");
								activeChar.sendMessage("Privilege removed successfully");
							}
							else
							{
								mysql.set("INSERT INTO character_variables  (obj_id, name, value, expire_time) VALUES (" + cm.getObjectId() + ",'canWhWithdraw','1',-1)");
								activeChar.sendMessage("Privilege given successfully");
							}
						}
						else
							activeChar.sendMessage("Player not found.");
					}
					else if(param[0].equalsIgnoreCase("list"))
					{
						StringBuilder sb = new StringBuilder("SELECT `obj_id` FROM `character_variables` WHERE `obj_id` IN (");
						List<UnitMember> members = activeChar.getClan().getAllMembers();
						for(int i = 0; i < members.size(); i++)
						{
							sb.append(members.get(i).getObjectId());
							if(i < members.size() - 1)
								sb.append(",");
						}
						sb.append(") AND `name`='canWhWithdraw'");
						List<Object> list = mysql.get_array(sb.toString());
						sb = new StringBuilder("<html><body>Clan CP (.clan)<br><br><table>");
						for(Object o_id : list)
							for(UnitMember m : members)
								if(m.getObjectId() == Integer.parseInt(o_id.toString()))
									sb.append("<tr><td width=10></td><td width=60>").append(m.getName()).append("</td><td width=20><button width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan allowwh ").append(m.getName()).append("\" value=\"Remove\">").append("<br></td></tr>");
						sb.append("<tr><td width=10></td><td width=20><button width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan\" value=\"Back\"></td></tr></table></body></html>");
						Functions.show(sb.toString(), activeChar);
						return true;
					}
			}
			String dialog = HtmCache.getInstance().getHtml("scripts/services/clan.htm", activeChar);
			if(!Config.SERVICES_EXPAND_CWH_ENABLED)
				dialog = dialog.replaceFirst("%whextprice%", "service disabled");
			else
				dialog = dialog.replaceFirst("%whextprice%", Config.SERVICES_EXPAND_CWH_PRICE + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_EXPAND_CWH_ITEM).getName());
			Functions.show(dialog, activeChar);
			return true;
		}
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}