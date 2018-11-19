package services;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.Functions;

public class ExpandCWH
{
	@Bypass("services.ExpandCWH:get")
	public void get(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.SERVICES_EXPAND_CWH_ENABLED)
		{
			Functions.show("Сервис отключен.", player);
			return;
		}

		if(player.getClan() == null)
		{
			player.sendMessage("You must be in clan.");
			return;
		}

		if(player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_CWH_ITEM, Config.SERVICES_EXPAND_CWH_PRICE))
		{
			player.getClan().setWhBonus(player.getClan().getWhBonus() + 1);
			player.sendMessage("Warehouse capacity is now " + (Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()));
		}
		else if(Config.SERVICES_EXPAND_CWH_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);

		show(player, npc, param);
	}

	@Bypass("services.ExpandCWH:show")
	public void show(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			Functions.show("Сервис отключен.", player);
			return;
		}

		if(player.getClan() == null)
		{
			player.sendMessage("You must be in clan.");
			return;
		}

		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_EXPAND_CWH_ITEM);

		String out = "";

		out += "<html><body>Расширение кланового склада";
		out += "<br><br><table>";
		out += "<tr><td>Текущий размер:</td><td>" + (Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()) + "</td></tr>";
		out += "<tr><td>Стоимость слота:</td><td>" + Config.SERVICES_EXPAND_CWH_PRICE + " " + item.getName() + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h htmbypass_services.ExpandCWH:get\" value=\"Расширить\">";
		out += "</body></html>";

		Functions.show(out, player);
	}
}