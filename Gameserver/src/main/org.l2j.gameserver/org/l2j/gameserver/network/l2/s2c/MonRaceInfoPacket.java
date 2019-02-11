package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class MonRaceInfoPacket extends L2GameServerPacket
{
	private int _unknown1;
	private int _unknown2;
	private NpcInstance[] _monsters;
	private int[][] _speeds;

	public MonRaceInfoPacket(int unknown1, int unknown2, NpcInstance[] monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race
		 * 0 15322 to start race
		 * 13765 -1 in middle of race
		 * -1 0 to end the race
		 */
		_unknown1 = unknown1;
		_unknown2 = unknown2;
		_monsters = monsters;
		_speeds = speeds;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_unknown1);
		buffer.putInt(_unknown2);
		buffer.putInt(8);

		for(int i = 0; i < 8; i++)
		{
			//logger.info.println("MOnster "+(i+1)+" npcid "+_monsters[i].getNpcTemplate().getNpcId());
			buffer.putInt(_monsters[i].getObjectId()); //npcObjectID
			buffer.putInt(_monsters[i].getNpcId() + 1000000); //npcID
			buffer.putInt(14107); //origin X
			buffer.putInt(181875 + 58 * (7 - i)); //origin Y
			buffer.putInt(-3566); //origin Z
			buffer.putInt(12080); //end X
			buffer.putInt(181875 + 58 * (7 - i)); //end Y
			buffer.putInt(-3566); //end Z
			buffer.putDouble(_monsters[i].getCollisionHeight()); //coll. height
			buffer.putDouble(_monsters[i].getCollisionRadius()); //coll. radius
			buffer.putInt(120); // ?? unknown
			for(int j = 0; j < 20; j++)
				buffer.put((byte) (_unknown1 == 0 ? _speeds[i][j] : 0));
			buffer.putInt(0);
			buffer.putInt(0x00); // ? GraciaFinal
		}
	}
}