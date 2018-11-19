package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

/**
 * dddddQ
 */
public class RecipeShopItemInfoPacket extends L2GameServerPacket
{
	private int _recipeId, _shopId, _curMp, _maxMp;
	private int _success = 0xFFFFFFFF;
	private long _price;

	public RecipeShopItemInfoPacket(Player activeChar, Player manufacturer, int recipeId, long price, int success)
	{
		_recipeId = recipeId;
		_shopId = manufacturer.getObjectId();
		_price = price;
		_success = success;
		_curMp = (int) manufacturer.getCurrentMp();
		_maxMp = manufacturer.getMaxMp();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_shopId);
		writeD(_recipeId);
		writeD(_curMp);
		writeD(_maxMp);
		writeD(_success);
		writeQ(_price);
	}
}