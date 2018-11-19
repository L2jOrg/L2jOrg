package l2s.gameserver.network.l2.c2s;

import java.util.List;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExBR_BuyProductPacket;
import l2s.gameserver.network.l2.s2c.ExBR_GamePointPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;
import l2s.gameserver.templates.item.product.ProductPointsType;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;

public class RequestExBR_BuyProduct extends L2GameClientPacket
{
	private int _productId;
	private int _count;

	@Override
	protected void readImpl()
	{
		_productId = readD();
		_count = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();

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
				if(!ItemFunctions.haveItem(activeChar, ItemTemplate.ITEM_ID_ADENA, pointsRequired))
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
				if(!ItemFunctions.deleteItem(activeChar, ItemTemplate.ITEM_ID_ADENA, pointsRequired, false))
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