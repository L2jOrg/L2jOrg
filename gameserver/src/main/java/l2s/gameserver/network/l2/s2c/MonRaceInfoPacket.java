package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.instances.NpcInstance;

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
		writeD(_unknown1);
		writeD(_unknown2);
		writeD(8);

		for(int i = 0; i < 8; i++)
		{
			//_log.info.println("MOnster "+(i+1)+" npcid "+_monsters[i].getNpcTemplate().getNpcId());
			writeD(_monsters[i].getObjectId()); //npcObjectID
			writeD(_monsters[i].getNpcId() + 1000000); //npcID
			writeD(14107); //origin X
			writeD(181875 + 58 * (7 - i)); //origin Y
			writeD(-3566); //origin Z
			writeD(12080); //end X
			writeD(181875 + 58 * (7 - i)); //end Y
			writeD(-3566); //end Z
			writeF(_monsters[i].getCollisionHeight()); //coll. height
			writeF(_monsters[i].getCollisionRadius()); //coll. radius
			writeD(120); // ?? unknown
			for(int j = 0; j < 20; j++)
				writeC(_unknown1 == 0 ? _speeds[i][j] : 0);
			writeD(0);
			writeD(0x00); // ? GraciaFinal
		}
	}
}