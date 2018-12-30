package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.SoulCrystal;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author: VISTALL
 * @date:  10:55/08.12.2010
 */
public final class SoulCrystalHolder extends AbstractHolder
{
	private static final SoulCrystalHolder _instance = new SoulCrystalHolder();

	public static SoulCrystalHolder getInstance()
	{
		return _instance;
	}

	private final HashIntObjectMap<SoulCrystal> _crystals = new HashIntObjectMap<SoulCrystal>();

	public void addCrystal(SoulCrystal crystal)
	{
		_crystals.put(crystal.getItemId(), crystal);
	}

	public SoulCrystal getCrystal(int item)
	{
		return _crystals.get(item);
	}

	public SoulCrystal[] getCrystals()
	{
		return _crystals.values().toArray(new SoulCrystal[_crystals.size()]);
	}

	@Override
	public int size()
	{
		return _crystals.size();
	}

	@Override
	public void clear()
	{
		_crystals.clear();
	}
}