package org.l2j.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.item.product.ProductItem;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 **/
public final class ProductDataHolder extends AbstractHolder
{
	private static final ProductDataHolder _instance = new ProductDataHolder();
	private final IntObjectMap<ProductItem> _products = new HashIntObjectMap<ProductItem>();

	public static ProductDataHolder getInstance()
	{
		return _instance;
	}

	public void addProduct(ProductItem product)
	{
		_products.put(product.getId(), product);
	}

	public Collection<ProductItem> getProducts()
	{
		return _products.values();
	}

	public Collection<ProductItem> getProductsOnSale(Player player)
	{
		List<ProductItem> products = new ArrayList<ProductItem>();
		for(ProductItem product : getProducts())
		{
			if(!product.isOnSale())
				continue;

			if(System.currentTimeMillis() < product.getStartTimeSale())
				continue;

			if(System.currentTimeMillis() > product.getEndTimeSale())
				continue;

			if(product.getLocationId() != -1 && product.getLocationId() != player.getLocationId())
				continue;

			products.add(product);
		}
		return products;
	}

	public ProductItem getProduct(int id)
	{
		return _products.get(id);
	}

	@Override
	public int size()
	{
		return _products.size();
	}

	@Override
	public void clear()
	{
		_products.clear();
	}
}