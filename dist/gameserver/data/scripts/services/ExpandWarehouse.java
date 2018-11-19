package services;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.handler.bypass.Bypass;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.Functions;

public class ExpandWarehouse
{
	@Bypass("services.ExpandWarehouse:get")
	public void get(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			Functions.show("Сервис отключен.", player);
			return;
		}

		if(player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_WAREHOUSE_ITEM, Config.SERVICES_EXPAND_WAREHOUSE_PRICE))
		{
			player.setExpandWarehouse(player.getExpandWarehouse() + 1);
			player.setVar("ExpandWarehouse", String.valueOf(player.getExpandWarehouse()), -1);
			player.sendMessage("Warehouse capacity is now " + player.getWarehouseLimit());
		}
		else if(Config.SERVICES_EXPAND_WAREHOUSE_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);

		show(player, npc, param);
	}

	@Bypass("services.ExpandWarehouse:show")
	public void show(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.SERVICES_EXPAND_WAREHOUSE_ENABLED)
		{
			Functions.show("Сервис отключен.", player);
			return;
		}

		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_EXPAND_WAREHOUSE_ITEM);

		String out = "";

		out += "<html><body>Расширение склада";
		out += "<br><br><table>";
		out += "<tr><td>Текущий размер:</td><td>" + player.getWarehouseLimit() + "</td></tr>";
		out += "<tr><td>Стоимость слота:</td><td>" + Config.SERVICES_EXPAND_WAREHOUSE_PRICE + " " + item.getName() + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h htmbypass_services.ExpandWarehouse:get\" value=\"Расширить\">";
		out += "</body></html>";

		Functions.show(out, player);
	}
}