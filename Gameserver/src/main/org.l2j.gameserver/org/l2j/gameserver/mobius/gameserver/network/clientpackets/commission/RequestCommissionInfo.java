package org.l2j.gameserver.mobius.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;

import java.nio.ByteBuffer;

/**
 * @author NosBit
 */
public class RequestCommissionInfo extends IClientIncomingPacket
{
	private int _itemObjectId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_itemObjectId = packet.getInt();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!CommissionManager.isPlayerAllowedToInteract(player))
		{
			client.sendPacket(ExCloseCommission.STATIC_PACKET);
			return;
		}
		
		final L2ItemInstance itemInstance = player.getInventory().getItemByObjectId(_itemObjectId);
		if (itemInstance != null)
		{
			client.sendPacket(player.getLastCommissionInfos().getOrDefault(itemInstance.getId(), ExResponseCommissionInfo.EMPTY));
		}
		else
		{
			client.sendPacket(ExResponseCommissionInfo.EMPTY);
		}
	}
}
