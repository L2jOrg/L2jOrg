package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class ExBaseAttributeCancelResult extends L2GameServerPacket
{
	private boolean _result;
	private int _objectId;
	private Element _element;

	public ExBaseAttributeCancelResult(boolean result, ItemInstance item, Element element)
	{
		_result = result;
		_objectId = item.getObjectId();
		_element = element;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_result ? 1 : 0);
		buffer.putInt(_objectId);
		buffer.putInt(_element.getId());
	}
}