package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
 */
public class ExChangeToAwakenedClass extends L2GameServerPacket
{
	private int _classId;

	public ExChangeToAwakenedClass(Player player, NpcInstance npc, int classId)
	{
		_classId = classId;
		player.setLastNpc(npc);
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_classId);
	}
}
