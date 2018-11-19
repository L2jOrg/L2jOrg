package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.product.ProductItem;

/**
 * @author Bonux
 **/
public final class ProductDataHolder extends AbstractHolder
{
	private static final ProductDataHolder _instance = new ProductDataHolder();
	private final TIntObjectMap<ProductItem> _products = new TIntObjectHashMap<ProductItem>();

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
		return _products.valueCollection();
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