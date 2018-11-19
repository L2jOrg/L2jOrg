package l2s.gameserver.templates.pet;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.base.PetType;
import l2s.gameserver.templates.item.data.RewardItemData;

/**
 * @author Bonux
 */
public class PetData
{
	private final int _npcId;
	private final int _controlItemId;
	private final int[] _syncLevels;
	private final List<PetSkillData> _skills;
	//TODO: [Bonux] public final int[] evolve;
	private final TIntObjectMap<PetLevelData> _lvlData;

	private final PetType _type;
	private final MountType _mountType;

	private int _minLvl = 0;
	private int _maxLvl = 0;

	private final List<RewardItemData> _expirationRewardItems = new ArrayList<RewardItemData>();

	public PetData(int npcId, int controlItemId, int[] syncLevels, PetType type, MountType mountType)
	{
		_npcId = npcId;
		_controlItemId = controlItemId;
		_syncLevels = syncLevels;
		_skills = new ArrayList<PetSkillData>();
		_lvlData = new TIntObjectHashMap<PetLevelData>();
		_type = type;
		_mountType = mountType;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public int getControlItemId()
	{
		return _controlItemId;
	}

	public void addSkill(PetSkillData skill)
	{
		_skills.add(skill);
	}

	public PetSkillData[] getSkills()
	{
		return _skills.toArray(new PetSkillData[_skills.size()]);
	}

	public void addLvlData(int lvl, PetLevelData lvlData)
	{
		_lvlData.put(lvl, lvlData);
	}

	public PetLevelData getLvlData(int level)
	{
		return _lvlData.get(Math.max(_minLvl, Math.min(_maxLvl, level)));
	}

	public int getMaxMeal(int level)
	{
		return getLvlData(level).getMaxMeal();
	}

	public long getExp(int level)
	{
		return getLvlData(level).getExp();
	}

	public int getExpType(int level)
	{
		return getLvlData(level).getExpType();
	}

	public int getBattleMealConsume(int level)
	{
		return getLvlData(level).getBattleMealConsume();
	}

	public int getNormalMealConsume(int level)
	{
		return getLvlData(level).getNormalMealConsume();
	}

	public double getPAtk(int level)
	{
		return getLvlData(level).getPAtk();
	}

	public double getPDef(int level)
	{
		return getLvlData(level).getPDef();
	}

	public double getMAtk(int level)
	{
		return getLvlData(level).getMAtk();
	}

	public double getMDef(int level)
	{
		return getLvlData(level).getMDef();
	}

	public double getHP(int level)
	{
		return getLvlData(level).getHP();
	}

	public double getMP(int level)
	{
		return getLvlData(level).getMP();
	}

	public double getHPRegen(int level)
	{
		return getLvlData(level).getHPRegen();
	}

	public double getMPRegen(int level)
	{
		return getLvlData(level).getMPRegen();
	}

	public int[] getFood(int level)
	{
		return getLvlData(level).getFood();
	}

	public int getHungryLimit(int level)
	{
		return getLvlData(level).getHungryLimit();
	}

	public int getSoulshotCount(int level)
	{
		return getLvlData(level).getSoulshotCount();
	}

	public int getSpiritshotCount(int level)
	{
		return getLvlData(level).getSpiritshotCount();
	}

	public int getMaxLoad(int level)
	{
		return getLvlData(level).getMaxLoad();
	}

	public PetType getType()
	{
		return _type;
	}

	public boolean isOfType(PetType type)
	{
		return _type == type;
	}

	public MountType getMountType()
	{
		return _mountType;
	}

	public void setMinLvl(int lvl)
	{
		_minLvl = lvl;
	}

	public int getMinLvl()
	{
		return _minLvl;
	}

	public void setMaxLvl(int lvl)
	{
		_maxLvl = lvl;
	}

	public int getMaxLvl()
	{
		return _maxLvl;
	}

	public int getFormId(int level)
	{
		for(int i = 0; i < _syncLevels.length; i++)
		{
			if(level >= _syncLevels[i])
				return i + 1;
		}
		return 0;
	}

	public void addExpirationRewardItem(RewardItemData item)
	{
		_expirationRewardItems.add(item);
	}

	public RewardItemData[] getExpirationRewardItems()
	{
		return _expirationRewardItems.toArray(new RewardItemData[_expirationRewardItems.size()]);
	}
}