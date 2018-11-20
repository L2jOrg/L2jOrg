package org.l2j.gameserver.templates.npc;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class Faction
{
	public final static String none = "none";
	public final static Faction NONE = new Faction(none, 0);

	private final String _namesStr;
	private List<String> _names = new ArrayList<String>();
	private final int _range;
	private TIntList _ignoreNpcIds = new TIntArrayList();

	public Faction(String names, int range)
	{
		_namesStr = names;
		for(String name : names.split(";"))
		{
			if(name == null || name.isEmpty() || name.equals(none))
				continue;

			_names.add(name.toLowerCase());
		}
		_range = range;
	}

	public int getRange()
	{
		return _range;
	}

	public void addIgnoreNpcId(int npcId)
	{
		_ignoreNpcIds.add(npcId);
	}

	public boolean isIgnoreNpcId(int npcId)
	{
		return _ignoreNpcIds.contains(npcId);
	}

	public boolean isNone()
	{
		return _names.isEmpty();
	}

	public boolean containsName(String name)
	{
		for(String n : _names)
		{
			if(n.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public boolean equals(Faction faction)
	{
		if(isNone())
			return false;

		for(String name : _names)
		{
			if(faction.containsName(name))
				return true;
		}
		return false;
	}

	@Override 
	public boolean equals(Object o)
	{
		if(o == this)
			return true;
		if(o == null)
			return false;
		if(o.getClass() != this.getClass())
			return false;
		return equals((Faction) o);
	}

	@Override     
	public int hashCode()
	{
		return 7 * _namesStr.hashCode() + 23210;
	}

	@Override
	public String toString()
	{
		return _namesStr;
	}
}