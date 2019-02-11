package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

//@Deprecated
public class ExBrExtraUserInfo extends L2GameServerPacket
{
	private int _objectId;
	private int _effect3;
	private int _lectureMark;

	public ExBrExtraUserInfo(Player cha)
	{
		_objectId = cha.getObjectId();
		_effect3 = 0/*cha.getAbnormalEffect3()*/;
		_lectureMark = cha.getLectureMark();
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId); //object id of player
		buffer.putInt(_effect3); // event effect id
		buffer.put((byte)_lectureMark);
	}
}