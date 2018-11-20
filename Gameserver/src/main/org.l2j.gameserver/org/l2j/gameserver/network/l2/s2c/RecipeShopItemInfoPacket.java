package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;

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
		writeInt(_shopId);
		writeInt(_recipeId);
		writeInt(_curMp);
		writeInt(_maxMp);
		writeInt(_success);
		writeLong(_price);
	}
}