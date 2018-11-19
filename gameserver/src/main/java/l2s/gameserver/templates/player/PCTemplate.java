package l2s.gameserver.templates.player;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.templates.StatsSet;

/**
 * @author Bonux
**/
public abstract class PCTemplate extends CreatureTemplate
{
	private final int _baseChestDef;
	private final int _baseLegsDef;
	private final int _baseHelmetDef;
	private final int _baseBootsDef;
	private final int _baseGlovesDef;
	private final int _basePendantDef;
	private final int _baseCloakDef;

	private final int _baseREarDef;
	private final int _baseLEarDef;
	private final int _baseRRingDef;
	private final int _baseLRingDef;
	private final int _baseNecklaceDef;

	private final double _baseRideRunSpd;
	private final double _baseRideWalkSpd;

	protected final TIntObjectMap<HpMpCpData> _regenData = new TIntObjectHashMap<HpMpCpData>();

	public PCTemplate(StatsSet set)
	{
		super(set);

		_baseChestDef = set.getInteger("baseChestDef");
		_baseLegsDef = set.getInteger("baseLegsDef");
		_baseHelmetDef = set.getInteger("baseHelmetDef");
		_baseBootsDef = set.getInteger("baseBootsDef");
		_baseGlovesDef = set.getInteger("baseGlovesDef");
		_basePendantDef = set.getInteger("basePendantDef");
		_baseCloakDef = set.getInteger("baseCloakDef");

		_baseREarDef = set.getInteger("baseREarDef");
		_baseLEarDef = set.getInteger("baseLEarDef");
		_baseRRingDef = set.getInteger("baseRRingDef");
		_baseLRingDef = set.getInteger("baseLRingDef");
		_baseNecklaceDef = set.getInteger("baseNecklaceDef");

		_baseRideRunSpd = set.getDouble("baseRideRunSpd");
		_baseRideWalkSpd = set.getDouble("baseRideWalkSpd");
	}

	public int getBaseChestDef()
	{
		return _baseChestDef;
	}

	public int getBaseLegsDef()
	{
		return _baseLegsDef;
	}

	public int getBaseHelmetDef()
	{
		return _baseHelmetDef;
	}

	public int getBaseBootsDef()
	{
		return _baseBootsDef;
	}

	public int getBaseGlovesDef()
	{
		return _baseGlovesDef;
	}

	public int getBasePendantDef()
	{
		return _basePendantDef;
	}

	public int getBaseCloakDef()
	{
		return _baseCloakDef;
	}

	public int getBaseREarDef()
	{
		return _baseREarDef;
	}

	public int getBaseLEarDef()
	{
		return _baseLEarDef;
	}

	public int getBaseRRingDef()
	{
		return _baseRRingDef;
	}

	public int getBaseLRingDef()
	{
		return _baseLRingDef;
	}

	public int getBaseNecklaceDef()
	{
		return _baseNecklaceDef;
	}

	public double getBaseRideRunSpd()
	{
		return _baseRideRunSpd;
	}

	public double getBaseRideWalkSpd()
	{
		return _baseRideWalkSpd;
	}

	public void addRegenData(int level, HpMpCpData data)
	{
		_regenData.put(level, data);
	}

	@Override
	public double getBaseHpReg(int level)
	{
		HpMpCpData data = _regenData.get(level);
		if(data == null)
			return 0.;
		return data.getHP();
	}

	@Override
	public double getBaseMpReg(int level)
	{
		HpMpCpData data = _regenData.get(level);
		if(data == null)
			return 0.;
		return data.getMP();
	}

	@Override
	public double getBaseCpReg(int level)
	{
		HpMpCpData data = _regenData.get(level);
		if(data == null)
			return 0.;
		return data.getCP();
	}
}