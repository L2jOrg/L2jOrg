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
		writeQ(_adena);                                              // Player Adena Count
		writeQ(_freeCoins);                                          // hero coin
		writeC(_history);                                            // producst list type (0 - store)
		writeD(_products.size());
		for(ProductItem product : _products)
		{
			writeD(product.getId());                                // product id
			writeC(product.getCategory());                          // category 1 - Main (?) 2 - supplies 3 - Cosmetic 4 - Species  5 - enchant
			writeC(product.getPointsType().ordinal());              // price unit (0 is coin)
			writeD(product.getPoints(true));                         // price
			writeC(product.getTabId());                             // tab id
			writeD(product.getMainCategory());                      // section: 0 - not on front page, 1 - Featured, 2 - Recommended, 4 - Popular
			writeD((int) (product.getStartTimeSale() / 1000));      // start sale unix date in seconds
			writeD((int) (product.getEndTimeSale() / 1000));        // end sale unix date in seconds
			writeC(127);                                      // day week (127 = not daily goods)
			writeC(product.getStartHour());                         // start hour
			writeC(product.getStartMin());                          // start min
			writeC(product.getEndHour());                           // end hour
			writeC(product.getEndMin());                            // end min
			writeD(0x00);                                           // stock -1/0
			writeD(-1);                                             // max stock -1/0
			writeC(product.getDiscount()); // % скидки
			writeC(0x00);                                           // Level restriction
			writeC(0x00);                                           // UNK
			writeD(0x00);                                           // UNK
			writeD(0x00);                                           // UNK
			writeD(0x00);                                           // Repurchase interval (days)
			writeD(0x00);                                           // Amount (per account)

			writeC(product.getComponents().size());                 // Related Items
			for(ProductItemComponent component : product.getComponents())
			{
				writeD(component.getId());
				writeD((int)component.getCount());
				writeD(component.getWeight());
				writeD(component.isDropable() ? 1 : 0);
			}
		}
	}
}