package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

import java.util.Objects;

import static org.l2j.commons.util.Util.STRING_EMPTY;

public class ExPrivateStoreWholeMsg extends L2GameServerPacket {

	private final int _objId;
	private final String _name;

	public ExPrivateStoreWholeMsg(Player player, boolean showName)
	{
		_objId = player.getObjectId();
		_name = showName ? Objects.requireNonNullElse(player.getPackageSellStoreName(), STRING_EMPTY) : STRING_EMPTY;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_objId);
		writeString(_name);
	}
}