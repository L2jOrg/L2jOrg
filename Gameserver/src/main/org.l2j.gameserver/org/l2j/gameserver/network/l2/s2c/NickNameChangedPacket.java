package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;

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
		writeInt(objectId);
		writeString(title);
	}
}