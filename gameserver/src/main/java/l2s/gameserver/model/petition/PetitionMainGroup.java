package l2s.gameserver.model.petition;

import java.util.Collection;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

/**
 * @author VISTALL
 * @date 7:28/25.07.2011
 */
public class PetitionMainGroup extends PetitionGroup
{
	private final IntObjectMap<PetitionSubGroup> _subGroups = new HashIntObjectMap<PetitionSubGroup>();

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
		return _subGroups.values();
	}
}