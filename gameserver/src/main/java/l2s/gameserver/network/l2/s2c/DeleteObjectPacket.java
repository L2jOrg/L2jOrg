package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;

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
	protected final void writeImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getObjectId() == _objectId)
			return;

		writeD(_objectId);
		writeD(0x01); // Что-то странное. Если объект сидит верхом то при 0 он сперва будет ссажен, при 1 просто пропадет.
	}

	@Override
	public String getType()
	{
		return super.getType() + " " + GameObjectsStorage.findObject(_objectId) + " (" + _objectId + ")";
	}
}