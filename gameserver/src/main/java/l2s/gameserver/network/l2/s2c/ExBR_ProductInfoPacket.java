package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;
import l2s.gameserver.utils.ItemFunctions;

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

		writeD(_productId.getId()); //product id
		writeD(_productId.getPoints(true)); // points
		writeD(_productId.getComponents().size()); //size

		for(ProductItemComponent com : _productId.getComponents())
		{
			writeD(com.getId());
			writeD((int)com.getCount());
			writeD(com.getWeight());
			writeD(com.isDropable() ? 1 : 0);
		}

		writeQ(_adena);
		writeQ(_premiumPoints);
		writeQ(_freeCoins);
	}
}