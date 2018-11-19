package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

public class NickNameChangedPacket extends L2GameServerPacket
{
	private final int objectId;
	private final String title;

	public NickNameChangedPacket(Creature cha)
	{
		objectId = cha.getObjectId();
		title = cha.getTitle();
	}

	@Override
	protected void writeImpl()
	{
		writeD(objectId);
		writeS(title);
	}
}