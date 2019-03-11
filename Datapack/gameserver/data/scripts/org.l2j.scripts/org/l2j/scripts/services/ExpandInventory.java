package org.l2j.scripts.services;

import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.handler.bypass.Bypass;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.utils.Functions;

public class ExpandInventory
{
	@Bypass("org.l2j.scripts.services.ExpandInventory:get")
	public void get(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			Functions.show("Сервис отключен.", player);
			return;
		}

		if(player.getInventoryLimit() >= Config.SERVICES_EXPAND_INVENTORY_MAX)
		{
			player.sendMessage("Already max count.");
			return;
		}

		if(player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_INVENTORY_ITEM, Config.SERVICES_EXPAND_INVENTORY_PRICE))
		{
			player.setExpandInventory(player.getExpandInventory() + 1);
			player.setVar("ExpandInventory", String.valueOf(player.getExpandInventory()), -1);
			player.sendMessage("Inventory capacity is now " + player.getInventoryLimit());
		}
		else if(Config.SERVICES_EXPAND_INVENTORY_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);

		show(player, npc, param);
	}

	@Bypass("org.l2j.scripts.services.ExpandInventory:show")
	public void show(Player player, NpcInstance npc, String[] param)
	{
		if(!Config.SERVICES_EXPAND_INVENTORY_ENABLED)
		{
			Functions.show("Сервис отключен.", player);
			return;
		}

		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_EXPAND_INVENTORY_ITEM);

		String out = "";

		out += "<html><body>Расширение инвентаря";
		out += "<br><br><table>";
		out += "<tr><td>Текущий размер:</td><td>" + player.getInventoryLimit() + "</td></tr>";
		out += "<tr><td>Максимальный размер:</td><td>" + Config.SERVICES_EXPAND_INVENTORY_MAX + "</td></tr>";
		out += "<tr><td>Стоимость слота:</td><td>" + Config.SERVICES_EXPAND_INVENTORY_PRICE + " " + item.getName() + "</td></tr>";
		out += "</table><br><br>";
		out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h htmbypass_services.ExpandInventory:get\" value=\"Расширить\">";
		out += "</body></html>";

		Functions.show(out, player);
	}
}