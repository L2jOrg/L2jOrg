package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;
import java.util.List;

import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.data.xml.holder.ProductDataHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ExBR_BuyProductPacket;
import org.l2j.gameserver.network.l2.s2c.ExBR_GamePointPacket;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.product.ProductItem;
import org.l2j.gameserver.templates.item.product.ProductItemComponent;
import org.l2j.gameserver.templates.item.product.ProductPointsType;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.Log;

public class RequestExBR_BuyProduct extends L2GameClientPacket
{
	private int _productId;
	private int _count;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_productId = buffer.getInt();
		_count = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();

		if(activeChar == null)
			return;

		if(_count > 99 || _count <= 0)
			return;

		ProductItem product = ProductDataHolder.getInstance().getProduct(_productId);
		if(product == null)
		{
			activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_WRONG_PRODUCT);
			return;
		}

		if(!product.isOnSale() || (System.currentTimeMillis() < product.getStartTimeSale()) || (System.currentTimeMillis() > product.getEndTimeSale()))
		{
			activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_SALE_PERIOD_ENDED);
			return;
		}

		final int pointsRequired = product.getPoints(true) * _count;
		if(pointsRequired <= 0)
		{
			activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_WRONG_PRODUCT);
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
			if(product.getPointsType() == ProductPointsType.POINTS)
			{
				if(pointsRequired > activeChar.getPremiumPoints())
				{
					activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
					return;
				}
			}
			else if(product.getPointsType() == ProductPointsType.ADENA)
			{
				if(!ItemFunctions.haveItem(activeChar, Items.ADENA, pointsRequired))
				{
					activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
					return;
				}
			}
			else if(product.getPointsType() == ProductPointsType.FREE_COIN)
			{
				if(!ItemFunctions.haveItem(activeChar, 23805, pointsRequired))
				{
					activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
					return;
				}
			}
			else
			{
				activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_WRONG_PRODUCT);
				return;
			}

			int totalWeight = 0;
			for(ProductItemComponent com : product.getComponents())
				totalWeight += com.getWeight();

			totalWeight *= _count; //увеличиваем вес согласно количеству

			int totalCount = 0;

			for(ProductItemComponent com : product.getComponents())
			{
				ItemTemplate item = ItemHolder.getInstance().getTemplate(com.getId());
				if(item == null)
				{
					activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_WRONG_PRODUCT);
					return; //what
				}
				totalCount += item.isStackable() ? 1 : com.getCount() * _count;
			}

			if(!activeChar.getInventory().validateCapacity(totalCount) || !activeChar.getInventory().validateWeight(totalWeight))
			{
				activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_INVENTORY_FULL);
				return;
			}

			if(product.getPointsType() == ProductPointsType.POINTS)
			{
				if(!activeChar.reducePremiumPoints(pointsRequired))
				{
					activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
					return;
				}
			}
			else if(product.getPointsType() == ProductPointsType.ADENA)
			{
				if(!ItemFunctions.deleteItem(activeChar, Items.ADENA, pointsRequired, false))
				{
					activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
					return;
				}
			}
			else if(product.getPointsType() == ProductPointsType.FREE_COIN)
			{
				if(!ItemFunctions.deleteItem(activeChar, 23805, pointsRequired, false))
				{
					activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_NOT_ENOUGH_POINTS);
					return;
				}
			}
			else
			{
				activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_WRONG_PRODUCT);
				return;
			}

			activeChar.getProductHistoryList().onPurchaseProduct(product);

			for(ProductItemComponent $comp : product.getComponents())
			{
				List<ItemInstance> items = ItemFunctions.addItem(activeChar, $comp.getId(), $comp.getCount() * _count, false);
				for(ItemInstance item : items)
					Log.LogItem(activeChar, Log.ItemMallBuy, item);
			}

			activeChar.updateRecentProductList(_productId);
			activeChar.sendPacket(new ExBR_GamePointPacket(activeChar));
			activeChar.sendPacket(ExBR_BuyProductPacket.RESULT_OK);
			activeChar.sendChanges();
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}
	}
}