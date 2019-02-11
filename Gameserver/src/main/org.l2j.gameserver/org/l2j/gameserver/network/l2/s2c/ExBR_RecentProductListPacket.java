package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x01); // UNK
		buffer.putInt(_products.size());

		for(ProductItem product : _products)
		{
			buffer.putInt(product.getId()); //product id
			buffer.putShort((short) product.getCategory()); //category 1 - enchant 2 - supplies  3 - decoration 4 - package 5 - other
			buffer.putInt(product.getPoints(true)); //points
			buffer.putInt(product.getTabId()); // show tab 2-th group - 1 показывает окошко про итем
			buffer.putInt(Rnd.get(0, 4)); // категория главной страницы (0 - не показывать на главное (дефолт), 1 - верхнее окно, 2 - рекомендуемый товар, 3 - неизвестно, 4 - популярные товары)  // Glory Days 488
			buffer.putInt((int) (product.getStartTimeSale() / 1000)); // start sale unix date in seconds
			buffer.putInt((int) (product.getEndTimeSale() / 1000)); // end sale unix date in seconds
			buffer.put((byte)127); // day week (127 = not daily goods)
			buffer.put((byte)product.getStartHour()); // start hour
			buffer.put((byte)product.getStartMin()); // start min
			buffer.put((byte)product.getEndHour()); // end hour
			buffer.put((byte)product.getEndMin()); // end min
			buffer.putInt(0); // stock
			buffer.putInt(-1); // max stock
			// Glory Days 488
			buffer.putInt(product.getDiscount()); // % скидки
			//buffer.putInt(1); // Увеличение уровня (Дефолт = 1)
			buffer.putInt(product.getComponents().size()); // Количество итемов в продукте.
			for(ProductItemComponent component : product.getComponents())
			{
				buffer.putInt(component.getId()); //item id
				buffer.putInt((int)component.getCount()); //quality
				buffer.putInt(component.getWeight()); //weight
				buffer.putInt(component.isDropable() ? 1 : 0); //0 - dont drop/trade
			}
		}
	}
}