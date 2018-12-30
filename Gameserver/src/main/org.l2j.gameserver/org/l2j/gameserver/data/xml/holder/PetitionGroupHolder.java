package org.l2j.gameserver.data.xml.holder;

import java.util.Collection;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.petition.PetitionMainGroup;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author VISTALL
 * @date 7:22/25.07.2011
 */
public class PetitionGroupHolder extends AbstractHolder
{
	private static PetitionGroupHolder _instance = new PetitionGroupHolder();

	private IntObjectMap<PetitionMainGroup> _petitionGroups = new HashIntObjectMap<PetitionMainGroup>();

	public static PetitionGroupHolder getInstance()
	{
		return _instance;
	}

	private PetitionGroupHolder()
	{}

	public void addPetitionGroup(PetitionMainGroup g)
	{
		_petitionGroups.put(g.getId(), g);
	}

	public PetitionMainGroup getPetitionGroup(int val)
	{
		return _petitionGroups.get(val);
	}

	public Collection<PetitionMainGroup> getPetitionGroups()
	{
		return _petitionGroups.values();
	}

	@Override
	public int size()
	{
		return _petitionGroups.size();
	}

	@Override
	public void clear()
	{
		_petitionGroups.clear();
	}
}