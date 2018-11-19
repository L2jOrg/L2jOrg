package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

import org.apache.commons.lang3.StringUtils;

public class PrivateStoreBuyMsg extends L2GameServerPacket
{
	private int _objId;
	private String _name;

	/**
	 * Название личного магазина покупки
	 * @param player
	 */
	public PrivateStoreBuyMsg(Player player, boolean showName)
	{
		_objId = player.getObjectId();
		_name = showName ? StringUtils.defaultString(player.getBuyStoreName()) : StringUtils.EMPTY;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objId);
		writeS(_name);
	}
}