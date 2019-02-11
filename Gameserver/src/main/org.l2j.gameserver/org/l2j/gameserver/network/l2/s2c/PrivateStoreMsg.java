package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.l2j.commons.util.Util.STRING_EMPTY;

public class PrivateStoreMsg extends L2GameServerPacket
{
	private final int _objId;
	private final String _name;

	public PrivateStoreMsg(Player player, boolean showName) {
		_objId = player.getObjectId();
		_name = showName  ? Objects.requireNonNullElse(player.getSellStoreName(), STRING_EMPTY) : STRING_EMPTY;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objId);
		writeString(_name, buffer);
	}
}