package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;

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
