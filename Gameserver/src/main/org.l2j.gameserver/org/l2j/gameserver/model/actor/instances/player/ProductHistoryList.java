package org.l2j.gameserver.model.actor.instances.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.l2j.gameserver.data.dao.CharacterProductHistoryDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.item.product.ProductItem;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public class ProductHistoryList
{
	private static class ProductComparator implements Comparator<ProductHistoryItem>
	{
		private static final ProductComparator _instance = new ProductComparator();

		private static ProductComparator getInstance()
		{
			return _instance;
		}

		@Override
		public int compare(ProductHistoryItem o1, ProductHistoryItem o2)
		{
			if(o2.getLastPurchaseTime() == o1.getLastPurchaseTime())
				return o1.getProduct().getId() - o2.getProduct().getId();
			return o2.getLastPurchaseTime() - o1.getLastPurchaseTime();
		}
	}

	public static final int MAX_ITEMS_SIZE = Byte.MAX_VALUE;

	private IntObjectMap<ProductHistoryItem> _productHistoryMap = new HashIntObjectMap<ProductHistoryItem>();
	private final Player _owner;

	public ProductHistoryList(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		_productHistoryMap = CharacterProductHistoryDAO.getInstance().select(_owner);
	}

	public boolean add(ProductHistoryItem historyItem)
	{
		if(!CharacterProductHistoryDAO.getInstance().replace(_owner, historyItem))
			return false;

		_productHistoryMap.put(historyItem.getProduct().getId(), historyItem);
		return true;
	}

	public ProductHistoryItem get(int productId)
	{
		return _productHistoryMap.get(productId);
	}

	public boolean remove(int productId)
	{
		return _productHistoryMap.remove(productId) != null;
	}

	public boolean contains(int productId)
	{
		return _productHistoryMap.containsKey(productId);
	}

	public int size()
	{
		return _productHistoryMap.size();
	}

	public Collection<ProductHistoryItem> values()
	{
		List<ProductHistoryItem> items = new ArrayList<ProductHistoryItem>(_productHistoryMap.values());
		Collections.sort(items, ProductComparator.getInstance());
		return items;
	}

	public Collection<ProductItem> productValues()
	{
		List<ProductItem> products = new ArrayList<ProductItem>();

		Collection<ProductHistoryItem> items = values();
		for(ProductHistoryItem item : items)
			products.add(item.getProduct());

		return products;
	}

	public boolean isEmpty()
	{
		return _productHistoryMap.isEmpty();
	}

	@Override
	public String toString()
	{
		return "ProductHistoryList[owner=" + _owner.getName() + "]";
	}

	public void onPurchaseProduct(ProductItem product)
	{
		if(!contains(product.getId()))
			product.setBoughtCount(product.getBoughtCount() + 1);

		add(new ProductHistoryItem(product, (int) (System.currentTimeMillis() / 1000)));
	}
}