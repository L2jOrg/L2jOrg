package l2s.gameserver.templates.item.support;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Bonux
**/
public class EnchantVariation
{
	public static class EnchantLevel
	{
		private int _lvl;
		private double _baseChance;
		private double _magicWeaponChance;
		private double _fullBodyChance;
		private boolean _succVisualEffect;

		public EnchantLevel(int lvl, double baseChance, double magicWeaponChance, double fullBodyChance, boolean succVisualEffect)
		{
			_lvl = lvl;
			_baseChance = baseChance;
			_magicWeaponChance = magicWeaponChance;
			_fullBodyChance = fullBodyChance;
			_succVisualEffect = succVisualEffect;
		}

		public int getLevel()
		{
			return _lvl;
		}

		public double getBaseChance()
		{
			return _baseChance;
		}

		public double getMagicWeaponChance()
		{
			return _magicWeaponChance;
		}

		public double getFullBodyChance()
		{
			return _fullBodyChance;
		}

		public boolean haveSuccessVisualEffect()
		{
			return _succVisualEffect;
		}
	}

	private final int _id;
	private final TIntObjectMap<EnchantLevel> _levels = new TIntObjectHashMap<EnchantLevel>();

	private int _maxLvl;

	public EnchantVariation(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public void addLevel(EnchantLevel level)
	{
		if(_maxLvl < level.getLevel())
			_maxLvl = level.getLevel();

		_levels.put(level.getLevel(), level);
	}

	public EnchantLevel getLevel(int lvl)
	{
		if(lvl > _maxLvl)
			return _levels.get(_maxLvl);
		return _levels.get(lvl);
	}
}