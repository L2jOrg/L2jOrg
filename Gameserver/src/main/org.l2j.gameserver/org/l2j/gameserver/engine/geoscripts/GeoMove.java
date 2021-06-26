package org.l2j.gameserver.engine.geoscripts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExShowTrace;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.util.GameUtils;


/**
 * @Author: Diamond
 * @Date: 27/04/2009
 */
public class GeoMove
{
	public static List<List<Location>> findMovePath(int x, int y, int z, int destX, int destY, int destZ, Creature c, int geoIndex)
	{
		List<Location> path = PathFind.findPath(x, y, z, destX, destY, destZ, c != null && GameUtils.isPlayable(c), geoIndex);
		if(path == null)
			return Collections.emptyList();
		//admin_geo_trace
//		if(c != null && GameUtils.isPlayer(c) && ((Player) c).getVarBoolean("trace"))
//		{
//			Player player = (Player) c;
//			ExShowTrace trace = new ExShowTrace();
//			int i = 0;
//			for(Location loc : path)
//			{
//				i++;
//				if(i == 1 || i == path.size())
//					continue;
//				trace.addTrace(loc.getX(), loc.getY(), loc.getZ() + 15, 30000);
//			}
//			player.sendPacket(trace);
//		}

		return getNodePath(path, geoIndex);
	}

	private static List<List<Location>> getNodePath(List<Location> path, int geoIndex)
	{
		int size = path.size();
		if(size <= 1)
			return Collections.emptyList();
		List<List<Location>> result = new ArrayList<List<Location>>(size);
		for(int i = 1; i < size; i++)
		{
			Location p2 = path.get(i);
			Location p1 = path.get(i - 1);
			List<Location> moveList = GeoEngine.MoveList(p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), geoIndex, true); // onlyFullPath = true - проверяем весь путь до конца
			if(moveList == null) // если хотя-бы через один из участков нельзя пройти, забраковываем весь путь
				return Collections.emptyList();
			if(!moveList.isEmpty()) // это может случиться только если 2 одинаковых точки подряд
				result.add(moveList);
		}
		return result;
	}

	public static List<Location> constructMoveList(Location begin, Location end)
	{
		begin.world2geo();
		end.world2geo();

		int diff_x = end.getX() - begin.getX(), diff_y = end.getY() - begin.getY(), diff_z = end.getZ() - begin.getZ();
		int dx = Math.abs(diff_x), dy = Math.abs(diff_y), dz = Math.abs(diff_z);
		float steps = Math.max(Math.max(dx, dy), dz);
		if(steps == 0) // Никуда не идем
			return Collections.emptyList();

		float step_x = diff_x / steps, step_y = diff_y / steps, step_z = diff_z / steps;
		float next_x = begin.getX(), next_y = begin.getY(), next_z = begin.getZ();

		List<Location> result = new ArrayList<Location>((int) steps + 1);
		result.add(new Location(begin.getX(), begin.getY(), begin.getZ())); // Первая точка

		for(int i = 0; i < steps; i++)
		{
			next_x += step_x;
			next_y += step_y;
			next_z += step_z;

			result.add(new Location((int) (next_x + 0.5f), (int) (next_y + 0.5f), (int) (next_z + 0.5f)));
		}

		return result;
	}
}