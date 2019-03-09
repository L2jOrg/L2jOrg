package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExRpItemLink;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class RequestExRqItemLink extends IClientIncomingPacket
{
	private int _objectId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_objectId = packet.getInt();
	}
	
	@Override
	public void runImpl()
	{
		final L2Object object = L2World.getInstance().findObject(_objectId);
		if ((object != null) && object.isItem())
		{
			final L2ItemInstance item = (L2ItemInstance) object;
			if (item.isPublished())
			{
				client.sendPacket(new ExRpItemLink(item));
			}
		}
	}
}
