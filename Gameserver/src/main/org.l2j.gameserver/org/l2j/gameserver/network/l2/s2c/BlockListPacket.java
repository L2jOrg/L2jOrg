package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Block;

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
	protected final void writeImpl()
	{
		writeInt(_blockList.length);
		for(Block b : _blockList)
		{
			writeString(b.getName());
			writeString(b.getMemo());
		}
	}
}