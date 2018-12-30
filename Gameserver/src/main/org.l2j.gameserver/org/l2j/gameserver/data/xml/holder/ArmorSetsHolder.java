package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.ArmorSet;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

import java.util.ArrayList;
import java.util.List;

public final class ArmorSetsHolder extends AbstractHolder
{
	private static final ArmorSetsHolder _instance = new ArmorSetsHolder();
	private final HashIntObjectMap<List<ArmorSet>> _armorSets = new HashIntObjectMap<List<ArmorSet>>();

	public static ArmorSetsHolder getInstance()
	{
		return _instance;
	}

	public void addArmorSet(ArmorSet armorset)
	{
		for(int id : armorset.getChestIds())
		{
			List<ArmorSet> sets = _armorSets.get(id);
			if(sets == null)
				sets = new ArrayList<ArmorSet>();
			sets.add(armorset);
			_armorSets.put(id, sets);
		}

		for(int id : armorset.getLegIds())
		{
			List<ArmorSet> sets = _armorSets.get(id);
			if(sets == null)
				sets = new ArrayList<ArmorSet>();
			sets.add(armorset);
			_armorSets.put(id, sets);
		}

		for(int id : armorset.getHeadIds())
		{
			List<ArmorSet> sets = _armorSets.get(id);
			if(sets == null)
				sets = new ArrayList<ArmorSet>();
			sets.add(armorset);
			_armorSets.put(id, sets);
		}

		for(int id : armorset.getGlovesIds())
		{
			List<ArmorSet> sets = _armorSets.get(id);
			if(sets == null)
				sets = new ArrayList<ArmorSet>();
			sets.add(armorset);
			_armorSets.put(id, sets);
		}

		for(int id : armorset.getFeetIds())
		{
			List<ArmorSet> sets = _armorSets.get(id);
			if(sets == null)
				sets = new ArrayList<ArmorSet>();
			sets.add(armorset);
			_armorSets.put(id, sets);
		}

		for(int id : armorset.getShieldIds())
		{
			List<ArmorSet> sets = _armorSets.get(id);
			if(sets == null)
				sets = new ArrayList<ArmorSet>();
			sets.add(armorset);
			_armorSets.put(id, sets);
		}
	}

	public List<ArmorSet> getArmorSets(int id)
	{
		return _armorSets.get(id);
	}

	@Override
	public int size()
	{
		return _armorSets.size();
	}

	@Override
	public void clear()
	{
		_armorSets.clear();
	}
}