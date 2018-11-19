package l2s.gameserver.model.items;

import java.io.Serializable;

import l2s.gameserver.model.base.Element;

public class ItemAttributes implements Serializable
{
	private static final long serialVersionUID = 401594188363005415L;

	private int fire;
	private int water;
	private int wind;
	private int earth;
	private int holy;
	private int unholy;

	public ItemAttributes()
	{
		this(0, 0, 0, 0, 0, 0);
	}

	public ItemAttributes(int fire, int water, int wind, int earth, int holy, int unholy)
	{
		this.fire = fire;
		this.water = water;
		this.wind = wind;
		this.earth = earth;
		this.holy = holy;
		this.unholy = unholy;
	}

	public int getFire()
	{
		return fire;
	}

	public void setFire(int fire)
	{
		this.fire = fire;
	}

	public int getWater()
	{
		return water;
	}

	public void setWater(int water)
	{
		this.water = water;
	}

	public int getWind()
	{
		return wind;
	}

	public void setWind(int wind)
	{
		this.wind = wind;
	}

	public int getEarth()
	{
		return earth;
	}

	public void setEarth(int earth)
	{
		this.earth = earth;
	}

	public int getHoly()
	{
		return holy;
	}

	public void setHoly(int holy)
	{
		this.holy = holy;
	}

	public int getUnholy()
	{
		return unholy;
	}

	public void setUnholy(int unholy)
	{
		this.unholy = unholy;
	}

	public Element getElement()
	{
		if(fire > 0)
			return Element.FIRE;
		else if(water > 0)
			return Element.WATER;
		else if(wind > 0)
			return Element.WIND;
		else if(earth > 0)
			return Element.EARTH;
		else if(holy > 0)
			return Element.HOLY;
		else if(unholy > 0)
			return Element.UNHOLY;

		return Element.NONE;
	}

	public int getValue()
	{
		if(fire > 0)
			return fire;
		else if(water > 0)
			return water;
		else if(wind > 0)
			return wind;
		else if(earth > 0)
			return earth;
		else if(holy > 0)
			return holy;
		else if(unholy > 0)
			return unholy;

		return 0;
	}

	public void setValue(Element element, int value)
	{
		switch(element)
		{
			case FIRE:
				fire = value;
				break;
			case WATER:
				water = value;
				break;
			case WIND:
				wind = value;
				break;
			case EARTH:
				earth = value;
				break;
			case HOLY:
				holy = value;
				break;
			case UNHOLY:
				unholy = value;
				break;
		}
	}

	public int getValue(Element element)
	{
		switch(element)
		{
			case FIRE:
				return fire;
			case WATER:
				return water;
			case WIND:
				return wind;
			case EARTH:
				return earth;
			case HOLY:
				return holy;
			case UNHOLY:
				return unholy;
			default:
				return 0;
		}
	}

	public ItemAttributes clone()
	{
		return new ItemAttributes(fire, water, wind, earth, holy, unholy);
	}
}
