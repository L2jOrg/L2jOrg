package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_shopId);
		buffer.putInt(_recipeId);
		buffer.putInt(_curMp);
		buffer.putInt(_maxMp);
		buffer.putInt(_success);
		buffer.putLong(_price);
	}
}