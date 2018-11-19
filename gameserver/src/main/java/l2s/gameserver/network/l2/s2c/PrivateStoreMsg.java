package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

import org.apache.commons.lang3.StringUtils;

public class PrivateStoreMsg extends L2GameServerPacket
{
	private final int _objId;
	private final String _name;

	/**
	 * Название личного магазина продажи
	 * @param player
	 */
	public PrivateStoreMsg(Player player, boolean showName)
	{
		_objId = player.getObjectId();
		_name = showName ? StringUtils.defaultString(player.getSellStoreName()) : StringUtils.EMPTY;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objId);
		writeS(_name);
	}
}