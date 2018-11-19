package l2s.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.ResidenceType;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

/**
 * @author VISTALL
 * @date 0:17/12.02.2011
 */
@SuppressWarnings("unchecked")
public final class ResidenceHolder extends AbstractHolder
{
	private static ResidenceHolder _instance = new ResidenceHolder();

	private IntObjectMap<Residence> _residences = new TreeIntObjectMap<Residence>();

	public static ResidenceHolder getInstance()
	{
		return _instance;
	}

	private ResidenceHolder()
	{
		//
	}

	public void addResidence(Residence r)
	{
		_residences.put(r.getId(), r);
	}

	public <R extends Residence> R getResidence(int id)
	{
		return (R) _residences.get(id);
	}

	public <R extends Residence> R getResidence(Class<R> type, int id)
	{
		Residence r = getResidence(id);
		if(r == null || (r.getClass() != type && !type.isAssignableFrom(r.getClass())))
			return null;

		return (R) r;
	}

	public <R extends Residence> List<R> getResidenceList(Class<R> t)
	{
		List<R> residences = new ArrayList<R>();
		for(Residence r : _residences.values())
		{
			if(r.getClass() == t || t.isAssignableFrom(r.getClass()))
				residences.add((R) r);
		}

		return residences;
	}

	public Collection<Residence> getResidences()
	{
		return _residences.values();
	}

	public <R extends Residence> R getResidenceByObject(Class<? extends Residence> type, GameObject object)
	{
		return (R) getResidenceByCoord(type, object.getX(), object.getY(), object.getZ(), object.getReflection());
	}

	public <R extends Residence> R getResidenceByCoord(Class<R> type, int x, int y, int z, Reflection ref)
	{
		Collection<Residence> residences = type == null ? getResidences() : (Collection<Residence>) getResidenceList(type);
		for(Residence residence : residences)
		{
			if(residence.checkIfInZone(x, y, z, ref))
				return (R) residence;
		}
		return null;
	}

	public <R extends Residence> R findNearestResidence(Class<R> clazz, int x, int y, int z, Reflection ref, int offset)
	{
		Residence residence = getResidenceByCoord(clazz, x, y, z, ref);
		if(residence == null)
		{
			double closestDistance = offset;
			double distance;
			for(Residence r : getResidenceList(clazz))
			{
				Zone zone = r.getZone();
				if(zone == null)
					continue;

				distance = zone.findDistanceToZone(x, y, z, false);
				if(closestDistance > distance)
				{
					closestDistance = distance;
					residence = r;
				}
			}
		}
		return (R) residence;
	}

	public void callInit()
	{
		for(Residence r : getResidences())
			r.init();
	}

	@Override
	public void log()
	{
		info("total size: " + _residences.size());
		for(ResidenceType type : ResidenceType.VALUES)
		{
			int count = 0;
			for(Residence r : getResidences())
			{
				if(r.getType() == type)
					count++;
			}
			info(" - load " + count + " " + String.valueOf(type).toLowerCase() + "(s).");
		}
	}

	@Override
	public int size()
	{
		return _residences.size();
	}

	@Override
	public void clear()
	{
		_residences.clear();
	}
}