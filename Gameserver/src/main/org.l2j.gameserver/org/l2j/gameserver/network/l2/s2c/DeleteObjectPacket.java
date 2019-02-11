package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Пример:
 * 08
 * a5 04 31 48 ObjectId
 * 00 00 00 7c unk
 *
 * format  d
 */
public class DeleteObjectPacket extends L2GameServerPacket
{
	private int _objectId;

	public DeleteObjectPacket(GameObject obj)
	{
		_objectId = obj.getObjectId();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null || activeChar.getObjectId() == _objectId)
			return;

		buffer.putInt(_objectId);
		buffer.putInt(0x01); // Что-то странное. Если объект сидит верхом то при 0 он сперва будет ссажен, при 1 просто пропадет.
	}

	@Override
	public String getType()
	{
		return super.getType() + " " + GameObjectsStorage.findObject(_objectId) + " (" + _objectId + ")";
	}
}