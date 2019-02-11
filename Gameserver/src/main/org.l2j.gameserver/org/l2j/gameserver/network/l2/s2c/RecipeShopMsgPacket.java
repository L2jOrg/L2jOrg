package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.l2j.commons.util.Util.STRING_EMPTY;

public class RecipeShopMsgPacket extends L2GameServerPacket
{
	private int _objectId;
	private String _storeName;

	public RecipeShopMsgPacket(Player player, boolean showName)
	{
		_objectId = player.getObjectId();
		_storeName = showName ? Objects.requireNonNullElse(player.getManufactureName(), STRING_EMPTY) : STRING_EMPTY;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		writeString(_storeName, buffer);
	}
}