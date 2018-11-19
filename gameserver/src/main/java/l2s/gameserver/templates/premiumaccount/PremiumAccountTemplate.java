package l2s.gameserver.templates.premiumaccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.templates.item.data.ItemData;
import l2s.gameserver.utils.Language;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

/**
 * @author Bonux
 **/
public class PremiumAccountTemplate extends StatTemplate
{
	public static PremiumAccountProperties DEFAULT_PROPERTIES = new PremiumAccountProperties(-1, -1);
	public static PremiumAccountRates DEFAULT_RATES = new PremiumAccountRates(1, 1, 1, 1, 1, 1, 1);
	public static PremiumAccountModifiers DEFAULT_MODIFIERS = new PremiumAccountModifiers(1, 1);
	public static PremiumAccountBonus DEFAULT_BONUS = new PremiumAccountBonus(0, 0);

	private final int _type;
	private final Map<Language, String> _names = new HashMap<Language, String>();
	private final List<ItemData> _giveItemsOnStart = new ArrayList<ItemData>();
	private final List<ItemData> _takeItemsOnEnd = new ArrayList<ItemData>();
	private final IntObjectMap<List<ItemData>> _fees = new TreeIntObjectMap<List<ItemData>>();

	private PremiumAccountProperties _properties = DEFAULT_PROPERTIES;
	private PremiumAccountRates _rates = DEFAULT_RATES;
	private PremiumAccountModifiers _modifiers = DEFAULT_MODIFIERS;
	private PremiumAccountBonus _bonus = DEFAULT_BONUS;

	private SkillEntry[] _skills = SkillEntry.EMPTY_ARRAY;

	public PremiumAccountTemplate(int type)
	{
		_type = type;
	}

	public int getType()
	{
		return _type;
	}

	public void addName(Language lang, String name)
	{
		_names.put(lang, name);
	}

	public String getName(Language lang)
	{
		String name = _names.get(lang);
		if(name == null)
		{
			if(lang == Language.ENGLISH)
				name = _names.get(Language.RUSSIAN);
			else
				name = _names.get(Language.ENGLISH);
		}
		return name;
	}

	public void setProperties(PremiumAccountProperties properties)
	{
		_properties = properties;
	}

	public PremiumAccountProperties getProperties()
	{
		return _properties;
	}

	public void setRates(PremiumAccountRates rates)
	{
		_rates = rates;
	}

	public PremiumAccountRates getRates()
	{
		return _rates;
	}

	public void setModifiers(PremiumAccountModifiers modifiers)
	{
		_modifiers = modifiers;
	}

	public PremiumAccountModifiers getModifiers()
	{
		return _modifiers;
	}

	public void setBonus(PremiumAccountBonus bonus)
	{
		_bonus = bonus;
	}

	public PremiumAccountBonus getBonus()
	{
		return _bonus;
	}

	public void addGiveItemOnStart(ItemData item)
	{
		_giveItemsOnStart.add(item);
	}

	public ItemData[] getGiveItemsOnStart()
	{
		return _giveItemsOnStart.toArray(new ItemData[_giveItemsOnStart.size()]);
	}

	public void addTakeItemOnEnd(ItemData item)
	{
		_takeItemsOnEnd.add(item);
	}

	public ItemData[] getTakeItemsOnEnd()
	{
		return _takeItemsOnEnd.toArray(new ItemData[_takeItemsOnEnd.size()]);
	}

	public void addFee(int delay, ItemData item)
	{
		List<ItemData> items = _fees.get(delay);
		if(items == null)
		{
			items = new ArrayList<ItemData>();
			_fees.put(delay, items);
		}
		items.add(item);
	}

	public int[] getFeeDelays()
	{
		return _fees.keySet().toArray();
	}

	public ItemData[] getFeeItems(int delay)
	{
		List<ItemData> items = _fees.get(delay);
		if(items == null)
			return null;
		return items.toArray(new ItemData[items.size()]);
	}

	public void attachSkill(SkillEntry skill)
	{
		_skills = ArrayUtils.add(_skills, skill);
	}

	public SkillEntry[] getAttachedSkills()
	{
		return _skills;
	}

	public final Func[] getStatFuncs()
	{
		return getStatFuncs(this);
	}
}