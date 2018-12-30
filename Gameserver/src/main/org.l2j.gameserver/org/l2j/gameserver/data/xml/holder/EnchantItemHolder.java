package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.item.support.EnchantScroll;
import org.l2j.gameserver.templates.item.support.EnchantVariation;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public final class EnchantItemHolder extends AbstractHolder
{
	private static final EnchantItemHolder _instance = new EnchantItemHolder();

	private IntObjectMap<EnchantScroll> _enchantScrolls = new HashIntObjectMap<EnchantScroll>();
	private IntObjectMap<EnchantVariation> _enchantVariations = new HashIntObjectMap<EnchantVariation>();

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