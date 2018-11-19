package services;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Functions;

public class NickColor
{
	@Bypass("services.NickColor:list")
	public void list(Player player, NpcInstance npc, String[] param)
	{
		StringBuilder append = new StringBuilder();
		append.append("You can change nick color for small price ").append(Config.SERVICES_CHANGE_NICK_COLOR_PRICE).append(" ").append(ItemHolder.getInstance().getTemplate(Config.SERVICES_CHANGE_NICK_COLOR_ITEM).getName()).append(".");
		append.append("<br>Possible colors:<br>");
		for(String color : Config.SERVICES_CHANGE_NICK_COLOR_LIST)
			append.append("<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h htmbypass_services.NickColor:change ").append(color).append("\"><font color=\"").append(color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)).append("\">").append(color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2)).append("</font></button>");
		append.append("<Button ALIGN=LEFT ICON=\"NORMAL\" action=\"bypass -h htmbypass_services.NickColor:change FFFFFF\"><font color=\"FFFFFF\">Revert to default (free)</font></button>");
		Functions.show(append.toString(), player, null);
	}

	@Bypass("services.NickColor:change")
	public void change(Player player, NpcInstance npc, String[] param)
	{
		if(param[0].equalsIgnoreCase("FFFFFF"))
		{
			player.setNameColor(Integer.decode("0xFFFFFF"));
			player.broadcastUserInfo(true);
			return;
		}

		if(player.getInventory().destroyItemByItemId(Config.SERVICES_CHANGE_NICK_COLOR_ITEM, Config.SERVICES_CHANGE_NICK_COLOR_PRICE))
		{
			player.setNameColor(Integer.decode("0x" + param[0]));
			player.broadcastUserInfo(true);
		}
		else if(Config.SERVICES_CHANGE_NICK_COLOR_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}
}