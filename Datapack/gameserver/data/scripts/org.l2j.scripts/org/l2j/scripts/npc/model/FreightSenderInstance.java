package org.l2j.scripts.npc.model;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.MerchantInstance;
import org.l2j.gameserver.network.l2.s2c.PackageToListPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.WarehouseFunctions;

/**
 * @author VISTALL
 * @date 20:32/16.05.2011
 */
public class FreightSenderInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	public FreightSenderInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.equalsIgnoreCase("deposit_items"))
			player.sendPacket(new PackageToListPacket(player));
		else if(command.equalsIgnoreCase("withdraw_items"))
			WarehouseFunctions.showFreightWindow(player);
		else
			super.onBypassFeedback(player, command);
	}
}
