package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

import static java.util.Objects.requireNonNullElse;
import static org.l2j.commons.util.Util.STRING_EMPTY;

public class PrivateStoreBuyMsg extends L2GameServerPacket
{
	private int _objId;
	private String _name;

	public PrivateStoreBuyMsg(Player player, boolean showName)
	{
		_objId = player.getObjectId();
		_name = showName ? requireNonNullElse(player.getBuyStoreName(), STRING_EMPTY) : STRING_EMPTY;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objId);
		writeString(_name, buffer);
	}
}