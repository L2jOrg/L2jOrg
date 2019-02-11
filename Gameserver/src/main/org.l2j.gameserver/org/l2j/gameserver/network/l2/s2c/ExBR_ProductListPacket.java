package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.product.ProductItem;
import org.l2j.gameserver.templates.item.product.ProductItemComponent;
import org.l2j.gameserver.utils.ItemFunctions;

/**
 * upgradet to Ertheia [603] by Bonux
 **/
public class ExBR_ProductListPacket extends L2GameServerPacket
{
	private final long _adena;
	private final long _freeCoins;
	private final boolean _history;
	private final List<ProductItem> _products = new ArrayList<ProductItem>();

	public ExBR_ProductListPacket(Player player, boolean history)
	{
		_adena = player.getAdena();
		_freeCoins = ItemFunctions.getItemCount(player, 23805);
		_history = history;
		if (history)
			_products.addAll(player.getProductHistoryList().productValues());
		else
		{
			_products.addAll(ProductDataHolder.getInstance().getProductsOnSale(player));
			Collections.sort(_products);
		}
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putLong(_adena);                                              // Player Adena Count
		buffer.putLong(_freeCoins);                                          // hero coin
		buffer.put((byte) (_history ? 1  : 0));                                            // producst list type (0 - store)
		buffer.putInt(_products.size());
		for(ProductItem product : _products)
		{
			buffer.putInt(product.getId());                                // product id
			buffer.put((byte)product.getCategory());                          // category 1 - Main (?) 2 - supplies 3 - Cosmetic 4 - Species  5 - enchant
			buffer.put((byte)product.getPointsType().ordinal());              // price unit (0 is coin)
			buffer.putInt(product.getPoints(true));                         // price
			buffer.put((byte)product.getTabId());                             // tab id
			buffer.putInt(product.getMainCategory());                      // section: 0 - not on front page, 1 - Featured, 2 - Recommended, 4 - Popular
			buffer.putInt((int) (product.getStartTimeSale() / 1000));      // start sale unix date in seconds
			buffer.putInt((int) (product.getEndTimeSale() / 1000));        // end sale unix date in seconds
			buffer.put((byte)127);                                      // day week (127 = not daily goods)
			buffer.put((byte)product.getStartHour());                         // start hour
			buffer.put((byte)product.getStartMin());                          // start min
			buffer.put((byte)product.getEndHour());                           // end hour
			buffer.put((byte)product.getEndMin());                            // end min
			buffer.putInt(0x00);                                           // stock -1/0
			buffer.putInt(-1);                                             // max stock -1/0
			buffer.put((byte)product.getDiscount()); // % скидки
			buffer.put((byte)0x00);                                           // Level restriction
			buffer.put((byte)0x00);                                           // UNK
			buffer.putInt(0x00);                                           // UNK
			buffer.putInt(0x00);                                           // UNK
			buffer.putInt(0x00);                                           // Repurchase interval (days)
			buffer.putInt(0x00);                                           // Amount (per account)

			buffer.put((byte)product.getComponents().size());                 // Related Items
			for(ProductItemComponent component : product.getComponents())
			{
				buffer.putInt(component.getId());
				buffer.putInt((int)component.getCount());
				buffer.putInt(component.getWeight());
				buffer.putInt(component.isDropable() ? 1 : 0);
			}
		}
	}
}