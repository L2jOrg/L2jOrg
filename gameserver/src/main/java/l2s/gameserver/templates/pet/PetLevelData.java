package l2s.gameserver.templates.pet;

import l2s.gameserver.templates.StatsSet;

/**
 * @author Bonux
 */
public class PetLevelData
{
	private final int _maxMeal;
	private final long _exp;
	private final int _expType;
	private final int _battleMealConsume;
	private final int _normalMealConsume;
	private final double _pAtk;
	private final double _pDef;
	private final double _mAtk;
	private final double _mDef;
	private final double _hp;
	private final double _mp;
	private final double _hpRegen;
	private final double _mpRegen;
	private final int[] _food;
	private final int _hungryLimit;
	private final int _soulshotCount;
	private final int _spiritshotCount;
	private final int _maxLoad;

	//Статты в режиме маунта
	private final int _battleMealConsumeOnRide;
	private final int _normalMealConsumeOnRide;
	private final int _walkSpdOnRide;
	private final int _runSpdOnRide;
	private final int _waterWalkSpdOnRide;
	private final int _waterRunSpdOnRide;
	private final int _flyWalkSpdOnRide;
	private final int _flyRunSpdOnRide;
	private final int _atkSpdOnRide;
	private final double _pAtkOnRide;
	private final double _mAtkOnRide;
	private final int _maxHpOnRide;
	private final int _maxMpOnRide;

	public PetLevelData(StatsSet set)
	{
		_maxMeal = set.getInteger("max_meal");
		_exp = set.getLong("exp");
		_expType = set.getInteger("exp_type");
		_battleMealConsume = set.getInteger("battle_meal_consume");
		_normalMealConsume = set.getInteger("normal_meal_consume");
		_hungryLimit = set.getInteger("hungry_limit");
		_soulshotCount = set.getInteger("soulshot_count");
		_spiritshotCount = set.getInteger("spiritshot_count");
		_pAtk = set.getDouble("p_atk");
		_pDef = set.getDouble("p_def");
		_mAtk = set.getDouble("m_atk");
		_mDef = set.getDouble("m_def");
		_hp = set.getDouble("hp");
		_mp = set.getDouble("mp");
		_hpRegen = set.getDouble("hp_regen");
		_mpRegen = set.getDouble("mp_regen");
		_food = set.getIntegerArray("food", new int[0]);
		_maxLoad = set.getInteger("max_load");
		_battleMealConsumeOnRide = set.getInteger("battle_meal_consume_on_ride", 0);
		_normalMealConsumeOnRide = set.getInteger("normal_meal_consume_on_ride", 0);
		_walkSpdOnRide = set.getInteger("walk_speed_on_ride", 0);
		_runSpdOnRide = set.getInteger("run_speed_on_ride", 0);
		_waterWalkSpdOnRide = set.getInteger("water_walk_speed_on_ride", 0);
		_waterRunSpdOnRide = set.getInteger("water_run_speed_on_ride", 0);
		_flyWalkSpdOnRide = set.getInteger("fly_walk_speed_on_ride", 0);
		_flyRunSpdOnRide = set.getInteger("fly_run_speed_on_ride", 0);
		_atkSpdOnRide = set.getInteger("attack_speed_on_ride", 0);
		_pAtkOnRide = set.getDouble("p_attack_on_ride", 0.);
		_mAtkOnRide = set.getDouble("m_attack_on_ride", 0.);
		_maxHpOnRide = set.getInteger("max_hp_on_ride", 0);
		_maxMpOnRide = set.getInteger("max_mp_on_ride", 0);
	}

	public int getMaxMeal()
	{
		return _maxMeal;
	}

	public long getExp()
	{
		return _exp;
	}

	public int getExpType()
	{
		return _expType;
	}

	public int getBattleMealConsume()
	{
		return _battleMealConsume;
	}

	public int getNormalMealConsume()
	{
		return _normalMealConsume;
	}

	public double getPAtk()
	{
		return _pAtk;
	}

	public double getPDef()
	{
		return _pDef;
	}

	public double getMAtk()
	{
		return _mAtk;
	}

	public double getMDef()
	{
		return _mDef;
	}

	public double getHP()
	{
		return _hp;
	}

	public double getMP()
	{
		return _mp;
	}

	public double getHPRegen()
	{
		return _hpRegen;
	}

	public double getMPRegen()
	{
		return _mpRegen;
	}

	public int[] getFood()
	{
		return _food;
	}

	public int getHungryLimit()
	{
		return _hungryLimit;
	}

	public int getSoulshotCount()
	{
		return _soulshotCount;
	}

	public int getSpiritshotCount()
	{
		return _spiritshotCount;
	}

	public int getMaxLoad()
	{
		return _maxLoad;
	}

	public int getBattleMealConsumeOnRide()
	{
		return _battleMealConsumeOnRide;
	}

	public int getNormalMealConsumeOnRide()
	{
		return _normalMealConsumeOnRide;
	}

	public int getWalkSpdOnRide()
	{
		return _walkSpdOnRide;
	}

	public int getRunSpdOnRide()
	{
		return _runSpdOnRide;
	}

	public int getWaterWalkSpdOnRide()
	{
		return _waterWalkSpdOnRide;
	}

	public int getWaterRunSpdOnRide()
	{
		return _waterRunSpdOnRide;
	}

	public int getFlyWalkSpdOnRide()
	{
		return _flyWalkSpdOnRide;
	}

	public int getFlyRunSpdOnRide()
	{
		return _flyRunSpdOnRide;
	}

	public int getAtkSpdOnRide()
	{
		return _atkSpdOnRide;
	}

	public double getPAtkOnRide()
	{
		return _pAtkOnRide;
	}

	public double getMAtkOnRide()
	{
		return _mAtkOnRide;
	}

	public int getMaxHpOnRide()
	{
		return _maxHpOnRide;
	}

	public int getMaxMpOnRide()
	{
		return _maxMpOnRide;
	}
}