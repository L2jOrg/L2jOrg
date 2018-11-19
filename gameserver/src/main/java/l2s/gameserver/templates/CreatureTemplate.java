package l2s.gameserver.templates;

import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

public class CreatureTemplate
{
	private static int[] EMPTY_ATTRIBUTES = new int[6];

	private final StatsSet _statsSet;

	private final int _baseINT;
	private final int _baseSTR;
	private final int _baseCON;
	private final int _baseMEN;
	private final int _baseDEX;
	private final int _baseWIT;

	private final int _baseAtkRange;
	private final int _baseAttackRadius;
	private final int _baseAttackAngle;
	private final int _baseRandDam;

	private double _baseHpMax;
	private final double _baseCpMax;
	private final double _baseMpMax;

	/** HP Regen base */
	private final double _baseHpReg;

	/** MP Regen base */
	private final double _baseMpReg;

	/** CP Regen base */
	private final double _baseCpReg;

	private double _basePAtk;
	private double _baseMAtk;
	private double _basePDef;
	private double _baseMDef;
	private final double _basePAtkSpd;
	private final double _baseMAtkSpd;
	private final double _baseShldDef;
	private final double _baseShldRate;
	private final double _basePCritRate;
	private final double _baseMCritRate;
	private final double _baseRunSpd;
	private final double _baseWalkSpd;
	private final double _baseWaterRunSpd;
	private final double _baseWaterWalkSpd;
	private final double _baseFlyRunSpd;
	private final double _baseFlyWalkSpd;

	private final int[] _baseAttributeAttack;
	private final int[] _baseAttributeDefence;

	private final double _collisionRadius;
	private final double _collisionHeight;

	private final WeaponType _baseAttackType;

	private final int _physicalAbnormalResist;
	private final int _magicAbnormalResist;

	public CreatureTemplate(StatsSet set)
	{
		_statsSet = set;
		_baseINT = set.getInteger("baseINT", 1);
		_baseSTR = set.getInteger("baseSTR", 1);
		_baseCON = set.getInteger("baseCON", 1);
		_baseMEN = set.getInteger("baseMEN", 1);
		_baseDEX = set.getInteger("baseDEX", 1);
		_baseWIT = set.getInteger("baseWIT", 1);
		_baseHpMax = set.getDouble("baseHpMax", 0);
		_baseCpMax = set.getDouble("baseCpMax", 0);
		_baseMpMax = set.getDouble("baseMpMax", 0);
		_baseHpReg = set.getDouble("baseHpReg", 1.);
		_baseCpReg = set.getDouble("baseCpReg", 1.);
		_baseMpReg = set.getDouble("baseMpReg", 1.);
		_basePAtk = set.getDouble("basePAtk", 0);
		_baseMAtk = set.getDouble("baseMAtk", 0);
		_basePDef = set.getDouble("basePDef", 0);
		_baseMDef = set.getDouble("baseMDef", 0);
		_basePAtkSpd = set.getDouble("basePAtkSpd", 0);
		_baseMAtkSpd = set.getDouble("baseMAtkSpd", 333);
		_baseShldDef = set.getDouble("baseShldDef", 0);
		_baseAtkRange = set.getInteger("baseAtkRange", 0);

		String[] damageRange = set.getString("damage_range", "").split(";");
		if(damageRange.length >= 4)
		{
			_baseAttackRadius = Integer.parseInt(damageRange[2]);
			_baseAttackAngle = Integer.parseInt(damageRange[3]);
		}
		else
		{
			_baseAttackRadius = 26;
			_baseAttackAngle = 120;
		}

		_baseRandDam = set.getInteger("baseRandDam", 0);
		_baseShldRate = set.getDouble("baseShldRate", 0);
		_basePCritRate = set.getDouble("basePCritRate", 0);
		_baseMCritRate = set.getDouble("baseMCritRate", 0);
		_baseRunSpd = set.getDouble("baseRunSpd", 0);
		_baseWalkSpd = set.getDouble("baseWalkSpd", 0);
		_baseWaterRunSpd = set.getDouble("baseWaterRunSpd", 50);
		_baseWaterWalkSpd = set.getDouble("baseWaterWalkSpd", 50);
		_baseFlyRunSpd = set.getDouble("baseFlyRunSpd", 0);
		_baseFlyWalkSpd = set.getDouble("baseFlyWalkSpd", 0);
		_baseAttributeAttack = set.getIntegerArray("baseAttributeAttack", EMPTY_ATTRIBUTES);
		_baseAttributeDefence = set.getIntegerArray("baseAttributeDefence",  EMPTY_ATTRIBUTES); // set.getInteger("level", 1) >= 80 ? (set.getInteger("level", 1) < 90 ? HALF_ATTRIBUTES : FULL_ATTRIBUTES) :
		_collisionRadius = set.getDoubleArray("collision_radius", new double[]{ 5 })[0];
		_collisionHeight = set.getDoubleArray("collision_height", new double[]{ 5 })[0];
		_baseAttackType = WeaponType.valueOf(set.getString("baseAttackType", "FIST").toUpperCase());

		_physicalAbnormalResist = set.getInteger("physical_abnormal_resist", 10);
		_magicAbnormalResist = set.getInteger("magic_abnormal_resist", 10);
	}

	public StatsSet getStatsSet()
	{
		return _statsSet;
	}

	public int getId()
	{
		return 0;
	}

	public int getBaseINT()
	{
		return _baseINT;
	}

	public int getBaseSTR()
	{
		return _baseSTR;
	}

	public int getBaseCON()
	{
		return _baseCON;
	}

	public int getBaseMEN()
	{
		return _baseMEN;
	}

	public int getBaseDEX()
	{
		return _baseDEX;
	}

	public int getBaseWIT()
	{
		return _baseWIT;
	}

	public double getBaseHpMax(int level)
	{
		return _baseHpMax;
	}

	public double getBaseMpMax(int level)
	{
		return _baseMpMax;
	}

	public double getBaseCpMax(int level)
	{
		return _baseCpMax;
	}

	public double getBaseHpReg(int level)
	{
		return _baseHpReg;
	}

	public double getBaseMpReg(int level)
	{
		return _baseMpReg;
	}

	public double getBaseCpReg(int level)
	{
		return _baseCpReg;
	}

	public double getBasePAtk()
	{
		return _basePAtk;
	}

	public double getBaseMAtk()
	{
		return _baseMAtk;
	}

	public double getBasePDef()
	{
		return _basePDef;
	}

	public double getBaseMDef()
	{
		return _baseMDef;
	}

	public double getBasePAtkSpd()
	{
		return _basePAtkSpd;
	}

	public double getBaseMAtkSpd()
	{
		return _baseMAtkSpd;
	}

	public double getBaseShldDef()
	{
		return _baseShldDef;
	}

	public int getBaseAtkRange()
	{
		return _baseAtkRange;
	}

	public int getBaseAttackRadius()
	{
		return _baseAttackRadius;
	}

	public int getBaseAttackAngle()
	{
		return _baseAttackAngle;
	}

	public int getBaseRandDam()
	{
		return _baseRandDam;
	}

	public double getBaseShldRate()
	{
		return _baseShldRate;
	}

	public double getBasePCritRate()
	{
		return _basePCritRate;
	}

	public double getBaseMCritRate()
	{
		return _baseMCritRate;
	}

	public double getBaseRunSpd()
	{
		return _baseRunSpd;
	}

	public double getBaseWalkSpd()
	{
		return _baseWalkSpd;
	}

	public double getBaseWaterRunSpd()
	{
		return _baseWaterRunSpd;
	}

	public double getBaseWaterWalkSpd()
	{
		return _baseWaterWalkSpd;
	}

	public double getBaseFlyRunSpd()
	{
		return _baseFlyRunSpd;
	}

	public double getBaseFlyWalkSpd()
	{
		return _baseFlyWalkSpd;
	}

	public int[] getBaseAttributeAttack()
	{
		return _baseAttributeAttack;
	}

	public int[] getBaseAttributeDefence()
	{
		return _baseAttributeDefence;
	}

	public double getCollisionRadius()
	{
		return _collisionRadius;
	}

	public double getCollisionHeight()
	{
		return _collisionHeight;
	}

	public WeaponType getBaseAttackType()
	{
		return _baseAttackType;
	}

	public int getPhysicalAbnormalResist()
	{
		return _physicalAbnormalResist;
	}

	public int getMagicAbnormalResist()
	{
		return _magicAbnormalResist;
	}

	public static StatsSet getEmptyStatsSet()
	{
		return new StatsSet();
	}

	public void setHpMax(double baseHpMax)
	{
		_baseHpMax = baseHpMax;
	}

	public void setPDef(double basePDef)
	{
		_basePDef = basePDef;
	}

	public void setMDef(double baseMDef)
	{
		_baseMDef = baseMDef;
	}

	public void setPAtk(double basePAtk)
	{
		_basePAtk = basePAtk;
	}

	public void setMAtk(double baseMAtk)
	{
		_baseMAtk = baseMAtk;
	}
}