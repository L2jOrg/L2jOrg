package org.l2j.gameserver.engine.geoscripts;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.geoscripts.PathFindBuffers.GeoNode;
import org.l2j.gameserver.engine.geoscripts.PathFindBuffers.PathFindBuffer;
import org.l2j.gameserver.model.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.l2j.gameserver.engine.geoscripts.GeoEngine.*;

public class PathFind
{
	public final static int BOOST_NONE = 0;
	public final static int BOOST_START = 1;
	public final static int BOOST_BOTH = 2;
	
	public static List<Location> findPath(int x, int y, int z, Location target, boolean isPlayable, int geoIndex)
	{
		return findPath(x, y, z, target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), isPlayable, geoIndex);
	}
	
	public static final List<Location> findPath(int x, int y, int z, int destX, int destY, int destZ, boolean isPlayable, int geoIndex)
	{
		if(Math.abs(z - destZ) > (isPlayable ? Config.NPC_PATH_FIND_MAX_HEIGHT : Config.PLAYABLE_PATH_FIND_MAX_HEIGHT))
			return null;

		z = GeoEngine.getLowerHeight(x, y, z, geoIndex);
		destZ = GeoEngine.getLowerHeight(destX, destY, destZ, geoIndex);

		Location startPoint = Config.PATHFIND_BOOST == BOOST_NONE ? new Location(x, y, z) : GeoEngine.moveCheckWithCollision(x, y, z, destX, destY, true, geoIndex);
		if(startPoint == null)
			startPoint = new Location(x, y, z);

		Location endPoint = Config.PATHFIND_BOOST != BOOST_BOTH || Math.abs(destZ - z) > 200 ? new Location(destX, destY, destZ) : GeoEngine.moveCheckBackwardWithCollision(destX, destY, destZ, startPoint.getX(), startPoint.getY(), true, geoIndex);
		if(endPoint == null)
			endPoint = new Location(destX, destY, destZ);

		startPoint.world2geo();
		endPoint.world2geo();
		
		//startPoint.z = GeoEngine.NgetHeight(startPoint.x, startPoint.y, startPoint.z, geoIndex);
		//endPoint.z = GeoEngine.NgetHeight(endPoint.x, endPoint.y, endPoint.z, geoIndex);

		int xdiff = Math.abs(endPoint.getX() - startPoint.getX());
		int ydiff = Math.abs(endPoint.getY() - startPoint.getY());

		if(xdiff == 0 && ydiff == 0)
		{
			if(Math.abs(endPoint.getZ() - startPoint.getZ()) < Config.PATHFIND_MAX_Z_DIFF)
			{
				List<Location> path = new ArrayList<Location>(2);
				path.add(new Location(x, y, z));
				path.add(new Location(destX, destY, destZ));
				return path;
			}
			return null;
		}

		List<Location> path = null;
		
		int mapSize = Config.PATHFIND_MAP_MUL * Math.max(xdiff, ydiff);
		
		PathFindBuffer buff;
		if((buff = PathFindBuffers.alloc(mapSize)) != null)
		{
			buff.offsetX = startPoint.getX() - buff.mapSize / 2;
			buff.offsetY = startPoint.getY() - buff.mapSize / 2;

			//статистика
			buff.totalUses++;
			if(isPlayable)
				buff.playableUses++;

			PathFind n = new PathFind(startPoint, endPoint, buff, geoIndex);
			path = n.findPath();

			buff.free();

			PathFindBuffers.recycle(buff);
		}
		
		if(path == null || path.isEmpty())
			return null;

		List<Location> targetRecorder = new ArrayList<Location>(path.size() + 2);

		// добавляем первую точку в список (начальная позиция чара)
		targetRecorder.add(new Location(x, y, z));

		for(Location p : path)
			targetRecorder.add(p.geo2world());

		// добавляем последнюю точку в список (цель)
		targetRecorder.add(new Location(destX, destY, destZ));

		if(Config.PATH_CLEAN)
			pathClean(targetRecorder, geoIndex);

		return targetRecorder;
	}
	
	/**
	 * Очищает путь от ненужных точек.
	 * @param path путь который следует очистить
	 */
	private static void pathClean(List<Location> path, int geoIndex)
	{
		int size = path.size();
		if(size > 2)
			for(int i = 2; i < size; i++)
			{
				Location p3 = path.get(i); // точка конца движения
				Location p2 = path.get(i - 1); // точка в середине, кандидат на вышибание
				Location p1 = path.get(i - 2); // точка начала движения
				if(p1.equals(p2) || p3.equals(p2) || IsPointInLine(p1, p2, p3)) // если вторая точка совпадает с первой/третьей или на одной линии с ними - она не нужна
				{
					path.remove(i - 1); // удаляем ее
					size--; // отмечаем это в размере массива
					i = Math.max(2, i - 2); // сдвигаемся назад, FIXME: может я тут не совсем прав
				}
			}

		int current = 0;
		int sub;
		while(current < path.size() - 2)
		{
			Location one = path.get(current);
			sub = current + 2;
			while(sub < path.size())
			{				
				Location two = path.get(sub);
				if(one.equals(two) || GeoEngine.canMoveWithCollision(one.getX(), one.getY(), one.getZ(), two.getX(), two.getY(), two.getZ(), geoIndex)) //canMoveWithCollision  /  canMoveToCoord
					while(current + 1 < sub)
					{
						path.remove(current + 1);
						sub--;
					}
				sub++;
			}
			current++;
		}
	}
	
	private static boolean IsPointInLine(Location p1, Location p2, Location p3)
	{
		// Все 3 точки на одной из осей X или Y.
		if(p1.getX() == p3.getX() && p3.getX() == p2.getX() || p1.getY() == p3.getY() && p3.getY() == p2.getY())
			return true;
		// Условие ниже выполнится если все 3 точки выстроены по диагонали.
		// Это работает потому, что сравниваем мы соседние точки (расстояния между ними равны, важен только знак).
		// Для случая с произвольными точками работать не будет.
		if((p1.getX() - p2.getX()) * (p1.getY() - p2.getY()) == (p2.getX() - p3.getX()) * (p2.getY() - p3.getY()))
			return true;
		return false;
	}
	
	private final int geoIndex;
	private final PathFindBuffer buff;
	private final short[] hNSWE = new short[2];
	private final Location startPoint, endPoint;
	private GeoNode startNode, endNode, currentNode;
	
	public PathFind(Location startPoint, Location endPoint, PathFindBuffer buff, int geoIndex)
	{
		this.geoIndex = geoIndex;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.buff = buff;
	}

	private List<Location> findPath()
	{
		startNode = buff.nodes[startPoint.getX() - buff.offsetX][startPoint.getY() - buff.offsetY].set(startPoint.getX(), startPoint.getY(), (short) startPoint.getZ());

		GeoEngine.NgetLowerHeightAndNSWE(startPoint.getX(), startPoint.getY(), (short) startPoint.getZ(), hNSWE, geoIndex);
		startNode.z = hNSWE[0];
		startNode.nswe = hNSWE[1];
		startNode.costFromStart = 0f;
		startNode.state = GeoNode.OPENED;
		startNode.parent = null;

		endNode = buff.nodes[endPoint.getX() - buff.offsetX][endPoint.getY() - buff.offsetY].set(endPoint.getX(), endPoint.getY(), (short) endPoint.getZ());

		startNode.costToEnd = pathCostEstimate(startNode);
		startNode.totalCost = startNode.costFromStart + startNode.costToEnd;

		buff.open.add(startNode);

		long nanos = System.nanoTime();
		long searhTime = 0;
		int itr = 0;

		List<Location> path = null;
		while((searhTime = System.nanoTime() - nanos) < Config.PATHFIND_MAX_TIME && (currentNode = buff.open.poll()) != null)
		{
			itr++;
			if(currentNode.x == endPoint.getX() && currentNode.y == endPoint.getY() && Math.abs(currentNode.z - endPoint.getZ()) < Config.MAX_Z_DIFF)
			{
				path = tracePath(currentNode);
				break;
			}

			handleNode(currentNode);
			currentNode.state = GeoNode.CLOSED;
		}

		buff.totalTime += searhTime;
		buff.totalItr += itr;
		if(path != null)
			buff.successUses++;
		else if(searhTime > Config.PATHFIND_MAX_TIME)
			buff.overtimeUses++;

		return path;
	}

	private List<Location> tracePath(GeoNode f)
	{
		LinkedList<Location> locations = new LinkedList<Location>();
		do
		{
			locations.addFirst(f.getLoc());
			f = f.parent;
		} while(f.parent != null);
		return locations;
	}

	private void handleNode(GeoNode node)
	{
		int clX = node.x;
		int clY = node.y;
		short clZ = node.z;

		getHeightAndNSWE(clX, clY, clZ);
		short NSWE = hNSWE[1];

		if(Config.PATHFIND_DIAGONAL)
		{
			// Юго-восток
			if((NSWE & SOUTH) == SOUTH && (NSWE & EAST) == EAST)
			{
				getHeightAndNSWE(clX + 1, clY, clZ);
				if((hNSWE[1] & SOUTH) == SOUTH)
				{
					getHeightAndNSWE(clX, clY + 1, clZ);
					if((hNSWE[1] & EAST) == EAST)
					{
						handleNeighbour(clX + 1, clY + 1, node, true);
					}
				}
			}

			// Юго-запад
			if((NSWE & SOUTH) == SOUTH && (NSWE & WEST) == WEST)
			{
				getHeightAndNSWE(clX - 1, clY, clZ);
				if((hNSWE[1] & SOUTH) == SOUTH)
				{
					getHeightAndNSWE(clX, clY + 1, clZ);
					if((hNSWE[1] & WEST) == WEST)
					{
						handleNeighbour(clX - 1, clY + 1, node, true);
					}
				}
			}

			// Северо-восток
			if((NSWE & NORTH) == NORTH && (NSWE & EAST) == EAST)
			{
				getHeightAndNSWE(clX + 1, clY, clZ);
				if((hNSWE[1] & NORTH) == NORTH)
				{
					getHeightAndNSWE(clX, clY - 1, clZ);
					if((hNSWE[1] & EAST) == EAST)
					{
						handleNeighbour(clX + 1, clY - 1, node, true);
					}
				}
			}

			// Северо-запад
			if((NSWE & NORTH) == NORTH && (NSWE & WEST) == WEST)
			{
				getHeightAndNSWE(clX - 1, clY, clZ);
				if((hNSWE[1] & NORTH) == NORTH)
				{
					getHeightAndNSWE(clX, clY - 1, clZ);
					if((hNSWE[1] & WEST) == WEST)
					{
						handleNeighbour(clX - 1, clY - 1, node, true);
					}
				}
			}
		}

		// Восток
		if((NSWE & EAST) == EAST)
		{
			handleNeighbour(clX + 1, clY, node, false);
		}

		// Запад
		if((NSWE & WEST) == WEST)
		{
			handleNeighbour(clX - 1, clY, node, false);
		}

		// Юг
		if((NSWE & SOUTH) == SOUTH)
		{
			handleNeighbour(clX, clY + 1, node, false);
		}

		// Север
		if((NSWE & NORTH) == NORTH)
		{
			handleNeighbour(clX, clY - 1, node, false);
		}
	}

	private float pathCostEstimate(GeoNode n)
	{
		int diffx = endNode.x - n.x;
		int diffy = endNode.y - n.y;
		int diffz = endNode.z - n.z;

		return (float) Math.sqrt(diffx * diffx + diffy * diffy + diffz * diffz / 256);
	}

	private float traverseCost(GeoNode from, GeoNode n, boolean d)
	{
		if(n.nswe != NSWE_ALL || Math.abs(n.z - from.z) > 16)
			return 3f;
		else
		{
			getHeightAndNSWE(n.x + 1, n.y, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16){ return 2f; }

			getHeightAndNSWE(n.x - 1, n.y, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16){ return 2f; }

			getHeightAndNSWE(n.x, n.y + 1, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16){ return 2f; }

			getHeightAndNSWE(n.x, n.y - 1, n.z);
			if(hNSWE[1] != NSWE_ALL || Math.abs(n.z - hNSWE[0]) > 16){ return 2f; }
		}

		return d ? 1.414f : 1f;
	}

	private void handleNeighbour(int x, int y, GeoNode from, boolean d)
	{
		int nX = x - buff.offsetX, nY = y - buff.offsetY;
		if(nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0)
			return;

		GeoNode n = buff.nodes[nX][nY];
		float newCost;

		if(!n.isSet())
		{
			n = n.set(x, y, from.z);
			GeoEngine.NgetLowerHeightAndNSWE(x, y, from.z, hNSWE, geoIndex);
			n.z = hNSWE[0];
			n.nswe = hNSWE[1];
		}

		int height = Math.abs(n.z - from.z);
		if(height > Config.PATHFIND_MAX_Z_DIFF || n.nswe == NSWE_NONE)
			return;

		newCost = from.costFromStart + traverseCost(from, n, d);
		if(n.state == GeoNode.OPENED || n.state == GeoNode.CLOSED)
		{
			if(n.costFromStart <= newCost)
				return;
		}

		if(n.state == GeoNode.NONE)
			n.costToEnd = pathCostEstimate(n);

		n.parent = from;
		n.costFromStart = newCost;
		n.totalCost = n.costFromStart + n.costToEnd;

		if(n.state == GeoNode.OPENED)
			return;

		n.state = GeoNode.OPENED;
		buff.open.add(n);
	}

	private void getHeightAndNSWE(int x, int y, short z)
	{
		int nX = x - buff.offsetX, nY = y - buff.offsetY;
		if(nX >= buff.mapSize || nX < 0 || nY >= buff.mapSize || nY < 0)
		{
			hNSWE[1] = NSWE_NONE; // Затычка
			return;
		}

		GeoNode n = buff.nodes[nX][nY];
		if(!n.isSet())
		{
			n = n.set(x, y, z);
			GeoEngine.NgetLowerHeightAndNSWE(x, y, z, hNSWE, geoIndex);
			n.z = hNSWE[0];
			n.nswe = hNSWE[1];
		}
		else
		{
			hNSWE[0] = n.z;
			hNSWE[1] = n.nswe;
		}
	}
}