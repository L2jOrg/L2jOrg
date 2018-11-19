package l2s.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import l2s.commons.data.xml.AbstractHolder;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.InstantZone;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

/**
 * @author VISTALL
 * @date 1:35/30.06.2011
 */
public class InstantZoneHolder extends AbstractHolder
{
	private static final InstantZoneHolder _instance = new InstantZoneHolder();
	private IntObjectMap<InstantZone> _zones = new HashIntObjectMap<InstantZone>();

	public static InstantZoneHolder getInstance()
	{
		return _instance;
	}

	public void addInstantZone(InstantZone zone)
	{
		_zones.put(zone.getId(), zone);
	}

	public InstantZone getInstantZone(int id)
	{
		return _zones.get(id);
	}

	private SchedulingPattern getResetReuseById(int id)
	{
		InstantZone zone = getInstantZone(id);
		return zone == null ? null : zone.getResetReuse();
	}

	public int getMinutesToNextEntrance(int id, Player player)
	{
		SchedulingPattern resetReuse = getResetReuseById(id);
		if(resetReuse == null)
			return 0;

		Long time = null;
		if(getSharedReuseInstanceIds(id) != null && !getSharedReuseInstanceIds(id).isEmpty())
		{
			List<Long> reuses = new ArrayList<Long>();
			for(int i : getSharedReuseInstanceIds(id))
				if(player.getInstanceReuse(i) != null)
					reuses.add(player.getInstanceReuse(i));
			if(!reuses.isEmpty())
			{
				Collections.sort(reuses);
				time = reuses.get(reuses.size() - 1);
			}
		}
		else
			time = player.getInstanceReuse(id);
		if(time == null)
			return 0;
		return (int) Math.max((resetReuse.next(time) - System.currentTimeMillis()) / 60000L, 0);
	}

	public List<Integer> getLockedInstancesList(Player player)
	{
		if(player.getInstanceReuses().isEmpty())
			return Collections.emptyList();

		List<Integer> result = new ArrayList<Integer>();
		for(Entry<Integer, Long> reuse : player.getInstanceReuses().entrySet())
		{
			if(reuse == null || reuse.getKey() == null || reuse.getValue() == null)
				continue;

			SchedulingPattern resetReuse = getResetReuseById(reuse.getKey());
			if(resetReuse == null)
				continue;

			if(resetReuse.next(reuse.getValue()) > System.currentTimeMillis())
				result.add(reuse.getKey());
		}
		return result;
	}

	public List<Integer> getSharedReuseInstanceIds(int id)
	{
		if(getInstantZone(id).getSharedReuseGroup() < 1)
			return null;
		List<Integer> sharedInstanceIds = new ArrayList<Integer>();
		for(InstantZone iz : _zones.values())
			if(iz.getSharedReuseGroup() > 0 && getInstantZone(id).getSharedReuseGroup() > 0 && iz.getSharedReuseGroup() == getInstantZone(id).getSharedReuseGroup())
				sharedInstanceIds.add(iz.getId());
		return sharedInstanceIds;
	}

	public List<Integer> getSharedReuseInstanceIdsByGroup(int groupId)
	{
		if(groupId < 1)
			return null;
		List<Integer> sharedInstanceIds = new ArrayList<Integer>();
		for(InstantZone iz : _zones.values())
			if(iz.getSharedReuseGroup() > 0 && iz.getSharedReuseGroup() == groupId)
				sharedInstanceIds.add(iz.getId());
		return sharedInstanceIds;
	}

	@Override
	public int size()
	{
		return _zones.size();
	}

	@Override
	public void clear()
	{
		_zones.clear();
	}
}