package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.item.WeaponFightType;
import org.l2j.gameserver.templates.item.support.variation.VariationGroup;
import org.l2j.gameserver.templates.item.support.variation.VariationStone;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 */
public final class VariationDataHolder extends AbstractHolder
{
	private static final VariationDataHolder _instance = new VariationDataHolder();

	private IntObjectMap<IntObjectMap<VariationStone>> _stones = new HashIntObjectMap<>(WeaponFightType.VALUES.length);
	private IntObjectMap<VariationGroup> _groups = new HashIntObjectMap<VariationGroup>();

	public static VariationDataHolder getInstance()
	{
		return _instance;
	}

	public void addStone(WeaponFightType weaponType, VariationStone stone)
	{
		IntObjectMap<VariationStone> stones = _stones.get(weaponType.ordinal());
		if(stones == null)
		{
			stones = new HashIntObjectMap<VariationStone>();
			_stones.put(weaponType.ordinal(), stones);
		}

		stones.put(stone.getId(), stone);
	}

	public VariationStone getStone(WeaponFightType weaponType, int id)
	{
		IntObjectMap<VariationStone> stones = _stones.get(weaponType.ordinal());
		if(stones == null)
			return null;

		return stones.get(id);
	}

	public void addGroup(VariationGroup group)
	{
		_groups.put(group.getId(), group);
	}

	public VariationGroup getGroup(int id)
	{
		return _groups.get(id);
	}

	@Override
	public int size()
	{
		return _stones.size() + _groups.size();
	}

	@Override
	public void clear()
	{
		_stones.clear();
		_groups.clear();
	}
}