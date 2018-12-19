package org.l2j.gameserver.model.petition;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

/**
 * @author VISTALL
 * @date 7:28/25.07.2011
 */
public class PetitionMainGroup extends PetitionGroup
{
	private final TIntObjectMap<PetitionSubGroup> _subGroups = new TIntObjectHashMap<>();

	public PetitionMainGroup(int id)
	{
		super(id);
	}

	public void addSubGroup(PetitionSubGroup subGroup)
	{
		_subGroups.put(subGroup.getId(), subGroup);
	}

	public PetitionSubGroup getSubGroup(int val)
	{
		return _subGroups.get(val);
	}

	public Collection<PetitionSubGroup> getSubGroups()
	{
		return _subGroups.valueCollection();
	}
}