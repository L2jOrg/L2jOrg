package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.PackageSendableListPacket;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 20:35/16.05.2011
 */
public class RequestPackageSendableItemList extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{
		_objectId = buffer.getInt();
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new PackageSendableListPacket(_objectId, player));
	}
}
