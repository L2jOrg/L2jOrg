package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.model.Player;
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
	protected void writeImpl()
	{
		writeLong(_adena);                                              // Player Adena Count
		writeLong(_freeCoins);                                          // hero coin
		writeByte(_history);                                            // producst list type (0 - store)
		writeInt(_products.size());
		for(ProductItem product : _products)
		{
			writeInt(product.getId());                                // product id
			writeByte(product.getCategory());                          // category 1 - Main (?) 2 - supplies 3 - Cosmetic 4 - Species  5 - enchant
			writeByte(product.getPointsType().ordinal());              // price unit (0 is coin)
			writeInt(product.getPoints(true));                         // price
			writeByte(product.getTabId());                             // tab id
			writeInt(product.getMainCategory());                      // section: 0 - not on front page, 1 - Featured, 2 - Recommended, 4 - Popular
			writeInt((int) (product.getStartTimeSale() / 1000));      // start sale unix date in seconds
			writeInt((int) (product.getEndTimeSale() / 1000));        // end sale unix date in seconds
			writeByte(127);                                      // day week (127 = not daily goods)
			writeByte(product.getStartHour());                         // start hour
			writeByte(product.getStartMin());                          // start min
			writeByte(product.getEndHour());                           // end hour
			writeByte(product.getEndMin());                            // end min
			writeInt(0x00);                                           // stock -1/0
			writeInt(-1);                                             // max stock -1/0
			writeByte(product.getDiscount()); // % скидки
			writeByte(0x00);                                           // Level restriction
			writeByte(0x00);                                           // UNK
			writeInt(0x00);                                           // UNK
			writeInt(0x00);                                           // UNK
			writeInt(0x00);                                           // Repurchase interval (days)
			writeInt(0x00);                                           // Amount (per account)

			writeByte(product.getComponents().size());                 // Related Items
			for(ProductItemComponent component : product.getComponents())
			{
				writeInt(component.getId());
				writeInt((int)component.getCount());
				writeInt(component.getWeight());
				writeInt(component.isDropable() ? 1 : 0);
			}
		}
	}
}