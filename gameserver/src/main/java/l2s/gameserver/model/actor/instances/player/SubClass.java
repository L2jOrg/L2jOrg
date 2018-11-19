package l2s.gameserver.model.actor.instances.player;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.SubClassType;

public class SubClass
{
	private final Player _owner;

	private int _classId = 0;
	private int _index = 1;

	private boolean _active = false;
	private SubClassType _type = SubClassType.BASE_CLASS;

	private int _level = 1;
	private long _exp = 0;
	private long _sp = 0;

	private int _maxLvl = Experience.getMaxLevel();
	private long _minExp = 0;
	private long _maxExp = Experience.getExpForLevel(_maxLvl + 1) - 1;

	private double _hp = 1;
	private double _mp = 1;
	private double _cp = 1;

	public SubClass(Player owner)
	{
		_owner = owner;
	}

	public int getClassId()
	{
		return _classId;
	}

	public long getExp()
	{
		return _exp;
	}

	public long getMaxExp()
	{
		return _maxExp;
	}

	public void addExp(long val, boolean delevel)
	{
		setExp(_exp + val, delevel);
	}

	public long getSp()
	{
		return _sp;
	}

	public void addSp(long val)
	{
		setSp(_sp + val);
	}

	public int getLevel()
	{
		return _level;
	}

	public void setClassId(int id)
	{
		if(_classId == id)
			return;

		_classId = id;
	}

	public void setExp(long val, boolean delevel)
	{
		_exp = val;

		if(!delevel)
			_exp = Math.min(Math.max(Experience.getExpForLevel(_level), _exp), _maxExp);

		_exp = Math.min(_exp, _maxExp);
		_exp = Math.max(_minExp, _exp);
		_level = Experience.getLevel(_exp);
	}

	public void setSp(long spValue)
	{
		_sp = Math.min(Math.max(0L, spValue), Config.SP_LIMIT);
	}

	public void setHp(double hpValue)
	{
		_hp = Math.max(0., hpValue);
	}

	public double getHp()
	{
		return _hp;
	}

	public void setMp(final double mpValue)
	{
		_mp = Math.max(0., mpValue);
	}

	public double getMp()
	{
		return _mp;
	}

	public void setCp(final double cpValue)
	{
		_cp = Math.max(0., cpValue);
	}

	public double getCp()
	{
		return _cp;
	}

	public void setActive(final boolean active)
	{
		_active = active;
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setType(final SubClassType type)
	{
		if(_type == type)
			return;

		_type = type;

		if(_type == SubClassType.SUBCLASS)
		{
			_maxLvl = Experience.getMaxSubLevel();
			_minExp = Experience.getExpForLevel(Config.SUB_START_LEVEL);
			_level = Math.min(Math.max(Config.SUB_START_LEVEL, _level), _maxLvl);
		}
		else
		{
			_maxLvl = Experience.getMaxLevel();
			_minExp = 0;
			_level = Math.min(Math.max(1, _level), _maxLvl);
		}
		_minExp = Math.max(0, _minExp);
		_maxExp = Experience.getExpForLevel(_maxLvl + 1) - 1;
		_exp = Math.min(Math.max(Experience.getExpForLevel(_level), _exp), _maxExp);
	}

	public SubClassType getType()
	{
		return _type;
	}

	public boolean isBase()
	{
		return _type == SubClassType.BASE_CLASS;
	}

	@Override
	public String toString()
	{
		return ClassId.VALUES[_classId].toString() + " " + _level;
	}

	public int getMaxLevel()
	{
		return _maxLvl;
	}

	public void setIndex(int i)
	{
		_index = i;
	}

	public int getIndex()
	{
		return _index;
	}
}