package l2s.gameserver.model.base;

import java.util.Arrays;

import l2s.gameserver.stats.Stats;

public enum Element
{
	FIRE(0, Stats.ATTACK_FIRE, Stats.DEFENCE_FIRE, 1),
	WATER(1, Stats.ATTACK_WATER, Stats.DEFENCE_WATER, 2),
	WIND(2, Stats.ATTACK_WIND, Stats.DEFENCE_WIND, 4),
	EARTH(3, Stats.ATTACK_EARTH, Stats.DEFENCE_EARTH, 8),
	HOLY(4, Stats.ATTACK_HOLY, Stats.DEFENCE_HOLY, 16),
	UNHOLY(5, Stats.ATTACK_UNHOLY, Stats.DEFENCE_UNHOLY, 32),
	NONE(-1, null, Stats.BASE_ELEMENTS_DEFENCE, 0),
	NONE_ARMOR(-2, null, Stats.BASE_ELEMENTS_DEFENCE, 0);

	/** Массив элементов без NONE **/
	public final static Element[] VALUES = Arrays.copyOf(values(), 6);

	private final int id;
	private final Stats attack;
	private final Stats defence;
	private final int mask;

	private Element(int id, Stats attack, Stats defence, int mask)
	{
		this.id = id;
		this.attack = attack;
		this.defence = defence;
		this.mask = mask;
	}

	public int getId()
	{
		return id;
	}

	public Stats getAttack()
	{
		return attack;
	}

	public Stats getDefence()
	{
		return defence;
	}

	public int getMask()
	{
		return mask;
	}

	public static Element getElementById(int id)
	{
		for(Element e : VALUES)
			if(e.getId() == id)
				return e;
		return NONE;
	}

	/**
	 * Возвращает противоположный тип элемента
	 * @return значение элемента
	 */
	public static Element getReverseElement(Element element)
	{
		switch(element)
		{
			case WATER:
				return FIRE;
			case FIRE:
				return WATER;
			case WIND:
				return EARTH;
			case EARTH:
				return WIND;
			case HOLY:
				return UNHOLY;
			case UNHOLY:
				return HOLY;
		}

		return NONE;
	}

	public static Element getElementByName(String name)
	{
		for(Element e : VALUES)
			if(e.name().equalsIgnoreCase(name))
				return e;
		return NONE;
	}
}
