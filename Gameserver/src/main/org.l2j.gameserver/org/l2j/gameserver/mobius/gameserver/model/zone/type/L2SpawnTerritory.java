package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneForm;

/**
 * Just dummy zone, needs only for geometry calculations
 * @author GKR
 */
public class L2SpawnTerritory
{
	private final String _name;
	private final L2ZoneForm _territory;
	
	public L2SpawnTerritory(String name, L2ZoneForm territory)
	{
		_name = name;
		_territory = territory;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public Location getRandomPoint()
	{
		return _territory.getRandomPoint();
	}
	
	public boolean isInsideZone(int x, int y, int z)
	{
		return _territory.isInsideZone(x, y, z);
	}
	
	public void visualizeZone(int z)
	{
		_territory.visualizeZone(z);
	}
}