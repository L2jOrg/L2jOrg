package l2s.gameserver.templates.item.support;

import l2s.gameserver.templates.item.ItemGrade;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

import java.util.Set;

public class EnchantScroll
{
	private final int _itemId;
	private final int _maxEnchant;
	private final EnchantType _type;
	private final Set<ItemGrade> _grades;
	private final FailResultType _resultType;
	private final int _enchantDropCount;
	private final int _variation;
	private boolean _showFailEffect;
	private final int _minEnchantStep;
	private final int _maxEnchantStep;

	private IntSet _items = Containers.EMPTY_INT_SET;

	public EnchantScroll(int itemId, int variation, int maxEnchant, EnchantType type, Set<ItemGrade> grades, FailResultType resultType, int enchantDropCount, boolean showFailEffect, int minEnchantStep, int maxEnchantStep)
	{
		_itemId = itemId;
		_maxEnchant = maxEnchant;
		_type = type;
		_grades = grades;
		_resultType = resultType;
		_enchantDropCount = enchantDropCount;
		_variation = variation;
		_showFailEffect = showFailEffect;
		_minEnchantStep = minEnchantStep;
		_maxEnchantStep = maxEnchantStep;
	}

	public void addItemId(int id)
	{
		if(_items.isEmpty())
			_items = new HashIntSet();

		_items.add(id);
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getMaxEnchant()
	{
		return _maxEnchant;
	}

	public boolean containsGrade(ItemGrade grade)
	{
		return _grades.contains(grade);
	}

	public IntSet getItems()
	{
		return _items;
	}

	public EnchantType getType()
	{
		return _type;
	}

	public int getVariationId()
	{
		return _variation;
	}

	public FailResultType getResultType()
	{
		return _resultType;
	}

	public int getEnchantDropCount()
	{
		return _enchantDropCount;
	}

	public boolean showFailEffect()
	{
		return _showFailEffect;
	}

	public int getMinEnchantStep()
	{
		return _minEnchantStep;
	}

	public int getMaxEnchantStep()
	{
		return _maxEnchantStep;
	}
}