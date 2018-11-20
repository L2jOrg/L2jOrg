package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collection;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.item.product.ProductItem;
import org.l2j.gameserver.templates.item.product.ProductItemComponent;

public class ExBR_RecentProductListPacket extends L2GameServerPacket
{
	private Collection<ProductItem> _products;

	public ExBR_RecentProductListPacket(Player activeChar)
	{
		_products = new ArrayList<>();
		int[] products = activeChar.getRecentProductList();
		if(products != null)
		{
			for(int productId : products)
			{
				ProductItem product = ProductDataHolder.getInstance().getProduct(productId);
				if(product == null)
					continue;

				_products.add(product);
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x01); // UNK
		writeD(_products.size());

		for(ProductItem product : _products)
		{
			writeD(product.getId()); //product id
			writeH(product.getCategory()); //category 1 - enchant 2 - supplies  3 - decoration 4 - package 5 - other
			writeD(product.getPoints(true)); //points
			writeD(product.getTabId()); // show tab 2-th group - 1 показывает окошко про итем
			writeD(Rnd.get(0, 4)); // категория главной страницы (0 - не показывать на главное (дефолт), 1 - верхнее окно, 2 - рекомендуемый товар, 3 - неизвестно, 4 - популярные товары)  // Glory Days 488
			writeD((int) (product.getStartTimeSale() / 1000)); // start sale unix date in seconds
			writeD((int) (product.getEndTimeSale() / 1000)); // end sale unix date in seconds
			writeC(127); // day week (127 = not daily goods)
			writeC(product.getStartHour()); // start hour
			writeC(product.getStartMin()); // start min
			writeC(product.getEndHour()); // end hour
			writeC(product.getEndMin()); // end min
			writeD(0); // stock
			writeD(-1); // max stock
			// Glory Days 488
			writeD(product.getDiscount()); // % скидки
			//writeD(1); // Увеличение уровня (Дефолт = 1)
			writeD(product.getComponents().size()); // Количество итемов в продукте.
			for(ProductItemComponent component : product.getComponents())
			{
				writeD(component.getId()); //item id
				writeD((int)component.getCount()); //quality
				writeD(component.getWeight()); //weight
				writeD(component.isDropable() ? 1 : 0); //0 - dont drop/trade
			}
		}
	}
}