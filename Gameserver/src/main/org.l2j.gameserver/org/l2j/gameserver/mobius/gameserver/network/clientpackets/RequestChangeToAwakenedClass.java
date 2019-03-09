package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerChangeToAwakenedClass;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestChangeToAwakenedClass extends IClientIncomingPacket
{
	private boolean _change;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_change = packet.getInt() == 1;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (_change)
		{
			EventDispatcher.getInstance().notifyEventAsync(new OnPlayerChangeToAwakenedClass(player), player);
		}
		else
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
}
