package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.item.product.ProductItem;
import org.l2j.gameserver.templates.item.product.ProductItemComponent;
import org.l2j.gameserver.utils.ItemFunctions;

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
	protected void writeImpl()
	{
		if(_productId == null)
			return;

		writeInt(_productId.getId()); //product id
		writeInt(_productId.getPoints(true)); // points
		writeInt(_productId.getComponents().size()); //size

		for(ProductItemComponent com : _productId.getComponents())
		{
			writeInt(com.getId());
			writeInt((int)com.getCount());
			writeInt(com.getWeight());
			writeInt(com.isDropable() ? 1 : 0);
		}

		writeLong(_adena);
		writeLong(_premiumPoints);
		writeLong(_freeCoins);
	}
}