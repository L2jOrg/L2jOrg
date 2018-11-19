package l2s.gameserver.model.base;

import java.util.ArrayList;
import java.util.List;

public class MultiSellEntry
{
	private int _entryId;
	private List<MultiSellIngredient> _ingredients = new ArrayList<MultiSellIngredient>();
	private List<MultiSellIngredient> _production = new ArrayList<MultiSellIngredient>();
	private long _tax;

	public MultiSellEntry()
	{}

	public MultiSellEntry(int id)
	{
		_entryId = id;
	}

	/**
	 * @param entryId The entryId to set.
	 */
	public void setEntryId(int entryId)
	{
		_entryId = entryId;
	}

	/**
	 * @return Returns the entryId.
	 */
	public int getEntryId()
	{
		return _entryId;
	}

	/**
	 * @param ingredient The ingredients to set.
	 */
	public void addIngredient(MultiSellIngredient ingredient)
	{
		_ingredients.add(ingredient);
	}

	/**
	 * @return Returns the ingredients.
	 */
	public List<MultiSellIngredient> getIngredients()
	{
		return _ingredients;
	}

	/**
	 * @param ingredient The ingredients to set.
	 */
	public void addProduct(MultiSellIngredient ingredient)
	{
		_production.add(ingredient);
	}

	/**
	 * @return Returns the ingredients.
	 */
	public List<MultiSellIngredient> getProduction()
	{
		return _production;
	}

	public long getTax()
	{
		return _tax;
	}

	public void setTax(long tax)
	{
		_tax = tax;
	}

	@Override
	public int hashCode()
	{
		return _entryId;
	}

	@Override
	public MultiSellEntry clone()
	{
		MultiSellEntry ret = new MultiSellEntry(_entryId);
		for(MultiSellIngredient i : _ingredients)
			ret.addIngredient(i.clone());
		for(MultiSellIngredient i : _production)
			ret.addProduct(i.clone());
		return ret;
	}
}