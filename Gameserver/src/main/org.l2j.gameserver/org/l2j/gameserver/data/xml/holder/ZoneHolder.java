package org.l2j.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.commons.util.Rnd;

import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.templates.ZoneTemplate;
import org.l2j.gameserver.utils.ReflectionUtils;

/**
 * @author G1ta0
 */
public class ZoneHolder extends AbstractHolder
{
	private static final ZoneHolder _instance = new ZoneHolder();

	private final Map<String, ZoneTemplate> _zones = new HashMap<String, ZoneTemplate>();

	public static ZoneHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(ZoneTemplate zone)
	{
		_zones.put(zone.getName(), zone);
	}

	public ZoneTemplate getTemplate(String name)
	{
		return _zones.get(name);
	}

	public Map<String, ZoneTemplate> getZones()
	{
		return _zones;
	}

	public Zone getRandomZone()
	{
		String _zoneName = "";
		int index = Rnd.get(1, getZones().size() - 1);
		int inx = 0;
		for(String tmp : getZones().keySet())
		{
			if(index == inx)
			{
				_zoneName = tmp;
				break;
			}
			inx++;
		}

		Zone zone = ReflectionUtils.getZone(_zoneName);
		if(zone == null)
		{
			System.out.println("null zone randomized");
			return getRandomZone();
		}
		return zone;
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