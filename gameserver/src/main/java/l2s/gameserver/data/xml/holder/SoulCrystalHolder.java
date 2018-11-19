package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.SoulCrystal;

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

	private final TIntObjectHashMap<SoulCrystal> _crystals = new TIntObjectHashMap<SoulCrystal>();

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
		return _crystals.values(new SoulCrystal[_crystals.size()]);
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