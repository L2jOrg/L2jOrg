package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import org.apache.commons.lang3.StringUtils;

public class RecipeShopMsgPacket extends L2GameServerPacket
{
	private int _objectId;
	private String _storeName;

	public RecipeShopMsgPacket(Player player, boolean showName)
	{
		_objectId = player.getObjectId();
		_storeName = showName ? StringUtils.defaultString(player.getManufactureName()) : StringUtils.EMPTY;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeS(_storeName);
	}
}