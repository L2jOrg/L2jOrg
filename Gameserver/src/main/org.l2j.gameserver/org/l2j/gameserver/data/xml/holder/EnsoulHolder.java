package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.item.ItemGrade;
import org.l2j.gameserver.templates.item.support.Ensoul;
import org.l2j.gameserver.templates.item.support.EnsoulFee;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 **/
public class EnsoulHolder extends AbstractHolder
{
	private static final EnsoulHolder _instance = new EnsoulHolder();

	private IntObjectMap<EnsoulFee> _ensoulsFee = new HashIntObjectMap<EnsoulFee>();
	private IntObjectMap<Ensoul> _ensouls = new HashIntObjectMap<Ensoul>();

	public static EnsoulHolder getInstance()
	{
		return _instance;
	}

	public void addEnsoulFee(ItemGrade grade, EnsoulFee ensoulFee)
	{
		_ensoulsFee.put(grade.ordinal(), ensoulFee);
	}

	public EnsoulFee getEnsoulFee(ItemGrade grade)
	{
		return _ensoulsFee.get(grade.ordinal());
	}

	public void addEnsoul(Ensoul ensoul)
	{
		_ensouls.put(ensoul.getId(), ensoul);
	}

	public Ensoul getEnsoul(int id)
	{
		return _ensouls.get(id);
	}

	@Override
	public int size()
	{
		return _ensouls.size();
	}

	@Override
	public void clear()
	{
		_ensoulsFee.clear();
		_ensouls.clear();
	}
}