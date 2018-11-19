package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.item.support.EnchantScroll;
import l2s.gameserver.templates.item.support.EnchantVariation;

/**
 * @author Bonux
**/
public final class EnchantItemHolder extends AbstractHolder
{
	private static final EnchantItemHolder _instance = new EnchantItemHolder();

	private TIntObjectMap<EnchantScroll> _enchantScrolls = new TIntObjectHashMap<EnchantScroll>();
	private TIntObjectMap<EnchantVariation> _enchantVariations = new TIntObjectHashMap<EnchantVariation>();

	public static EnchantItemHolder getInstance()
	{
		return _instance;
	}

	public void addEnchantScroll(EnchantScroll enchantScroll)
	{
		_enchantScrolls.put(enchantScroll.getItemId(), enchantScroll);
	}

	public EnchantScroll getEnchantScroll(int id)
	{
		return _enchantScrolls.get(id);
	}

	public void addEnchantVariation(EnchantVariation variation)
	{
		_enchantVariations.put(variation.getId(), variation);
	}

	public EnchantVariation getEnchantVariation(int id)
	{
		return _enchantVariations.get(id);
	}

	@Override
	public int size()
	{
		return _enchantScrolls.size();
	}

	@Override
	public void clear()
	{
		_enchantScrolls.clear();
		_enchantVariations.clear();
	}
}