package org.l2j.gameserver.model.actor.instances.player;

import org.l2j.gameserver.templates.item.product.ProductItem;

/**
 * @author Bonux
**/
public class ProductHistoryItem
{
	private final ProductItem _product;
	private final int _lastPurchaseTime;

	public ProductHistoryItem(ProductItem product, int lastPurchaseTime)
	{
		_product = product;
		_lastPurchaseTime = lastPurchaseTime;
	}

	public ProductItem getProduct()
	{
		return _product;
	}

	public int getLastPurchaseTime()
	{
		return _lastPurchaseTime;
	}

	@Override
	public String toString()
	{
		return "ProductHistoryItem[product ID=" + _product.getId() + ", last purchase time=" + _lastPurchaseTime + "]";
	}
}