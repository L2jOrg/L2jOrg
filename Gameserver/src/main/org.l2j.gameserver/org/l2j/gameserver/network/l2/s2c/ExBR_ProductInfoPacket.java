package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.product.ProductItem;
import org.l2j.gameserver.templates.item.product.ProductItemComponent;
import org.l2j.gameserver.utils.ItemFunctions;

import java.nio.ByteBuffer;

public class ExBR_ProductInfoPacket extends L2GameServerPacket
{
	private final long _adena, _premiumPoints, _freeCoins;
	private final ProductItem _productId;

	public ExBR_ProductInfoPacket(Player player, int id)
	{
		_adena = player.getAdena();
		_premiumPoints = player.getPremiumPoints();
		_freeCoins = ItemFunctions.getItemCount(player, 23805);
		_productId = ProductDataHolder.getInstance().getProduct(id);
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		if(_productId == null)
			return;

		buffer.putInt(_productId.getId()); //product id
		buffer.putInt(_productId.getPoints(true)); // points
		buffer.putInt(_productId.getComponents().size()); //size

		for(ProductItemComponent com : _productId.getComponents())
		{
			buffer.putInt(com.getId());
			buffer.putInt((int)com.getCount());
			buffer.putInt(com.getWeight());
			buffer.putInt(com.isDropable() ? 1 : 0);
		}

		buffer.putLong(_adena);
		buffer.putLong(_premiumPoints);
		buffer.putLong(_freeCoins);
	}
}