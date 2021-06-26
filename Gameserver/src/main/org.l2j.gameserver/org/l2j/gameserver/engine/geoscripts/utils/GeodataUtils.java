package org.l2j.gameserver.engine.geoscripts.utils;

import java.awt.Color;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.geoscripts.GeoEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import org.l2j.gameserver.model.Location;

public class GeodataUtils
{
	public static final byte EAST = 1, WEST = 2, SOUTH = 4, NORTH = 8, NSWE_ALL = 15, NSWE_NONE = 0;
	
	public static void debug2DLine(Player player, int x, int y, int tx, int ty, int z)
	{
		int gx = GeoEngine.getMapX(x);
		int gy = GeoEngine.getMapY(y);
		
		int tgx = GeoEngine.getMapX(tx);
		int tgy = GeoEngine.getMapY(ty);
		
		ExServerPrimitive prim = new ExServerPrimitive("Debug2DLine", x, y, z);
		prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), z);
		
		LinePointIterator iter = new LinePointIterator(gx, gy, tgx, tgy);
		
		while(iter.next())
		{
			int wx = GeoEngine.getWorldX(iter.x());
			int wy = GeoEngine.getWorldY(iter.y());
			
			prim.addPoint(Color.RED, wx, wy, z);
		}
		player.sendPacket(prim);
	}
	
	public static void debug3DLine(Player player, int x, int y, int z, int tx, int ty, int tz)
	{
		int gx = GeoEngine.getMapX(x);
		int gy = GeoEngine.getMapY(y);
		
		int tgx = GeoEngine.getMapX(tx);
		int tgy = GeoEngine.getMapY(ty);
		
		ExServerPrimitive prim = new ExServerPrimitive("Debug3DLine", x, y, z);
		prim.addLine(Color.BLUE, GeoEngine.getWorldX(gx), GeoEngine.getWorldY(gy), z, GeoEngine.getWorldX(tgx), GeoEngine.getWorldY(tgy), tz);
		
		LinePointIterator3D iter = new LinePointIterator3D(gx, gy, z, tgx, tgy, tz);
		iter.next();
		int prevX = iter.x();
		int prevY = iter.y();
		int wx = GeoEngine.getWorldX(prevX);
		int wy = GeoEngine.getWorldY(prevY);
		int wz = iter.z();
		prim.addPoint(Color.RED, wx, wy, wz);
		
		while(iter.next())
		{
			int curX = iter.x();
			int curY = iter.y();
			
			if((curX != prevX) || (curY != prevY))
			{
				wx = GeoEngine.getWorldX(curX);
				wy = GeoEngine.getWorldY(curY);
				wz = iter.z();
				
				prim.addPoint(Color.RED, wx, wy, wz);
				
				prevX = curX;
				prevY = curY;
			}
		}
		player.sendPacket(prim);
	}
	
	private static Color getDirectionColor(int x, int y, int z, int geoIndex, byte NSWE)
	{
		// TODO: Цвет зависящий от положения персонажа и высоты слоя.
		if((GeoEngine.getLowerNSWE(x, y, z, geoIndex) & NSWE) != 0)
		{
			return Color.WHITE;
		}
		return Color.RED;
	}
	
	public static void debugGrid(Player player)
	{
		final int geoRadius = 20;
		final int blocksPerPacket = 10;
		int iBlock = blocksPerPacket;
		int iPacket = 0;
		
		ExServerPrimitive exsp = null;
		Location playerGeoLoc = player.getActingPlayer().getLocation().clone().world2geo();
		for(int dx = -geoRadius; dx <= geoRadius; ++dx)
		{
			for(int dy = -geoRadius; dy <= geoRadius; ++dy)
			{
				if(iBlock >= blocksPerPacket)
				{
					iBlock = 0;
					if(exsp != null)
					{
						++iPacket;
						player.sendPacket(exsp);
					}
					exsp = new ExServerPrimitive("DebugGrid_" + iPacket, player.getX(), player.getY(), -16000);
				}
				
				if(exsp == null)
				{
					throw new IllegalStateException();
				}

				int gx = playerGeoLoc.getX() + dx;
				int gy = playerGeoLoc.getY() + dy;

				int geoIndex = player.getGeoIndex();
				Location worldLoc = new Location(gx, gy, playerGeoLoc.getZ() + Config.MIN_LAYER_HEIGHT).geo2world();
				int x = worldLoc.getX();
				int y = worldLoc.getY();
				int z = GeoEngine.getLowerHeight(worldLoc, geoIndex);
				// north arrow
				Color col = getDirectionColor(x, y, z, geoIndex, NORTH);
				exsp.addLine("", col, true, x - 1, y - 7, z, x + 1, y - 7, z);
				exsp.addLine("N", col, true, x - 2, y - 6, z, x + 2, y - 6, z);
				exsp.addLine("", col, true, x - 3, y - 5, z, x + 3, y - 5, z);
				exsp.addLine("", col, true, x - 4, y - 4, z, x + 4, y - 4, z);
				
				// east arrow
				col = getDirectionColor(x, y, z, geoIndex, EAST);
				exsp.addLine("", col, true, x + 7, y - 1, z, x + 7, y + 1, z);
				exsp.addLine("E", col, true, x + 6, y - 2, z, x + 6, y + 2, z);
				exsp.addLine("", col, true, x + 5, y - 3, z, x + 5, y + 3, z);
				exsp.addLine("", col, true, x + 4, y - 4, z, x + 4, y + 4, z);
				
				// south arrow
				col = getDirectionColor(x, y, z, geoIndex, SOUTH);
				exsp.addLine("", col, true, x - 1, y + 7, z, x + 1, y + 7, z);
				exsp.addLine("S", col, true, x - 2, y + 6, z, x + 2, y + 6, z);
				exsp.addLine("", col, true, x - 3, y + 5, z, x + 3, y + 5, z);
				exsp.addLine("", col, true, x - 4, y + 4, z, x + 4, y + 4, z);
				
				col = getDirectionColor(x, y, z, geoIndex, WEST);
				exsp.addLine("", col, true, x - 7, y - 1, z, x - 7, y + 1, z);
				exsp.addLine("W", col, true, x - 6, y - 2, z, x - 6, y + 2, z);
				exsp.addLine("", col, true, x - 5, y - 3, z, x - 5, y + 3, z);
				exsp.addLine("", col, true, x - 4, y - 4, z, x - 4, y + 4, z);
				
				++iBlock;
			}
		}
		player.sendPacket(exsp);
	}
}