package npc.model;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.MerchantInstance;
import l2s.gameserver.network.l2.s2c.PackageToListPacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.WarehouseFunctions;

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
