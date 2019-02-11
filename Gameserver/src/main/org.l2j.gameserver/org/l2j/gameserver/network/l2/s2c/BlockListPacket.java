package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Block;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
 */
public class BlockListPacket extends L2GameServerPacket
{
	private Block[] _blockList;

	public BlockListPacket(Player player)
	{
		_blockList = player.getBlockList().values();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_blockList.length);
		for(Block b : _blockList)
		{
			writeString(b.getName(), buffer);
			writeString(b.getMemo(), buffer);
		}
	}
}