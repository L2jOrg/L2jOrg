package org.l2j.gameserver.model;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.stats.Env;
import org.l2j.gameserver.stats.conditions.Condition;
import org.l2j.gameserver.templates.item.data.ItemData;
import org.l2j.gameserver.utils.SkillUtils;

/**
 * @author VISTALL
 */
public final class SkillLearn implements Comparable<SkillLearn>
{
	private final int _id;
	private final int _level;
	private final int _minLevel;
	private final int _cost;
	private final int _itemId;
	private final long _itemCount;
	private final Race _race;
	private final boolean _autoGet;
	private final ClassLevel _classLevel;
	private final List<ItemData> _additionalRequiredItems = new ArrayList<ItemData>();
	private final List<ItemData> _allRequiredItems = new ArrayList<ItemData>();
	private final List<Condition> _conditions = new ArrayList<Condition>();

	public SkillLearn(int id, int lvl, int minLvl, int cost, int itemId, long itemCount, boolean autoGet, Race race, ClassLevel classLevel)
	{
		_id = id;
		_level = lvl;
		_minLevel = minLvl;
		_cost = cost;
		_itemId = itemId;
		_itemCount = itemCount;
		if(itemId > 0 && itemCount > 0)
			_allRequiredItems.add(new ItemData(itemId, itemCount));
		_autoGet = autoGet;
		_race = race;
		_classLevel = classLevel;
	}

	public SkillLearn(int id, int lvl, int minLvl, int cost, int itemId, long itemCount, boolean autoGet, Race race)
	{
		this(id, lvl, minLvl, cost, itemId, itemCount, autoGet, race, ClassLevel.NONE);
	}

	public int getId()
	{
		return _id;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getCost()
	{
		return _cost;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getItemCount()
	{
		return _itemCount;
	}

	public boolean isAutoGet()
	{
		return _autoGet;
	}

	public Race getRace()
	{
		return _race;
	}

	public boolean isFreeAutoGet(AcquireType type)
	{
		return isAutoGet() && getCost() == 0 && !haveRequiredItemsForLearn(type);
	}

	public boolean isOfRace(Race race)
	{
		return _race == null || _race == race;
	}

	public ClassLevel getClassLevel()
	{
		return _classLevel;
	}

	public void addAdditionalRequiredItem(int id, long count)
	{
		if(id > 0 && count > 0)
		{
			ItemData item = new ItemData(id, count);
			_additionalRequiredItems.add(item);
			_allRequiredItems.add(item);
		}
	}

	public void addAdditionalRequiredItems(List<ItemData> items)
	{
		_additionalRequiredItems.addAll(items);
		_allRequiredItems.addAll(items);
	}

	public List<ItemData> getAdditionalRequiredItems()
	{
		return _additionalRequiredItems;
	}

	public List<ItemData> getRequiredItemsForLearn(AcquireType type)
	{
		if(Config.DISABLED_SPELLBOOKS_FOR_ACQUIRE_TYPES.contains(type))
			return _additionalRequiredItems;
		return _allRequiredItems;
	}

	public boolean haveRequiredItemsForLearn(AcquireType type)
	{
		return !getRequiredItemsForLearn(type).isEmpty();
	}

	public void addCondition(Condition condition)
	{
		_conditions.add(condition);
	}

	public boolean testCondition(Player player)
	{
		if(_conditions.isEmpty())
			return true;

		Env env = new Env();
		env.character = player;

		for(Condition condition : _conditions)
		{
			if(!condition.test(env))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return SkillUtils.generateSkillHashCode(_id, _level);
	}

	@Override
	public int compareTo(SkillLearn o)
	{
		if(getId() == o.getId())
			return getLevel() - o.getLevel();
		else
			return getId() - o.getId();
	}
}