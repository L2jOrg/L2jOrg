package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.instances.NpcInstance;

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
	protected final void writeImpl()
	{
		writeInt(_unknown1);
		writeInt(_unknown2);
		writeInt(8);

		for(int i = 0; i < 8; i++)
		{
			//logger.info.println("MOnster "+(i+1)+" npcid "+_monsters[i].getNpcTemplate().getNpcId());
			writeInt(_monsters[i].getObjectId()); //npcObjectID
			writeInt(_monsters[i].getNpcId() + 1000000); //npcID
			writeInt(14107); //origin X
			writeInt(181875 + 58 * (7 - i)); //origin Y
			writeInt(-3566); //origin Z
			writeInt(12080); //end X
			writeInt(181875 + 58 * (7 - i)); //end Y
			writeInt(-3566); //end Z
			writeDouble(_monsters[i].getCollisionHeight()); //coll. height
			writeDouble(_monsters[i].getCollisionRadius()); //coll. radius
			writeInt(120); // ?? unknown
			for(int j = 0; j < 20; j++)
				writeByte(_unknown1 == 0 ? _speeds[i][j] : 0);
			writeInt(0);
			writeInt(0x00); // ? GraciaFinal
		}
	}
}