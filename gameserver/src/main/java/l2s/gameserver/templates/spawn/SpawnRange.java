package l2s.gameserver.templates.spawn;

import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 4:08/19.05.2011
 */
public interface SpawnRange
{
	Location getRandomLoc(int geoIndex);
}